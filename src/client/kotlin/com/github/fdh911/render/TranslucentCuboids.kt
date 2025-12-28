package com.github.fdh911.render

import com.github.fdh911.render.opengl.*
import com.github.fdh911.render.opengl.GLVertexArray.AttribInstanced
import com.github.fdh911.render.opengl.GLVertexArray.AttribRegular
import com.github.fdh911.utils.interpolatedPos
import com.github.fdh911.utils.mc
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*

object TranslucentCuboids
{
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

    private val faceIndices = intArrayOf(
        0, 1, 2,
        0, 2, 3,

        4, 6, 5,
        4, 7, 6,

        3, 2, 6,
        3, 6, 7,

        0, 5, 1,
        0, 4, 5,

        0, 3, 7,
        0, 7, 4,

        1, 5, 6,
        1, 6, 2,
    )

    private val outlineIndices = intArrayOf(
        0, 1,
        1, 2,
        2, 3,
        3, 0,

        0, 4,
        1, 5,
        2, 6,
        3, 7,

        4, 5,
        5, 6,
        6, 7,
        7, 4,
    )

    private val programSingle: GLProgram

    private val singleVao: GLVertexArray

    private val solidEbo: GLElementBuffer
    private val outlineEbo: GLElementBuffer

    private val localPosVbo: GLVertexBuffer

    class Instanced()
    {
        companion object {
            private const val INSTANCE_FLOAT_COUNT = 10
            private val program: GLProgram

            init {
                val state = GLState2().apply { saveAll() }

                program = GLProgram.fromClasspath("cuboidsInstanced").apply {
                    bind()
                    setVec4("uHighlightCol", Vector4f(1.0f, 0.0f, 0.0f, 0.4f))
                }

                state.restoreAll()
            }
        }

        private val instancedVbo: GLVertexBuffer
        private val instancedVao: GLVertexArray

        private var offset = 0
        private var count = 0
        private var array: FloatArray? = null
        private var finished = false

        init {
            val state = GLState2().apply { saveAll() }

            instancedVbo = GLVertexBuffer()

            instancedVao = GLVertexArray(
                AttribRegular(localPosVbo, 3),      // model vertices
                AttribInstanced(instancedVbo, 3, 1), // instance translation
                AttribInstanced(instancedVbo, 3, 1), // instance scaling
                AttribInstanced(instancedVbo, 4, 1), // instance color
                elementBuffer = solidEbo,
            )

            state.restoreAll()
        }

        fun begin(howManyInstances: Int) {
            finished = false

            offset = 0
            count = howManyInstances
            array = FloatArray(count * INSTANCE_FLOAT_COUNT)
        }

        fun addCube(pos: Vector3f, color: Vector4f) = addRect(pos, Vector3f(1.0f, 1.0f, 1.0f), color)

        fun addRect(pos: Vector3f, scale: Vector3f, color: Vector4f) {
            val offset = offset * INSTANCE_FLOAT_COUNT
            val array = array!!
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
            this@Instanced.offset++
        }

        fun finish() {
            val state = GLState2().apply { saveProgramAndBuffers() }

            instancedVbo.setData(array!!, GLVertexBuffer.Usage.STATIC)

            state.restoreProgramAndBuffers()

            finished = true
        }

        fun render(
            ctx: WorldRenderContext,
            drawSolids: Boolean = false,
            drawOutlines: Boolean = false,
        ) {
            require(finished) { "Using unfinished renderer $this" }

            val projView = TranslucentUtils.getProjView(ctx)
            val state = TranslucentUtils.saveAndSetupState()

            val playerPos = mc.player!!.interpolatedPos().toVector3f().add(0.0f, 1.0f, 0.0f)

            program.apply {
                bind()
                setVec3("uPlayerPos", playerPos)
                setMat4("uProjView", projView)
            }

            instancedVao.bind()

            if(drawSolids) {
                solidEbo.bind()
                glDrawElementsInstanced(GL_TRIANGLES, faceIndices.size, GL_UNSIGNED_INT, 0L, count)
            }

            if(drawOutlines) {
                outlineEbo.bind()
                glDrawElementsInstanced(GL_LINES, outlineIndices.size, GL_UNSIGNED_INT, 0L, count)
            }

            TranslucentUtils.restoreState(state)
        }
    }

    init {
        val state = GLState2().apply { saveAll() }

        programSingle = GLProgram.fromClasspath("poscolor")

        localPosVbo = GLVertexBuffer.withStaticVertices(*vertices)

        solidEbo = GLElementBuffer.withStaticIndices(*faceIndices)
        outlineEbo = GLElementBuffer.withStaticIndices(*outlineIndices)

        singleVao = GLVertexArray(
            AttribRegular(localPosVbo, 3),
            elementBuffer = solidEbo,
        )

        state.restoreAll()
    }

    fun renderSingle(ctx: WorldRenderContext, pos: Vector3f, scale: Vector3f, color: Vector4f) {
        val projView = TranslucentUtils.getProjView(ctx)
        val model = Matrix4f()
            .translate(pos)
            .scale(scale)

        val state = TranslucentUtils.saveAndSetupState()

        singleVao.bind()
        programSingle.bind()
        programSingle.setMat4("uProjView", projView)
        programSingle.setMat4("uModel", model)
        programSingle.setVec4("uColor", color)

        glDrawElements(GL_TRIANGLES, faceIndices.size, GL_UNSIGNED_INT, 0L)

        TranslucentUtils.restoreState(state)
    }
}
