package com.github.fdh911.modules.macro.nodeactions

import kotlinx.serialization.Serializable

@Serializable
sealed class NodeAction: Cloneable
{
    abstract suspend fun execute()
    abstract fun renderUI(): Boolean
    public abstract override fun clone(): NodeAction
    abstract override fun toString(): String
}