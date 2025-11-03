package com.github.fdh911.render

import com.github.fdh911.render.opengl.GLState2
import imgui.ImFont
import imgui.ImGui
import imgui.ImVec4
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiWindowFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.io.File

object UserInterface {
    init {
        ImGui.createContext()
    }
    private val io = ImGui.getIO().apply {
        iniFilename = null
        logFilename = null
    }
    private val glfwImpl = ImGuiImplGlfw().apply {
        val windowHandle = MinecraftClient.getInstance().window.handle
        init(windowHandle, true)
    }
    private val gl3Impl = ImGuiImplGl3().apply {
        init("#version 150")
    }

    private val smallFont: ImFont
    private val largeFont: ImFont

    init {
        val classpathSmall = UserInterface::class.java.getResourceAsStream("/fonts/0xProtoNerdFont-Regular.ttf")
            ?: throw RuntimeException("Could not open small font file")
        val tempfileSmall = File.createTempFile("imgui", "smallfont")
        tempfileSmall.writeBytes(classpathSmall.readAllBytes())
        smallFont = io.fonts.addFontFromFileTTF(tempfileSmall.absolutePath, 20.0f)
        tempfileSmall.delete()

        val classpathLarge = UserInterface::class.java.getResourceAsStream("/fonts/0xProtoNerdFont-Bold.ttf")
            ?: throw RuntimeException("Could not open large font file")
        val tempfileLarge = File.createTempFile("imgui", "largefont")
        tempfileLarge.writeBytes(classpathLarge.readAllBytes())
        largeFont = io.fonts.addFontFromFileTTF(tempfileLarge.absolutePath, 28.0f)
        tempfileLarge.delete()

        io.fonts.build()

        val colors = arrayOf(
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
        gl3Impl.newFrame()
        glfwImpl.newFrame()
        ImGui.newFrame()

        val state = GLState2().apply {
            saveProgramAndBuffers()
            saveRaster()
            saveDepth()
            saveBlend()
        }
        OverlayRender.renderWithProgram(OverlayRender.sheetProgram)
        state.apply {
            restoreBlend()
            restoreDepth()
            restoreRaster()
            restoreProgramAndBuffers()
        }

        block()

        ImGui.render()
        gl3Impl.renderDrawData(ImGui.getDrawData())
    }

    private var windowStkCount = 0

    fun newWindow(name: String, block: () -> Unit) {
        if(windowStkCount > 0) {
            val pos = ImGui.getWindowPos()
            ImGui.setNextWindowPos(pos.x + 10.0f, pos.y + 10.0f, ImGuiCond.FirstUseEver)
        }

        windowStkCount++

        ImGui.pushFont(largeFont)
        val bordering = 2.0f * ImGui.getStyle().windowPaddingX
        val arrowWidth = ImGui.getFontSize() + 2.0f * ImGui.getStyle().framePaddingX
        val titleWidth = ImGui.calcTextSize(name).x
        val minWindowWidth = titleWidth + arrowWidth + bordering
        ImGui.setNextWindowSizeConstraints(minWindowWidth, 0.0f, Float.MAX_VALUE, Float.MAX_VALUE)
        ImGui.begin(name, ImGuiWindowFlags.NoResize)
        ImGui.popFont()

        ImGui.pushFont(smallFont)
        block()
        ImGui.popFont()

        ImGui.end()
        windowStkCount--
    }

    object MCScreen : Screen(Text.empty()) {
        private val renderEvents = mutableListOf<() -> Unit>()

        fun onRender(action: () -> Unit) = renderEvents.add(action)

        override fun init() {
            super.init()
            io.appAcceptingEvents = true
            io.clearEventsQueue()
        }

        override fun removed() {
            super.removed()
            io.appAcceptingEvents = false
            io.clearEventsQueue()
        }

        override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
            for(action in renderEvents)
                action()
        }

        override fun shouldPause(): Boolean = false
    }
}