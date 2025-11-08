package com.github.fdh911.ui

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean

open class UIWindow(
    val title: String,
    var parent: UIWindow? = null,
    val block: UIWindow.() -> Unit
) {
    private val children = mutableMapOf<String, UIWindow>()

    private val keepOpen = ImBoolean(true)

    private fun addWindow(child: UIWindow) {
        children[child.title] = child
    }

    private fun removeWindow(child: UIWindow) {
        if(children.containsKey(child.title))
            children.remove(child.title)
    }

    operator fun UIWindow.unaryPlus() {
        this@unaryPlus.parent = this@UIWindow
        this@UIWindow.addWindow(this@unaryPlus)
    }

    fun closeThisWindow() {
        parent?.removeWindow(this)
    }

    fun render(): Boolean {
        if(!keepOpen.get()) {
            keepOpen.set(true)
            return false
        }

        ImGui.pushFont(UI.largeFont)

        val bordering = 2.0f * ImGui.getStyle().windowPaddingX
        val arrowWidth = ImGui.getFontSize() + 2.0f * ImGui.getStyle().framePaddingX
        val titleWidth = ImGui.calcTextSize(title).x
        val minWindowWidth = titleWidth + arrowWidth + bordering + (if(parent != null) arrowWidth else 0.0f)
        ImGui.setNextWindowSizeConstraints(minWindowWidth, 0.0f, Float.MAX_VALUE, Float.MAX_VALUE)
        ImGui.setNextWindowSize(0.0f, 0.0f)

        val openOrNull = if(parent != null) keepOpen else null
        if(ImGui.begin(title, openOrNull, ImGuiWindowFlags.NoResize)) {
            ImGui.pushFont(UI.smallFont)
            this.block()
            ImGui.popFont()
        }

        val windowPosition = ImGui.getWindowPos()
        val childPosition = ImVec2(windowPosition.x + 15.0f, windowPosition.y + 15.0f)

        ImGui.popFont()
        ImGui.end()

        children
            .filter { (_, window) ->
                ImGui.setNextWindowPos(childPosition, ImGuiCond.FirstUseEver)
                !window.render()
            }
            .forEach { (title, _) ->
                children.remove(title)
            }

        return true
    }
}