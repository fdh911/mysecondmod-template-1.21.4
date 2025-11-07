package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.MouseLock
import kotlinx.serialization.Serializable

@Serializable
class NodeActionLockMouse: NodeAction()
{
    override suspend fun execute() {
        MouseLock.isLocked = true
    }

    override fun renderUI(): Boolean {
        return false
    }

    override fun clone() = NodeActionLockMouse()

    override fun toString() = "Lock yaw & pitch"
}