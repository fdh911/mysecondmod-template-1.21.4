package com.github.fdh911.modules.garden

object MouseLock {
    var isLocked = false
        get() = field && ModuleGardenMacro.toggled

    fun update() {
        if(!ModuleGardenMacro.toggled)
            isLocked = false
    }
}