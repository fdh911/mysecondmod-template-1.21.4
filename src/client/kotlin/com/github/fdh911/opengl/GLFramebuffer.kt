package com.github.fdh911.opengl

import net.minecraft.client.MinecraftClient
import org.lwjgl.opengl.GL45.*

class GLFramebuffer(w: Int, h: Int)
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

    fun clearAll() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun resize(w: Int, h: Int) {
        colorAttachment.delete()
        colorAttachment = GLTexture2D.colorAttachment(w, h)
        depthStencilAttachment.delete()
        depthStencilAttachment = GLTexture2D.depthStencilAttachment(w, h)
        updateAttachments()
    }

    private fun updateAttachments() {
        bind()
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment.id, 0)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilAttachment.id, 0)
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw RuntimeException("Framebuffer $id is not complete!")
        unbind()
    }
}