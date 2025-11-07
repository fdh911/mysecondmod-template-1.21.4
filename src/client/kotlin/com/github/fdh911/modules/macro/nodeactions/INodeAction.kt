package com.github.fdh911.modules.macro.nodeactions

interface INodeAction: Cloneable {
    suspend fun execute()
    fun renderUI(): Boolean
    public override fun clone(): INodeAction
    override fun toString(): String
    val fileFormat: String
}