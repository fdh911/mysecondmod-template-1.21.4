package com.github.fdh911.modules.garden

class NodeActionLockMouse: INodeAction {
    override suspend fun execute() {
        MouseLock.isLocked = true
    }

    override fun renderUI(): Boolean {
        return false
    }

    override val fileFormat: String
        get() = "lockMouse"

    override fun toString() = "Lock yaw & pitch"
}