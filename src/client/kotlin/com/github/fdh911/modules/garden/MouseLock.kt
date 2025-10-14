package com.github.fdh911.modules.garden

object MouseLock {
    var isLocked = false
        get() = field && GardenMacro.toggled.get()
}