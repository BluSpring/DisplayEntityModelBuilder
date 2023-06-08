package xyz.bluspring.displayentitymodelbuilder.animator.model

import java.util.*

object EntityModelManager {
    private val entities = mutableMapOf<UUID, EntityModel>()
    val uuids: Collection<UUID>
        get() {
            return entities.keys
        }

    fun register(model: EntityModel): UUID {
        entities[model.uuid] = model
        return model.uuid
    }

    fun get(uuid: UUID): EntityModel? {
        return entities[uuid]
    }

    fun remove(uuid: UUID) {
        if (!entities.contains(uuid))
            return

        remove(entities[uuid]!!)
    }

    fun remove(entity: EntityModel) {
        entities.remove(entity.uuid)
        entity.kill()
    }
}