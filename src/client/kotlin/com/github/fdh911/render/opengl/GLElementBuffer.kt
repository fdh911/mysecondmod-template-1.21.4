package com.github.fdh911.render.opengl

import org.lwjgl.opengl.GL45.*
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer

class GLElementBuffer
{
    enum class Usage(val code: Int) {
        STATIC(GL_STATIC_DRAW),
        DYNAMIC(GL_DYNAMIC_DRAW)
    }

    companion object {
        fun withStaticIndices(vararg indices: Int): GLElementBuffer = GLElementBuffer().apply {
            setData(indices, Usage.STATIC)
        }
    }

    val id = glGenBuffers()

    fun bind() { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id) }
    fun unbind() { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0) }

    fun setData(indices: IntBuffer, usage: Usage) {
        bind()
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage.code)
    }

    fun setData(indices: IntArray, usage: Usage) = MemoryStack.stackPush().use { stk ->
        val buf = stk.mallocInt(indices.size).put(indices).flip()
        setData(buf, usage)
    }

    fun setData(indices: List<Int>, usage: Usage) = MemoryStack.stackPush().use { stk ->
        val buf = stk.mallocInt(indices.size).put(indices.toIntArray()).flip()
        setData(buf, usage)
    }
}