package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

object ModuleList {
    private val modules = listOf(
        ModuleGardenMacro,
        ModuleEntityScanner,
        ModuleNoPause,
    )

    fun update() {
        for(module in modules)
            if(module.toggled)
                module.onUpdate()
    }

    fun renderUpdate(ctx: WorldRenderContext) {
        for(module in modules)
            if(module.toggled)
                module.onRenderUpdate(ctx)
    }

    val window = UIWindow("Modules") {
        for(module in modules)
            if(ImGui.selectable(module.name))
                + module.getWindow()
    }
}