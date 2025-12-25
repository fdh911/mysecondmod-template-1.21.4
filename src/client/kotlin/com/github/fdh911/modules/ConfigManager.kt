package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImString
import java.io.File

object ConfigManager {
    var activeConfig = ModuleList()

    val window = UIWindow("Config") {
        if(ImGui.button("Load config"))
            + loadConfigWindow
        if(ImGui.button("Save config"))
            + saveConfigWindow
    }

    private val loadConfigWindow = UIWindow("Load config") {
        val files = File(".").listFiles().filter { ".*[.]msmconfig$".toRegex().matchEntire(it.name) != null }

        if(ImGui.beginListBox("##_1")) {
            for(file in files) {
                if(ImGui.button(file.name)) {
                    activeConfig = ModuleList.load(file)
                    closeThisWindow()
                }
            }
            ImGui.endListBox()
        }
    }

    private val configNameImString = ImString()

    private val saveConfigWindow = UIWindow("Save config") {
        ImGui.separatorText("Name your config")
        ImGui.inputText("##_1", configNameImString)
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.button("Save")) {
            activeConfig.save(configNameImString.get())
            closeThisWindow()
        }
    }
}