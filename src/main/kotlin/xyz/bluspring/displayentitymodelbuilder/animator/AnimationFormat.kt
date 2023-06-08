package xyz.bluspring.displayentitymodelbuilder.animator

import com.mojang.math.Transformation
import net.minecraft.resources.ResourceLocation

data class AnimationFormat(
    val id: ResourceLocation,
    val model: ResourceLocation,
    val keyframes: List<AnimationKeyframe>
) {
    data class AnimationKeyframe(
        val frame: Int,
        val parts: List<AnimatedPart>
    )

    data class AnimatedPart(
        val part: String,
        val transformation: Transformation
    )

    companion object {
        // FPS is still 20, but it appears to interpolate
        // based on frame rate.
        const val FPS = 20
    }
}
