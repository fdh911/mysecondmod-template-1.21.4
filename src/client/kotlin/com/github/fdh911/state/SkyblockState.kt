package com.github.fdh911.state

import com.github.fdh911.modules.Module
import imgui.ImGui
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

object SkyblockState: Module("Skyblock State") {
    enum class Area {
        HUB,
        PRIVATE_ISLAND,
        GARDEN,
        UNKNOWN
    }

    private fun String?.toSkyblockArea() = when(this) {
        null            -> null
        "Hub"           -> Area.HUB
        "Private Island"-> Area.PRIVATE_ISLAND
        "Garden"        -> Area.GARDEN
        else            -> Area.UNKNOWN
    }

    object Garden {
        val isInGarden: Boolean
            get() = currentArea == Area.GARDEN

        var currentPlot: Int? = null
            private set

        var pestCount: Int? = null
            private set

        var isUsingRepellent: Boolean? = null
            private set

        fun update() {
            if(!isInGarden) {
                currentPlot = null
                pestCount = null
                isUsingRepellent = null
                return
            }

            val scoreboard = ScoreboardReader.scoreboard!!
            val tab = TabReader.tab!!

            currentPlot = ".*Plot - (?<plot>[0-9]+).*".toRegex().matchEntire(scoreboard)
                ?.groups
                ?.get("plot")
                ?.value
                ?.toInt()

            pestCount = tab.firstNotNullOfOrNull { line ->
                ".*Alive: (?<pestcount>[0-8]).*".toRegex().matchEntire(line)
                    ?.groups
                    ?.get("pestcount")
                    ?.value
                    ?.toInt()
            }

            isUsingRepellent = tab.any { line ->
                line.matches(".*Repellent: MAX.*".toRegex())
            }
        }
    }

    var isInSkyblock = false
        private set

    var isServerClosing: Boolean? = null
        private set

    var currentArea: Area? = null
        private set

    override fun update() {
        ScoreboardReader.update()
        val scoreboard = ScoreboardReader.scoreboard

        isInSkyblock = scoreboard?.run {
            contains("SKYBLOCK") && contains("www.hypixel.net")
        } ?: false

        if(!isInSkyblock) {
            isServerClosing = null
            currentArea = null
            return
        }

        TabReader.update()
        val tab = TabReader.tab

        isServerClosing = scoreboard?.contains("Server closing")

        currentArea = tab?.firstNotNullOfOrNull { line ->
            ".*Area: (?<area>.+)".toRegex().matchEntire(line)
                ?.groups
                ?.get("area")
                ?.value
        }.toSkyblockArea()

        Garden.update()
    }

    override fun renderUpdate(ctx: WorldRenderContext) { }

    override fun renderUI() {
        ImGui.separatorText("Basic")
        ImGui.text("Is in skyblock: $isInSkyblock")
        ImGui.text("Server closing: $isServerClosing")
        ImGui.text("Current area: $currentArea")
        ImGui.separatorText("Garden")
        ImGui.text("Plot: ${Garden.currentPlot}")
        ImGui.text("Pest count: ${Garden.pestCount}")
        ImGui.text("Repellent: ${Garden.isUsingRepellent}")
    }
}