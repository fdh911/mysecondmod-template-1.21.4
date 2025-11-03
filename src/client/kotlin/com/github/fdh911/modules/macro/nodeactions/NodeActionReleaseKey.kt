package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.KeySimulator
import com.github.fdh911.render.UserInterface
import imgui.ImGui
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding

class NodeActionReleaseKey(private var keyToRelease: KeyBinding = MinecraftClient.getInstance().options.forwardKey): INodeAction {
    override suspend fun execute() {
        KeySimulator.toRelease.add(keyToRelease)
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Release key action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.separatorText("Currently selected")
            ImGui.text("${keyToRelease.translationKey}")
            ImGui.separatorText("Select another key")
            if(ImGui.beginListBox("##_possibleKeys")) {
                val keyArray = MinecraftClient.getInstance().options.allKeys
                for(i in keyArray.indices) {
                    val key = keyArray[i]
                    if(ImGui.selectable("${key.translationKey}##_key$i"))
                        keyToRelease = key
                }
                ImGui.endListBox()
            }
            if(ImGui.button("Finish")) {
                keepRendering = false
            }
        }
        return keepRendering
    }

    override fun clone() = NodeActionReleaseKey(keyToRelease)

    override val fileFormat: String
        get() = "release\n${keyToRelease.translationKey}"

    override fun toString() = "Release: ${keyToRelease.translationKey}"
}