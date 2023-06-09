package xyz.bluspring.displayentitymodelbuilder.mixin;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker
    void callSetTransformation(Transformation transformation);

    @Invoker
    void callSetInterpolationDuration(int i);

    @Invoker
    void callSetInterpolationDelay(int i);

    @Invoker
    void callSetWidth(float f);

    @Invoker
    void callSetHeight(float f);
}
