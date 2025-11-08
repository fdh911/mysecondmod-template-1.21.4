package com.github.fdh911.render.opengl

import org.lwjgl.opengl.GL45.*
import org.lwjgl.system.MemoryStack

class GLState2 {
    data class BlendState(
        val enabled: Boolean,
        val srcRGB: Int,
        val dstRGB: Int,
        val srcAlpha: Int,
        val dstAlpha: Int,
        val eqRGB: Int,
        val eqAlpha: Int,
        val colorMaskR: Boolean,
        val colorMaskG: Boolean,
        val colorMaskB: Boolean,
        val colorMaskA: Boolean,
    )

    data class DepthState(
        val enabled: Boolean,
        val func: Int,
        val mask: Boolean,
        val rangeNear: Float,
        val rangeFar: Float,
    )

    data class StencilState(
        val enabled: Boolean,
        val funcFront: Int,
        val refFront: Int,
        val maskFront: Int,
        val opSfailFront: Int,
        val opDpfailFront: Int,
        val opDppassFront: Int,
        val writeMaskFront: Int,

        val funcBack: Int,
        val refBack: Int,
        val maskBack: Int,
        val opSfailBack: Int,
        val opDpfailBack: Int,
        val opDppassBack: Int,
        val writeMaskBack: Int,

        val clearValue: Int,
    )

    data class RasterState(
        val cullEnabled: Boolean,
        val cullFace: Int,
        val frontFace: Int,
        val polygonModeFrontAndBack: Int,
        val polygonOffsetFactor: Float,
        val polygonOffsetUnits: Float,
        val polygonOffsetEnabled: Boolean,
        val lineWidth: Float,
        val pointSize: Float,
    )

    data class ProgramBufferState(
        val program: Int,
        val vao: Int,
        val vbo: Int,
        val ebo: Int,
        val activeTexture: Int,
        val boundTexture2D: Int,
        val boundTextureCube: Int,
    )

    data class ViewportScissorState(
        val viewportX: Int,
        val viewportY: Int,
        val viewportW: Int,
        val viewportH: Int,
        val scissorX: Int,
        val scissorY: Int,
        val scissorW: Int,
        val scissorH: Int,
        val scissorEnabled: Boolean,
    )

    data class FboState(
        val drawFbo: Int,
        val readFbo: Int,
    )

    var blendState: BlendState? = null
        private set
    var depthState: DepthState? = null
        private set
    var stencilState: StencilState? = null
        private set
    var rasterState: RasterState? = null
        private set
    var programBufferState: ProgramBufferState? = null
        private set
    var viewportScissorState: ViewportScissorState? = null
        private set
    var fboState: FboState? = null
        private set

    fun saveBlend() = MemoryStack.stackPush().use { stk ->
        val colorMaskBuf = stk.malloc(4)
        glGetBooleanv(GL_COLOR_WRITEMASK, colorMaskBuf)
        val srcRGB = glGetInteger(GL_BLEND_SRC_RGB)
        val dstRGB = glGetInteger(GL_BLEND_DST_RGB)
        val srcAlpha = glGetInteger(GL_BLEND_SRC_ALPHA)
        val dstAlpha = glGetInteger(GL_BLEND_DST_ALPHA)
        val eqRGB = glGetInteger(GL_BLEND_EQUATION_RGB)
        val eqAlpha = glGetInteger(GL_BLEND_EQUATION_ALPHA)
        val enabled = glIsEnabled(GL_BLEND)

        blendState = BlendState(
            enabled = enabled,
            srcRGB = srcRGB,
            dstRGB = dstRGB,
            srcAlpha = srcAlpha,
            dstAlpha = dstAlpha,
            eqRGB = eqRGB,
            eqAlpha = eqAlpha,
            colorMaskR = (colorMaskBuf.get(0) != 0.toByte()),
            colorMaskG = (colorMaskBuf.get(1) != 0.toByte()),
            colorMaskB = (colorMaskBuf.get(2) != 0.toByte()),
            colorMaskA = (colorMaskBuf.get(3) != 0.toByte()),
        )
    }

    fun restoreBlend() {
        val s = blendState ?: return
        helperEnableOrDisable(GL_BLEND, s.enabled)
        glBlendFuncSeparate(s.srcRGB, s.dstRGB, s.srcAlpha, s.dstAlpha)
        glBlendEquationSeparate(s.eqRGB, s.eqAlpha)
        glColorMask(s.colorMaskR, s.colorMaskG, s.colorMaskB, s.colorMaskA)
    }

    fun saveDepth() = MemoryStack.stackPush().use { stk ->
        val enabled = glIsEnabled(GL_DEPTH_TEST)
        val func = glGetInteger(GL_DEPTH_FUNC)
        val mask = glGetBoolean(GL_DEPTH_WRITEMASK)
        val rangeBuf = stk.mallocFloat(2)
        glGetFloatv(GL_DEPTH_RANGE, rangeBuf)
        depthState = DepthState(
            enabled = enabled,
            func = func,
            mask = mask,
            rangeNear = rangeBuf.get(0),
            rangeFar = rangeBuf.get(1),
        )
    }

    fun restoreDepth() {
        val s = depthState ?: return
        helperEnableOrDisable(GL_DEPTH_TEST, s.enabled)
        glDepthFunc(s.func)
        glDepthMask(s.mask)
        glDepthRange(s.rangeNear.toDouble(), s.rangeFar.toDouble())
    }

    fun saveStencil() {
        val funcFront = glGetInteger(GL_STENCIL_FUNC)
        val refFront = glGetInteger(GL_STENCIL_REF)
        val maskFront = glGetInteger(GL_STENCIL_VALUE_MASK)
        val opSfailFront = glGetInteger(GL_STENCIL_FAIL)
        val opDpfailFront = glGetInteger(GL_STENCIL_PASS_DEPTH_FAIL)
        val opDppassFront = glGetInteger(GL_STENCIL_PASS_DEPTH_PASS)
        val writeMaskFront = glGetInteger(GL_STENCIL_WRITEMASK)

        val funcBack = glGetInteger(GL_STENCIL_BACK_FUNC)
        val refBack = glGetInteger(GL_STENCIL_BACK_REF)
        val maskBack = glGetInteger(GL_STENCIL_BACK_VALUE_MASK)
        val opSfailBack = glGetInteger(GL_STENCIL_BACK_FAIL)
        val opDpfailBack = glGetInteger(GL_STENCIL_BACK_PASS_DEPTH_FAIL)
        val opDppassBack = glGetInteger(GL_STENCIL_BACK_PASS_DEPTH_PASS)
        val writeMaskBack = glGetInteger(GL_STENCIL_BACK_WRITEMASK)

        val enabled = glIsEnabled(GL_STENCIL_TEST)
        val clearValue = glGetInteger(GL_STENCIL_CLEAR_VALUE)

        stencilState = StencilState(
            enabled = enabled,

            funcFront = funcFront,
            refFront = refFront,
            maskFront = maskFront,
            opSfailFront = opSfailFront,
            opDpfailFront = opDpfailFront,
            opDppassFront = opDppassFront,
            writeMaskFront = writeMaskFront,

            funcBack = funcBack,
            refBack = refBack,
            maskBack = maskBack,
            opSfailBack = opSfailBack,
            opDpfailBack = opDpfailBack,
            opDppassBack = opDppassBack,
            writeMaskBack = writeMaskBack,

            clearValue = clearValue,
        )
    }

    fun restoreStencil() {
        val s = stencilState ?: return
        helperEnableOrDisable(GL_STENCIL_TEST, s.enabled)

        glStencilFuncSeparate(GL_FRONT, s.funcFront, s.refFront, s.maskFront)
        glStencilOpSeparate(GL_FRONT, s.opSfailFront, s.opDpfailFront, s.opDppassFront)
        glStencilMaskSeparate(GL_FRONT, s.writeMaskFront)

        glStencilFuncSeparate(GL_BACK, s.funcBack, s.refBack, s.maskBack)
        glStencilOpSeparate(GL_BACK, s.opSfailBack, s.opDpfailBack, s.opDppassBack)
        glStencilMaskSeparate(GL_BACK, s.writeMaskBack)

        glClearStencil(s.clearValue)
    }

    fun saveRaster() {
        val cullEnabled = glIsEnabled(GL_CULL_FACE)
        val cullFace = glGetInteger(GL_CULL_FACE_MODE)
        val frontFace = glGetInteger(GL_FRONT_FACE)
        val polygonModeFrontAndBack = glGetInteger(GL_POLYGON_MODE)
        val polygonOffsetFactor = glGetFloat(GL_POLYGON_OFFSET_FACTOR)
        val polygonOffsetUnits = glGetFloat(GL_POLYGON_OFFSET_UNITS)
        val polyOffsetEnabled = glIsEnabled(GL_POLYGON_OFFSET_FILL)
        val lineWidth = glGetFloat(GL_LINE_WIDTH)
        val pointSize = glGetFloat(GL_POINT_SIZE)

        rasterState = RasterState(
            cullEnabled = cullEnabled,
            cullFace = cullFace,
            frontFace = frontFace,
            polygonModeFrontAndBack = polygonModeFrontAndBack,
            polygonOffsetFactor = polygonOffsetFactor,
            polygonOffsetUnits = polygonOffsetUnits,
            polygonOffsetEnabled = polyOffsetEnabled,
            lineWidth = lineWidth,
            pointSize = pointSize,
        )
    }

    fun restoreRaster() {
        val s = rasterState ?: return
        helperEnableOrDisable(GL_CULL_FACE, s.cullEnabled)
        glCullFace(s.cullFace)
        glFrontFace(s.frontFace)
        glPolygonMode(GL_FRONT_AND_BACK, s.polygonModeFrontAndBack)
        helperEnableOrDisable(GL_POLYGON_OFFSET_FILL, s.polygonOffsetEnabled)
        glPolygonOffset(s.polygonOffsetFactor, s.polygonOffsetUnits)
        glLineWidth(s.lineWidth)
        glPointSize(s.pointSize)
    }

    fun saveProgramAndBuffers() {
        val program = glGetInteger(GL_CURRENT_PROGRAM)
        val vao = glGetInteger(GL_VERTEX_ARRAY_BINDING)
        val vbo = glGetInteger(GL_ARRAY_BUFFER_BINDING)
        val ebo = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING)
        val activeTexture = glGetInteger(GL_ACTIVE_TEXTURE)
        val boundTexture2D = glGetInteger(GL_TEXTURE_BINDING_2D)
        val boundTextureCube = glGetInteger(GL_TEXTURE_BINDING_CUBE_MAP)

        programBufferState = ProgramBufferState(
            program = program,
            vao = vao,
            vbo = vbo,
            ebo = ebo,
            activeTexture = activeTexture,
            boundTexture2D = boundTexture2D,
            boundTextureCube = boundTextureCube,
        )
    }

    fun restoreProgramAndBuffers() {
        val s = programBufferState ?: return
        if(s.vao == 0 || glIsVertexArray(s.vao)) glBindVertexArray(s.vao)
        if(s.vbo == 0 || glIsBuffer(s.vbo)) glBindBuffer(GL_ARRAY_BUFFER, s.vbo)
        if(s.ebo == 0 || glIsBuffer(s.ebo)) glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, s.ebo)
        if(s.program == 0 || glIsProgram(s.program)) glUseProgram(s.program)
        glActiveTexture(s.activeTexture)
        if(s.boundTexture2D == 0 || glIsTexture(s.boundTexture2D)) glBindTexture(GL_TEXTURE_2D, s.boundTexture2D)
        if(s.boundTextureCube == 0 || glIsTexture(s.boundTextureCube)) glBindTexture(GL_TEXTURE_CUBE_MAP, s.boundTextureCube)
    }

    fun saveViewportScissor() = MemoryStack.stackPush().use { stk ->
        val vpBuf = stk.mallocInt(4)
        val scBuf = stk.mallocInt(4)
        glGetIntegerv(GL_VIEWPORT, vpBuf)
        glGetIntegerv(GL_SCISSOR_BOX, scBuf)
        val scEnabled = glIsEnabled(GL_SCISSOR_TEST)
        viewportScissorState = ViewportScissorState(
            viewportX = vpBuf.get(0),
            viewportY = vpBuf.get(1),
            viewportW = vpBuf.get(2),
            viewportH = vpBuf.get(3),
            scissorX = scBuf.get(0),
            scissorY = scBuf.get(1),
            scissorW = scBuf.get(2),
            scissorH = scBuf.get(3),
            scissorEnabled = scEnabled
        )
    }

    fun restoreViewportScissor() {
        val s = viewportScissorState ?: return
        glViewport(s.viewportX, s.viewportY, s.viewportW, s.viewportH)
        helperEnableOrDisable(GL_SCISSOR_TEST, s.scissorEnabled)
        glScissor(s.scissorX, s.scissorY, s.scissorW, s.scissorH)
    }

    fun saveFramebuffer() {
        val drawFbo = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING)
        val readFbo = glGetInteger(GL_READ_FRAMEBUFFER_BINDING)
        fboState = FboState(
            drawFbo = drawFbo,
            readFbo = readFbo
        )
    }

    fun restoreFramebuffer() {
        val s = fboState ?: return
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, s.drawFbo)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, s.readFbo)
    }

    fun saveAll() {
        saveFramebuffer()
        saveProgramAndBuffers()
        saveBlend()
        saveDepth()
        saveStencil()
        saveRaster()
        saveViewportScissor()
    }

    fun restoreAll() {
        restoreViewportScissor()
        restoreRaster()
        restoreStencil()
        restoreDepth()
        restoreBlend()
        restoreProgramAndBuffers()
        restoreFramebuffer()
    }

    private fun helperEnableOrDisable(cap: Int, enabled: Boolean) {
        if (enabled) glEnable(cap) else glDisable(cap)
    }
}
