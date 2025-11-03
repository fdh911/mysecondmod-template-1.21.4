package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImString
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class NodeActionSendMessage(private val msgImString: ImString = ImString().apply { set("Message") }): INodeAction {
    override suspend fun execute() {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.literal(msgImString.get()))
    }

    override fun renderUI(): Boolean {
        var keepRendering = true
        UserInterface.newWindow("Send message action") {
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.text("Message to send:")
            ImGui.inputText("##_msgInput", msgImString)
            if(ImGui.button("Finish"))
                keepRendering = false
        }
        return keepRendering
    }

    override fun clone() = NodeActionSendMessage(ImString().apply{ set(msgImString.get()) })

    override val fileFormat: String
        get() = "send\n$msgImString"

    override fun toString(): String {
        val msg = msgImString.get()
        val msgToDisplay = if(msg.length > 15)
            "${msg.take(15)}..."
        else msg
        return "Send: $msgToDisplay"
    }
}