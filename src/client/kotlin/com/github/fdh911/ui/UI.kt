package com.github.fdh911.ui

import com.github.fdh911.render.OverlayRender
import com.github.fdh911.render.Unicodes
import com.github.fdh911.render.opengl.GLDebug
import com.github.fdh911.render.opengl.GLState2
import imgui.*
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.opengl.GL33.*

object UI
{
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
        init("#version 330 core")
    }

    val smallFont: ImFont
    val largeFont: ImFont

    init {
        fun loadMergedFont(baseFont: String, iconsFont: String, size: Float, range: ShortArray): ImFont {
            val cfg = ImFontConfig()
            val baseIS = object{}::class.java.getResourceAsStream("/fonts/$baseFont.ttf")
                ?: throw RuntimeException("Could not open base font file $baseFont")
            val iconsIS = object{}::class.java.getResourceAsStream("/fonts/$iconsFont.ttf")
                ?: throw RuntimeException("Could not open icons font file $iconsFont")
            val font = io.fonts.addFontFromMemoryTTF(baseIS.readAllBytes(), size, cfg, range)
            cfg.mergeMode = true
            io.fonts.addFontFromMemoryTTF(iconsIS.readAllBytes(), size, cfg, range)
            baseIS.close()
            iconsIS.close()
            return font
        }

        val customRange = with(ImFontGlyphRangesBuilder()) {
            addRanges(io.fonts.glyphRangesDefault)
            addText(Unicodes.Companion.toString())
            buildRanges()
        }

        smallFont = loadMergedFont("0xProtoNerdFont-Regular", "fa-regular-400", 20.0f, customRange)
        largeFont = loadMergedFont("0xProtoNerdFont-Bold", "fa-solid-900", 28.0f, customRange)

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

        // val state = GLState2().apply { saveAll() }

        OverlayRender.renderWithProgram(OverlayRender.sheetProgram)

        block()

        ImGui.render()

        gl3Impl.renderDrawData(ImGui.getDrawData())

        // state.restoreAll()
    }

    object MCScreen : Screen(Text.empty()) {
        private val renderEvents = mutableListOf<() -> Unit>()

        fun addRenderAction(action: () -> Unit) = renderEvents.add(action)

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
            super.render(context, mouseX, mouseY, delta)

            render {
                for(action in renderEvents) action()
            }
        }

        override fun shouldPause(): Boolean = false
    }
}