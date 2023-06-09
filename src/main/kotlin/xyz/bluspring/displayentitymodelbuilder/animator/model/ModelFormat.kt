package xyz.bluspring.displayentitymodelbuilder.animator.model

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import org.joml.Vector2f
import org.joml.Vector3f

data class ModelFormat(
    val id: ResourceLocation,
    val items: Map<String, ItemStack>,
    val parts: List<ModelPart>,
    val hitBoxes: List<HitBox>
) {
    data class ModelPart(
        val id: String,
        val position: Vector3f,
        val origin: Vector3f,
        val rotation: Vector3f,
        val scale: Vector3f,
        val parts: List<ModelPart>,
        val cullBox: Vector2f
    )

    data class HitBox(
        val id: String,
        val position: Vector3f,
        val size: Vector3f
    )

    companion object {
        fun deserialize(data: JsonObject): ModelFormat {
            val parts = mutableListOf<ModelPart>()

            data.getAsJsonArray("shapes").forEach {
                val partData = it.asJsonObject

                parts.add(loadPart(partData))
            }

            val items = mutableMapOf<String, ItemStack>()

            val itemsData = data.getAsJsonObject("items")
            itemsData.keySet().forEach { key ->
                val itemData = itemsData.getAsJsonObject(key)

                val stack = ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation(itemData.get("id").asString)), 1)
                if (itemData.has("model"))
                    stack.orCreateTag.putInt("CustomModelData", itemData.get("model").asInt)

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

            return ModelFormat(
                ResourceLocation(data.get("id").asString),
                items,
                parts,
                hitBoxes
            )
        }

        private fun loadPart(partData: JsonObject): ModelPart {
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
            )
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
