package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.CommandBlockExecutor;

@Mixin(CommandBlockExecutor.class)
public interface IMixinCommandBlockExecutor
{
    @Accessor("updateLastExecution")
    boolean getUpdateLastExecution();

    @Accessor("updateLastExecution")
    void setUpdateLastExecution(boolean value);
}
