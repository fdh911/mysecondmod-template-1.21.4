package com.github.fdh911.modules.macro

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class NodeScene(var name: String)
{
    companion object {
        val json = Json {
            classDiscriminator = "ACTIONTYPE"
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