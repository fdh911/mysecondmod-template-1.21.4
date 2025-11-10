package com.github.fdh911.modules.macro

import com.github.fdh911.modules.macro.nodeactions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.io.File

@Serializable
class NodeScene(var name: String)
{
    companion object {
        val serializerModule = SerializersModule {
            polymorphic(NodeAction::class) {
                subclass(NodeActionKey::class, NodeActionKey.serializer())
                subclass(NodeActionMouselock::class, NodeActionMouselock.serializer())
                subclass(NodeActionRotate::class, NodeActionRotate.serializer())
                subclass(NodeActionSendMessage::class, NodeActionSendMessage.serializer())
                subclass(NodeActionWait::class, NodeActionWait.serializer())
                subclass(NodeActionMoveCursor::class, NodeActionMoveCursor.serializer())
            }
        }

        val json = Json {
            classDiscriminator = "ACTIONTYPE"
            serializersModule = serializerModule
            prettyPrint = true
            encodeDefaults = true
        }

        fun loadFromFile(file: File): NodeScene {
            val objectJson = file.readText()
            return json.decodeFromString<NodeScene>(objectJson)
        }
    }

    val nodeList = mutableListOf<Node>()

    fun saveToFile() {
        val file = File("$name.msmscene.json")
        if(file.exists()) {
            file.delete()
            file.createNewFile()
        }
        file.writeText(json.encodeToString(serializer(), this))
    }
}