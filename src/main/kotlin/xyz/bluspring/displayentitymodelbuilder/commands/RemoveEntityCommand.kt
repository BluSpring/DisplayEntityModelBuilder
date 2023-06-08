package xyz.bluspring.displayentitymodelbuilder.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.network.chat.Component
import xyz.bluspring.displayentitymodelbuilder.animator.model.EntityModelManager

object RemoveEntityCommand : Command<CommandSourceStack> {
    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val uuid = UuidArgument.getUuid(context, "entity")
        val entity = EntityModelManager.get(uuid)

        if (entity == null) {
            context.source.sendFailure(Component.literal("Entity $uuid does not exist!"))
            return 0
        }

        EntityModelManager.remove(entity)
        context.source.sendSuccess(Component.literal("Removed entity $uuid"), false)

        return 1
    }
}