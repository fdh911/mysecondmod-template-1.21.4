package com.github.fdh911.io

import com.github.fdh911.opengl.GLState2
import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

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
        val state = GLState2().apply { saveAll() }

        imGuiGl3.newFrame()
        imGuiGlfw.newFrame()
        ImGui.newFrame()

        block()

        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())

        state.restoreAll()
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