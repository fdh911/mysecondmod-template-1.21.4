package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.KeySimulator
import com.github.fdh911.ui.UIWindow
import com.github.fdh911.utils.translate
import imgui.ImGui
import imgui.type.ImInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient

@Serializable
@SerialName("Key")
class NodeActionKey(
    private var type: Type = Type.HOLD,
    private var kbTranslationKey: String = MinecraftClient.getInstance().options.forwardKey.translationKey,
): NodeAction()
{
    enum class Type(val string: String) {
        HOLD("Hold"),
        RELEASE("Release"),
        PRESS("Press");

        override fun toString() = string
    }

    override suspend fun execute() {
        when(type) {
            Type.HOLD     -> KeySimulator.hold(kbTranslationKey)
            Type.RELEASE  -> KeySimulator.release(kbTranslationKey)
            Type.PRESS    -> KeySimulator.press(kbTranslationKey)
        }
    }

    override fun clone() = NodeActionKey(type, kbTranslationKey)

    override fun toString() = "$type: ${kbTranslationKey.translate()}"

    override fun getEditorWindow() = EditorWindow.getWindow(this)

    private object EditorWindow {
        val selectedType = ImInt()

        fun getWindow(action: NodeActionKey) = UIWindow("Key action") {
            ImGui.separatorText("Type")
            selectedType.set(action.type.ordinal)

            ImGui.radioButton(Type.HOLD.string, selectedType, Type.HOLD.ordinal)
            ImGui.sameLine()
            ImGui.radioButton(Type.RELEASE.string, selectedType, Type.RELEASE.ordinal)
            ImGui.sameLine()
            ImGui.radioButton(Type.PRESS.string, selectedType, Type.PRESS.ordinal)

            ImGui.separatorText("Currently selected")
            ImGui.text(action.kbTranslationKey.translate())

            ImGui.separatorText("Select another key")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##_possibleKeys")) {
                val keyArray = MinecraftClient.getInstance().options.allKeys
                for(i in keyArray.indices) {
                    ImGui.pushID(i)
                    val key = keyArray[i].translationKey
                    if(ImGui.selectable(key.translate()))
                        action.kbTranslationKey = key
                    ImGui.popID()
                }
                ImGui.endListBox()
            }

            action.type = Type.entries[selectedType.get()]
        }
    }
}