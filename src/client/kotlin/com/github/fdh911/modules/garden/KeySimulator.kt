package com.github.fdh911.modules.garden

import net.minecraft.client.option.KeyBinding

object KeySimulator {
    val toHold = mutableSetOf<KeyBinding>()
    val toPress = mutableSetOf<KeyBinding>()
    val toRelease = mutableSetOf<KeyBinding>()

    private val finishedPress = mutableSetOf<KeyBinding>()

    fun update() {
        if(!ModuleGardenMacro.toggled.get()) {
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