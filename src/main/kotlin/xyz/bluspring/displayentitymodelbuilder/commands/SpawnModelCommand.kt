package xyz.bluspring.displayentitymodelbuilder.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import xyz.bluspring.displayentitymodelbuilder.animator.AtrophyRegistry
import xyz.bluspring.displayentitymodelbuilder.animator.model.EntityModel
import xyz.bluspring.displayentitymodelbuilder.animator.model.EntityModelManager
import xyz.bluspring.displayentitymodelbuilder.math.TransformUtil

object SpawnModelCommand : Command<CommandSourceStack> {
    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val id = ResourceLocationArgument.getId(context, "model")
        val pos = context.source.position

        val format = AtrophyRegistry.MODEL_FORMAT.get(id)

        if (format == null) {
            context.source.sendFailure(Component.literal("Unknown model $id"))
            return 0
        }

        val model = EntityModel(context.source.entity!!.level as ServerLevel, format)
        model.spawn(TransformUtil.mcToJoml(pos))
        model.rotateWithoutPartSet(Rotations(context.source.rotation.x, context.source.rotation.y, 0F))
        model.pos = pos

        EntityModelManager.register(model)

        context.source.sendSuccess(Component.literal("Successfully spawned model $id with UUID ${model.uuid}"), false)

        return 1
    }
}