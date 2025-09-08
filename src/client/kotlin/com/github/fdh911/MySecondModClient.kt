package com.github.fdh911

import imgui.ImGui
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

object MySecondModClient : ClientModInitializer {
	override fun onInitializeClient() {
		WorldRenderEvents.AFTER_ENTITIES.register {
			context: WorldRenderContext ->
			EntitySearcher.update(context)
			UserInterface.render {
				ImGui.begin("yo")
				ImGui.text("hello bro")
				ImGui.end()
			}
		}
	}
}