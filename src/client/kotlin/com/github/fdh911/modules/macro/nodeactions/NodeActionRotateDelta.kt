package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.WindMouse
import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImFloat
import kotlinx.serialization.Serializable

@Serializable
class NodeActionRotateDelta(private var yawDelta: Float = 0.0f, private var pitchDelta: Float = 0.0f): NodeAction()
{
    override suspend fun execute() {
        WindMouse.rotateHeadDelta(yawDelta, pitchDelta)
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Rotate by delta action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            val yawImFloat = ImFloat(yawDelta)
            val pitchImFloat = ImFloat(pitchDelta)
            ImGui.inputFloat("Yaw (delta)", yawImFloat)
            ImGui.inputFloat("Pitch (delta)", pitchImFloat)
            yawDelta = yawImFloat.get()
            pitchDelta = pitchImFloat.get()
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionRotateDelta(yawDelta, pitchDelta)

    override fun toString() = "Rotate delta: $yawDelta $pitchDelta"
}