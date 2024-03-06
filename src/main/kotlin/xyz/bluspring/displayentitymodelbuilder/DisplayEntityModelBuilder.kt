package xyz.bluspring.displayentitymodelbuilder

import com.google.gson.JsonParser
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.slf4j.LoggerFactory
import xyz.bluspring.displayentitymodelbuilder.animator.AtrophyRegistry
import xyz.bluspring.displayentitymodelbuilder.animator.model.EntityModelManager
import xyz.bluspring.displayentitymodelbuilder.animator.model.ModelFormat
import xyz.bluspring.displayentitymodelbuilder.commands.MicrowaveCommand
import xyz.bluspring.displayentitymodelbuilder.commands.MoveHereCommand
import xyz.bluspring.displayentitymodelbuilder.commands.RemoveEntityCommand
import xyz.bluspring.displayentitymodelbuilder.commands.SpawnModelCommand

class DisplayEntityModelBuilder : ModInitializer {
    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register { dispatcher, registry, environment ->
            dispatcher.register(
                Commands
                    .literal("spawnmodel")
                    .requires {
                        it.hasPermission(2)
                    }
                    .then(
                        Commands
                            .argument("model", ResourceLocationArgument.id())
                            .suggests { _, builder ->
                                builder.apply {
                                    AtrophyRegistry.MODEL_FORMAT.forEach {
                                        this.suggest(it.id.toString())
                                    }
                                }.buildFuture()
                            }
                            .executes(SpawnModelCommand)
                    )
            )

            dispatcher.register(
                Commands
                    .literal("movehere")
                    .requires {
                        it.hasPermission(2)
                    }
                    .then(
                        Commands
                            .argument("entity", UuidArgument.uuid())
                            .suggests { _, builder ->
                                builder.apply {
                                    EntityModelManager.uuids.forEach {
                                        this.suggest(it.toString())
                                    }
                                }.buildFuture()
                            }
                            .executes(MoveHereCommand)
                    )
            )

            dispatcher.register(
                Commands
                    .literal("microwave")
                    .requires {
                        it.hasPermission(2)
                    }
                    .then(
                        Commands
                            .argument("entity", UuidArgument.uuid())
                            .suggests { _, builder ->
                                builder.apply {
                                    EntityModelManager.uuids.forEach {
                                        this.suggest(it.toString())
                                    }
                                }.buildFuture()
                            }
                            .executes(MicrowaveCommand)
                    )
            )

            dispatcher.register(
                Commands
                    .literal("removeentity")
                    .requires {
                        it.hasPermission(2)
                    }
                    .then(
                        Commands
                            .argument("entity", UuidArgument.uuid())
                            .suggests { _, builder ->
                                builder.apply {
                                    EntityModelManager.uuids.forEach {
                                        this.suggest(it.toString())
                                    }
                                }.buildFuture()
                            }
                            .executes(RemoveEntityCommand)
                    )
            )
        }

        loadAnimatorData()
    }

    private fun loadAnimatorData() {
        ResourceManagerHelper.get(PackType.SERVER_DATA)
            .registerReloadListener(object : SimpleSynchronousResourceReloadListener {
                override fun getFabricId(): ResourceLocation {
                    return ResourceLocation("vandal", "animator_data")
                }

                override fun onResourceManagerReload(manager: ResourceManager) {
                    // Model loading
                    val models = manager.listResources("models") {
                        it.path.endsWith(".json")
                    }

                    models.forEach { (id, resource) ->
                        try {
                            val json = JsonParser.parseReader(resource.openAsReader()).asJsonObject

                            val format = ModelFormat.deserialize(ResourceLocation(id.namespace, id.path.removeSuffix(".model.json").removeSuffix(".json")), json)
                            Registry.register(AtrophyRegistry.MODEL_FORMAT, format.id, format)
                            logger.info("Registered model $id as ${format.id}")
                        } catch (e: Exception) {
                            logger.error("Failed to load model $id")
                            e.printStackTrace()
                        }
                    }
                }
            })
    }

    companion object {
        private val logger = LoggerFactory.getLogger("Atrophy")
    }
}