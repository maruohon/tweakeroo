package fi.dy.masa.tweakeroo.mixin;

import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.common.collect.Lists;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@Mixin(ClientSuggestionProvider.class)
public abstract class MixinClientSuggestionProvider
{
    @Inject(method = "getCoordinates", at = @At("HEAD"), cancellable = true)
    private void onGetCoordinates(boolean allowFloatCoords, CallbackInfoReturnable<Collection<ISuggestionProvider.Coordinates>> cir)
    {
        Minecraft mc = Minecraft.getInstance();

        if (FeatureToggle.TWEAK_TAB_COMPLETE_COORDINATE.getBooleanValue() &&
            mc.player != null && (mc.objectMouseOver == null || mc.objectMouseOver.type != RayTraceResult.Type.BLOCK))
        {
            BlockPos pos = new BlockPos(mc.player);
            cir.setReturnValue(Lists.newArrayList(new ISuggestionProvider.Coordinates(Integer.toString(pos.getX()), Integer.toString(pos.getY()), Integer.toString(pos.getZ()))));
        }
    }
}
