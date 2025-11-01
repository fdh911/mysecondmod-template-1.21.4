package com.github.fdh911.modules.garden

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding

class NodeActionHoldKey(private var keyToHold: KeyBinding = MinecraftClient.getInstance().options.forwardKey): INodeAction {
    override suspend fun execute() {
        KeySimulator.toHold.add(keyToHold)
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Hold key action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.separatorText("Currently selected")
            ImGui.text("${keyToHold.translationKey}")
            ImGui.separatorText("Select another key")
            if(ImGui.beginListBox("##_possibleKeys")) {
                val keyArray = MinecraftClient.getInstance().options.allKeys
                for(i in keyArray.indices) {
                    val key = keyArray[i]
                    if(ImGui.selectable("${key.translationKey}##_key$i"))
                        keyToHold = key
                }
                ImGui.endListBox()
            }
            if(ImGui.button("Finish")) {
                keepRendering = false
            }
        }
        return keepRendering
    }

    override fun clone() = NodeActionHoldKey(keyToHold)

    override val fileFormat: String
        get() {
            return "hold\n${keyToHold.translationKey}"
        }

    override fun toString() = "Hold: ${keyToHold.translationKey}"
}