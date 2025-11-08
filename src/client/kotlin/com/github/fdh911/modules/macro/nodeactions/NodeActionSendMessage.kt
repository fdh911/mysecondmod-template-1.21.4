package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

@Serializable
@SerialName("SendMessage")
class NodeActionSendMessage(private var message: String = "Message"): NodeAction()
{
    override suspend fun execute() {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.literal(message))
    }

    override fun clone() = NodeActionSendMessage(message)

    override fun toString(): String {
        val msgToDisplay = if(message.length > 15)
            "${message.take(15)}..."
        else message
        return "Send: $msgToDisplay"
    }

    override fun getEditorWindow() = EditorWindow.getWindow(this)

    private object EditorWindow {
        val messageImString = ImString()

        fun getWindow(action: NodeActionSendMessage) = UIWindow("Message action") {
            messageImString.set(action.message)

            ImGui.text("Message to send:")
            ImGui.inputText("##_msgInput", messageImString)

            action.message = messageImString.get()
        }
    }
}