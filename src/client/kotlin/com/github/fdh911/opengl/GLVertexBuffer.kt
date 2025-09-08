package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*
import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer

class GLVertexBuffer
{
    enum class Usage(val code: Int) {
        STATIC(GL_STATIC_DRAW),
        DYNAMIC(GL_DYNAMIC_DRAW)
    }

    val id = glGenBuffers()

    fun bind() { glBindBuffer(GL_ARRAY_BUFFER, id) }
    fun unbind() { glBindBuffer(GL_ARRAY_BUFFER, 0) }

    fun setData(vertices: FloatBuffer, usage: Usage) {
        bind()
        glBufferData(GL_ARRAY_BUFFER, vertices, usage.code)
    }

    fun setData(vertices: FloatArray, usage: Usage) {
        val stk = MemoryStack.stackPush()
        try {
            val buf = stk.mallocFloat(vertices.size).put(vertices).flip()
            setData(buf, usage)
        } finally {
            stk.pop()
        }
    }

    fun setData(vertices: List<Float>, usage: Usage) {
        val stk = MemoryStack.stackPush()
        try {
            val buf = stk.mallocFloat(vertices.size).put(vertices.toFloatArray()).flip()
            setData(buf, usage)
        } finally {
            stk.pop()
        }
    }
}
