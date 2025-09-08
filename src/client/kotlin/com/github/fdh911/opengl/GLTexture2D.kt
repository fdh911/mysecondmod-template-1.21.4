package com.github.fdh911.opengl

import org.lwjgl.opengl.GL45.*
import java.nio.ByteBuffer

class GLTexture2D(data: ByteBuffer, w: Int, h: Int, format: Formats)
{
    enum class Formats(val code: Int) {
        RED(GL_RED),
        RGB(GL_RGB),
        RGBA(GL_RGBA)
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
        glTexImage2D(GL_TEXTURE_2D, 0, format.code, w, h, 0, format.code, GL_UNSIGNED_BYTE, data)
        unbind()
    }
}