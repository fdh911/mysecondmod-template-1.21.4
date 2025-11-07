package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImString
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

@Serializable
class NodeActionSendMessage(private var message: String = "Message"): NodeAction()
{
    override suspend fun execute() {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.literal(message))
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Send message action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.text("Message to send:")
            val msgImString = ImString().apply { set(message) }
            ImGui.inputText("##_msgInput", msgImString)
            message = msgImString.get()
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionSendMessage(message)

    override fun toString(): String {
        val msgToDisplay = if(message.length > 15)
            "${message.take(15)}..."
        else message
        return "Send: $msgToDisplay"
    }
}