package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImInt
import kotlinx.coroutines.delay

class NodeActionWait(private var ms: ImInt = ImInt(500)): INodeAction {
    override suspend fun execute() {
        delay(ms.get().toLong())
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Wait action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.text("Milliseconds amount")
            ImGui.inputInt("ms", ms)
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionWait(ImInt(ms.get()))

    override val fileFormat: String
        get() = "wait\n${ms.get()}"

    override fun toString() = "Wait"
}