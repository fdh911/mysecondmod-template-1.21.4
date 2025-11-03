package com.github.fdh911.state

import com.github.fdh911.utils.noModifiers
import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.Team
import net.minecraft.text.Text

object ScoreboardReader {
    var scoreboard: String? = null
        private set

    fun update() {
        scoreboard = null

        val sb = MinecraftClient.getInstance().world?.scoreboard
            ?: return

        val objective = sb.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR)
            ?: return

        val title = objective.displayName.string

        val contents = StringBuilder()
        contents.append(title)

        val entries = sb.getScoreboardEntries(objective)

        for(entry in entries) {
            val owner = entry.owner()
            val team = sb.getScoreHolderTeam(owner)
            val decorated = (if(team != null)
                Team.decorateName(team, Text.of(owner))
            else
                Text.of(owner)).string.trimEnd()
            contents.append(decorated)
        }

        scoreboard = contents.toString().noModifiers()
    }
}