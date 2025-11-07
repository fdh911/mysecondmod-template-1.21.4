package com.github.fdh911.modules

import com.github.fdh911.render.UserInterface
import com.github.fdh911.state.SkyblockState
import imgui.ImGui
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

object ModuleList {
    private val modules = listOf(
        ModuleGardenMacro,
        ModuleEntityScanner,
        ModuleNoPause,
        SkyblockState,
    )

    private val toDisplay = mutableSetOf<Module>()

    fun update() {
        for(module in modules)
            if(module.toggled)
                module.update()
    }

    fun renderUpdate(ctx: WorldRenderContext) {
        for(module in modules)
            if(module.toggled)
                module.renderUpdate(ctx)
    }

    fun renderUI() = UserInterface.newWindow("Modules") {
        for(module in modules)
            if(ImGui.selectable(module.name)) {
                if(toDisplay.contains(module))
                    toDisplay.remove(module)
                else
                    toDisplay.add(module)
            }
        for(module in toDisplay) {
            UserInterface.newWindow(module.name) {
                module.renderToggleUI()
                module.renderUI()
            }
        }
    }
}