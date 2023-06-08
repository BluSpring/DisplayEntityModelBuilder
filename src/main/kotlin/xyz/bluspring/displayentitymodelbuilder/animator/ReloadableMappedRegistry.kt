package xyz.bluspring.displayentitymodelbuilder.animator

import com.mojang.serialization.Lifecycle
import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

// A registry type that is fully capable of having the same registry keys be re-registered without
// much work needing to be done to handle it.
class ReloadableMappedRegistry<T : Any>(key: ResourceKey<out Registry<T>>, lifecycle: Lifecycle, bl: Boolean) : MappedRegistry<T>(key, lifecycle, bl) {
    constructor(key: ResourceKey<out Registry<T>>, lifecycle: Lifecycle) : this(key, lifecycle, false)

    override fun register(resourceKey: ResourceKey<T>, obj: T, lifecycle: Lifecycle): Holder.Reference<T> {
        if (this.containsKey(resourceKey)) {
            val original = super.get(resourceKey)
            val id = super.getId(original)

            return super.registerMapping(id, resourceKey, obj, lifecycle)
        }

        return super.register(resourceKey, obj, lifecycle)
    }
}