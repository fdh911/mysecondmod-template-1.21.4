package com.github.fdh911.skyblock

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.Team
import net.minecraft.text.Text

object ScoreboardReader {
    val scoreboard: String?
        get() {
            val scoreboard = MinecraftClient.getInstance().world?.scoreboard
                ?: return null

            val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR)
                ?: return null

            val title = objective.displayName.string

            val contents = StringBuilder()
            contents.append(title)

            val entries = scoreboard.getScoreboardEntries(objective)

            for(entry in entries) {
                val owner = entry.owner()
                val team = scoreboard.getScoreHolderTeam(owner)
                val decorated = (if(team != null)
                    Team.decorateName(team, Text.of(owner))
                else
                    Text.of(owner)).string.trimEnd()
                contents.append(decorated)
            }

            return contents.toString().replace("\u00A7.".toRegex(), "")
        }
}