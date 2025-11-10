package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.controls.CursorManager
import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImFloat
import imgui.type.ImInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Rotate")
class NodeActionRotate(var type: Type = Type.EXACT, var yaw: Float = 0.0f, var pitch: Float = 0.0f): NodeAction()
{
    enum class Type(val string: String) {
        EXACT("Exact"),
        DELTA("Delta");

        override fun toString() = string
    }

    override suspend fun execute() {
        when(type) {
            Type.EXACT -> CursorManager.rotateHeadAbsolute(yaw, pitch)
            Type.DELTA -> CursorManager.rotateHeadRelative(yaw, pitch)
        }
    }

    override fun clone() = NodeActionRotate(type, yaw, pitch)

    override fun toString() = "$type rotation: $yaw $pitch"

    override fun getEditorWindow() = EditorWindow.getWindow(this)

    private object EditorWindow {
        val selectedType = ImInt()
        val yawImFloat = ImFloat()
        val pitchImFloat = ImFloat()

        fun getWindow(action: NodeActionRotate) = UIWindow("Rotation action") {
            selectedType.set(action.type.ordinal)
            yawImFloat.set(action.yaw)
            pitchImFloat.set(action.pitch)

            ImGui.separatorText("Absolute / relative rotation")
            ImGui.radioButton(Type.EXACT.string, selectedType, Type.EXACT.ordinal)
            ImGui.sameLine()
            ImGui.radioButton(Type.DELTA.string, selectedType, Type.DELTA.ordinal)

            ImGui.separatorText("Amount")
            ImGui.inputFloat("Yaw", yawImFloat)
            ImGui.inputFloat("Pitch", pitchImFloat)

            action.type = Type.entries[selectedType.get()]
            action.yaw = yawImFloat.get()
            action.pitch = pitchImFloat.get()
        }
    }
}