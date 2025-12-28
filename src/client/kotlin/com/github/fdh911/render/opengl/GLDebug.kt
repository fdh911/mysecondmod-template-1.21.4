package com.github.fdh911.render.opengl

import org.lwjgl.opengl.ARBDebugOutput.*

object GLDebug {
    fun marker(msg: String) = glDebugMessageInsertARB(GL_DEBUG_SOURCE_APPLICATION_ARB, GL_DEBUG_TYPE_OTHER_ARB, 0, GL_DEBUG_SEVERITY_HIGH_ARB, msg)
}