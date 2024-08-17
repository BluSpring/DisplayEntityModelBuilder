package xyz.bluspring.displayentitymodelbuilder.animator

import com.mojang.serialization.Lifecycle
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import xyz.bluspring.displayentitymodelbuilder.animator.model.ModelFormat

object AtrophyRegistry {
    val MODEL_FORMAT = ReloadableMappedRegistry<ModelFormat>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("atrophy", "entity_model")), Lifecycle.stable())
    val ANIMATION = ReloadableMappedRegistry<AnimationFormat>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("atrophy", "animation")), Lifecycle.stable())
}