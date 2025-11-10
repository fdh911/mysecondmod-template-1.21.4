package com.github.fdh911.modules.macro.controls

import com.github.fdh911.utils.boundKey
import com.github.fdh911.utils.handleBlockBreaking
import com.github.fdh911.utils.mc
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding

object KeybindManager {
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

    fun clear() {
        for(key in toHold)
            KeyBinding.setKeyPressed(key.boundKey, false)
        for(key in toPress)
            KeyBinding.setKeyPressed(key.boundKey, false)
        for(key in finishedPress)
            KeyBinding.setKeyPressed(key.boundKey, false)
        toHold.clear()
        toPress.clear()
        toRelease.clear()
        finishedPress.clear()
    }

    fun update() {
        for(key in finishedPress)
            KeyBinding.setKeyPressed(key.boundKey, false)
        finishedPress.clear()

        for(key in toHold) {
            when(key) {
                mc.options.attackKey -> {
                    mc.attackCooldown = 0
                    mc.handleBlockBreaking(true)
                }
                else -> {
                    KeyBinding.setKeyPressed(key.boundKey, true)
                }
            }
        }

        for(key in toPress) {
            KeyBinding.onKeyPressed(key.boundKey)
            finishedPress.add(key)
        }

        toPress.clear()

        for(key in toRelease)
            if(toHold.contains(key)) {
                KeyBinding.setKeyPressed(key.boundKey, false)
                toHold.remove(key)
            }

        toRelease.clear()
    }
}