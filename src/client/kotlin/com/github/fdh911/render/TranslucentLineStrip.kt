package com.github.fdh911.render

import com.github.fdh911.modules.ModuleGardenMacro
import com.github.fdh911.render.opengl.GLProgram
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.render.opengl.GLVertexArray
import com.github.fdh911.render.opengl.GLVertexArray.AttribRegular
import com.github.fdh911.render.opengl.GLVertexBuffer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL32.GL_LINE_STRIP
import org.lwjgl.opengl.GL32.glDrawArrays

object TranslucentLineStrip {
    private val program: GLProgram
    private val vao: GLVertexArray
    private val vbo: GLVertexBuffer

    private var vertices: FloatArray? = null
    private var index = 0

    init {
        val state = GLState2().apply { saveAll() }

        program = GLProgram.fromClasspath("linestrip")
        program.bind()
        program.setVec4("uColor", Vector4f(ModuleGardenMacro.RenderConstants.blue, 1.0f))

        vbo = GLVertexBuffer()

        vao = GLVertexArray(
            AttribRegular(vbo, 3)
        )

        state.restoreAll()
    }

    fun newLinestrip(amountOfPoints: Int) {
        vertices = FloatArray(3 * amountOfPoints)
        index = 0
    }

    fun addPoint(pos: Vector3f) {
        val array = vertices!!
        array[index + 0] = pos.x
        array[index + 1] = pos.y
        array[index + 2] = pos.z
        index += 3
    }

    fun render(ctx: WorldRenderContext) {
        val projView = TranslucentUtils.getProjView(ctx)
        val state = TranslucentUtils.saveAndSetupState()

        val array = vertices!!
        val amountOfPoints = array.size / 3

        vao.bind()
        vbo.setData(array, GLVertexBuffer.Usage.DYNAMIC)

        program.bind()
        program.setMat4("uProjView", projView)

        glDrawArrays(GL_LINE_STRIP, 0, amountOfPoints)

        TranslucentUtils.restoreState(state)
    }
}