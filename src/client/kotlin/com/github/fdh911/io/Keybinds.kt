package com.github.fdh911.io

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding

object Keybinds {
    private val registered = mutableMapOf<KeyBinding, () -> Unit>()

    fun register(name: String, key: Int, action: () -> Unit) {
        val kb = KeyBinding(name, key, "MySecondMod")
        KeyBindingHelper.registerKeyBinding(kb)
        registered[kb] = action
    }

    fun update() {
        for((key, action) in registered)
            if(key.wasPressed())
                action()
    }
}