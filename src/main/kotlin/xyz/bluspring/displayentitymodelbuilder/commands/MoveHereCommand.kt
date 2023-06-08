package xyz.bluspring.displayentitymodelbuilder.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import xyz.bluspring.displayentitymodelbuilder.animator.model.EntityModelManager

object MoveHereCommand : Command<CommandSourceStack> {
    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val uuid = UuidArgument.getUuid(context, "entity")
        val player = context.source.entityOrException
        val entity = EntityModelManager.get(uuid)

        if (entity == null) {
            context.source.sendFailure(Component.literal("Entity $uuid does not exist!"))
            return 0
        }

        entity.rotateWithoutPartSet(Rotations(player.xRot, player.yRot, 0F))
        entity.pos = player.position()

        return 1
    }
}