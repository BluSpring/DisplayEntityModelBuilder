package xyz.bluspring.displayentitymodelbuilder.animator.model

import com.mojang.math.Transformation
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Interaction
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3f
import org.slf4j.LoggerFactory
import xyz.bluspring.displayentitymodelbuilder.math.TransformUtil
import xyz.bluspring.displayentitymodelbuilder.mixin.DisplayAccessor
import xyz.bluspring.displayentitymodelbuilder.mixin.InteractionAccessor
import xyz.bluspring.displayentitymodelbuilder.mixin.ItemDisplayAccessor
import java.util.*

class EntityModel(val level: ServerLevel, val format: ModelFormat, val uuid: UUID = UUID.randomUUID()) {
    data class EntityModelPart(
        val display: Display,
        val part: ModelFormat.ModelPart
    )

    data class EntityHitBox(
        val interaction: Interaction,
        val hitBox: ModelFormat.HitBox
    )

    private val internalParts = mutableMapOf<String, EntityModelPart>()
    val parts: Map<String, EntityModelPart>
        get() {
            return internalParts.toMap()
        }

    private val internalHitBoxes = mutableMapOf<String, EntityHitBox>()
    val hitBoxes: Map<String, EntityHitBox>
        get() {
            return internalHitBoxes.toMap()
        }

    private lateinit var internalPos: Vec3
    var rotations = Rotations(0F, 0F, 0F)
        private set

    var isDead: Boolean = false
        private set

    var pos: Vec3
        get() = internalPos
        set(value) {
            internalPos = value
            rotate(rotations)
        }

    fun clearParts() {
        val copy = internalParts.values
        internalParts.clear()

        copy.forEach { entity ->
            entity.display.remove(Entity.RemovalReason.DISCARDED)
        }
    }

    fun rotateWithoutPartSet(rotations: Rotations) {
        this.rotations = rotations
    }

    fun rotate(rotations: Rotations) {
        this.rotations = rotations

        val origin = pos.toVector3f()
        val angle = rotations.wrappedY

        // i hate degrees and radians so much
        val sinValue = Mth.sin(angle * Mth.DEG_TO_RAD)
        val cosValue = Mth.cos(angle * Mth.DEG_TO_RAD)

        parts.forEach { (_, part) ->
            val entity = part.display
            val partPos = Vector3f(if (format.followParentTransforms) part.part.originRelative else part.part.origin).div(16F)

            entity.yRot = angle

            entity.moveTo(
                origin.x + ((partPos.x * cosValue) - (partPos.z * sinValue)).toDouble(),
                origin.y + partPos.y.toDouble(),
                origin.z + ((partPos.z * cosValue) + partPos.x * sinValue).toDouble()
            )
        }

        hitBoxes.forEach { (_, hitBox) ->
            val entity = hitBox.interaction
            val hitBoxPos = Vector3f(hitBox.hitBox.position).div(16F)

            entity.yRot = angle

            entity.moveTo(origin.x + ((hitBoxPos.x * cosValue) - (hitBoxPos.z * sinValue)).toDouble(), origin.y + hitBoxPos.y.toDouble(), origin.z + ((hitBoxPos.z * cosValue) + hitBoxPos.x * sinValue).toDouble())
        }

        // this is more for breakpointing than anything really
        return
    }

    fun spawn(spawnPos: Vector3d) {
        format.parts.forEach {
            loadPart(it, spawnPos)
        }

        format.hitBoxes.forEach {
            loadHitBox(it, spawnPos)
        }

        internalPos = TransformUtil.jomlToMc(spawnPos)
    }

    fun kill() {
        isDead = true

        this.parts.forEach { (_, part) ->
            part.display.remove(Entity.RemovalReason.DISCARDED)
        }

        this.hitBoxes.forEach { (_, hitBox) ->
            hitBox.interaction.remove(Entity.RemovalReason.DISCARDED)
        }
    }

    private fun loadHitBox(hitBox: ModelFormat.HitBox, spawnPos: Vector3d) {
        val interaction = EntityType.INTERACTION.create(level)

        if (interaction == null) {
            logger.error("Failed to generate hitbox ID ${hitBox.id} for entity ${format.id}")
            return
        }

        val pos = Vector3f(hitBox.position).div(16F)

        val offsetPos = TransformUtil.jomlToMc(Vector3d(spawnPos).add(pos))
        interaction.moveTo(offsetPos)

        val size = Vector3f(hitBox.size)

        // apparently there's no depth setting, bruh
        (interaction as InteractionAccessor).callSetWidth(size.x)
        (interaction as InteractionAccessor).callSetHeight(size.y)

        format.tags.forEach {
            interaction.addTag(it)
        }

        internalHitBoxes[hitBox.id] = EntityHitBox(interaction, hitBox)

        level.addFreshEntityWithPassengers(interaction)
    }

    private fun loadPart(part: ModelFormat.ModelPart, spawnPos: Vector3d) {
        val displayEntity = EntityType.ITEM_DISPLAY.create(level)

        if (displayEntity == null) {
            logger.error("Failed to generate part ID ${part.id} for entity ${format.id}")
            return
        }

        val item = format.items[part.id]!!
        val pos = Vector3f(if (format.followParentTransforms) part.originRelative else part.origin).div(16F)

        val offsetPos = TransformUtil.jomlToMc(Vector3d(spawnPos).add(pos))

        displayEntity.moveTo(offsetPos)

        (displayEntity as ItemDisplayAccessor).callSetItemStack(item.copy())
        (displayEntity as DisplayAccessor).callSetTransformation(Transformation(
            Vector3f(if (format.followParentTransforms) part.posRelative else part.position).div(16F),
            TransformUtil.toQuaternion(if (format.followParentTransforms) part.rotationRelative else part.rotation),
            Vector3f(if (format.followParentTransforms) part.scaleRelative else part.scale),
            null
        ))
        (displayEntity as DisplayAccessor).callSetWidth(part.cullBox.x)
        (displayEntity as DisplayAccessor).callSetHeight(part.cullBox.y)

        format.tags.forEach {
            displayEntity.addTag(it)
        }

        internalParts[part.id] = EntityModelPart(
            displayEntity,
            part
        )

        part.parts.forEach {
            loadPart(it, spawnPos)
        }

        level.addFreshEntityWithPassengers(displayEntity)
    }

    companion object {
        private val logger = LoggerFactory.getLogger("Atrophy Entity Model")
    }
}