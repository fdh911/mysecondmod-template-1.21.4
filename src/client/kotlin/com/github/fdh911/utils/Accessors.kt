package com.github.fdh911.utils

import com.github.fdh911.mixin.client.KeybindingAccessor
import com.github.fdh911.mixin.client.MinecraftClientInvoker
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

val KeyBinding.boundKey: InputUtil.Key
    get() = (this as KeybindingAccessor).mysecondmod_getBoundKey()

fun MinecraftClient.handleBlockBreaking(b: Boolean) = (this as MinecraftClientInvoker).mysecondmod_handleBlockBreaking(b)