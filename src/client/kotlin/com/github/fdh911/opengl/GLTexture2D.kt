package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*
import java.nio.ByteBuffer

class GLTexture2D(data: ByteBuffer?, w: Int, h: Int, format: Formats)
{
    enum class Formats(val internalformat: Int, val format: Int, val type: Int) {
        RED(GL_RED, GL_RED, GL_UNSIGNED_BYTE),
        RGB(GL_RGB, GL_RGB, GL_UNSIGNED_BYTE),
        RGBA(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
        DEPTH24_STENCIL8(GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8)
    }

    val id = glGenTextures()

    fun bindToTexSlot(texSlot: Int) {
        glActiveTexture(GL_TEXTURE0 + texSlot)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun bind() { bindToTexSlot(0) }
    fun unbind() { glBindTexture(GL_TEXTURE_2D, 0) }

    init {
        bind()
        if(format == Formats.RED) glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexImage2D(GL_TEXTURE_2D, 0, format.internalformat, w, h, 0, format.format, format.type, data)
        unbind()
    }
}