package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient

@Serializable
@SerialName("no_pause")
class ModuleNoPause: Module("No Pause")
{
    override fun onUpdate() {
        MinecraftClient.getInstance().options.pauseOnLostFocus = !toggled
    }

    override fun onRenderUpdate(ctx: WorldRenderContext) { }

    override fun UIWindow.setWindowContents() { }
}