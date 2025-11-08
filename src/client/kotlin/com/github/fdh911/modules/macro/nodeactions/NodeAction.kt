package com.github.fdh911.modules.macro.nodeactions

import com.github.fdh911.ui.UIWindow
import kotlinx.serialization.Serializable

@Serializable
sealed class NodeAction: Cloneable
{
    abstract suspend fun execute()
    public abstract override fun clone(): NodeAction
    abstract override fun toString(): String
    abstract fun getEditorWindow(): UIWindow
}