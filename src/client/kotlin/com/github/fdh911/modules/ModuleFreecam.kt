package com.github.fdh911.modules

import com.github.fdh911.utils.mc
import com.github.fdh911.utils.extPos
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.decoration.ArmorStandEntity

@Serializable
@SerialName("freecam")
class ModuleFreecam: Module("Freecam")
{
    @Transient private var dummy: ArmorStandEntity? = null

    override fun onEnable() {
        val world = mc.world!!
        val pos = mc.player!!.extPos

        dummy = ArmorStandEntity(world, pos.x, pos.y, pos.z).apply {
            isInvisible = true
            isInvulnerable = true
            noClip = true
        }
    }

    override fun onDisable() {
        dummy = null
    }

    override fun onUpdate() {
        mc.cameraEntity = dummy
    }
}