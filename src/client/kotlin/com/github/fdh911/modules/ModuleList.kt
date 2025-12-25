package com.github.fdh911.modules

import com.github.fdh911.render.Unicodes
import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import java.io.File

@Serializable
class ModuleList
{
    companion object {
        private val json = Json {
            classDiscriminator = "MODULETYPE"
            encodeDefaults = true
            prettyPrint = true
        }

        fun load(file: File) = json.decodeFromString<ModuleList>(file.readText())
    }

    @SerialName("modules") private val modules = listOf(
        ModuleGardenMacro(),
        ModuleEntityScanner(),
        ModuleNoPause(),
    )

    @Transient val window = UIWindow("Modules") {
        for(module in modules) {
            val title = "${if(module.toggled) Unicodes.CHECKBOX_1 else Unicodes.CHECKBOX_0} ${module.name}"
            if(ImGui.selectable(title))
                + module.getWindow()
        }
    }

    fun save(name: String) {
        val file = File("$name.msmconfig")
        if(!file.exists())
            file.createNewFile()

        file.writeText(json.encodeToString(this))
    }

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
}