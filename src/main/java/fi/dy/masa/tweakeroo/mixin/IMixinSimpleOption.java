package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.option.SimpleOption;

@Mixin(SimpleOption.class)
public interface IMixinSimpleOption<T>
{
    @Accessor("value")
    void tweakeroo_setValueWithoutCheck(T value);
}
