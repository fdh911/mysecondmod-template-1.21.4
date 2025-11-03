package com.github.fdh911.modules.macro.nodeactions

interface INodeAction: Cloneable {
    suspend fun execute()
    fun renderUI(): Boolean
    public override fun clone(): INodeAction
    val fileFormat: String
}