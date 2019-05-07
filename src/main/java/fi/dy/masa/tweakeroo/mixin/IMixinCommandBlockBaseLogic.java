package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.tileentity.CommandBlockBaseLogic;

@Mixin(CommandBlockBaseLogic.class)
public interface IMixinCommandBlockBaseLogic
{
    @Accessor("updateLastExecution")
    boolean getUpdateLastExecution();
}
