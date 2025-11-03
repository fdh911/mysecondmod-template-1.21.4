package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.MouseLock

class NodeActionLockMouse: INodeAction {
    override suspend fun execute() {
        MouseLock.isLocked = true
    }

    override fun renderUI(): Boolean {
        return false
    }

    override fun clone() = NodeActionLockMouse()

    override val fileFormat: String
        get() = "lockMouse"

    override fun toString() = "Lock yaw & pitch"
}