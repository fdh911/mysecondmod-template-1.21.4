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
import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImInt
import imgui.type.ImString
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import org.joml.Vector3d
import org.joml.Vector3f
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

    val disableOutsideGarden: Boolean
        get() = UIState.disableOutsideGarden.get()

    val disableOnServerClose: Boolean
        get() = UIState.disableOnServerClose.get()

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

    private object UIState {
        val sceneCreationNameEdit = ImString()
        var nodePtr: Node? = null
        var actionPtr: NodeAction? = null
        val nodeNameEdit = ImString()
        val nodeX = ImInt()
        val nodeY = ImInt()
        val nodeZ = ImInt()
        val disableOutsideGarden = ImBoolean(true)
        val disableOnServerClose = ImBoolean(true)
    }

    override fun UIWindow.setWindowContents() {
        ImGui.checkbox("Disable when outside garden", UIState.disableOutsideGarden)
        ImGui.checkbox("Disable on server close", UIState.disableOnServerClose)
        ImGui.separatorText("Scene")
        ImGui.text("Current: ${currentScene?.name ?: "None"}")
        ImGui.setNextItemWidth(-Float.MIN_VALUE)

        if(ImGui.button("New scene"))
            + sceneCreationWindow

        ImGui.sameLine()
        if(ImGui.button("Save scene"))
            currentScene?.saveToFile()

        ImGui.sameLine()
        if(ImGui.button("Load scene"))
            + sceneLoadingWindow

        if(currentScene == null)
            return

        val scene = currentScene!!

        val selectNodeInUI: UIWindow.(Node?) -> Unit = {
            node: Node? ->

            UIState.nodePtr = node
            if(node != null) {
                UIState.nodeNameEdit.set(node.name)
                UIState.nodeX.set(node.pos.x)
                UIState.nodeY.set(node.pos.y)
                UIState.nodeZ.set(node.pos.z)
            }

            + nodeEditorWindow
        }

        ImGui.separatorText("Nodes")
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.button("Add new node")) {
            val playerPos = MinecraftClient.getInstance().player!!.blockPos
            val newNode = Node(playerPos, "Node ${scene.nodeList.size}")
            scene.nodeList.add(newNode)
            selectNodeInUI(newNode)
        }

        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        var toRemove = -1
        if(ImGui.collapsingHeader("Existing nodes")) {
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##_2")) {
                for(i in scene.nodeList.indices) {
                    val node = scene.nodeList[i]
                    ImGui.pushID(i)
                    if(ImGui.button(node.name))
                        selectNodeInUI(node)
                    ImGui.sameLine()
                    if(ImGui.button(Unicodes.DUPLICATE.s)) {
                        val clonedNode = node.clone()
                        val regex = "(?<name>.*) (?<number>[0-9]*)".toRegex()
                        val match = regex.matchEntire(clonedNode.name)
                        clonedNode.name = if(match != null) {
                            val name = match.groups["name"]!!.value
                            val number = match.groups["number"]!!.value.toInt() + 1
                            "$name $number"
                        } else {
                            "${clonedNode.name} 1"
                        }
                        scene.nodeList.add(i + 1, clonedNode)
                        selectNodeInUI(clonedNode)
                    }
                    ImGui.sameLine()
                    if(ImGui.button(Unicodes.REMOVE.s)) {
                        toRemove = i
                        selectNodeInUI(null)
                    }
                    ImGui.popID()
                }
                ImGui.endListBox()
            }
        }
        if(toRemove != -1)
            scene.nodeList.removeAt(toRemove)
    }

    private val sceneCreationWindow = UIWindow("New scene") {
        ImGui.inputText("##_1", UIState.sceneCreationNameEdit)
        ImGui.setNextItemWidth(-Float.MIN_VALUE)

        if(ImGui.button("Create scene")) {
            val sceneName = if(UIState.sceneCreationNameEdit.get() == "")
                "Unnamed"
            else
                UIState.sceneCreationNameEdit.get()

            currentScene = NodeScene(sceneName)
        }
    }

    private val sceneLoadingWindow = UIWindow("Load scene") {
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

    private val nodeEditorWindow = UIWindow("Edit node") {
        val node = UIState.nodePtr

        if(node == null) {
            closeThisWindow()
            return@UIWindow
        }

        ImGui.separatorText("Name")
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        ImGui.inputText("##_name", UIState.nodeNameEdit)

        ImGui.separatorText("Position")
        ImGui.inputInt("X", UIState.nodeX)
        ImGui.inputInt("Y", UIState.nodeY)
        ImGui.inputInt("Z", UIState.nodeZ)

        ImGui.separatorText("Actions")
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.collapsingHeader("Add a new action ")) {
            var actionToAdd: NodeAction? = null
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##_addActionList")) {
                if(ImGui.selectable("Key action"))
                    actionToAdd = NodeActionKey()
                if(ImGui.selectable("Send chat message"))
                    actionToAdd = NodeActionSendMessage()
                if(ImGui.selectable("Wait"))
                    actionToAdd = NodeActionWait()
                if(ImGui.selectable("Rotation"))
                    actionToAdd = NodeActionRotate()
                if(ImGui.selectable("Lock / unlock mouse"))
                    actionToAdd = NodeActionMouselock()
                if(ImGui.selectable("Move cursor"))
                    actionToAdd = NodeActionMoveCursor()
                ImGui.endListBox()
            }
            if(actionToAdd != null) {
                node.actions.add(actionToAdd)
                UIState.actionPtr = actionToAdd
            }
        }

        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.collapsingHeader("Current actions")) {
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##_4")) {
                for(i in node.actions.indices) {
                    val action = node.actions[i]
                    if(ImGui.selectable("${action}##action$i")) {
                        UIState.actionPtr = action
                        + action.getEditorWindow()
                    }
                }
                ImGui.endListBox()
            }
        }

        val updatedPos = BlockPos(
            UIState.nodeX.get(),
            UIState.nodeY.get(),
            UIState.nodeZ.get(),
        )

        node.name = UIState.nodeNameEdit.get()
        node.pos = updatedPos

        ImGui.dummy(300.0f, 0.0f)
    }
}