package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.utils.mc
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import org.joml.Matrix4f
import org.lwjgl.opengl.GL32.*

object TranslucentUtils {
    fun getProjView(ctx: WorldRenderContext): Matrix4f {
        val cam = mc.gameRenderer.camera
        val proj = mc.gameRenderer.getBasicProjectionMatrix(mc.options.fov.value.toFloat())
        val view = Matrix4f(ctx.matrices().peek().positionMatrix)
            .translate(cam.cameraPos.toVector3f().negate())
        return Matrix4f(proj).mul(view)
    }

    fun saveAndSetupState(): GLState2 {
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
        glEnable(GL_CULL_FACE)
        glFrontFace(GL_CCW)
        glCullFace(GL_BACK)

        return stateSave
    }

    fun restoreState(state: GLState2) {
        state.apply {
            restoreProgramAndBuffers()
            restoreRaster()
            restoreDepth()
            restoreBlend()
        }
    }
}