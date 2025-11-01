package com.github.fdh911.modules.garden

import net.minecraft.util.math.BlockPos

data class Node(var pos: BlockPos, var name: String, val actions: MutableList<INodeAction> = mutableListOf()): Cloneable {
    public override fun clone() = copy(actions = mutableListOf<INodeAction>().apply {
        for(action in actions)
            add(action.clone())
    })
}