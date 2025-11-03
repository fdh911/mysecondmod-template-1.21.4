package com.github.fdh911.modules.macro

import com.github.fdh911.modules.ModuleGardenMacro

object MouseLock {
    var isLocked = false
        get() = field && ModuleGardenMacro.toggled

    fun update() {
        if(!ModuleGardenMacro.toggled)
            isLocked = false
    }
}