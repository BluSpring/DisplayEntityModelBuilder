package xyz.bluspring.displayentitymodelbuilder.animator

import com.mojang.serialization.Lifecycle
import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistrationInfo
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import xyz.bluspring.displayentitymodelbuilder.mixin.MappedRegistryAccessor

// A registry type that is fully capable of having the same registry keys be re-registered without
// much work needing to be done to handle it.
class ReloadableMappedRegistry<T : Any>(key: ResourceKey<out Registry<T>>, lifecycle: Lifecycle, bl: Boolean) : MappedRegistry<T>(key, lifecycle, bl) {
    constructor(key: ResourceKey<out Registry<T>>, lifecycle: Lifecycle) : this(key, lifecycle, false)

    override fun register(
        resourceKey: ResourceKey<T>,
        obj: T,
        registrationInfo: RegistrationInfo
    ): Holder.Reference<T> {
        if (this.containsKey(resourceKey)) {
            val original = super.getValue(resourceKey)
            val ref = super.getOrThrow(resourceKey)

            (this as MappedRegistryAccessor<T>).byId.remove(ref)
            this.byValue.remove(original)
            this.byKey.remove(resourceKey)
            this.byLocation.remove(resourceKey.location())
            this.toId.removeInt(original)
        }

        return super.register(resourceKey, obj, registrationInfo)
    }
}