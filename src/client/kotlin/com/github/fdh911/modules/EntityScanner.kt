package com.github.fdh911.modules

import com.github.fdh911.io.UserInterface
import com.github.fdh911.opengl.GLElementBuffer
import com.github.fdh911.opengl.GLProgram
import com.github.fdh911.opengl.GLState2
import com.github.fdh911.opengl.GLVertexArray
import com.github.fdh911.opengl.GLVertexBuffer
import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL45.*
import kotlin.math.pow

object EntityScanner {
    private val closeColor = Vector4f(1.0f, 0.0f, 0.0f, 0.4f)
    private val farColor = Vector4f(0.0f, 0.0f, 1.0f, 0.4f)

    private val enabled = ImBoolean(false)
    private val limitRadius = ImBoolean(false)
    private val saveEntities = ImBoolean(false)
    private val savedEntityList = mutableListOf<Pair<Int, Entity>>()

    private var individualEntity: Entity? = null

    private val lowerBound = intArrayOf(1)
    private val upperBound = intArrayOf(64)

    private val regexList = mutableListOf<Regex>()
    private val textModifiersRegex = Regex("\u00A7.")

    fun update(ctx: WorldRenderContext) {
        if(!enabled.get()) return

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
                ?.toString()
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

            if(saveEntities.get())
                savedEntityList.add(dist to entity)
        }

        if(saveEntities.get())
            savedEntityList.sortWith { p1, p2 ->
                p1.first - p2.first
            }
    }

    private val regexText = ImString()
    private var selectedRegex = 0

    fun renderUI() = UserInterface.render {
        ImGui.begin("Entity Searcher")

        ImGui.setWindowSize(0.0f, 0.0f)
        ImGui.checkbox("Enabled?", enabled)
        ImGui.checkbox("Limit the radius?", limitRadius)
        if(limitRadius.get()) {
            ImGui.sliderInt("Lower bound", lowerBound, 1, upperBound[0])
            ImGui.sliderInt("Upper bound", upperBound, lowerBound[0], 256)
        }
        ImGui.checkbox("List entities", saveEntities)
        if(saveEntities.get()) {
            ImGui.begin("Detected entities")
            ImGui.text("Detected entities")
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##")) {
                for((dist, entity) in savedEntityList) {
                    val name = entity.displayName?.string?.replace(textModifiersRegex, "") ?: "Unnamed"
                    if(ImGui.selectable("(${dist}m) $name"))
                        individualEntity = entity
                }
                ImGui.endListBox()
            }
            ImGui.end()
        }
        if(ImGui.collapsingHeader("Regex filtering")) {
            ImGui.text("Entity name should fit at least one regex:")
            ImGui.inputText("##", regexText)
            if(ImGui.button("Add regex")) {
                val s = regexText.get()
                if(s.isNotEmpty() && s.isNotBlank())
                    regexList += Regex(s)
            }
            ImGui.setNextItemWidth(-Float.MIN_VALUE)
            if(ImGui.beginListBox("##")) {
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
        if(individualEntity != null) with(individualEntity!!) {
            ImGui.begin("Individual Entity")
            ImGui.setWindowSize(0.0f, 0.0f)
            ImGui.text("Name: ${displayName?.string ?: "Unnamed"}")
            ImGui.text("Type: $type")
            ImGui.text("Position: ${x.round(1)} ${y.round(1)} ${z.round(1)}")
            if(this is LivingEntity) {
                ImGui.text("Health: $health")
                ImGui.text("Helmet: ${getEquippedStack(EquipmentSlot.HEAD)}")
                ImGui.text("Chestplate: ${getEquippedStack(EquipmentSlot.BODY)}")
                ImGui.text("Leggings: ${getEquippedStack(EquipmentSlot.LEGS)}")
                ImGui.text("Boots: ${getEquippedStack(EquipmentSlot.FEET)}")
            }
            ImGui.end()
        }

        ImGui.end()
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
    private fun Double.round(digits: Int): Double {
        val p = 10.0.pow(digits)
        return (this * p).toLong().toDouble() / p
    }

    private object CuboidRenderer {
        private val vertices = floatArrayOf(
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
        )

        private val indices = intArrayOf(
            0, 2, 1,
            0, 3, 2,
            4, 5, 6,
            4, 6, 7,
            3, 2, 6,
            3, 6, 7,
            0, 5, 1,
            0, 4, 5,
            0, 7, 3,
            0, 4, 7,
            1, 2, 6,
            1, 6, 5,
        )

        private val program: GLProgram
        private val vbo: GLVertexBuffer
        private val ebo: GLElementBuffer
        private val vao: GLVertexArray

        init {
            val state = GLState2().apply { saveAll() }

            program = GLProgram("/shaders/poscolor.vert", "/shaders/poscolor.frag").apply {
                bind()
            }

            vbo = GLVertexBuffer().apply {
                bind()
                setData(vertices, GLVertexBuffer.Usage.STATIC)
            }

            ebo = GLElementBuffer().apply {
                bind()
                setData(indices, GLElementBuffer.Usage.STATIC)
            }

            vao = GLVertexArray(3 to GLVertexArray.Attrib.FLOAT).apply {
                bind()
            }

            vao.unbind()
            vbo.unbind()
            ebo.unbind()
            program.unbind()

            state.restoreAll()
        }

        fun render(ctx: WorldRenderContext, pos: Vector3f, scale: Vector3f, color: Vector4f) {
            val cam = ctx.camera()

            val proj = Matrix4f(ctx.projectionMatrix())
            val view = Matrix4f(ctx.positionMatrix())
                .translate(cam.pos.toVector3f().negate())
            val model = Matrix4f()
                .translate(pos)
                .scale(scale)

            val state = GLState2().apply { saveAll() }

            glEnable(GL_BLEND)
            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_ALWAYS)
            glDisable(GL_CULL_FACE)

            program.bind()
            program.setMat4("uProjView", Matrix4f(proj).mul(view))
            program.setMat4("uModel", model)
            program.setVec4("uColor", color)
            vao.bind()
            vbo.bind()
            ebo.bind()
            glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0L)

            state.restoreAll()
        }
    }
}