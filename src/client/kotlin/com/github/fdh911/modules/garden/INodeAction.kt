package com.github.fdh911.modules.garden

interface INodeAction {
    suspend fun execute()
    fun renderUI(): Boolean
    val fileFormat: String
}