package com.github.fdh911

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

object MySecondModClient : ClientModInitializer {
	override fun onInitializeClient() {
		WorldRenderEvents.END.register {
			context: WorldRenderContext ->
			EntitySearcher.update(context)
			EntitySearcher.renderUI()
		}
	}
}