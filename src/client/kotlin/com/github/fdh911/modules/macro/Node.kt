package com.github.fdh911.modules.macro

import com.github.fdh911.modules.macro.nodeactions.NodeAction
import com.github.fdh911.utils.BlockPosSerializer
import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
data class Node(
    @Serializable(with = BlockPosSerializer::class)
    var pos: BlockPos,
    var name: String,
    val actions: MutableList<NodeAction> = mutableListOf()
): Cloneable
{
    public override fun clone() = copy(actions = mutableListOf<NodeAction>().apply {
        for(action in actions)
            add(action.clone())
    })
}