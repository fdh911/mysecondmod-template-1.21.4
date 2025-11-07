package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.modules.macro.MouseLock
import kotlinx.serialization.Serializable

@Serializable
class NodeActionUnlockMouse: NodeAction()
{
    override suspend fun execute() {
        MouseLock.isLocked = false
    }

    override fun renderUI(): Boolean {
        return false
    }

    override fun clone() = NodeActionUnlockMouse()

    override fun toString() = "Unlock yaw & pitch"
}