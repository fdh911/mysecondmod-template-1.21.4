package com.github.fdh911

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import net.minecraft.client.MinecraftClient
import org.lwjgl.opengl.GL45.*

object UserInterface {
    init {
        ImGui.createContext()
    }
    private val io = ImGui.getIO()
    private val fonts = io.fonts.apply {
        addFontDefault()
    }
    val imGuiGlfw = ImGuiImplGlfw().apply {
        val windowHandle = MinecraftClient.getInstance().window.handle
        init(windowHandle, true)
    }
    val imGuiGl3 = ImGuiImplGl3().apply {
        init("#version 150")
    }

    fun render(block: () -> Unit) {
        val state = GLState.currentState()
        imGuiGl3.newFrame()
        imGuiGlfw.newFrame()
        ImGui.newFrame()

        block()

        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())
        state.restore()
    }

    private data class GLState(
        val blend: Boolean,
        val depthTest: Boolean,
        val cull: Boolean,
        val scissor: Boolean,
        val stencil: Boolean,

        val blendSrcRGB: Int,
        val blendDstRGB: Int,
        val blendSrcAlpha: Int,
        val blendDstAlpha: Int,
        val blendEqRGB: Int,
        val blendEqAlpha: Int,

        val depthFunc: Int,
        val depthMask: Boolean,

        val stencilFunc: Int,
        val stencilRef: Int,
        val stencilMask: Int,
        val stencilFail: Int,
        val stencilPassDepthFail: Int,
        val stencilPassDepthPass: Int,
        val stencilWriteMask: Int,

        val colorMask: List<Boolean>,

        val cullFaceMode: Int,
        val frontFace: Int,
        val polygonMode: List<Int>,
        val polyOffsetFactor: Float,
        val polyOffsetUnits: Float,

        val program: Int,
        val vao: Int,
        val vbo: Int,
        val ebo: Int,
        val tex2d: Int,

        val viewport: List<Int>,
        val scissorBox: List<Int>,
    ) {
        fun restore() {
            setCap(GL_BLEND, blend)
            setCap(GL_DEPTH_TEST, depthTest)
            setCap(GL_CULL_FACE, cull)
            setCap(GL_SCISSOR_TEST, scissor)
            setCap(GL_STENCIL_TEST, stencil)

            glBlendFuncSeparate(blendSrcRGB, blendDstRGB, blendSrcAlpha, blendDstAlpha)
            glBlendEquationSeparate(blendEqRGB, blendEqAlpha)

            glDepthFunc(depthFunc)
            glDepthMask(depthMask)

            glStencilFunc(stencilFunc, stencilRef, stencilMask)
            glStencilOp(stencilFail, stencilPassDepthFail, stencilPassDepthPass)
            glStencilMask(stencilWriteMask)

            glColorMask(colorMask[0], colorMask[1], colorMask[2], colorMask[3])

            glCullFace(cullFaceMode)
            glFrontFace(frontFace)
            glPolygonMode(GL_FRONT_AND_BACK, polygonMode[0])
            glPolygonOffset(polyOffsetFactor, polyOffsetUnits)

            glUseProgram(program)
            glBindVertexArray(vao)
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
            glBindTexture(GL_TEXTURE_2D, tex2d)

            glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
            glScissor(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3])
        }

        companion object {
            fun currentState(): GLState {
                fun getCap(cap: Int) = glIsEnabled(cap)

                val buf4 = IntArray(4)
                val fbuf = FloatArray(2)
                val bbuf = IntArray(2)

                glGetIntegerv(GL_BLEND_SRC_RGB, bbuf)
                val srcRgb = bbuf[0]
                glGetIntegerv(GL_BLEND_DST_RGB, bbuf)
                val dstRgb = bbuf[0]
                glGetIntegerv(GL_BLEND_SRC_ALPHA, bbuf)
                val srcAlpha = bbuf[0]
                glGetIntegerv(GL_BLEND_DST_ALPHA, bbuf)
                val dstAlpha = bbuf[0]

                glGetIntegerv(GL_BLEND_EQUATION_RGB, bbuf)
                val eqRgb = bbuf[0]
                glGetIntegerv(GL_BLEND_EQUATION_ALPHA, bbuf)
                val eqAlpha = bbuf[0]

                val depthFunc = glGetInteger(GL_DEPTH_FUNC)
                val depthMask = glGetBoolean(GL_DEPTH_WRITEMASK)

                val stencilFunc = glGetInteger(GL_STENCIL_FUNC)
                val stencilRef = glGetInteger(GL_STENCIL_REF)
                val stencilMask = glGetInteger(GL_STENCIL_VALUE_MASK)
                val stencilFail = glGetInteger(GL_STENCIL_FAIL)
                val stencilPassDepthFail = glGetInteger(GL_STENCIL_PASS_DEPTH_FAIL)
                val stencilPassDepthPass = glGetInteger(GL_STENCIL_PASS_DEPTH_PASS)
                val stencilWriteMask = glGetInteger(GL_STENCIL_WRITEMASK)

                val colorMask = BooleanArray(4)
                val maskBuf = java.nio.ByteBuffer.allocateDirect(4)
                glGetBooleanv(GL_COLOR_WRITEMASK, maskBuf)
                for (i in 0 until 4) colorMask[i] = maskBuf.get(i) != 0.toByte()

                val cullFaceMode = glGetInteger(GL_CULL_FACE_MODE)
                val frontFace = glGetInteger(GL_FRONT_FACE)

                val polyMode = IntArray(2)
                glGetIntegerv(GL_POLYGON_MODE, polyMode)
                glGetFloatv(GL_POLYGON_OFFSET_FACTOR, fbuf)
                val polyOffsetFactor = fbuf[0]
                glGetFloatv(GL_POLYGON_OFFSET_UNITS, fbuf)
                val polyOffsetUnits = fbuf[0]

                val program = glGetInteger(GL_CURRENT_PROGRAM)
                val vao = glGetInteger(GL_VERTEX_ARRAY_BINDING)
                val vbo = glGetInteger(GL_ARRAY_BUFFER_BINDING)
                val ebo = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING)
                val tex2d = glGetInteger(GL_TEXTURE_BINDING_2D)

                glGetIntegerv(GL_VIEWPORT, buf4)
                val viewport = buf4.clone()

                glGetIntegerv(GL_SCISSOR_BOX, buf4)
                val scissorBox = buf4.clone()

                return GLState(
                    getCap(GL_BLEND),
                    getCap(GL_DEPTH_TEST),
                    getCap(GL_CULL_FACE),
                    getCap(GL_SCISSOR_TEST),
                    getCap(GL_STENCIL_TEST),

                    srcRgb, dstRgb, srcAlpha, dstAlpha,
                    eqRgb, eqAlpha,

                    depthFunc, depthMask,

                    stencilFunc, stencilRef, stencilMask,
                    stencilFail, stencilPassDepthFail, stencilPassDepthPass, stencilWriteMask,

                    colorMask.toList(),

                    cullFaceMode, frontFace, polyMode.toList(), polyOffsetFactor, polyOffsetUnits,

                    program, vao, vbo, ebo, tex2d,

                    viewport.toList(), scissorBox.toList()
                )
            }

            private fun setCap(cap: Int, enabled: Boolean) {
                if (enabled) glEnable(cap) else glDisable(cap)
            }
        }
    }
}