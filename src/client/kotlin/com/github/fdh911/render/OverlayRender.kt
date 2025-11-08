package com.github.fdh911.render

import com.github.fdh911.render.opengl.*
import org.lwjgl.opengl.GL45.*

object OverlayRender {
    val texProgram: GLProgram
    val sheetProgram: GLProgram

    private val vao: GLVertexArray
    private val vbo: GLVertexBuffer
    private val ebo: GLElementBuffer

    init {
        val state = GLState2().apply { saveAll() }

        texProgram = GLProgram.fromClasspath("overlay").apply {
            bind()
            setInt("uTex", 0)
        }

        sheetProgram = GLProgram.fromClasspath("overlay", "sheet")

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

        texProgram.unbind()
        vao.unbind()
        vbo.unbind()
        ebo.unbind()

        state.restoreAll()
    }

    fun renderWithProgram(program: GLProgram) {
        program.bind()
        vao.bind()
        vbo.bind()
        ebo.bind()
        glDisable(GL_CULL_FACE)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L)
    }
}