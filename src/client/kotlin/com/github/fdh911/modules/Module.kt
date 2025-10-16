package com.github.fdh911.modules

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
    abstract fun renderUI()
    fun renderToggleUI() { ImGui.checkbox("Enabled?", imBooleanToggled) }
}