package xyz.bluspring.displayentitymodelbuilder.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import xyz.bluspring.displayentitymodelbuilder.animator.model.EntityModelManager

object MicrowaveCommand : Command<CommandSourceStack> {
    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val uuid = UuidArgument.getUuid(context, "entity")
        val player = context.source.entityOrException
        val entity = EntityModelManager.get(uuid)

        if (entity == null) {
            context.source.sendFailure(Component.literal("Entity $uuid does not exist!"))
            return 0
        }

        var rotation = 0F

        ServerTickEvents.START_WORLD_TICK.register {
            if (entity.isDead)
                return@register

            entity.rotate(Rotations(0F, rotation, 0F))
            rotation += 1F
        }

        return 1
    }
}