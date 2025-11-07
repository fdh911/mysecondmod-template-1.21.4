package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImInt
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
class NodeActionWait(private var ms: Long = 500L): NodeAction()
{
    override suspend fun execute() {
        delay(ms)
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Wait action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.text("Milliseconds amount")
            val msImInt = ImInt(ms.toInt())
            ImGui.inputInt("ms", msImInt)
            ms = msImInt.get().toLong()
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionWait(ms)

    override fun toString() = "Wait: $ms ms"
}