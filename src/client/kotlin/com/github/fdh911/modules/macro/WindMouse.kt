package com.github.fdh911.modules.macro

import kotlinx.coroutines.delay
import net.minecraft.client.MinecraftClient
import org.joml.Vector2f
import kotlin.math.min
import kotlin.random.Random

/*
    I literally just copied WindMouse lmao
    But it still feels shit I dont know
    https://ben.land/post/2021/04/25/windmouse-human-mouse-movement/
 */
object WindMouse {
    private const val G_0 = 2.0f
    private const val W_0 = 3.0f
    private const val D_0 = 12.0f
    private const val SQRT_3 = 1.732050f
    private const val SQRT_5 = 2.236067f
    private const val EPS = 1.0f

    suspend fun rotateHeadDelta(deltaYaw: Float, deltaPitch: Float) {
        val player = MinecraftClient.getInstance().player!!
        rotateHeadExact(player.yaw + deltaYaw, player.pitch + deltaPitch)
    }

    suspend fun rotateHeadExact(destYaw: Float, destPitch: Float) {
        val player = MinecraftClient.getInstance().player!!

        val startVA = Vector2f(player.yaw, player.pitch)
        val destVA = Vector2f(destYaw, destPitch)
        val vel = Vector2f(0.0f, 0.0f)
        val wind = Vector2f(0.0f, 0.0f)

        var m0 = 15.0f
        var dist = destVA.distance(startVA)

        while(dist >= EPS) {
            val wMag = min(W_0, dist)
            if(dist >= D_0) {
                wind.x = wind.x / SQRT_3 + (2.0f * Random.nextFloat() - 1.0f) * wMag / SQRT_5
                wind.y = wind.y / SQRT_3 + (2.0f * Random.nextFloat() - 1.0f) * wMag / SQRT_5
            } else {
                wind.x /= SQRT_3
                wind.y /= SQRT_3
                if(m0 <= 3.0f)
                    m0 = 3.0f * Random.nextFloat() + 3.0f
                else
                    m0 /= SQRT_5
            }
            vel.x += wind.x + G_0 * (destVA.x - startVA.x) / dist
            vel.y += wind.y + G_0 * (destVA.y - startVA.y) / dist
            if(vel.length() > m0) {
                val velClip = m0 * 0.5f + Random.nextFloat() * m0 * 0.5f
                vel.x = (vel.x / vel.length()) * velClip
                vel.y = (vel.y / vel.length()) * velClip
            }
            startVA.x += vel.x
            startVA.y += vel.y

            delay(16L)
            player.yaw = startVA.x
            player.pitch = startVA.y
            println("${player.yaw} ${player.pitch}")

            dist = destVA.distance(startVA)
        }
    }
}