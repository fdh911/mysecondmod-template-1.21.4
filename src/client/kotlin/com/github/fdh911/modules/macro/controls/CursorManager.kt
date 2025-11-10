package com.github.fdh911.modules.macro.controls

import com.github.fdh911.utils.RandomizedArc
import com.github.fdh911.utils.plus
import com.github.fdh911.utils.times
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import net.minecraft.client.MinecraftClient
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.random.Random

object CursorManager
{
    var isMouseLocked = false

    suspend fun moveMouseCursor(xPos: Int, yPos: Int) {
        val client = MinecraftClient.getInstance()
        val windowPtr = client.window.handle

        val job = CompletableDeferred<Vector2f>()
        client.execute {
            val xPtr = doubleArrayOf(0.0)
            val yPtr = doubleArrayOf(0.0)
            glfwGetCursorPos(windowPtr, xPtr, yPtr)
            job.complete(Vector2f(xPtr[0].toFloat(), yPtr[0].toFloat()))
        }

        val start = job.await()
        val end = Vector2f(xPos.toFloat(), yPos.toFloat())

        generate(
            start = start,
            end = end,
            precision = 1.0f,
            maxError = 15.0f,
        ) {
            point: Vector2f -> client.execute { glfwSetCursorPos(windowPtr, point.x.toDouble(), point.y.toDouble()) }
        }
    }

    suspend fun rotateHeadRelative(yaw: Float, pitch: Float) {
        val player = MinecraftClient.getInstance().player!!
        rotateHeadAbsolute(player.yaw + yaw, player.pitch + pitch)
    }

    suspend fun rotateHeadAbsolute(yaw: Float, pitch: Float) {
        val player = MinecraftClient.getInstance().player!!
        generate(
            start = Vector2f(player.yaw, player.pitch),
            end = Vector2f(yaw, pitch),
            precision = 1.0f,
            maxError = 30.0f
        ) {
            point: Vector2f -> player.rotate(point.x, point.y)
        }
    }

    suspend fun generate(
        start: Vector2f,
        end: Vector2f,
        precision: Float,
        maxError: Float,
        effect: (Vector2f) -> Unit
    ) {
        val steps = 25

        var current = Vector2f(start)

        var dist = current.distance(end)

        while(dist > precision) {
            val errorMargin = min(maxError, dist * 0.1f)
            val errorVec = errorMargin * Vector2f(
                2.0f * Random.nextFloat() - 1.0f,
                2.0f * Random.nextFloat() - 1.0f
            )

            val approximateEnd = end + errorVec
            val arc = RandomizedArc(
                current,
                approximateEnd,
                if (dist < 30.0f) 0.0f else min(maxError, dist * 0.1f)
            )

            for(i in 0..steps) {
                val t = i.toFloat() / steps
                val easeT = 0.5f - 0.5f * cos(PI.toFloat() * t)
                val point = arc.getPoint(easeT)

                effect(point)

                delay(Random.nextLong(8L, 16L))
            }

            delay(min(250L, (dist * 1.0f).toLong()))

            current = Vector2f(approximateEnd)
            dist = current.distance(end)
        }
    }
}