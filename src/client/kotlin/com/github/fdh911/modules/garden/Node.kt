package com.github.fdh911.modules.garden

import net.minecraft.util.math.BlockPos

class Node(var pos: BlockPos, var name: String) {
    val actions = mutableListOf<INodeAction>()
}