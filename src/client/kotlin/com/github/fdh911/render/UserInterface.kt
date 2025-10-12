package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLState2
import imgui.ImGui
import imgui.flag.ImGuiCond
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
    private val imguiIO = ImGui.getIO().apply {
        iniFilename = null
        logFilename = null
    }
    private val fonts = imguiIO.fonts.apply {
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

    private var windowStkCount = 0

    fun newWindow(name: String, block: () -> Unit) {
        if(windowStkCount > 0) {
            val pos = ImGui.getWindowPos()
            val size = ImGui.getWindowSize()
            ImGui.setNextWindowPos(pos.x + size.x, pos.y, ImGuiCond.FirstUseEver)
        }

        windowStkCount++
        ImGui.begin(name)

        block()

        ImGui.end()
        windowStkCount--
    }

    object MCScreen : Screen(Text.empty()) {
        private val renderEvents = mutableListOf<() -> Unit>()

        fun onRender(action: () -> Unit) = renderEvents.add(action)

        override fun init() {
            super.init()
            imguiIO.appAcceptingEvents = true
            imguiIO.clearEventsQueue()
        }

        override fun removed() {
            super.removed()
            imguiIO.appAcceptingEvents = false
            imguiIO.clearEventsQueue()
        }

        override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
            for(action in renderEvents)
                action()
        }

        override fun shouldPause(): Boolean = false
    }
}