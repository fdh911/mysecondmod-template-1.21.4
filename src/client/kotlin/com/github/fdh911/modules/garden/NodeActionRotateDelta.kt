package com.github.fdh911.modules.garden

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImFloat
import net.minecraft.client.MinecraftClient

class NodeActionRotateDelta(private var yawDelta: ImFloat = ImFloat(0.0f), private var pitchDelta: ImFloat = ImFloat(0.0f)): INodeAction {
    override suspend fun execute() {
        WindMouse.rotateHeadDelta(yawDelta.get(), pitchDelta.get())
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Rotate by delta action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.inputFloat("Yaw (delta)", yawDelta)
            ImGui.inputFloat("Pitch (delta)", pitchDelta)
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionRotateDelta(ImFloat(yawDelta.get()), ImFloat(pitchDelta.get()))

    override val fileFormat: String
        get() = "rotatedelta\n${yawDelta.get()}\n${pitchDelta.get()}"

    override fun toString() = "Rotate delta: ${yawDelta.get()} ${pitchDelta.get()}"
}