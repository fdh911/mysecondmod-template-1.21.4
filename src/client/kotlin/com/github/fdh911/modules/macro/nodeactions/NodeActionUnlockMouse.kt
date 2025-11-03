package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.MouseLock

class NodeActionUnlockMouse: INodeAction {
    override suspend fun execute() {
        MouseLock.isLocked = false
    }

    override fun renderUI(): Boolean {
        return false
    }

    override fun clone() = NodeActionUnlockMouse()

    override val fileFormat: String
        get() = "unlockMouse"

    override fun toString() = "Unlock yaw & pitch"
}