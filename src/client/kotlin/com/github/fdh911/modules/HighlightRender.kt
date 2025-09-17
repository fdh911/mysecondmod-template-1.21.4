package com.github.fdh911.modules

import com.github.fdh911.opengl.GLElementBuffer
import com.github.fdh911.opengl.GLProgram
import com.github.fdh911.opengl.GLState
import com.github.fdh911.opengl.GLTexture2D
import com.github.fdh911.opengl.GLVertexArray
import com.github.fdh911.opengl.GLVertexBuffer
import net.minecraft.client.MinecraftClient
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*

object HighlightRender {
    private val program: GLProgram
    private val vao: GLVertexArray
    private val vbo: GLVertexBuffer
    private val ebo: GLElementBuffer
    private val dsTex: GLTexture2D

    init {
        val state = GLState.currentState()

        program = GLProgram("/shaders/solid.vert", "/shaders/solid.frag").apply {
            bind()
            setVec4("uColor", Vector4f(1.0f, 0.0f, 0.0f, 1.0f))
        }

        vbo = GLVertexBuffer().apply {
            bind()
            setData(floatArrayOf(
                -1.0f, -1.0f,
                -1.0f, +1.0f,
                +1.0f, +1.0f,
                +1.0f, -1.0f,
            ), GLVertexBuffer.Usage.STATIC)
        }

        ebo = GLElementBuffer().apply {
            bind()
            setData(intArrayOf(
                0, 1, 2,
                2, 3, 0,
            ), GLElementBuffer.Usage.STATIC)
        }

        vao = GLVertexArray(2 to GLVertexArray.Attrib.FLOAT).apply {
            bind()
        }

        val w = MinecraftClient.getInstance().window.width
        val h = MinecraftClient.getInstance().window.height
        dsTex = GLTexture2D(null, w, h, GLTexture2D.Formats.DEPTH24_STENCIL8)

        program.unbind()
        vao.unbind()
        vbo.unbind()
        ebo.unbind()

        state.restore()
    }

    private var state: GLState? = null

    fun renderStart() {
        state = GLState.currentState()
        glEnable(GL_STENCIL_TEST)
        glStencilMask(0xff)
        glStencilFunc(GL_ALWAYS, 1, 0xff)
        glStencilOp(GL_KEEP, GL_REPLACE, GL_REPLACE)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, dsTex.id, 0)
    }

    fun renderEnd() {
        program.bind()
        vao.bind()
        vbo.bind()
        ebo.bind()
        glDisable(GL_CULL_FACE)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)
        glStencilMask(0x00)
        glStencilFunc(GL_EQUAL, 1, 0xff)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L)
        glStencilMask(0xff)
        glClear(GL_STENCIL_BUFFER_BIT)
        glStencilMask(0x00)
        state?.restore()
    }
}