package com.github.fdh911.modules.garden

object MouseLock {
    var isLocked = false
        get() = field && ModuleGardenMacro.toggled.get()

    fun update() {
        if(!ModuleGardenMacro.toggled.get())
            isLocked = false
    }
}