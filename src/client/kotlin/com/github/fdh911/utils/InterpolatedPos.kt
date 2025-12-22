package com.github.fdh911.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import org.joml.Vector3f

fun Entity.interpolatedPos(): Vector3f {
    val partialTick = MinecraftClient.getInstance().renderTickCounter!!.getTickDelta(true)
    return Vector3f(
        (prevX + (x - prevX) * partialTick).toFloat(),
        (prevY + (y - prevY) * partialTick).toFloat(),
        (prevZ + (z - prevZ) * partialTick).toFloat(),
    )
}
