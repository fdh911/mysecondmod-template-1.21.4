package com.github.fdh911.utils

import com.github.fdh911.mixin.client.KeybindingAccessor
import com.github.fdh911.mixin.client.MinecraftClientInvoker
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d

val KeyBinding.extBoundKey: InputUtil.Key
    get() = (this as KeybindingAccessor).mysecondmod_getBoundKey()

fun MinecraftClient.extHandleBlockBreaking(b: Boolean) = (this as MinecraftClientInvoker).mysecondmod_handleBlockBreaking(b)

val Entity.extPos: Vec3d
    get() = Vec3d(x, y, z)