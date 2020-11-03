package fi.dy.masa.tweakeroo.mixin;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(ClientCommandSource.class)
public abstract class MixinClientCommandSource
{
    @Inject(method = "getBlockPositionSuggestions", at = @At("HEAD"), cancellable = true)
    private void onGetBlockPositionSuggestions(CallbackInfoReturnable<Collection<CommandSource.RelativePosition>> cir)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_TAB_COMPLETE_COORDINATE.getBooleanValue() &&
            mc.player != null && (mc.crosshairTarget == null || mc.crosshairTarget.getType() == HitResult.Type.MISS))
        {
            BlockPos pos = fi.dy.masa.malilib.util.PositionUtils.getEntityBlockPos(mc.player);
            cir.setReturnValue(Collections.singleton(new CommandSource.RelativePosition(formatInt(pos.getX()), formatInt(pos.getY()), formatInt(pos.getZ()))));
        }
    }

    @Inject(method = "getPositionSuggestions", at = @At("HEAD"), cancellable = true)
    private void onGetPositionSuggestions(CallbackInfoReturnable<Collection<CommandSource.RelativePosition>> cir)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_TAB_COMPLETE_COORDINATE.getBooleanValue() &&
            mc.player != null && (mc.crosshairTarget == null || mc.crosshairTarget.getType() == HitResult.Type.MISS))
        {
            cir.setReturnValue(Collections.singleton(new CommandSource.RelativePosition(formatDouble(mc.player.getX()), formatDouble(mc.player.getY()), formatDouble(mc.player.getZ()))));
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
