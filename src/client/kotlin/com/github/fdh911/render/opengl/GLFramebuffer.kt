package com.github.fdh911.render.opengl

import net.minecraft.client.MinecraftClient
import org.lwjgl.opengl.GL45.*

class GLFramebuffer(private var w: Int, private var h: Int)
{
    val id = glGenFramebuffers()
    var colorAttachment = GLTexture2D.colorAttachment(w, h)
        private set
    var depthStencilAttachment = GLTexture2D.depthStencilAttachment(w, h)
        private set

    init { updateAttachments() }

    companion object {
        fun withWindowSize(): GLFramebuffer = GLFramebuffer(
            MinecraftClient.getInstance().window.width,
            MinecraftClient.getInstance().window.height,
        )
    }

    fun bind() { glBindFramebuffer(GL_FRAMEBUFFER, id) }
    fun unbind() { glBindFramebuffer(GL_FRAMEBUFFER, 0) }

    fun blit(from: Int, blitColor: Boolean = false, blitDepth: Boolean = false, blitStencil: Boolean = false) {
        val mask =
            (if(blitColor) GL_COLOR_BUFFER_BIT else 0) or
            (if(blitDepth) GL_DEPTH_BUFFER_BIT else 0) or
            (if(blitStencil) GL_STENCIL_BUFFER_BIT else 0)
        glBlitNamedFramebuffer(from, id, 0, 0, w, h, 0, 0, w, h, mask, GL_NEAREST)
    }

    fun clearAll() {
        bind()
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun resize(newW: Int, newH: Int) {
        w = newW
        h = newH
        colorAttachment.delete()
        colorAttachment = GLTexture2D.colorAttachment(newW, newH)
        depthStencilAttachment.delete()
        depthStencilAttachment = GLTexture2D.depthStencilAttachment(newW, newH)
        updateAttachments()
    }

    private fun updateAttachments() {
        bind()
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment.id, 0)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilAttachment.id, 0)
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw RuntimeException("Framebuffer $id is not complete!")
    }
}