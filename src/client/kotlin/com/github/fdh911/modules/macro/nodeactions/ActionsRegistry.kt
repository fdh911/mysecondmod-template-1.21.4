package com.github.fdh911.modules.macro.nodeactions

object ActionsRegistry {
    data class Entry<T: NodeAction>(val name: String, val provider: () -> T)

    private val entriesMutable = mutableListOf<Entry<*>>()

    val entries: List<Entry<*>>
        get() = entriesMutable

    fun <T: NodeAction> register(name: String, provider: () -> T) {
        entriesMutable.add(Entry(name, provider))
    }

    init {
        register("Keybind") { NodeActionKey() }
        register("Rotation") { NodeActionRotate() }
        register("Mouselock") { NodeActionMouselock() }
        register("Chat message") { NodeActionSendMessage() }
        register("Move cursor") { NodeActionMoveCursor() }
        register("Pause") { NodeActionWait() }
    }
}