package com.github.fdh911.modules.macro.controls

import com.github.fdh911.modules.macro.nodeactions.NodeAction
import com.github.fdh911.utils.Chat
import com.github.fdh911.utils.clientScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.MinecraftClient
import java.util.LinkedList
import java.util.Queue

object ActionQueue
{
    var debugMode = true

    private val q: Queue<NodeAction> = LinkedList()

    init {
        clientScope.launch {
            while(true) {
                if(q.isEmpty()) {
                    delay(1L)
                    continue
                }
                val action = q.remove()
                if(debugMode)
                    Chat.message("Starting action: \"$action\"")
                action.execute()
                if(debugMode)
                    Chat.message("Finished action: \"$action\"")
            }
        }
    }

    operator fun plusAssign(action: NodeAction) {
        if(debugMode)
            Chat.message("Queuing action: \"$action\"")
        q.add(action)
    }

    operator fun plusAssign(actions: Collection<NodeAction>) {
        if(debugMode)
            Chat.message("Queuing ${actions.size} actions: " + actions.joinToString { "\"$it\"" })
        q.addAll(actions)
    }

    fun clear() {
        if(debugMode)
            Chat.message("Clearing action queue")
        q.clear()
    }
}