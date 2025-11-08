package com.github.fdh911

import com.github.fdh911.io.Keybinds
import com.github.fdh911.modules.ModuleGardenMacro
import com.github.fdh911.modules.ModuleList
import com.github.fdh911.modules.macro.KeySimulator
import com.github.fdh911.modules.macro.MouseLock
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.ui.UI
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
			if(mc.currentScreen === UI.MCScreen)
				mc.setScreen(null)
			else
				mc.setScreen(UI.MCScreen)
		}

        // This should be removed at some point
        // Should make a system that allows
        // any module toggle to be bound to any key
        Keybinds.register("Toggle Garden Macro", GLFW.GLFW_KEY_K) {
            ModuleGardenMacro.toggled = !ModuleGardenMacro.toggled
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

            ModuleList.update()

			Keybinds.update()
			KeySimulator.update()
			MouseLock.update()
		}

		UI.MCScreen.onRender {
			val state = GLState2().apply { saveAll() }
            UI.render {
                ModuleList.window.render()
            }
			state.restoreAll()
		}
	}
}