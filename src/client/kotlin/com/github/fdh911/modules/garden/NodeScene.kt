package com.github.fdh911.modules.garden

import imgui.type.ImFloat
import imgui.type.ImInt
import imgui.type.ImString
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.util.math.BlockPos
import java.io.File

class NodeScene(var name: String) {
    companion object {
        fun loadFromFile(file: File): NodeScene {
            val br = file.bufferedReader()
            val name = br.readLine()
            val scene = NodeScene(name)
            val nodeCount = br.readLine().toInt()
            repeat(nodeCount) {
                val nodeName = br.readLine()
                val nodeX = br.readLine().toInt()
                val nodeY = br.readLine().toInt()
                val nodeZ = br.readLine().toInt()
                val nodeActionCnt = br.readLine().toInt()
                val node = Node(BlockPos(nodeX, nodeY, nodeZ), nodeName)
                repeat(nodeActionCnt) {
                    val actionType = br.readLine()
                    var action: INodeAction? = null
                    if(actionType == "hold" || actionType == "press" || actionType == "release") {
                        val keyTranslation = br.readLine()
                        var nodeKey: KeyBinding? = null
                        for(key in MinecraftClient.getInstance().options.allKeys)
                            if(key.translationKey == keyTranslation) {
                                nodeKey = key
                                break
                            }
                        if(nodeKey == null)
                            throw RuntimeException("No such key exists: $keyTranslation")
                        action = when(actionType) {
                            "hold" -> NodeActionHoldKey(nodeKey)
                            "press" -> NodeActionPressKey(nodeKey)
                            "release" -> NodeActionReleaseKey(nodeKey)
                            else -> null
                        }
                    }
                    action = when(actionType) {
                        "hold" -> action
                        "press" -> action
                        "release" -> action
                        "send" -> {
                            val msg = br.readLine()
                            NodeActionSendMessage(ImString().apply { set(msg) })
                        }
                        "wait" -> {
                            val ms = br.readLine().toInt()
                            NodeActionWait(ImInt(ms))
                        }
                        "rotateexact" -> {
                            val yaw = br.readLine().toFloat()
                            val pitch = br.readLine().toFloat()
                            NodeActionRotateExact(ImFloat(yaw), ImFloat(pitch))
                        }
                        "rotatedelta" -> {
                            val yawDelta = br.readLine().toFloat()
                            val pitchDelta = br.readLine().toFloat()
                            NodeActionRotateDelta(ImFloat(yawDelta), ImFloat(pitchDelta))
                        }
                        "lockMouse" -> NodeActionLockMouse()
                        "unlockMouse" -> NodeActionUnlockMouse()
                        else -> null
                    }
                    if(action == null)
                        throw RuntimeException("Unknown action: $actionType")
                    node.actions.add(action)
                }
                scene.nodeList.add(node)
            }
            return scene
        }
    }

    val nodeList = mutableListOf<Node>()

    fun saveToFile() {
        val file = File("$name.mysecondmod.scene.txt")
        if(file.exists()) {
            file.delete()
            file.createNewFile()
        }
        file.appendText("$name\n${nodeList.size}\n")
        for(node in nodeList) {
            file.appendText("${node.name}\n")
            file.appendText("${node.pos.x}\n")
            file.appendText("${node.pos.y}\n")
            file.appendText("${node.pos.z}\n")
            file.appendText("${node.actions.size}\n")
            for(action in node.actions)
                file.appendText("${action.fileFormat}\n")
        }
    }
}