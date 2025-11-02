package com.github.fdh911.skyblock

object SkyblockState {
    enum class Area {
        HUB,
        PRIVATE_ISLAND,
        GARDEN,
        UNKNOWN
    }

    var isInSkyblock = false
        private set

    var currentArea = Area.UNKNOWN
        private set

    private val areaSearchRegex = ".*Area: (?<area>.+)".toRegex()

    fun update() {
        val scoreboard = ScoreboardReader.scoreboard ?: return
        val tab = TabReader.tab ?: return

        isInSkyblock = scoreboard.contains("SKYBLOCK") && scoreboard.contains("www.hypixel.net")
        if(!isInSkyblock) return

        for(line in tab) {
            val areaMatch = areaSearchRegex.matchEntire(line)
                ?: continue
            val areaName = areaMatch.groups["area"]!!.value
            currentArea = when(areaName) {
                "The Garden"    -> Area.GARDEN
                "Private Island"-> Area.PRIVATE_ISLAND
                "Hub"           -> Area.HUB
                else            -> Area.UNKNOWN
            }
        }
    }
}