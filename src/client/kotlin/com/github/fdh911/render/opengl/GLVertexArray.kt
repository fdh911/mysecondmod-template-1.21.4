package com.github.fdh911.render.opengl

import org.lwjgl.opengl.GL45.*
import org.lwjgl.opengl.ARBInstancedArrays.*

class GLVertexArray(
    vararg attribs: IAttrib,
    elementBuffer: GLElementBuffer? = null,
)
{
    sealed interface IAttrib {
        val vbo: GLVertexBuffer
        val amount: Int
        val type: AttribType
    }

    class AttribRegular(
        override val vbo: GLVertexBuffer,
        override val amount: Int,
        override val type: AttribType = AttribType.FLOAT,
    ): IAttrib

    class AttribInstanced(
        override val vbo: GLVertexBuffer,
        override val amount: Int,
        val divisor: Int,
        override val type: AttribType = AttribType.FLOAT,
    ): IAttrib

    enum class AttribType(val code: Int, val size: Int) {
        FLOAT(GL_FLOAT, 4)
    }

    val id = glGenVertexArrays()

    fun bind() { glBindVertexArray(id) }
    fun unbind() { glBindVertexArray(0) }

    // todo switch to dsa at some point
    init {
        bind()
        elementBuffer?.bind()

        var attribIdx = 0

        attribs.groupBy { it.vbo }.forEach { (vbo, attribList) ->
            vbo.bind()

            val stride = attribList.sumOf { it.amount * it.type.size }
            var offset = 0L

            attribList.forEach {
                glEnableVertexAttribArray(attribIdx)
                glVertexAttribPointer(attribIdx, it.amount, it.type.code, false, stride, offset)

                if(it is AttribInstanced)
                    glVertexAttribDivisorARB(attribIdx, it.divisor)

                offset += it.amount * it.type.size
                attribIdx++
            }
        }

        unbind()
    }
}