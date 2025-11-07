package com.github.fdh911.modules.macro.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.util.math.BlockPos

@Serializable
@SerialName("BlockPos")
private class BlockPosSurrogate(val x: Int, val y: Int, val z: Int)

object BlockPosSerializer: KSerializer<BlockPos>
{
    override val descriptor = SerialDescriptor("net.minecraft.util.math.BlockPos", BlockPosSurrogate.serializer().descriptor)

    override fun serialize(encoder: Encoder, value: BlockPos) {
        val surrogate = BlockPosSurrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(BlockPosSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): BlockPos {
        val surrogate = decoder.decodeSerializableValue(BlockPosSurrogate.serializer())
        return BlockPos(surrogate.x, surrogate.y, surrogate.z)
    }
}