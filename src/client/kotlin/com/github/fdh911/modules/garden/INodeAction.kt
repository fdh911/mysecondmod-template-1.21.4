package com.github.fdh911.modules.garden

interface INodeAction: Cloneable {
    suspend fun execute()
    fun renderUI(): Boolean
    public override fun clone(): INodeAction
    val fileFormat: String
}