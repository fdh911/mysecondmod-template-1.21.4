package com.github.fdh911.modules.garden

class NodeActionUnlockMouse: INodeAction {
    override suspend fun execute() {
        MouseLock.isLocked = false
    }

    override fun renderUI(): Boolean {
        return false
    }

    override val fileFormat: String
        get() = "unlockMouse"

    override fun toString() = "Unlock yaw & pitch"
}