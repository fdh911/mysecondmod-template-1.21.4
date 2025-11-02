package com.github.fdh911

import com.github.fdh911.io.Keybinds
import com.github.fdh911.modules.Module
import com.github.fdh911.modules.ModuleEntityScanner
import com.github.fdh911.modules.ModuleList
import com.github.fdh911.render.UserInterface
import com.github.fdh911.modules.garden.ModuleGardenMacro
import com.github.fdh911.modules.garden.KeySimulator
import com.github.fdh911.modules.garden.MouseLock
import com.github.fdh911.modules.ModuleNoPause
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.skyblock.SkyblockState
import com.github.fdh911.skyblock.TabReader
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

        Keybinds.register("Toggle Garden Macro", GLFW.GLFW_KEY_K) {
            ModuleGardenMacro.toggled = !ModuleGardenMacro.toggled
        }

        Keybinds.register("Test tab", GLFW.GLFW_KEY_H) {
            println("Is in skyblock: ${SkyblockState.isInSkyblock}")
            println("Skyblock area: ${SkyblockState.currentArea}")
        }

		WorldRenderEvents.END.register {
			ctx: WorldRenderContext ->
			if(mc.player == null || mc.world == null) return@register
            val state = GLState2().apply { saveAll() }
            ModuleList.renderUpdate(ctx)
            state.restoreAll()
		}

		ClientTickEvents.END_CLIENT_TICK.register {
			if(mc.player == null || mc.world == null) return@register
            SkyblockState.update()
            ModuleList.update()
            // TODO rm
			Keybinds.update()
			KeySimulator.update()
			MouseLock.update()
		}

		UserInterface.MCScreen.onRender {
			val state = GLState2().apply { saveAll() }
            UserInterface.render(ModuleList::renderUI)
			state.restoreAll()
		}
	}
}