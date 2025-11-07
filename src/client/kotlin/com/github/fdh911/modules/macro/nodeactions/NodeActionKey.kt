package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.KeySimulator
import com.github.fdh911.render.UserInterface
import com.github.fdh911.utils.translate
import imgui.ImGui
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import java.util.Locale.getDefault

@Serializable
class NodeActionKey(
    private var kbTranslationKey: String = MinecraftClient.getInstance().options.forwardKey.translationKey,
    private var action: Action = Action.HOLD
): NodeAction()
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
            Action.HOLD     -> KeySimulator.hold(kbTranslationKey)
            Action.RELEASE  -> KeySimulator.release(kbTranslationKey)
            Action.PRESS    -> KeySimulator.press(kbTranslationKey)
        }
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("${action.capitalizedString()} key action") {
            ImGui.separatorText("Currently selected")
            ImGui.text(kbTranslationKey.translate())
            ImGui.separatorText("Select another key")
            if(ImGui.beginListBox("##_possibleKeys")) {
                val keyArray = MinecraftClient.getInstance().options.allKeys
                for(i in keyArray.indices) {
                    val key = keyArray[i].translationKey
                    if(ImGui.selectable("${key.translate()}##_key$i"))
                        kbTranslationKey = key
                }
                ImGui.endListBox()
            }
            if(ImGui.button("Finish")) {
                keepRendering = false
            }
        }
        return keepRendering
    }

    override fun clone() = NodeActionKey(kbTranslationKey, action)

    override fun toString() = "${action.capitalizedString()}: ${kbTranslationKey.translate()}"
}