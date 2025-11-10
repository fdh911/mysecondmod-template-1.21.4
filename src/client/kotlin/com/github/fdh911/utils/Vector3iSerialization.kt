package com.github.fdh911.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Vector3i

@Serializable
@SerialName("Vector3i")
private class Vector3iSurrogate(val x: Int, val y: Int, val z: Int)

object Vector3iSerializer: KSerializer<Vector3i>
{
    override val descriptor = SerialDescriptor("org.joml.Vector3i", Vector3iSurrogate.serializer().descriptor)

    override fun serialize(encoder: Encoder, value: Vector3i) {
        val surrogate = Vector3iSurrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(Vector3iSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Vector3i {
        val surrogate = decoder.decodeSerializableValue(Vector3iSurrogate.serializer())
        return Vector3i(surrogate.x, surrogate.y, surrogate.z)
    }
}