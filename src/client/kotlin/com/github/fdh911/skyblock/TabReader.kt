package com.github.fdh911.skyblock

import net.minecraft.client.MinecraftClient

object TabReader {
    val tab: List<String>?
        get() {
            val playerListEntries = MinecraftClient.getInstance().networkHandler?.playerList
                ?: return null

            return playerListEntries.map { it.displayName?.string ?: "Null" }
        }
}