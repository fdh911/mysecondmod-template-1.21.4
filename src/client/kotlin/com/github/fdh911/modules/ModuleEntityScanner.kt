package com.github.fdh911.modules

import com.github.fdh911.ui.UIWindow
import com.github.fdh911.utils.extPos
import com.github.fdh911.utils.interpolatedPos
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.render.LayeringTransform
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.RenderSetup
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import org.joml.Vector3f

@Serializable
@SerialName("entity_scanner")
class ModuleEntityScanner : Module("Entity Scanner")
{
    @Transient private val savedEntityList = mutableListOf<Pair<Int, Entity>>()

    override fun onUpdate() {
        val player = MinecraftClient.getInstance().player
            ?: return

        val entityList = MinecraftClient.getInstance().world?.entities
            ?: return

        savedEntityList.clear()

        for(entity in entityList) {
            if(entity === player) continue

            val dist = entity.distanceTo(player).toInt()
            if(limitRadius.get())
                if(dist < lowerBound[0] || upperBound[0] < dist)
                    continue

            val nametag = entity.displayName
                ?.string
                ?.replace(textModifiersRegex, "")
                ?: "Unnamed"

            if(regexList.isNotEmpty()) {
                var matches = false
                for(re in regexList)
                    if(re.matches(nametag)) {
                        matches = true
                        break
                    }
                if(!matches) continue
            }

            savedEntityList.add(dist to entity)
        }

        savedEntityList.sortWith { p1, p2 ->
            p1.first - p2.first
        }
    }

    override fun onRenderUpdate(ctx: WorldRenderContext) {
        for((_, entity) in savedEntityList) {
            val consumer = ctx.consumers().getBuffer(renderLayer)

            val matrixStack = ctx.matrices()
            matrixStack.push()

            val cameraCorrect = ctx.gameRenderer().camera.cameraPos.negate()
            matrixStack.translate(cameraCorrect)

            val aabb = entity.boundingBox

            val delta = entity.interpolatedPos().subtract(entity.extPos)
            val boxCorner = aabb.minPos.add(delta)
            matrixStack.translate(boxCorner)

            val boxScale = aabb.maxPos.subtract(aabb.minPos).toVector3f()
            matrixStack.scale(boxScale.x, boxScale.y, boxScale.z)

            val mat = matrixStack.peek()

            makeBox(consumer, mat)

            matrixStack.pop()
        }
    }

    @Transient private val renderLayer = RenderLayer.of(
        "fdh911_esp",
        RenderSetup.builder(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                .withLocation("pipeline/debug_filled_box")
                .withCull(true)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build()
        ).build()
    )

    @Transient private val vertices = arrayOf(
        Vector3f(0.0f, 0.0f, 0.0f),
        Vector3f(1.0f, 0.0f, 0.0f),
        Vector3f(1.0f, 0.0f, 1.0f),
        Vector3f(0.0f, 0.0f, 1.0f),
        Vector3f(0.0f, 1.0f, 0.0f),
        Vector3f(1.0f, 1.0f, 0.0f),
        Vector3f(1.0f, 1.0f, 1.0f),
        Vector3f(0.0f, 1.0f, 1.0f)
    )

    @Transient private val faceIndices = intArrayOf(
        4, 0, 3, 7, // -X
        0, 1, 2, 3, // -Y
        0, 4, 5, 1, // -Z
        5, 6, 2, 1, // +X
        4, 7, 6, 5, // +Y
        6, 7, 3, 2, // +Z
    )

    private fun makeBox(consumer: VertexConsumer, mat: MatrixStack.Entry) {
        for(i in faceIndices)
            consumer.vertex(mat, vertices[i]).color(1.0f, 0.0f, 0.0f, 0.3f)
    }

    @Transient private val limitRadius = ImBoolean(false)

    @Transient private var individualEntity: Entity? = null

    @Transient private val lowerBound = intArrayOf(1)
    @Transient private val upperBound = intArrayOf(64)

    @Transient private val regexList = mutableListOf<Regex>()
    @Transient private val textModifiersRegex = Regex("\u00A7.")
    @Transient private val regexText = ImString()
    @Transient private var selectedRegex = 0

    override fun UIWindow.setWindowContents() {
        ImGui.separatorText("Radius")
        ImGui.checkbox("Limit the radius?", limitRadius)
        if(limitRadius.get()) {
            ImGui.sliderInt("Lower bound", lowerBound, 1, upperBound[0])
            ImGui.sliderInt("Upper bound", upperBound, lowerBound[0], 256)
        }

        ImGui.separatorText("Detected entities")
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.button("Show detected entities"))
            + detectedEntitiesWindow

        if(ImGui.collapsingHeader("Regex filtering")) {
            ImGui.text("Entity name should fit at least one regex:")
            ImGui.inputText("##_regex", regexText)
            if(ImGui.button("Add regex")) {
                val s = regexText.get()
                if(s.isNotEmpty() && s.isNotBlank())
                    regexList += Regex(s)
            }
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##_regexList")) {
                for(i in regexList.indices) {
                    val re = regexList[i]
                    if(ImGui.selectable(re.toString()))
                        selectedRegex = i
                }
                ImGui.endListBox()
            }
            if(ImGui.button("Remove selected regex")) {
                if(selectedRegex in regexList.indices)
                    regexList.removeAt(selectedRegex)
            }
        }
    }

    @Transient val detectedEntitiesWindow = UIWindow("Detected entities") {
        ImGui.setNextItemWidth(-Float.MIN_VALUE)
        if(ImGui.beginListBox("##")) {
            for(i in savedEntityList.indices) {
                val (dist, entity) = savedEntityList[i]

                val name = entity
                    .displayName
                    ?.string
                    ?.replace(textModifiersRegex, "")
                    ?: "Unnamed"

                ImGui.pushID(i)
                if(ImGui.selectable("(${dist}m) $name"))
                    individualEntity = entity
                ImGui.popID()
            }

            ImGui.endListBox()
        }
    }

    private operator fun Vector3f.plus(other: Vector3f): Vector3f = add(other)
    private operator fun Vector3f.minus(other: Vector3f): Vector3f = sub(other)
}