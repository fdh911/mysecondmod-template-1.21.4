package com.github.fdh911.modules

import com.github.fdh911.modules.macro.Node
import com.github.fdh911.modules.macro.NodeScene
import com.github.fdh911.modules.macro.controls.ActionQueue
import com.github.fdh911.modules.macro.controls.CursorManager
import com.github.fdh911.modules.macro.controls.KeybindManager
import com.github.fdh911.modules.macro.nodeactions.*
import com.github.fdh911.render.CuboidRenderer
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
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

@OptIn(DelicateCoroutinesApi::class)
object ModuleGardenMacro: Module("Garden Macro")
{
    var currentScene: NodeScene? = null
    var currentNode: Node? = null

    var disableOutsideGarden = false
    var disableOnServerClose = false

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
        val red = Vector4f(1.0f, 0.0f, 0.0f, 0.4f)
        val blue = Vector4f(0.0f, 0.0f, 1.0f, 0.4f)
        val scale = Vector3f(1.0f, 1.0f, 1.0f)
    }

    override fun onRenderUpdate(ctx: WorldRenderContext) {
        if(currentScene == null) return
        val scene = currentScene!!
        for(node in scene.nodeList) {
            val posVector3f = Vector3f(
                node.pos.x.toFloat(),
                node.pos.y.toFloat(),
                node.pos.z.toFloat(),
            )
            val color = if(node == currentNode)
                RenderConstants.red
            else
                RenderConstants.blue
            CuboidRenderer.render(
                ctx,
                posVector3f,
                RenderConstants.scale,
                color
            )
        }
    }

    override fun UIWindow.setWindowContents() = with(MainWindowContents) { windowContents() }

    private object NodeEditorWindow {
        private val nameImString = ImString()
        private val xImInt = ImInt()
        private val yImInt = ImInt()
        private val zImInt = ImInt()
        private var actionWindow: UIWindow? = null

        fun getWindow(node: Node?) = UIWindow("Edit node") {
            if(node == null) {
                closeThisWindow()
                return@UIWindow
            }

            nameImString.set(node.name)
            xImInt.set(node.pos.x)
            yImInt.set(node.pos.y)
            zImInt.set(node.pos.z)

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
                    node.actions.add(actionToAdd)
                    actionWindow?.closeThisWindow()
                    actionWindow = actionToAdd.getEditorWindow()
                    + actionWindow!!
                }
            }

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.collapsingHeader("Current actions")) {
                val actions = node.actions

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

            node.name = nameImString.get()
            node.pos.x = xImInt.get()
            node.pos.y = yImInt.get()
            node.pos.z = zImInt.get()
        }
    }

    private object SceneLoadingWindow {
        fun getWindow() = UIWindow("Load scene") {
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
                    currentScene = NodeScene.loadFromFile(file)
                    closeThisWindow()
                }
                ImGui.popID()
            }
        }
    }

    private object SceneCreationWindow {
        private val nameImString = ImString()

        fun getWindow() = UIWindow("Create scene") {
            ImGui.separatorText("Name")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            ImGui.inputText("##_name", nameImString)

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.button("Create")) {
                val sceneName = nameImString.let { if(it.isEmpty) "Unnamed" else it.get() }

                currentScene = NodeScene(sceneName)

                closeThisWindow()
            }
        }
    }

    private object MainWindowContents {
        private val disableOutsideGardenImBoolean = ImBoolean(false)
        private val disableOnServerCloseImBoolean = ImBoolean(false)

        fun UIWindow.windowContents() {
            ImGui.separatorText("Failsafes")
            ImGui.checkbox("Disable outside Garden", disableOutsideGardenImBoolean)
            ImGui.checkbox("Disable on server close", disableOnServerCloseImBoolean)

            disableOutsideGarden = disableOutsideGardenImBoolean.get()
            disableOnServerClose = disableOnServerCloseImBoolean.get()

            ImGui.separatorText("Scene")
            ImGui.text("Current: ${currentScene?.name ?: "None"}")

            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.button("New scene"))
                + SceneCreationWindow.getWindow()

            ImGui.sameLine()
            if(ImGui.button("Save scene"))
                currentScene?.saveToFile()

            ImGui.sameLine()
            if(ImGui.button("Load scene"))
                + SceneLoadingWindow.getWindow()

            if(currentScene == null)
                return

            val scene = currentScene!!
            val nodes = scene.nodeList

            ImGui.separatorText("Nodes")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.button("Add new node")) {
                val pos = mc.player!!.blockPos.let { Vector3i(it.x, it.y, it.z) }
                val name = "Node ${nodes.size}"

                val node = Node(pos, name)

                nodes.add(node)

                + NodeEditorWindow.getWindow(node)
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
                            + NodeEditorWindow.getWindow(clonedNode)
                        }
                        ImGui.popStyleVar()

                        ImGui.sameLine()
                        if(ImGui.button(node.name))
                            + NodeEditorWindow.getWindow(node)

                        ImGui.popID()
                    }

                    if(toRemove != null)
                        nodes.removeAt(toRemove)

                    ImGui.endListBox()
                }
            }
        }
    }
}