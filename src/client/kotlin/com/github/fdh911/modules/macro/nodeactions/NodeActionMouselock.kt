package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.controls.CursorManager
import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Mouselock")
class NodeActionMouselock(var type: Type = Type.LOCK): NodeAction()
{
    enum class Type(val string: String) {
        LOCK("Lock"),
        UNLOCK("Unlock");

        override fun toString() = string
    }

    override suspend fun execute() {
        CursorManager.isMouseLocked = when(type) {
            Type.LOCK   -> true
            Type.UNLOCK -> false
        }
    }

    override fun clone() = NodeActionMouselock(type)

    override fun toString() = "$type mouse"

    override fun getEditorWindow() = EditorWindow.getWindow(this)

    private object EditorWindow {
        val selectedType = ImInt()

        fun getWindow(action: NodeActionMouselock) = UIWindow("Mouselock action") {
            selectedType.set(action.type.ordinal)

            ImGui.separatorText("Lock / unlock mouse movement")
            ImGui.radioButton(Type.LOCK.string, selectedType, Type.LOCK.ordinal)
            ImGui.sameLine()
            ImGui.radioButton(Type.UNLOCK.string, selectedType, Type.UNLOCK.ordinal)

            action.type = Type.entries[selectedType.get()]
        }
    }
}