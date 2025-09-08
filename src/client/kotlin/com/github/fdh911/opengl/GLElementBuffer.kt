package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer

class GLElementBuffer
{
    enum class Usage(val code: Int) {
        STATIC(GL_STATIC_DRAW),
        DYNAMIC(GL_DYNAMIC_DRAW)
    }

    val id = glGenBuffers()

    fun bind() { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id) }
    fun unbind() { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0) }

    fun setData(indices: IntBuffer, usage: Usage) {
        bind()
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage.code)
    }

    fun setData(indices: IntArray, usage: Usage) {
        val stk = MemoryStack.stackPush()
        try {
            val buf = stk.mallocInt(indices.size).put(indices).flip()
            setData(buf, usage)
        } finally {
            stk.pop()
        }
    }

    fun setData(indices: List<Int>, usage: Usage) {
        val stk = MemoryStack.stackPush()
        try {
            val buf = stk.mallocInt(indices.size).put(indices.toIntArray()).flip()
            setData(buf, usage)
        } finally {
            stk.pop()
        }
    }
}