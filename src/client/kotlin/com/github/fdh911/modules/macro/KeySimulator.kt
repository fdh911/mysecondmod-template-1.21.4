package com.github.fdh911.modules.macro

import com.github.fdh911.modules.ModuleGardenMacro
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding

object KeySimulator {
    private val toHold = mutableSetOf<KeyBinding>()
    private val toPress = mutableSetOf<KeyBinding>()
    private val toRelease = mutableSetOf<KeyBinding>()

    private val finishedPress = mutableSetOf<KeyBinding>()

    private val translationKeyToKeybinding = MinecraftClient.getInstance().options.allKeys.associateBy { it.translationKey }

    fun hold(key: KeyBinding) = toHold.add(key)
    fun press(key: KeyBinding) = toPress.add(key)
    fun release(key: KeyBinding) = toRelease.add(key)

    fun hold(translationKey: String) = toHold.add(translationKeyToKeybinding[translationKey]!!)
    fun press(translationKey: String) = toPress.add(translationKeyToKeybinding[translationKey]!!)
    fun release(translationKey: String) = toRelease.add(translationKeyToKeybinding[translationKey]!!)

    fun update() {
        if(!ModuleGardenMacro.toggled) {
            for(key in toHold)
                key.isPressed = false
            for(key in toPress)
                key.isPressed = false
            for(key in finishedPress)
                key.isPressed = false
            toHold.clear()
            toPress.clear()
            toRelease.clear()
            finishedPress.clear()
        }

        for(key in finishedPress)
            key.isPressed = false
        finishedPress.clear()

        for(key in toHold)
            key.isPressed = true

        for(key in toPress) {
            key.isPressed = true
            finishedPress.add(key)
        }
        toPress.clear()

        for(key in toRelease)
            if(toHold.contains(key)) {
                key.isPressed = false
                toHold.remove(key)
            }
        toRelease.clear()
    }
}