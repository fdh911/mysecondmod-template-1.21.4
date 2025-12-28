package com.github.fdh911.modules

import com.github.fdh911.utils.mc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("no_pause")
class ModuleNoPause: Module("No Pause")
{
    override fun onEnable() { mc.options.pauseOnLostFocus = true }
    override fun onDisable() { mc.options.pauseOnLostFocus = false }
}