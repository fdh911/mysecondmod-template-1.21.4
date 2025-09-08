package com.github.fdh911

import com.github.fdh911.opengl.GLElementBuffer
import com.github.fdh911.opengl.GLProgram
import com.github.fdh911.opengl.GLVertexArray
import com.github.fdh911.opengl.GLVertexArray.Attrib.*
import com.github.fdh911.opengl.GLVertexBuffer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*

object EntitySearcher {
    fun update(ctx: WorldRenderContext) {
        val player = MinecraftClient.getInstance().player
        if(player == null)
            return

        val entityList = MinecraftClient.getInstance().world?.entities
            ?: return

        for(entity in entityList) {
            if(entity === player) continue

            val aabb = entity.boundingBox

            val delta = entity.interpolatedPos().subtract(entity.pos)

            drawRect(ctx, aabb.minPos.add(delta), aabb.maxPos.add(delta))
        }
    }

    private fun Entity.interpolatedPos(): Vec3d {
        val partialTick = MinecraftClient.getInstance().renderTickCounter!!.getTickDelta(true)
        return Vec3d(
            prevX + (x - prevX) * partialTick,
            prevY + (y - prevY) * partialTick,
            prevZ + (z - prevZ) * partialTick,
        )
    }

    object RectRender {
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

        val program = GLProgram("/shaders/poscolor.vert", "/shaders/poscolor.frag").apply { bind() }

        val vbo = GLVertexBuffer().apply {
            bind()
            setData(vertices, GLVertexBuffer.Usage.STATIC)
        }

        val ebo = GLElementBuffer().apply {
            bind()
            setData(indices, GLElementBuffer.Usage.STATIC)
        }

        val vao = GLVertexArray(3 to FLOAT).apply { bind() }

        init {
            vao.unbind()
            vbo.unbind()
            ebo.unbind()
            program.unbind()
        }

        data class GLState(
            val blendEnabled: Boolean,
            val srcRGB: Int,
            val dstRGB: Int,
            val srcAlpha: Int,
            val dstAlpha: Int,
            val blendEqRGB: Int,
            val blendEqAlpha: Int,

            val depthEnabled: Boolean,
            val depthFunc: Int,
            val depthMask: Boolean,

            val cullEnabled: Boolean,
            val frontFace: Int,
            val cullFaceMode: Int,

            val program: Int,
            val vao: Int,
            val vbo: Int,
            val ebo: Int
        ) {
            fun restore() {
                if (blendEnabled) glEnable(GL_BLEND) else glDisable(GL_BLEND)
                glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha)
                glBlendEquationSeparate(blendEqRGB, blendEqAlpha)

                if (depthEnabled) glEnable(GL_DEPTH_TEST) else glDisable(GL_DEPTH_TEST)
                glDepthFunc(depthFunc)
                glDepthMask(depthMask)

                if (cullEnabled) glEnable(GL_CULL_FACE) else glDisable(GL_CULL_FACE)
                glFrontFace(frontFace)
                glCullFace(cullFaceMode)

                glUseProgram(program)
                glBindVertexArray(vao)
                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
            }

            companion object {
                fun currentState() : GLState = GLState(
                    blendEnabled = glIsEnabled(GL_BLEND),
                    srcRGB = glGetInteger(GL_BLEND_SRC_RGB),
                    dstRGB = glGetInteger(GL_BLEND_DST_RGB),
                    srcAlpha = glGetInteger(GL_BLEND_SRC_ALPHA),
                    dstAlpha = glGetInteger(GL_BLEND_DST_ALPHA),
                    blendEqRGB = glGetInteger(GL_BLEND_EQUATION_RGB),
                    blendEqAlpha = glGetInteger(GL_BLEND_EQUATION_ALPHA),

                    depthEnabled = glIsEnabled(GL_DEPTH_TEST),
                    depthFunc = glGetInteger(GL_DEPTH_FUNC),
                    depthMask = glGetBoolean(GL_DEPTH_WRITEMASK),

                    cullEnabled = glIsEnabled(GL_CULL_FACE),
                    frontFace = glGetInteger(GL_FRONT_FACE),
                    cullFaceMode = glGetInteger(GL_CULL_FACE_MODE),

                    program = glGetInteger(GL_CURRENT_PROGRAM),
                    vao = glGetInteger(GL_VERTEX_ARRAY_BINDING),
                    vbo = glGetInteger(GL_ARRAY_BUFFER_BINDING),
                    ebo = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING)
                )
            }
        }

        fun render(ctx: WorldRenderContext, corner: Vector3f, scale: Vector3f, color: Vector4f) {
            val player = MinecraftClient.getInstance().player!!
            val playerPos = player.interpolatedPos().toVector3f()

            val cam = ctx.camera()

            val proj = Matrix4f(ctx.projectionMatrix())
            val view = Matrix4f(ctx.positionMatrix())
                .translate(cam.pos.toVector3f().sub(playerPos).negate())
            val model = Matrix4f()
                .translate(corner.sub(playerPos))
                .scale(scale)

            val state = GLState.currentState()

            glEnable(GL_BLEND)
            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_ALWAYS)
            glDisable(GL_CULL_FACE)
            program.bind()
            program.setMat4("uModel", model)
            program.setMat4("uViewProj", proj.mul(view))
            program.setVec4("uColor", color)
            vao.bind()
            vbo.bind()
            ebo.bind()
            glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0L)

            state.restore()
        }
    }

    private fun drawRect(ctx: WorldRenderContext, minPos: Vec3d, maxPos: Vec3d) {
        val translate = minPos.toVector3f()
        val scale = maxPos.subtract(minPos).toVector3f()

        RectRender.render(
            ctx,
            translate,
            scale,
            Vector4f(1.0f, 0.0f, 0.0f, 0.2f)
        )
    }
}