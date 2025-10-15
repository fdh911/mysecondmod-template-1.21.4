package com.github.fdh911

import com.github.fdh911.io.Keybinds
import com.github.fdh911.modules.Module
import com.github.fdh911.modules.ModuleEntityScanner
import com.github.fdh911.render.UserInterface
import com.github.fdh911.modules.garden.ModuleGardenMacro
import com.github.fdh911.modules.garden.KeySimulator
import com.github.fdh911.modules.garden.MouseLock
import com.github.fdh911.modules.ModuleNoPause
import com.github.fdh911.render.opengl.GLState2
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

object MySecondModClient : ClientModInitializer {
	override fun onInitializeClient() {
		val mc = MinecraftClient.getInstance()
		val moduleList = listOf<Module>(
			ModuleGardenMacro,
			ModuleEntityScanner,
			ModuleNoPause,
		)

		Keybinds.register("Open / Close UI", GLFW.GLFW_KEY_J) {
			if(mc.currentScreen === UserInterface.MCScreen)
				mc.setScreen(null)
			else
				mc.setScreen(UserInterface.MCScreen)
		}

		WorldRenderEvents.END.register {
			ctx: WorldRenderContext ->
			if(mc.player == null || mc.world == null) return@register

			val state = GLState2().apply { saveAll() }

			for(module in moduleList)
				if(module.toggled)
					module.renderUpdate(ctx)

			state.restoreAll()
		}

		ClientTickEvents.END_CLIENT_TICK.register {
			if(mc.player == null || mc.world == null) return@register

			for(module in moduleList)
				if(module.toggled)
					module.update()

			Keybinds.update()
			KeySimulator.update()
			MouseLock.update()
		}

		UserInterface.MCScreen.onRender {
			val state = GLState2().apply { saveAll() }
			UserInterface.render {
				ModuleGardenMacro.renderUI()
				ModuleNoPause.renderUI()
			}
			state.restoreAll()
		}
	}
}