package com.github.fdh911.utils

import org.joml.Vector2f
import kotlin.random.Random

class RandomizedArc(
    private val start: Vector2f,
    private val end: Vector2f,
    coefficient: Float
) {
    val control: Vector2f

    init {
        val lerpValue = Random.Default.nextFloat().coerceIn(0.03f, 0.97f)
        val between = start + lerpValue * (end - start)
        val perp = Vector2f(end - start).normalize().perpendicular()
        val orientation = if(Random.Default.nextFloat() <= 0.7f) 1.0f else -1.0f
        control = between + orientation * coefficient * perp
    }

    fun getPoint(t: Float): Vector2f {
        require(t in 0.0f..1.0f)
        val invT = 1.0f - t
        val coeff1 = invT * invT
        val coeff2 = 2.0f * invT * t
        val coeff3 = t * t
        return coeff1 * start + coeff2 * control + coeff3 * end
    }
}