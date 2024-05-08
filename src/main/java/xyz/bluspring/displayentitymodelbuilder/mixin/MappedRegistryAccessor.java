package xyz.bluspring.displayentitymodelbuilder.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
    @Accessor
    Map<ResourceKey<T>, Holder.Reference<T>> getByKey();

    @Accessor
    Map<ResourceLocation, Holder.Reference<T>> getByLocation();

    @Accessor
    Map<T, Holder.Reference<T>> getByValue();

    @Accessor
    Reference2IntMap<T> getToId();

    @Accessor
    ObjectList<Holder.Reference<T>> getById();
}
