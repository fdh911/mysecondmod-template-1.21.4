package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImInt
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Wait")
class NodeActionWait(private var ms: Long = 500L): NodeAction()
{
    override suspend fun execute() {
        delay(ms)
    }

    override fun clone() = NodeActionWait(ms)

    override fun toString() = "Wait: $ms ms"

    override fun getEditorWindow() = EditorWindow.getWindow(this)

    private object EditorWindow {
        val msImInt = ImInt()

        fun getWindow(action: NodeActionWait) = UIWindow("Wait action") {
            msImInt.set(action.ms.toInt())

            ImGui.separatorText("Amount of time")
            ImGui.textDisabled("Time in milliseconds")
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            ImGui.inputInt("##_ms", msImInt)

            action.ms = msImInt.get().toLong()
        }
    }
}