package com.github.fdh911.modules

import com.github.fdh911.render.CuboidRenderer
import com.github.fdh911.ui.UIWindow
import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import org.joml.Vector3f
import org.joml.Vector4f

object ModuleEntityScanner : Module("Entity Scanner") {
    private val savedEntityList = mutableListOf<Pair<Int, Entity>>()

    override fun update() {
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

    private val closeColor = Vector4f(1.0f, 0.0f, 0.0f, 0.4f)
    private val farColor = Vector4f(0.0f, 0.0f, 1.0f, 0.4f)

    override fun renderUpdate(ctx: WorldRenderContext) {
        for((dist, entity) in savedEntityList) {
            val aabb = entity.boundingBox
            val delta = entity.interpolatedPos() - entity.pos.toVector3f()
            val lerp = (dist - lowerBound[0]).toFloat() / (upperBound[0] - lowerBound[0] + 1)
            val color = Vector4f(closeColor)
                .add(Vector4f(farColor).sub(closeColor).mul(lerp))

            CuboidRenderer.render(
                ctx,
                aabb.minPos.toVector3f() + delta,
                aabb.maxPos.toVector3f() - aabb.minPos.toVector3f(),
                color
            )
        }
    }

    private val limitRadius = ImBoolean(false)

    private var individualEntity: Entity? = null

    private val lowerBound = intArrayOf(1)
    private val upperBound = intArrayOf(64)

    private val regexList = mutableListOf<Regex>()
    private val textModifiersRegex = Regex("\u00A7.")
    private val regexText = ImString()
    private var selectedRegex = 0

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

    val detectedEntitiesWindow = UIWindow("Detected entities") {
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

    private fun Entity.interpolatedPos(): Vector3f {
        val partialTick = MinecraftClient.getInstance().renderTickCounter!!.getTickDelta(true)
        return Vector3f(
            (prevX + (x - prevX) * partialTick).toFloat(),
            (prevY + (y - prevY) * partialTick).toFloat(),
            (prevZ + (z - prevZ) * partialTick).toFloat(),
        )
    }

    private operator fun Vector3f.plus(other: Vector3f): Vector3f = add(other)
    private operator fun Vector3f.minus(other: Vector3f): Vector3f = sub(other)
}