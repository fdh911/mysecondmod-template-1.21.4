package com.github.fdh911.modules.garden

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImFloat

class NodeActionRotateExact(private var yaw: ImFloat = ImFloat(0.0f), private var pitch: ImFloat = ImFloat(0.0f)): INodeAction {
    override suspend fun execute() {
        WindMouse.rotateHeadExact(yaw.get(), pitch.get())
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Rotate exact action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.inputFloat("Yaw (exact)", yaw)
            ImGui.inputFloat("Pitch (exact)", pitch)
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionRotateExact(ImFloat(yaw.get()), ImFloat(pitch.get()))

    override val fileFormat: String
        get() = "rotateexact\n${yaw.get()}\n${pitch.get()}"

    override fun toString() = "Rotate exact: ${yaw.get()} ${pitch.get()}"
}