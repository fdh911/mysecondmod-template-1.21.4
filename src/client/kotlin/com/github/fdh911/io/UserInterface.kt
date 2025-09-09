package com.github.fdh911.io

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.ByteBuffer

object UserInterface {
    init {
        ImGui.createContext()
    }
    private val io = ImGui.getIO().apply {
        iniFilename = null
        logFilename = null
    }
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

    // GPT generated class
    // Keep an eye on it
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
            setCap(GL11.GL_BLEND, blend)
            setCap(GL11.GL_DEPTH_TEST, depthTest)
            setCap(GL11.GL_CULL_FACE, cull)
            setCap(GL11.GL_SCISSOR_TEST, scissor)
            setCap(GL11.GL_STENCIL_TEST, stencil)

            GL14.glBlendFuncSeparate(blendSrcRGB, blendDstRGB, blendSrcAlpha, blendDstAlpha)
            GL20.glBlendEquationSeparate(blendEqRGB, blendEqAlpha)

            GL11.glDepthFunc(depthFunc)
            GL11.glDepthMask(depthMask)

            GL11.glStencilFunc(stencilFunc, stencilRef, stencilMask)
            GL11.glStencilOp(stencilFail, stencilPassDepthFail, stencilPassDepthPass)
            GL11.glStencilMask(stencilWriteMask)

            GL11.glColorMask(colorMask[0], colorMask[1], colorMask[2], colorMask[3])

            GL11.glCullFace(cullFaceMode)
            GL11.glFrontFace(frontFace)
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode[0])
            GL11.glPolygonOffset(polyOffsetFactor, polyOffsetUnits)

            GL20.glUseProgram(program)
            GL30.glBindVertexArray(vao)
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex2d)

            GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
            GL11.glScissor(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3])
        }

        companion object {
            fun currentState(): GLState {
                fun getCap(cap: Int) = GL11.glIsEnabled(cap)

                val buf4 = IntArray(4)
                val fbuf = FloatArray(2)
                val bbuf = IntArray(2)

                GL11.glGetIntegerv(GL14.GL_BLEND_SRC_RGB, bbuf)
                val srcRgb = bbuf[0]
                GL11.glGetIntegerv(GL14.GL_BLEND_DST_RGB, bbuf)
                val dstRgb = bbuf[0]
                GL11.glGetIntegerv(GL14.GL_BLEND_SRC_ALPHA, bbuf)
                val srcAlpha = bbuf[0]
                GL11.glGetIntegerv(GL14.GL_BLEND_DST_ALPHA, bbuf)
                val dstAlpha = bbuf[0]

                GL11.glGetIntegerv(GL20.GL_BLEND_EQUATION_RGB, bbuf)
                val eqRgb = bbuf[0]
                GL11.glGetIntegerv(GL20.GL_BLEND_EQUATION_ALPHA, bbuf)
                val eqAlpha = bbuf[0]

                val depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC)
                val depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)

                val stencilFunc = GL11.glGetInteger(GL11.GL_STENCIL_FUNC)
                val stencilRef = GL11.glGetInteger(GL11.GL_STENCIL_REF)
                val stencilMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK)
                val stencilFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL)
                val stencilPassDepthFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL)
                val stencilPassDepthPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS)
                val stencilWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK)

                val colorMask = BooleanArray(4)
                val maskBuf = ByteBuffer.allocateDirect(4)
                GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, maskBuf)
                for (i in 0 until 4) colorMask[i] = maskBuf.get(i) != 0.toByte()

                val cullFaceMode = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE)
                val frontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE)

                val polyMode = IntArray(2)
                GL11.glGetIntegerv(GL11.GL_POLYGON_MODE, polyMode)
                GL11.glGetFloatv(GL11.GL_POLYGON_OFFSET_FACTOR, fbuf)
                val polyOffsetFactor = fbuf[0]
                GL11.glGetFloatv(GL11.GL_POLYGON_OFFSET_UNITS, fbuf)
                val polyOffsetUnits = fbuf[0]

                val program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)
                val vao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING)
                val vbo = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING)
                val ebo = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING)
                val tex2d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)

                GL11.glGetIntegerv(GL11.GL_VIEWPORT, buf4)
                val viewport = buf4.clone()

                GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, buf4)
                val scissorBox = buf4.clone()

                return GLState(
                    getCap(GL11.GL_BLEND),
                    getCap(GL11.GL_DEPTH_TEST),
                    getCap(GL11.GL_CULL_FACE),
                    getCap(GL11.GL_SCISSOR_TEST),
                    getCap(GL11.GL_STENCIL_TEST),

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
                if (enabled) GL11.glEnable(cap) else GL11.glDisable(cap)
            }
        }
    }

    object MCScreen : Screen(Text.empty()) {
        private val renderEvents = mutableListOf<() -> Unit>()

        fun onRender(action: () -> Unit) = renderEvents.add(action)

        override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
            super.render(context, mouseX, mouseY, delta)
            for(action in renderEvents)
                action()
        }

        override fun shouldPause(): Boolean = false
    }
}