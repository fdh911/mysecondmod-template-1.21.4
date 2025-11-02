package com.github.fdh911.skyblock

import com.github.fdh911.utils.noModifiers
import net.minecraft.client.MinecraftClient

object TabReader {
    var tab: List<String>? = null
        private set

    fun update() {
        tab = null

        val playerListEntries = MinecraftClient.getInstance().networkHandler?.playerList
            ?: return

        tab = playerListEntries.map { it.displayName?.string?.noModifiers() ?: "null" }
    }
}