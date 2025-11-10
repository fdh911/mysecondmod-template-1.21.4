package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.controls.CursorManager
import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient

@Serializable
@SerialName("MoveCursor")
class NodeActionMoveCursor(
    var xPos: Int = 0,
    var yPos: Int = 0
): NodeAction()
{
    override suspend fun execute() {
        if(MinecraftClient.getInstance().currentScreen != null)
            CursorManager.moveMouseCursor(xPos, yPos)
    }

    override fun clone() = NodeActionMoveCursor(xPos, yPos)

    override fun toString() = "Move cursor to $xPos $yPos"

    override fun getEditorWindow() = EditorWindow.getWindow(this)

    private object EditorWindow {
        val xPosImInt = ImInt()
        val yPosImInt = ImInt()

        fun getWindow(action: NodeActionMoveCursor) = UIWindow("Move cursor action") {
            xPosImInt.set(action.xPos)
            yPosImInt.set(action.yPos)

            ImGui.separatorText("Position")
            ImGui.inputInt("X", xPosImInt)
            ImGui.inputInt("Y", yPosImInt)

            ImGui.separatorText("Warning")
            ImGui.textDisabled("This only works inside a UI!")
            ImGui.textDisabled("For looking around, use Rotation")

            action.xPos = xPosImInt.get()
            action.yPos = yPosImInt.get()
        }
    }
}