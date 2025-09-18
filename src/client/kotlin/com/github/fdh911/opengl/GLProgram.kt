package com.github.fdh911.opengl

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*
import org.lwjgl.system.MemoryStack
import java.io.FileNotFoundException

class GLProgram(
    vertPath: String,
    fragPath: String
) {
    private val id: Int

    companion object {
        fun fromClasspath(vertName: String, fragName: String = vertName): GLProgram = GLProgram(
            "/shaders/$vertName.vert",
            "/shaders/$fragName.frag",
        )
    }

    init {
        val vertShader = glCreateShader(GL_VERTEX_SHADER)
        val vertSource = readShaderFromClasspath(vertPath)
        glShaderSource(vertShader, vertSource)
        glCompileShader(vertShader)
        if(glGetShaderi(vertShader, GL_COMPILE_STATUS) != GL_TRUE)
            shaderError(vertShader, vertPath, "vertex")

        val fragShader = glCreateShader(GL_FRAGMENT_SHADER)
        val fragSource = readShaderFromClasspath(fragPath)
        glShaderSource(fragShader, fragSource)
        glCompileShader(fragShader)
        if(glGetShaderi(fragShader, GL_COMPILE_STATUS) != GL_TRUE)
            shaderError(fragShader, fragPath, "fragment")

        id = glCreateProgram()
        glAttachShader(id, vertShader)
        glAttachShader(id, fragShader)
        glLinkProgram(id)
        glDeleteShader(vertShader)
        glDeleteShader(fragShader)
    }

    fun bind() { glUseProgram(id) }
    fun unbind() { glUseProgram(0) }

    fun setInt(name: String, value: Int) {
        val uniLocation = glGetUniformLocation(id, name)
        glUniform1i(uniLocation, value)
    }

    fun setFloat(name: String, value: Float) {
        val uniLocation = glGetUniformLocation(id, name)
        glUniform1f(uniLocation, value)
    }

    fun setVec2(name: String, value: Vector2f) {
        val uniLocation = glGetUniformLocation(id, name)
        glUniform2f(uniLocation, value.x, value.y)
    }

    fun setVec3(name: String, value: Vector3f) {
        val uniLocation = glGetUniformLocation(id, name)
        glUniform3f(uniLocation, value.x, value.y, value.z)
    }

    fun setVec4(name: String, value: Vector4f) {
        val uniLocation = glGetUniformLocation(id, name)
        glUniform4f(uniLocation, value.x, value.y, value.z, value.w)
    }

    fun setMat4(name: String, value: Matrix4f) {
        val uniLocation = glGetUniformLocation(id, name)
        val stk = MemoryStack.stackPush()
        try {
            val buf = stk.mallocFloat(16)
            value.get(buf)
            glUniformMatrix4fv(uniLocation, false, buf)
        } finally {
            stk.pop()
        }
    }

    private fun readShaderFromClasspath(classPath: String): String {
        val inputStream = GLProgram::class.java.getResourceAsStream(classPath)
        val bytes = inputStream?.readBytes() ?: throw FileNotFoundException(classPath)
        inputStream.close()
        return bytes.toString(Charsets.UTF_8)
    }

    private fun shaderError(shaderId: Int, shaderPath: String, shaderType: String) {
        val infoLog = glGetShaderInfoLog(shaderId, 1024)
        println("Failed to generate $shaderType shader with source at $shaderPath\n: $infoLog")
    }
}