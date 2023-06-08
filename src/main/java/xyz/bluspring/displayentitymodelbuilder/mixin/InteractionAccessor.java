package xyz.bluspring.displayentitymodelbuilder.mixin;

import net.minecraft.world.entity.Interaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Interaction.class)
public interface InteractionAccessor {
    @Invoker
    void callSetWidth(float f);

    @Invoker
    float callGetWidth();

    @Invoker
    void callSetHeight(float f);

    @Invoker
    float callGetHeight();

    @Invoker
    void callSetResponse(boolean bl);

    @Invoker
    boolean callGetResponse();
}
