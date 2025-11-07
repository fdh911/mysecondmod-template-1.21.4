package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.KeySimulator
import com.github.fdh911.render.UserInterface
import com.github.fdh911.utils.translate
import imgui.ImGui
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import java.util.Locale.getDefault

class NodeActionKey(
    private var keyToAffect: KeyBinding = MinecraftClient.getInstance().options.forwardKey,
    private var action: Action = Action.HOLD
): INodeAction
{
    enum class Action(val string: String) {
        HOLD("hold"),
        RELEASE("release"),
        PRESS("press");

        override fun toString() = string
        fun capitalizedString() = string.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
    }

    override suspend fun execute() {
        when(action) {
            Action.HOLD     -> KeySimulator.toHold.add(keyToAffect)
            Action.RELEASE  -> KeySimulator.toRelease.add(keyToAffect)
            Action.PRESS    -> KeySimulator.toPress.add(keyToAffect)
        }
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("${action.capitalizedString()} key action") {
            ImGui.separatorText("Currently selected")
            ImGui.text(keyToAffect.translationKey.translate())
            ImGui.separatorText("Select another key")
            if(ImGui.beginListBox("##_possibleKeys")) {
                val keyArray = MinecraftClient.getInstance().options.allKeys
                for(i in keyArray.indices) {
                    val key = keyArray[i]
                    if(ImGui.selectable("${key.translationKey.translate()}##_key$i"))
                        keyToAffect = key
                }
                ImGui.endListBox()
            }
            if(ImGui.button("Finish")) {
                keepRendering = false
            }
        }
        return keepRendering
    }

    override fun clone() = NodeActionKey(keyToAffect, action)

    override fun toString() = "${action.capitalizedString()}: ${keyToAffect.translationKey.translate()}"

    override val fileFormat = "${action.string}\n${keyToAffect.translationKey}"
}