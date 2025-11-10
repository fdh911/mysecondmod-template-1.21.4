package com.github.fdh911.utils

import org.joml.Vector2f

operator fun Vector2f.plus(other: Vector2f) = Vector2f(x + other.x, y + other.y)
operator fun Vector2f.minus(other: Vector2f) = Vector2f(x - other.x, y - other.y)
operator fun Float.times(other: Vector2f) = Vector2f(other.x * this, other.y * this)
operator fun Vector2f.times(scalar: Float) = Vector2f(x * scalar, y * scalar)
