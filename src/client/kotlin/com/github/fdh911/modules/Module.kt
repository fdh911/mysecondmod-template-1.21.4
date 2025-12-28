package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext

@Serializable
sealed class Module(val name: String)
{
    @SerialName("toggled") var toggled = false
        set(value) {
            field = value
            imBooleanToggled.set(value)
            when(value) {
                true -> onEnable()
                false -> onDisable()
            }
        }

    @Transient protected val imBooleanToggled = ImBoolean()

    open fun onUpdate() { }

    open fun onEnable() { }
    open fun onDisable() { }

    open fun onRenderUpdate(ctx: WorldRenderContext) { }
    protected open fun UIWindow.setWindowContents() { }

    fun getWindow() = UIWindow(name) {
        imBooleanToggled.set(toggled)

        if(ImGui.checkbox("Enabled?", imBooleanToggled))
            toggled = imBooleanToggled.get()

        setWindowContents()
    }
}