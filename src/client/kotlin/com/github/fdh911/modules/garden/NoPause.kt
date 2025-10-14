package com.github.fdh911.modules.garden

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImBoolean
import net.minecraft.client.MinecraftClient

object NoPause {
    var isEnabled = ImBoolean(false)

    fun update() {
        MinecraftClient.getInstance().options.pauseOnLostFocus = !isEnabled.get()
    }

    fun renderUI() = UserInterface.newWindow("No Pause") {
        ImGui.checkbox("Enabled?", isEnabled)
    }
}