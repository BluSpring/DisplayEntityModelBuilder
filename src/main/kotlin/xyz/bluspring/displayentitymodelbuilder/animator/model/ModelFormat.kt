package xyz.bluspring.displayentitymodelbuilder.animator.model

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomModelData
import org.joml.Vector2f
import org.joml.Vector3f

data class ModelFormat(
    val id: ResourceLocation,
    val items: Map<String, ItemStack>,
    val parts: List<ModelPart>,
    val hitBoxes: List<HitBox>,
    val tags: List<String>,
    val followParentTransforms: Boolean,
    val useNewRotations: Boolean
) {
    data class ModelPart(
        val id: String,
        val position: Vector3f,
        val origin: Vector3f,
        val rotation: Vector3f,
        val scale: Vector3f,
        val parts: List<ModelPart>,
        val cullBox: Vector2f
    ) {
        var parent: ModelPart? = null

        val originRelative: Vector3f
            get() {
                val newOrigin = Vector3f(origin).add(parent?.originRelative ?: Vector3f())

                val cosValueX = Mth.cos(rotationRelative.z())
                val sinValueX = Mth.sin(rotationRelative.z())
                val cosValueY = Mth.cos(rotationRelative.y())
                val sinValueY = Mth.sin(rotationRelative.y())
                val cosValueZ = Mth.cos(rotationRelative.x())
                val sinValueZ = Mth.sin(rotationRelative.x())

                // when X is rotated, Y and Z will be affected
                // when Y is rotated, X and Z will be affected
                /*
                (newOrigin.x() * cosValueY) - (newOrigin.z() * sinValueY),
                newOrigin.y(),
                (newOrigin.z() * cosValueY) + (newOrigin.x() * sinValueY)
                 */
                // when Z is rotated, X and Y will be affected
                return Vector3f(
                    (newOrigin.x() * cosValueZ + cosValueY) + (newOrigin.y() * sinValueZ) - (newOrigin.z() * sinValueY),
                    (newOrigin.y() * cosValueZ) + (newOrigin.x() * sinValueZ),
                    (newOrigin.z() * cosValueY) + (newOrigin.x() * sinValueY)
                )
            }

        val posRelative: Vector3f
            get() = Vector3f(position).add(parent?.posRelative ?: Vector3f())

        val rotationRelative: Vector3f
            get() = Vector3f(rotation).add(parent?.rotationRelative ?: Vector3f())

        val scaleRelative: Vector3f
            get() = Vector3f(scale).add((parent?.scaleRelative?.sub(Vector3f(1f, 1f, 1f))) ?: Vector3f())
    }

    data class HitBox(
        val id: String,
        val position: Vector3f,
        val size: Vector3f
    )

    companion object {
        fun deserialize(id: ResourceLocation, data: JsonObject): ModelFormat {
            val parts = mutableListOf<ModelPart>()

            data.getAsJsonArray("shapes").forEach {
                val partData = it.asJsonObject

                parts.add(loadPart(partData))
            }

            val items = mutableMapOf<String, ItemStack>()

            val itemsData = data.getAsJsonObject("items")
            itemsData.keySet().forEach { key ->
                val itemData = itemsData.getAsJsonObject(key)

                val stack = ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemData.get("id").asString)), 1)
                if (itemData.has("model"))
                    stack.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(itemData.get("model").asInt))

                items[key] = stack
            }

            val hitBoxes = mutableListOf<HitBox>()

            if (data.has("hitboxes")) {
                data.getAsJsonArray("hitboxes").forEach {
                    val hitBoxData = it.asJsonObject

                    hitBoxes.add(
                        HitBox(
                            if (hitBoxData.has("id")) hitBoxData.get("id").asString else "root",
                            if (hitBoxData.has("position")) arrayToVec3f(hitBoxData.get("position").asJsonArray) else Vector3f(),
                            if (hitBoxData.has("size")) arrayToVec3f(hitBoxData.get("size").asJsonArray) else Vector3f(),
                        )
                    )
                }
            }

            val tags = mutableListOf<String>()

            if (data.has("tags")) {
                data.getAsJsonArray("tags").forEach {
                    tags.add(it.asString)
                }
            }

            val followParentTransforms = data.has("follow_parent_transforms") && data.get("follow_parent_transforms").asBoolean
            val useNewRotations = data.has("use_new_rotations") && data.get("use_new_rotations").asBoolean

            return ModelFormat(
                id,
                items,
                parts,
                hitBoxes,
                tags,
                followParentTransforms,
                useNewRotations
            )
        }

        private fun loadPart(partData: JsonObject, parent: ModelPart? = null): ModelPart {
            return ModelPart(
                partData.get("id").asString,
                if (partData.has("position")) arrayToVec3f(partData.get("position").asJsonArray) else Vector3f(),
                if (partData.has("origin")) arrayToVec3f(partData.get("origin").asJsonArray) else Vector3f(),
                if (partData.has("rotation")) arrayToVec3f(partData.get("rotation").asJsonArray).mul(Mth.DEG_TO_RAD) else Vector3f(),
                if (partData.has("scale")) arrayToVec3f(partData.get("scale").asJsonArray) else Vector3f(1F, 1F, 1F),
                mutableListOf<ModelPart>().apply {
                    if (partData.has("children"))
                        partData.getAsJsonArray("children").forEach {
                            this.add(loadPart(it.asJsonObject))
                        }
                },
                if (partData.has("cull_box")) arrayToVec2f(partData.get("cull_box").asJsonArray) else Vector2f(0.35f, 0.35f)
            ).apply {
                for (part in this.parts) {
                    part.parent = this
                }
            }
        }

        private fun arrayToVec3f(collection: List<Float>): Vector3f {
            return Vector3f(collection[0], collection[1], collection[2])
        }

        private fun arrayToVec2f(array: JsonArray): Vector2f {
            return Vector2f(array[0].asFloat, array[1].asFloat)
        }

        private fun arrayToVec3f(array: JsonArray): Vector3f {
            return Vector3f(array[0].asFloat, array[1].asFloat, array[2].asFloat)
        }
    }
}
