package com.github.fdh911.render

import com.github.fdh911.render.opengl.*
import com.github.fdh911.render.opengl.GLVertexArray.*
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

    private val programSingle: GLProgram
    private val programInstanced: GLProgram

    private val vao: GLVertexArray
    private val ebo: GLElementBuffer

    private val verticesVbo: GLVertexBuffer
    private val instanceVbo: GLVertexBuffer

    private const val INSTANCE_FLOAT_COUNT = 10
    private var instanceBufferData: FloatArray? = null
    private var currentInstance = 0
    private var instanceCount = 0

    init {
        val state = GLState2().apply { saveAll() }

        programSingle = GLProgram.fromClasspath("poscolor")
        programInstanced = GLProgram.fromClasspath("cuboidsInstanced")

        verticesVbo = GLVertexBuffer.withStaticVertices(*vertices)
        instanceVbo = GLVertexBuffer()

        ebo = GLElementBuffer.withStaticIndices(*indices)

        vao = GLVertexArray(
            AttribRegular(verticesVbo, 3),      // model vertices
            AttribInstanced(instanceVbo, 3, 1), // instance translation
            AttribInstanced(instanceVbo, 3, 1), // instance scaling
            AttribInstanced(instanceVbo, 4, 1), // instance color
            elementBuffer = ebo,
        )

        state.restoreAll()
    }

    fun newInstancing(howManyInstances: Int) {
        currentInstance = 0
        instanceCount = howManyInstances
        instanceBufferData = FloatArray(instanceCount * INSTANCE_FLOAT_COUNT)
    }

    fun addCubeInstance(pos: Vector3f, color: Vector4f) = addInstance(pos, Vector3f(1.0f, 1.0f, 1.0f), color)

    fun addInstance(pos: Vector3f, scale: Vector3f, color: Vector4f) {
        val offset = currentInstance * INSTANCE_FLOAT_COUNT
        val array = instanceBufferData!!
        array[offset + 0] = pos.x
        array[offset + 1] = pos.y
        array[offset + 2] = pos.z
        array[offset + 3] = scale.x
        array[offset + 4] = scale.y
        array[offset + 5] = scale.z
        array[offset + 6] = color.x
        array[offset + 7] = color.y
        array[offset + 8] = color.z
        array[offset + 9] = color.w
        currentInstance++
    }

    fun renderInstanced(ctx: WorldRenderContext) {
        val projView = getProjView(ctx)
        val state = saveAndSetupState()

        vao.bind()
        instanceVbo.setData(instanceBufferData!!, GLVertexBuffer.Usage.DYNAMIC)

        programInstanced.bind()
        programInstanced.setMat4("uProjView", projView)

        glDrawElementsInstanced(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0L, instanceCount)

        restoreState(state)
    }

    fun renderSingle(ctx: WorldRenderContext, pos: Vector3f, scale: Vector3f, color: Vector4f) {
        val projView = getProjView(ctx)
        val model = Matrix4f()
            .translate(pos)
            .scale(scale)

        val state = saveAndSetupState()

        vao.bind()
        programSingle.bind()
        programSingle.setMat4("uProjView", projView)
        programSingle.setMat4("uModel", model)
        programSingle.setVec4("uColor", color)

        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0L)

        restoreState(state)
    }

    private fun getProjView(ctx: WorldRenderContext): Matrix4f {
        val cam = ctx.camera()
        val proj = Matrix4f(ctx.projectionMatrix())
        val view = Matrix4f(ctx.positionMatrix())
            .translate(cam.pos.toVector3f().negate())
        return Matrix4f(proj).mul(view)
    }

    private fun saveAndSetupState(): GLState2 {
        val stateSave = GLState2().apply {
            saveBlend()
            saveDepth()
            saveRaster()
            saveProgramAndBuffers()
        }

        glEnable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_ALWAYS)
        glDepthMask(false)
        glDisable(GL_CULL_FACE)

        return stateSave
    }

    private fun restoreState(state: GLState2) {
        state.apply {
            restoreProgramAndBuffers()
            restoreRaster()
            restoreDepth()
            restoreBlend()
        }
    }
}
