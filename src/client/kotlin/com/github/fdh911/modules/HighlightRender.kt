package com.github.fdh911.modules

import com.github.fdh911.opengl.GLDebug
import com.github.fdh911.opengl.GLElementBuffer
import com.github.fdh911.opengl.GLFramebuffer
import com.github.fdh911.opengl.GLProgram
import com.github.fdh911.opengl.GLState2
import com.github.fdh911.opengl.GLVertexArray
import com.github.fdh911.opengl.GLVertexBuffer
import net.minecraft.client.MinecraftClient
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*

object HighlightRender {
    private val program: GLProgram
    private val vao: GLVertexArray
    private val vbo: GLVertexBuffer
    private val ebo: GLElementBuffer
    private val fb: GLFramebuffer

    init {
        val state = GLState2().apply { saveAll() }

        program = GLProgram.fromClasspath("entityhl").apply {
            bind()
            setInt("uFbColorAttachment", 0)
            setVec4("uOverlayColor", Vector4f(1.0f, 0.0f, 0.0f, 0.5f))
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

        vao = GLVertexArray(
            2 to GLVertexArray.Attrib.FLOAT,
            2 to GLVertexArray.Attrib.FLOAT,
        )

        fb = GLFramebuffer.withWindowSize()

        state.restoreAll()
    }

    private var state: GLState2? = null

    fun renderStart() {
        GLDebug.marker("Highlight start")
        state = GLState2().apply { saveAll() }

        fb.bind()
        fb.clearAll()
    }

    fun renderEnd() {
        state?.saveProgramAndBuffers()

        val wnd = MinecraftClient.getInstance().window
        program.bind()
        program.setVec2("uScreenSize", Vector2f(
            wnd.width.toFloat(),
            wnd.height.toFloat(),
        ))
        vao.bind()
        vbo.bind()
        ebo.bind()

        glDisable(GL_CULL_FACE)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_BLEND)

        fb.colorAttachment.bindToTexSlot(0)

        state?.restoreFramebuffer()

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L)

        state?.restoreAll()
        GLDebug.marker("Highlight end")
    }
}