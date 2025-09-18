package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*

object GLDebug {
    fun marker(msg: String) = glDebugMessageInsert(GL_DEBUG_SOURCE_APPLICATION, GL_DEBUG_TYPE_MARKER, 0, GL_DEBUG_SEVERITY_NOTIFICATION, msg)
}