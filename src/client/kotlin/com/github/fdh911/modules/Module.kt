package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImBoolean
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

abstract class Module(val name: String) {
    var toggled: Boolean
        get() = imBooleanToggled.get()
        set(value) { imBooleanToggled.set(value) }

    protected val imBooleanToggled = ImBoolean(false)

    abstract fun update()
    abstract fun renderUpdate(ctx: WorldRenderContext)
    protected abstract fun UIWindow.setWindowContents()

    fun getWindow() = UIWindow(name) {
        ImGui.checkbox("Enabled?", imBooleanToggled)
        setWindowContents()
    }
}