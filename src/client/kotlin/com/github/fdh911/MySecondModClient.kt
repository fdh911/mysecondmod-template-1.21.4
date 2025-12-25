package com.github.fdh911

import com.github.fdh911.modules.ConfigManager
import com.github.fdh911.modules.ModuleList
import com.github.fdh911.modules.macro.controls.ActionQueue
import com.github.fdh911.modules.macro.controls.KeybindManager
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.state.SkyblockState
import com.github.fdh911.ui.UI
import com.github.fdh911.utils.KeybindRegistry
import com.github.fdh911.utils.mc
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import org.lwjgl.glfw.GLFW

object MySecondModClient : ClientModInitializer
{
	override fun onInitializeClient() {
		KeybindRegistry.register("Open / Close UI", GLFW.GLFW_KEY_J) {
			if(mc.currentScreen === UI.MCScreen)
				mc.setScreen(null)
			else
				mc.setScreen(UI.MCScreen)
		}

        // This should be removed at some point
        // Should make a system that allows
        // any module toggle to be bound to any key
//        KeybindRegistry.register("Toggle Garden Macro", GLFW.GLFW_KEY_K) {
//            ModuleGardenMacro.toggled = !ModuleGardenMacro.toggled
//        }

		WorldRenderEvents.END.register {
			ctx: WorldRenderContext ->
			if(mc.player == null || mc.world == null) return@register

            val state = GLState2().apply { saveAll() }
            ConfigManager.activeConfig?.renderUpdate(ctx)
            state.restoreAll()
		}

		ClientTickEvents.START_CLIENT_TICK.register {
			if(mc.player == null || mc.world == null) return@register

            ConfigManager.activeConfig?.update()

			KeybindRegistry.update()
            SkyblockState.update()
		}

        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            KeybindManager.clear()
            ActionQueue.start()
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            KeybindManager.clear()
            ActionQueue.stop()
        }

		UI.MCScreen.onRender {
			val state = GLState2().apply { saveAll() }
            UI.render {
                ConfigManager.window.render()
            }
			state.restoreAll()
		}
	}
}