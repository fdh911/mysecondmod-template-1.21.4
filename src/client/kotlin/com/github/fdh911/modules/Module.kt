package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImBoolean
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

sealed class Module(val name: String) {
    var toggled: Boolean
        get() = imBooleanToggled.get()
        set(value) {
            imBooleanToggled.set(value)
            when(imBooleanToggled.get()) {
                true -> onEnable()
                false -> onDisable()
            }
        }

    protected val imBooleanToggled = ImBoolean(false)

    abstract fun onUpdate()

    open fun onEnable() { }
    open fun onDisable() { }

    open fun onRenderUpdate(ctx: WorldRenderContext) { }
    protected open fun UIWindow.setWindowContents() { }

    fun getWindow() = UIWindow(name) {
        ImGui.checkbox("Enabled?", imBooleanToggled)
        setWindowContents()
    }
}