package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLElementBuffer
import com.github.fdh911.render.opengl.GLProgram
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.render.opengl.GLVertexArray
import com.github.fdh911.render.opengl.GLVertexBuffer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*

object CuboidRenderer {
    private val vertices = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f,
    )

    private val indices = intArrayOf(
        0, 2, 1,
        0, 3, 2,
        4, 5, 6,
        4, 6, 7,
        3, 2, 6,
        3, 6, 7,
        0, 5, 1,
        0, 4, 5,
        0, 7, 3,
        0, 4, 7,
        1, 2, 6,
        1, 6, 5,
    )

    private val program: GLProgram
    private val vbo: GLVertexBuffer
    private val ebo: GLElementBuffer
    private val vao: GLVertexArray

    init {
        val state = GLState2().apply { saveAll() }

        program = GLProgram("/shaders/poscolor.vert", "/shaders/poscolor.frag").apply {
            bind()
        }

        vbo = GLVertexBuffer().apply {
            bind()
            setData(vertices, GLVertexBuffer.Usage.STATIC)
        }

        ebo = GLElementBuffer().apply {
            bind()
            setData(indices, GLElementBuffer.Usage.STATIC)
        }

        vao = GLVertexArray(3 to GLVertexArray.Attrib.FLOAT).apply {
            bind()
        }

        vao.unbind()
        vbo.unbind()
        ebo.unbind()
        program.unbind()

        state.restoreAll()
    }

    fun render(ctx: WorldRenderContext, pos: Vector3f, scale: Vector3f, color: Vector4f) {
        val cam = ctx.camera()

        val proj = Matrix4f(ctx.projectionMatrix())
        val view = Matrix4f(ctx.positionMatrix())
            .translate(cam.pos.toVector3f().negate())
        val model = Matrix4f()
            .translate(pos)
            .scale(scale)

        val state = GLState2().apply { saveAll() }

        glEnable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_ALWAYS)
        glDepthMask(false)
        glDisable(GL_CULL_FACE)

        program.bind()
        program.setMat4("uProjView", Matrix4f(proj).mul(view))
        program.setMat4("uModel", model)
        program.setVec4("uColor", color)
        vao.bind()
        vbo.bind()
        ebo.bind()
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0L)

        state.restoreAll()
    }
}
