package com.github.fdh911.modules.garden

import com.github.fdh911.modules.Module
import com.github.fdh911.render.CuboidRenderer
import com.github.fdh911.render.UserInterface
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
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.Team
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector4f
import java.io.File
import java.util.LinkedList
import java.util.Queue
import kotlin.math.max
import kotlin.math.min

@OptIn(DelicateCoroutinesApi::class)
object ModuleGardenMacro: Module("Garden Macro") {
    var currentScene: NodeScene? = null
    var currentNode: Node? = null
    private val actionQueue: Queue<INodeAction> = LinkedList()

    init {
        GlobalScope.launch {
            while(true) {
                if(!toggled || actionQueue.isEmpty()) {
                    actionQueue.clear()
                    delay(1L)
                    continue
                }
                val action = actionQueue.remove()
                action.execute()
            }
        }
    }

    val disableOutsideGarden: Boolean
        get() = UIState.disableOutsideGarden.get()

    val disableOnServerClose: Boolean
        get() = UIState.disableOnServerClose.get()

    override fun update() {
        val scoreboard = readScoreboard()

        // TODO maybe do this via tab
        if(disableOutsideGarden && scoreboard?.contains("The Garden") != true && scoreboard?.contains("Plot ") != true) {
            toggled = false
            return
        }

        if(disableOnServerClose && scoreboard?.contains("Server closing") == true) {
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
                actionQueue.addAll(newCurrentNode.actions)

            break
        }

        currentNode = newCurrentNode
    }

    object RenderConstants {
        val red = Vector4f(1.0f, 0.0f, 0.0f, 0.4f)
        val blue = Vector4f(0.0f, 0.0f, 1.0f, 0.4f)
        val scale = Vector3f(1.0f, 1.0f, 1.0f)
    }

    override fun renderUpdate(ctx: WorldRenderContext) {
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
        var sceneCreation = false
        val sceneCreationNameEdit = ImString()
        var nodePtr: Node? = null
        var actionPtr: INodeAction? = null
        val nodeNameEdit = ImString()
        val nodeX = ImInt()
        val nodeY = ImInt()
        val nodeZ = ImInt()
        var sceneLoading = false
        val disableOutsideGarden = ImBoolean(true)
        val disableOnServerClose = ImBoolean(true)
    }

    override fun renderUI() {
        ImGui.setWindowSize(0.0f, 0.0f)
        ImGui.checkbox("Disable when outside garden", UIState.disableOutsideGarden)
        ImGui.checkbox("Disable on server close", UIState.disableOnServerClose)
        ImGui.separatorText("Scene")
        ImGui.text("Current: ${currentScene?.name ?: "None"}")
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.button("New scene") || UIState.sceneCreation) {
            UIState.sceneCreation = true
            UserInterface.newWindow("Create a new scene") {
                ImGui.setWindowSize(0.0f, 0.0f)
                ImGui.inputText("##_1", UIState.sceneCreationNameEdit)
                ImGui.setNextItemWidth(-Float.MIN_VALUE)
                if(ImGui.button("Create scene")) {
                    val sceneName = if(UIState.sceneCreationNameEdit.get() == "")
                        "Unnamed"
                    else
                        UIState.sceneCreationNameEdit.get()
                    currentScene = NodeScene(sceneName)
                    UIState.sceneCreation = false
                }
                if(ImGui.button("Cancel"))
                    UIState.sceneCreation = false
            }
        }
        else UIState.sceneCreation = false

        if(ImGui.button("Save scene")) {
            currentScene!!.saveToFile()
        }
        ImGui.sameLine()
        if(ImGui.button("Load scene") || UIState.sceneLoading) {
            UIState.sceneLoading = true
            UserInterface.newWindow("Choose file") {
                ImGui.setWindowSize(0.0f, 0.0f)
                val files = File(".").listFiles()
                for(i in files.indices) {
                    val subfile = files[i]
                    if(!subfile.name.matches(".*mysecondmod[.]scene[.]txt".toRegex())) continue
                    if(ImGui.selectable("${subfile.nameWithoutExtension}##_file$i")) {
                        currentScene = NodeScene.loadFromFile(subfile)
                        UIState.sceneLoading = false
                    }
                }
                if(ImGui.button("Cancel")) {
                    UIState.sceneLoading = false
                }
            }
        }

        if(currentScene == null) return

        val scene = currentScene!!

        val selectNodeInUI = {
            node: Node? ->
            UIState.nodePtr = node
            if(node != null) {
                UIState.nodeNameEdit.set(node.name)
                UIState.nodeX.set(node.pos.x)
                UIState.nodeY.set(node.pos.y)
                UIState.nodeZ.set(node.pos.z)
            }
        }

        ImGui.separatorText("Nodes")
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
                    if(ImGui.button("dup")) {
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
                    if(ImGui.button("rm")) {
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

        if(UIState.nodePtr != null) {
            val node = UIState.nodePtr!!

            UserInterface.newWindow("Edit node") {
                ImGui.setWindowSize(0.0f, 0.0f)
                ImGui.inputText("##_3", UIState.nodeNameEdit)
                ImGui.inputInt("X", UIState.nodeX)
                ImGui.inputInt("Y", UIState.nodeY)
                ImGui.inputInt("Z", UIState.nodeZ)

                ImGui.setNextItemWidth(-Float.MIN_VALUE)
                if(ImGui.collapsingHeader("Add a new action ")) {
                    var actionToAdd: INodeAction? = null
                    ImGui.setNextItemWidth(-Float.MIN_VALUE)
                    if(ImGui.beginListBox("##_addActionList")) {
                        if(ImGui.selectable("Hold key"))
                            actionToAdd = NodeActionHoldKey()
                        if(ImGui.selectable("Press key"))
                            actionToAdd = NodeActionPressKey()
                        if(ImGui.selectable("Release key"))
                            actionToAdd = NodeActionReleaseKey()
                        if(ImGui.selectable("Send chat message"))
                            actionToAdd = NodeActionSendMessage()
                        if(ImGui.selectable("Wait"))
                            actionToAdd = NodeActionWait()
                        if(ImGui.selectable("Lock yaw & pitch"))
                            actionToAdd = NodeActionLockMouse()
                        if(ImGui.selectable("Unlock yaw & pitch"))
                            actionToAdd = NodeActionUnlockMouse()
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
                            }
                        }
                        ImGui.endListBox()
                    }
                }

                if(UIState.actionPtr?.renderUI() != true)
                    UIState.actionPtr = null

                val updatedPos = BlockPos(
                    UIState.nodeX.get(),
                    UIState.nodeY.get(),
                    UIState.nodeZ.get(),
                )

                node.name = UIState.nodeNameEdit.get()
                node.pos = updatedPos

                ImGui.setNextItemWidth(-Float.MIN_VALUE)
                if(ImGui.button("Finish"))
                    UIState.nodePtr = null
            }
        }
    }

    private fun readScoreboard(): String? {
        val scoreboard = MinecraftClient.getInstance().world?.scoreboard
            ?: return null

        val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR)
            ?: return null

        val title = objective.displayName.string

        val contents = StringBuilder()
        contents.append(title)

        val entries = scoreboard.getScoreboardEntries(objective)

        for(entry in entries) {
            val owner = entry.owner()
            val team = scoreboard.getScoreHolderTeam(owner)
            val decorated = (if(team != null)
                Team.decorateName(team, Text.of(owner))
            else
                Text.of(owner)).string.trimEnd()
            contents.append(decorated)
        }

        return contents.toString().replace("\u00A7.".toRegex(), "")
    }
}