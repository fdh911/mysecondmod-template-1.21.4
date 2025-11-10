package com.github.fdh911.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object Chat {
    fun message(contents: String) {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(
            Text.literal(
                "\u00A76[msm]\u00A7r $contents"
            )
        )
    }
}