package com.github.fdh911

import com.github.fdh911.io.Keybinds
import com.github.fdh911.render.UserInterface
import com.github.fdh911.modules.EntityScanner
import com.github.fdh911.modules.garden.GardenMacro
import com.github.fdh911.modules.garden.KeySimulator
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

object MySecondModClient : ClientModInitializer {
	override fun onInitializeClient() {
		val mc = MinecraftClient.getInstance()
		Keybinds.register("Open / Close UI", GLFW.GLFW_KEY_J) {
			if(mc.currentScreen === UserInterface.MCScreen)
				mc.setScreen(null)
			else
				mc.setScreen(UserInterface.MCScreen)
		}
		WorldRenderEvents.END.register {
			context: WorldRenderContext ->
			if(mc.player == null || mc.world == null)
				return@register
			GardenMacro.renderScene(context)
		}
		ClientTickEvents.END_CLIENT_TICK.register {
			if(mc.player == null || mc.world == null)
				return@register
			Keybinds.update()
			GardenMacro.update()
			KeySimulator.update()
		}
		UserInterface.MCScreen.onRender {
			UserInterface.render(GardenMacro::renderUI)
		}
	}
}