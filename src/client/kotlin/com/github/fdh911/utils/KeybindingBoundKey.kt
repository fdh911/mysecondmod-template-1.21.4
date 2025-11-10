package com.github.fdh911.utils

import com.github.fdh911.mixin.client.KeybindingAccessor
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

val KeyBinding.boundKey: InputUtil.Key
    get() = (this as KeybindingAccessor).mysecondmod_getBoundKey()