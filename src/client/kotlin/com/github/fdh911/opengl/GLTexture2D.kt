package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*
import java.nio.ByteBuffer

class GLTexture2D private constructor()
{
    val id = glGenTextures()

    companion object {
        fun singleChannelImage(w: Int, h: Int, data: ByteBuffer): GLTexture2D {
            val tex = GLTexture2D()
            tex.bind()
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, w, h, 0, GL_RED, GL_UNSIGNED_BYTE, data)
            tex.unbind()
            return tex
        }

        fun rgbImage(w: Int, h: Int, data: ByteBuffer): GLTexture2D {
            val tex = GLTexture2D()
            tex.bind()
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, data)
            tex.unbind()
            return tex
        }

        fun rgbaImage(w: Int, h: Int, data: ByteBuffer): GLTexture2D {
            val tex = GLTexture2D()
            tex.bind()
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
            tex.unbind()
            return tex
        }

        fun colorAttachment(w: Int, h: Int): GLTexture2D {
            val tex = GLTexture2D()
            tex.bind()
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, null as ByteBuffer?)
            tex.unbind()
            return tex
        }

        fun depthStencilAttachment(w: Int, h: Int): GLTexture2D {
            val tex = GLTexture2D()
            tex.bind()
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, w, h, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, null as ByteBuffer?)
            tex.unbind()
            return tex
        }
    }

    fun bindToTexSlot(texSlot: Int) {
        glActiveTexture(GL_TEXTURE0 + texSlot)
        glBindTexture(GL_TEXTURE_2D, id)
    }
    fun bind() { bindToTexSlot(0) }
    fun unbind() { glBindTexture(GL_TEXTURE_2D, 0) }
    fun delete() { glDeleteTextures(id) }
}