package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLDebug
import com.github.fdh911.render.opengl.GLElementBuffer
import com.github.fdh911.render.opengl.GLFramebuffer
import com.github.fdh911.render.opengl.GLProgram
import com.github.fdh911.render.opengl.GLState2
import com.github.fdh911.render.opengl.GLVertexArray
import com.github.fdh911.render.opengl.GLVertexBuffer
import net.minecraft.client.MinecraftClient
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11

object HighlightRender {
//    private val program: GLProgram
//    private val fb: GLFramebuffer

    init {
//        val state = GLState2().apply { saveAll() }
//
//        program = GLProgram.fromClasspath("overlay", "entityhl").apply {
//            bind()
//            setInt("uFbColorAttachment", 0)
//            setVec4("uColor", Vector4f(1.0f, 0.0f, 0.0f, 1.0f))
//        }
//
//        fb = GLFramebuffer.withWindowSize()
//
//        state.restoreAll()
    }

//    private var renderState: GLState2? = null

    fun renderStart() {
//        GLDebug.marker("Highlight start")
//        renderState = GLState2().apply { saveAll() }
//
//        val ogFb = renderState!!.fboState!!.readFbo
//
//        fb.bind()
//        fb.clearAll()
//        fb.blit(ogFb, blitDepth = true)
    }

    fun renderEnd() {
//        renderState?.saveProgramAndBuffers()
//
//        program.apply {
//            val wnd = MinecraftClient.getInstance().window
//            bind()
//            setVec2("uScreenSize", Vector2f(wnd.width.toFloat(), wnd.height.toFloat()))
//        }
//
//        renderState?.restoreFramebuffer()
//
//        OverlayRender.renderWithProgram(fb.colorAttachment, program)
//
//        renderState?.restoreAll()
//        GLDebug.marker("Highlight end")
    }
}