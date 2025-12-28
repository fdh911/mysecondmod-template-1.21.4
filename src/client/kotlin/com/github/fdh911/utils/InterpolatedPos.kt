package com.github.fdh911.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

fun Entity.interpolatedPos(): Vec3d {
    val partialTick = MinecraftClient.getInstance().renderTickCounter!!.getTickProgress(true)
    return Vec3d(
        lastX + (x - lastX) * partialTick,
        lastY + (y - lastY) * partialTick,
        lastZ + (z - lastZ) * partialTick,
    )
}
