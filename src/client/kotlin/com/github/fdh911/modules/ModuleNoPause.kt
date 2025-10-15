package com.github.fdh911.modules

import com.github.fdh911.render.UserInterface
import imgui.ImGui
import imgui.type.ImBoolean
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient

object ModuleNoPause: Module("No Pause") {
    override fun update() {
        MinecraftClient.getInstance().options.pauseOnLostFocus = !toggled
    }

    override fun renderUpdate(ctx: WorldRenderContext) { }

    override fun renderUI() = UserInterface.newWindow("No Pause") {
        ImGui.checkbox("Enabled?", imBooleanToggled)
    }
}