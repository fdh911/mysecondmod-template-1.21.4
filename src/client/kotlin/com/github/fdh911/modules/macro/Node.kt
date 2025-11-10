package com.github.fdh911.modules.macro

import com.github.fdh911.modules.macro.nodeactions.NodeAction
import com.github.fdh911.utils.Vector3iSerializer
import kotlinx.serialization.Serializable
import org.joml.Vector3i

@Serializable
data class Node(
    @Serializable(with = Vector3iSerializer::class)
    var pos: Vector3i,
    var name: String,
    val actions: MutableList<NodeAction> = mutableListOf()
): Cloneable
{
    public override fun clone() = copy(actions = mutableListOf<NodeAction>().apply {
        for(action in actions)
            add(action.clone())
    })
}