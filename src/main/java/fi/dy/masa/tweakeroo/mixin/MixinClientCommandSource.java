package fi.dy.masa.tweakeroo.mixin;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Mixin(ClientCommandSource.class)
public abstract class MixinClientCommandSource
{
    @Inject(method = "getBlockPositionSuggestions", at = @At("HEAD"), cancellable = true)
    private void onGetBlockPositionSuggestions(CallbackInfoReturnable<Collection<CommandSource.RelativePosition>> cir)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_TAB_COMPLETE_COORDINATE.getBooleanValue() &&
            mc.player != null && (mc.hitResult == null || mc.hitResult.getType() == HitResult.Type.MISS))
        {
            BlockPos pos = new BlockPos(mc.player);
            cir.setReturnValue(Collections.singleton(new CommandSource.RelativePosition(formatInt(pos.getX()), formatInt(pos.getY()), formatInt(pos.getZ()))));
        }
    }

    @Inject(method = "getPositionSuggestions", at = @At("HEAD"), cancellable = true)
    private void onGetPositionSuggestions(CallbackInfoReturnable<Collection<CommandSource.RelativePosition>> cir)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_TAB_COMPLETE_COORDINATE.getBooleanValue() &&
            mc.player != null && (mc.hitResult == null || mc.hitResult.getType() == HitResult.Type.MISS))
        {
            cir.setReturnValue(Collections.singleton(new CommandSource.RelativePosition(formatDouble(mc.player.x), formatDouble(mc.player.y), formatDouble(mc.player.z))));
        }
    }

     private static String formatDouble(double val)
     {
         return String.format(Locale.ROOT, "%.2f", val);
     }

     private static String formatInt(int val)
     {
         return Integer.toString(val);
     }
}
