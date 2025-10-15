package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLElementBuffer
import com.github.fdh911.render.opengl.GLProgram
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.render.opengl.GLTexture2D
import com.github.fdh911.render.opengl.GLVertexArray
import com.github.fdh911.render.opengl.GLVertexBuffer
import org.lwjgl.opengl.GL45.*

object OverlayRender {
    private val defaultProgram: GLProgram
    private val vao: GLVertexArray
    private val vbo: GLVertexBuffer
    private val ebo: GLElementBuffer

    init {
        val state = GLState2().apply { saveAll() }

        defaultProgram = GLProgram.fromClasspath("overlay").apply {
            bind()
            setInt("uTex", 0)
        }

        vbo = GLVertexBuffer.withStaticVertices(
            -1.0f, -1.0f,   0.0f, 0.0f,
            -1.0f, +1.0f,   0.0f, 1.0f,
            +1.0f, +1.0f,   1.0f, 1.0f,
            +1.0f, -1.0f,   1.0f, 0.0f,
        )

        ebo = GLElementBuffer.withStaticIndices(
            0, 1, 2,
            2, 3, 0,
        )

        vbo.bind()
        ebo.bind()
        vao = GLVertexArray(
            2 to GLVertexArray.Attrib.FLOAT,
            2 to GLVertexArray.Attrib.FLOAT,
        )

        defaultProgram.unbind()
        vao.unbind()
        vbo.unbind()
        ebo.unbind()

        state.restoreAll()
    }

    fun render(tex: GLTexture2D) {
        renderWithProgram(tex, defaultProgram)
    }

    fun renderWithProgram(tex: GLTexture2D, program: GLProgram) {
        program.bind()
        vao.bind()
        vbo.bind()
        ebo.bind()
        glDisable(GL_CULL_FACE)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        tex.bindToTexSlot(0)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L)
    }
}