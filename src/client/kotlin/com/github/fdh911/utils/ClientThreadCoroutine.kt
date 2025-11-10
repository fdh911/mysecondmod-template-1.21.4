package com.github.fdh911.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import net.minecraft.client.MinecraftClient
import kotlin.coroutines.CoroutineContext

private object ClientThreadDispatcher: CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        MinecraftClient.getInstance().execute(block)
    }
}

val clientScope = CoroutineScope(ClientThreadDispatcher)