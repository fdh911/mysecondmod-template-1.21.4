package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLState2
import imgui.ImGui
import imgui.ImVec4
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
    init {
        val colors = arrayOf<ImVec4>(
            ImVec4(1.00f, 1.00f, 1.00f, 1.00f),
            ImVec4(0.40f, 0.40f, 0.40f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 0.00f),
            ImVec4(0.08f, 0.08f, 0.08f, 0.94f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 0.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.20f, 0.20f, 0.20f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 1.00f),
            ImVec4(0.14f, 0.14f, 0.14f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 1.00f),
            ImVec4(0.31f, 0.31f, 0.31f, 1.00f),
            ImVec4(0.41f, 0.41f, 0.41f, 1.00f),
            ImVec4(0.51f, 0.51f, 0.51f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.20f, 0.20f, 0.20f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.20f, 0.20f, 0.20f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.20f, 0.20f, 0.20f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.20f, 0.20f, 0.20f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(1.00f, 1.00f, 1.00f, 1.00f),
            ImVec4(0.20f, 0.20f, 0.20f, 1.00f),
            ImVec4(0.10f, 0.10f, 0.10f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.07f, 0.10f, 0.15f, 0.97f),
            ImVec4(0.14f, 0.26f, 0.42f, 1.00f),
            ImVec4(0.50f, 0.50f, 0.50f, 0.00f),
            ImVec4(0.61f, 0.61f, 0.61f, 1.00f),
            ImVec4(1.00f, 0.43f, 0.35f, 1.00f),
            ImVec4(0.90f, 0.70f, 0.00f, 1.00f),
            ImVec4(1.00f, 0.60f, 0.00f, 1.00f),
            ImVec4(0.19f, 0.19f, 0.20f, 1.00f),
            ImVec4(0.31f, 0.31f, 0.35f, 1.00f),
            ImVec4(0.23f, 0.23f, 0.25f, 1.00f),
            ImVec4(0.00f, 0.00f, 0.00f, 0.00f),
            ImVec4(1.00f, 1.00f, 1.00f, 0.06f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(0.26f, 0.59f, 0.98f, 0.35f),
            ImVec4(0.43f, 0.43f, 0.50f, 0.50f),
            ImVec4(1.00f, 1.00f, 0.00f, 0.90f),
            ImVec4(0.26f, 0.59f, 0.98f, 1.00f),
            ImVec4(1.00f, 1.00f, 1.00f, 0.70f),
            ImVec4(0.80f, 0.80f, 0.80f, 0.20f),
            ImVec4(0.80f, 0.80f, 0.80f, 0.35f),
        )

        ImGui.getStyle().colors = colors
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