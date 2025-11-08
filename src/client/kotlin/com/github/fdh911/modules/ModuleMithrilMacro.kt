package com.github.fdh911.modules

import kotlinx.coroutines.*
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.*
import net.minecraft.world.World
import net.minecraft.world.RaycastContext
import kotlin.math.*
import kotlin.random.Random

@OptIn(DelicateCoroutinesApi::class)
object ModuleMithrilMacro : Module("Mithril Macro") {

    private val mc = MinecraftClient.getInstance()
    private var running = false

    private val targetBlocks = setOf(
        "block.minecraft.light_blue_wool",
        "block.minecraft.prismarine",
        "block.minecraft.dark_prismarine",
        "block.minecraft.prismarine_bricks",
        "block.minecraft.gray_wool",
        "block.minecraft.gray_concrete"
    )

    private const val REACH_DISTANCE = 4.5

    override fun update() {
        if (!toggled) {
            running = false
            return
        }
        if (running) return
        running = true

        GlobalScope.launch {
            val player = mc.player ?: return@launch
            val world = mc.world ?: return@launch

            val hit = mc.crosshairTarget
            if (hit !is BlockHitResult) {
                running = false
                return@launch
            }

            val startPos = hit.blockPos
            val block = world.getBlockState(startPos).block
            if (block.translationKey !in targetBlocks) {
                running = false
                return@launch
            }

            mineBlock(world, player, startPos)

            val nearby = scanAround(world, startPos, 5) { pos ->
                val b = world.getBlockState(pos).block
                targetBlocks.contains(b.translationKey)
            }

            for (pos in nearby) {
                if (!canReach(player, pos)) continue
                mineBlock(world, player, pos)
                delay(randomDelay(100L, 250L))
            }

            running = false
        }
    }

    private suspend fun mineBlock(world: World, player: ClientPlayerEntity, pos: BlockPos) {
        if (!canReach(player, pos)) return

        smoothLookAtBlock(player, pos)
        delay(randomDelay(30L, 80L))

        val interaction = mc.interactionManager ?: return
        player.swingHand(Hand.MAIN_HAND)
        interaction.attackBlock(pos, Direction.UP)

        repeat(60) {
            delay(50L)
            interaction.updateBlockBreakingProgress(pos, Direction.UP)
            val block = world.getBlockState(pos).block
            if (block.translationKey == "block.minecraft.air") return
        }
    }

    private suspend fun smoothLookAtBlock(player: ClientPlayerEntity, pos: BlockPos) {
        val eye = player.eyePos
        val randOffset = Vec3d(
            Random.nextDouble(-0.3, 0.3),
            Random.nextDouble(-0.3, 0.3),
            Random.nextDouble(-0.3, 0.3)
        )
        val target = Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5).add(randOffset)
        val diff = target.subtract(eye)
        val distXZ = sqrt(diff.x * diff.x + diff.z * diff.z)

        var targetYaw = (atan2(diff.z, diff.x) * 180.0 / Math.PI - 90.0).toFloat()
        val targetPitch = (-atan2(diff.y, distXZ) * 180.0 / Math.PI).toFloat().coerceIn(-90f, 90f)

        var deltaYaw = targetYaw - player.yaw
        deltaYaw = (deltaYaw + 540f) % 360f - 180f

        val deltaPitch = targetPitch - player.pitch

        val steps = Random.nextInt(6, 12)
        val yawStep = deltaYaw / steps
        val pitchStep = deltaPitch / steps

        repeat(steps) {
            player.yaw += yawStep
            player.pitch += pitchStep
            delay(randomDelay(10L, 25L))
        }
    }

    private fun canReach(player: ClientPlayerEntity, pos: BlockPos): Boolean {
        val eye = player.eyePos
        val target = Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
        if (eye.distanceTo(target) > REACH_DISTANCE) return false

        val world = player.world
        val context = RaycastContext(
            eye,
            target,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        )
        val hit = world.raycast(context)
        return hit.type == HitResult.Type.BLOCK && (hit as BlockHitResult).blockPos == pos
    }

    private fun scanAround(world: World, center: BlockPos, radius: Int, predicate: (BlockPos) -> Boolean): List<BlockPos> {
        val found = mutableListOf<BlockPos>()
        for (x in -radius..radius)
            for (y in -radius..radius)
                for (z in -radius..radius) {
                    val pos = center.add(x, y, z)
                    if (predicate(pos)) found.add(pos)
                }
        return found
    }

    private fun randomDelay(min: Long, max: Long): Long = Random.nextLong(min, max)

    override fun renderUpdate(ctx: WorldRenderContext) {}
    override fun renderUI() {}
}