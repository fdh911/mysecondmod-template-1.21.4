package com.github.fdh911.modules.macro.controls

import com.github.fdh911.modules.macro.nodeactions.NodeAction
import com.github.fdh911.utils.Chat
import com.github.fdh911.utils.clientScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.util.LinkedList
import java.util.Queue

object ActionQueue
{
    var debugMode = false

    private val q: Queue<NodeAction> = LinkedList()

    private var job: Job? = null

    fun start() {
        LogManager.getLogger().info("Started action queue job")
        q.clear()
        job = clientScope.launch { loop() }
    }

    fun stop() {
        q.clear()
        job?.cancel()
        job = null
        LogManager.getLogger().info("Ended action queue job")
    }

    operator fun plusAssign(action: NodeAction) {
        if(job == null) return

        if(debugMode)
            Chat.message("Queuing action: \"$action\"")
        q.add(action)
    }

    operator fun plusAssign(actions: Collection<NodeAction>) {
        if(job == null) return

        if(debugMode)
            Chat.message("Queuing ${actions.size} actions: " + actions.joinToString { "\"$it\"" })
        q.addAll(actions)
    }

    fun clear() {
        if(job == null) return

        if(debugMode)
            Chat.message("Clearing action queue")
        q.clear()
    }

    private suspend fun loop() {
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