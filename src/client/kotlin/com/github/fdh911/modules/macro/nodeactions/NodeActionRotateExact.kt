package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.WindMouse
import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImFloat
import kotlinx.serialization.Serializable

@Serializable
class NodeActionRotateExact(private var yaw: Float = 0.0f, private var pitch: Float = 0.0f): NodeAction()
{
    override suspend fun execute() {
        WindMouse.rotateHeadExact(yaw, pitch)
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Rotate exact action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            val yawImFloat = ImFloat(yaw)
            val pitchImFloat = ImFloat(pitch)
            ImGui.inputFloat("Yaw (exact)", yawImFloat)
            ImGui.inputFloat("Pitch (exact)", pitchImFloat)
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionRotateExact(yaw, pitch)

    override fun toString() = "Rotate exact: $yaw $pitch"
}