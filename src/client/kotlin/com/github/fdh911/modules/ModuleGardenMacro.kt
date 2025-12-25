package com.github.fdh911.modules

import com.github.fdh911.modules.macro.Node
import com.github.fdh911.modules.macro.NodeScene
import com.github.fdh911.modules.macro.controls.ActionQueue
import com.github.fdh911.modules.macro.controls.CursorManager
import com.github.fdh911.modules.macro.controls.KeybindManager
import com.github.fdh911.modules.macro.nodeactions.*
import com.github.fdh911.render.TranslucentCuboids
import com.github.fdh911.render.Unicodes
import com.github.fdh911.state.SkyblockState
import com.github.fdh911.ui.UIWindow
import com.github.fdh911.utils.mc
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.type.ImBoolean
import imgui.type.ImInt
import imgui.type.ImString
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Serializable
@SerialName("garden_macro")
class ModuleGardenMacro: Module("Garden Macro")
{
    @SerialName("disable_outside_garden") var disableOutsideGarden = true
    @SerialName("disable_sv_close") var disableOnServerClose = true

    @Transient var currentScene: NodeScene? = null
    @Transient var currentNode: Node? = null

    @Transient private var nodeRenderer: TranslucentCuboids.Instanced? = null
    @Transient private var nodesShouldUpdate = false

    override fun onDisable() {
        CursorManager.isMouseLocked = false
        KeybindManager.clear()
        ActionQueue.clear()
    }

    override fun onUpdate() {
        KeybindManager.update()

        if(disableOutsideGarden && !SkyblockState.Garden.isInGarden) {
            toggled = false
            return
        }

        if(disableOnServerClose && SkyblockState.isServerClosing == true) {
            toggled = false
            return
        }

        if(currentScene == null) return
        val scene = currentScene!!

        val player = MinecraftClient.getInstance().player!!
        val aabb = player.boundingBox
        val p1 = aabb.minPos
        val p2 = aabb.maxPos

        var newCurrentNode: Node? = null

        for(node in scene.nodeList) {
            val n1 = Vector3d(node.pos.x.toDouble(), node.pos.y.toDouble(), node.pos.z.toDouble())
            val n2 = Vector3d(n1).add(1.0, 1.0, 1.0)
            val i1 = Vector3d(
                max(p1.x, n1.x),
                max(p1.y, n1.y),
                max(p1.z, n1.z),
            )
            val i2 = Vector3d(
                min(p2.x, n2.x),
                min(p2.y, n2.y),
                min(p2.z, n2.z),
            )
            if(i1.x > i2.x || i1.y > i2.y || i1.z > i2.z)
                continue

            newCurrentNode = node

            if(currentNode != newCurrentNode)
                ActionQueue += newCurrentNode.actions

            break
        }

        currentNode = newCurrentNode
    }

    object RenderConstants {
        val red = Vector3f(1.0f, 0.0f, 0.0f)
        val blue = Vector3f(0.0f, 0.0f, 1.0f)
    }

    override fun onRenderUpdate(ctx: WorldRenderContext) {
        if(nodesShouldUpdate) {
            onNodesChanged()
            nodesShouldUpdate = false
        }

        nodeRenderer?.render(
            ctx = ctx,
            drawSolids = true,
            drawOutlines = true,
        )
    }

    override fun UIWindow.setWindowContents() = with(MainWindowContents) { windowContents(this@ModuleGardenMacro) }

    fun onNodesChanged() {
        if(currentScene == null) {
            nodeRenderer = null
            return
        }

        val scene = currentScene!!

        val renderer = TranslucentCuboids.Instanced()
        renderer.begin(scene.nodeList.size)

        for(node in scene.nodeList) {
            val color = if(node == currentNode)
                RenderConstants.red
            else
                RenderConstants.blue

            renderer.addCube(
                pos = Vector3f(node.pos),
                color = Vector4f(color, 0.3f)
            )
        }

        renderer.finish()
        nodeRenderer = renderer
    }

    private object NodeEditorWindow {
        var node: Node? = null
        private val nameImString = ImString()
        private val xImInt = ImInt()
        private val yImInt = ImInt()
        private val zImInt = ImInt()
        private var actionWindow: UIWindow? = null

        fun getWindow(owner: ModuleGardenMacro) = UIWindow("Edit node") {
            if(node == null) {
                actionWindow?.closeThisWindow()
                closeThisWindow()
                return@UIWindow
            }

            val n = node!!

            nameImString.set(n.name)
            xImInt.set(n.pos.x)
            yImInt.set(n.pos.y)
            zImInt.set(n.pos.z)

            ImGui.separatorText("Name")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            ImGui.inputText("##_name", nameImString)

            ImGui.separatorText("Position")
            ImGui.inputInt("X", xImInt)
            ImGui.inputInt("Y", yImInt)
            ImGui.inputInt("Z", zImInt)

            ImGui.separatorText("Actions")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.collapsingHeader("Add a new action ")) {
                ImGui.setNextItemWidth(-Float.MIN_VALUE)

                var actionToAdd: NodeAction? = null

                if(ImGui.beginListBox("##_addActionList")) {

                    ActionsRegistry.entries.forEach { (name, provider) ->
                        if(ImGui.selectable(name))
                            actionToAdd = provider()
                    }

                    ImGui.endListBox()
                }

                if(actionToAdd != null) {
                    n.actions.add(actionToAdd)
                    actionWindow?.closeThisWindow()
                    actionWindow = actionToAdd.getEditorWindow()
                    + actionWindow!!
                }
            }

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.collapsingHeader("Current actions")) {
                val actions = n.actions

                ImGui.setNextItemWidth(-Float.MIN_VALUE)
                if(ImGui.beginListBox("##_currentActions")) {
                    var toRemove: Int? = null

                    actions.zip(actions.indices).forEach { (action, i) ->
                        ImGui.pushID(i)

                        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f)
                        if(ImGui.smallButton(Unicodes.REMOVE.s)) {
                            toRemove = i
                        }
                        ImGui.sameLine()
                        if(ImGui.smallButton(Unicodes.ANGLE_UP.s) && i > 0) {
                            val aux = actions[i - 1]
                            actions[i - 1] = actions[i]
                            actions[i] = aux
                        }
                        ImGui.sameLine()
                        if(ImGui.smallButton(Unicodes.ANGLE_DOWN.s) && i < actions.size - 1) {
                            val aux = actions[i + 1]
                            actions[i + 1] = actions[i]
                            actions[i] = aux
                        }
                        ImGui.popStyleVar()

                        ImGui.sameLine()
                        if(ImGui.button(action.toString())) {
                            actionWindow?.closeThisWindow()
                            actionWindow = action.getEditorWindow()
                            + actionWindow!!
                        }

                        ImGui.popID()
                    }

                    if(toRemove != null)
                        actions.removeAt(toRemove)

                    ImGui.endListBox()
                }
            }

            ImGui.dummy(300.0f, 0.0f)

            val newPos = Vector3i(
                xImInt.get(),
                yImInt.get(),
                zImInt.get(),
            )

            if(n.pos != newPos)
                owner.nodesShouldUpdate = true

            n.name = nameImString.get()
            n.pos = newPos
        }
    }

    private object SceneLoadingWindow {
        fun getWindow(owner: ModuleGardenMacro) = UIWindow("Load scene") {
            val directory = File(".").listFiles()

            for(i in directory.indices) {
                val file = directory[i]

                val name = "(?<scenename>[^.]+)[.]msmscene[.]json".toRegex()
                    .matchEntire(file.name)
                    ?.groups
                    ?.get("scenename")
                    ?.value
                    ?:continue

                ImGui.pushID(i)
                if(ImGui.selectable(name)) {
                    owner.currentScene = NodeScene.loadFromFile(file)
                    owner.nodesShouldUpdate = true
                    NodeEditorWindow.node = null
                    closeThisWindow()
                }
                ImGui.popID()
            }
        }
    }

    private object SceneCreationWindow {
        private val nameImString = ImString()

        fun getWindow(owner: ModuleGardenMacro) = UIWindow("Create scene") {
            ImGui.separatorText("Name")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            ImGui.inputText("##_name", nameImString)

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.button("Create")) {
                val sceneName = nameImString.let { if(it.isEmpty) "Unnamed" else it.get() }

                owner.currentScene = NodeScene(sceneName)
                owner.nodesShouldUpdate = true
                NodeEditorWindow.node = null

                closeThisWindow()
            }
        }
    }

    private object MainWindowContents {
        private val disableOutsideGardenImBoolean = ImBoolean(false)
        private val disableOnServerCloseImBoolean = ImBoolean(false)

        fun UIWindow.windowContents(owner: ModuleGardenMacro) {
            ImGui.separatorText("Failsafes")
            ImGui.checkbox("Disable outside Garden", disableOutsideGardenImBoolean)
            ImGui.checkbox("Disable on server close", disableOnServerCloseImBoolean)

            owner.disableOutsideGarden = disableOutsideGardenImBoolean.get()
            owner.disableOnServerClose = disableOnServerCloseImBoolean.get()

            ImGui.separatorText("Scene")
            ImGui.text("Current: ${owner.currentScene?.name ?: "None"}")

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.button("New scene"))
                + SceneCreationWindow.getWindow(owner)

            ImGui.sameLine()
            if(ImGui.button("Save scene"))
                owner.currentScene?.saveToFile()

            ImGui.sameLine()
            if(ImGui.button("Load scene"))
                + SceneLoadingWindow.getWindow(owner)

            if(owner.currentScene == null)
                return

            val scene = owner.currentScene!!
            val nodes = scene.nodeList

            ImGui.separatorText("Nodes")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.button("Add new node")) {
                val pos = mc.player!!.blockPos.let { Vector3i(it.x, it.y, it.z) }
                val name = "Node ${nodes.size}"

                val node = Node(pos, name)

                nodes.add(node)
                owner.nodesShouldUpdate = true

                NodeEditorWindow.node = node
                + NodeEditorWindow.getWindow(owner)
            }

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.collapsingHeader("Existing nodes")) {

                ImGui.setNextItemWidth(-Float.MIN_VALUE)
                if(ImGui.beginListBox("##_2")) {
                    var toRemove: Int? = null

                    nodes.zip(nodes.indices).forEach { (node, i) ->
                        ImGui.pushID(i)

                        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f)
                        if(ImGui.smallButton(Unicodes.REMOVE.s))
                            toRemove = i

                        ImGui.sameLine()
                        if(ImGui.smallButton(Unicodes.DUPLICATE.s)) {
                            val clonedNode = node.clone()
                            clonedNode.name = "(?<name>.*) (?<number>[0-9]*)".toRegex()
                                .matchEntire(clonedNode.name)
                                ?.let {
                                    val name = it.groups["name"]!!.value
                                    val number = it.groups["number"]!!.value.toInt() + 1
                                    "$name $number"
                                }
                                ?: "${clonedNode.name} 1"

                            nodes.add(i + 1, clonedNode)
                            owner.nodesShouldUpdate = true

                            NodeEditorWindow.node = clonedNode
                            + NodeEditorWindow.getWindow(owner)
                        }
                        ImGui.popStyleVar()

                        ImGui.sameLine()
                        if(ImGui.button(node.name)) {
                            NodeEditorWindow.node = node
                            + NodeEditorWindow.getWindow(owner)
                        }

                        ImGui.popID()
                    }

                    if(toRemove != null) {
                        if(nodes[toRemove] == NodeEditorWindow.node)
                            NodeEditorWindow.node = null
                        nodes.removeAt(toRemove)
                        owner.nodesShouldUpdate = true
                    }

                    ImGui.endListBox()
                }
            }
        }
    }
}