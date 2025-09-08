package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*

class GLVertexArray(vararg attribs: Pair<Int, Attrib>)
{
    enum class Attrib(val code: Int, val size: Int) {
        FLOAT(GL_FLOAT, 4)
    }

    val id = glGenVertexArrays()

    fun bind() { glBindVertexArray(id) }
    fun unbind() { glBindVertexArray(0) }

    init {
        bind()

        val totalSize = attribs.sumOf { it.first * it.second.size }
        var attribIdx = 0
        var offset = 0L

        for((count, type) in attribs) {
            glEnableVertexAttribArray(attribIdx)
            glVertexAttribPointer(attribIdx, count, type.code, false, totalSize, offset)
            offset += type.size * count
            attribIdx++
        }

        unbind()
    }
}