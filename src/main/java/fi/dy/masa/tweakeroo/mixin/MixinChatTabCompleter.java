package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat.ChatTabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@Mixin(ChatTabCompleter.class)
public class MixinChatTabCompleter
{
    @Inject(method = "getTargetBlockPos", at = @At("RETURN"), cancellable = true)
    private void onGetTargetPos(CallbackInfoReturnable<BlockPos> cir)
    {
        if (FeatureToggle.TWEAK_TAB_COMPLETE_COORDINATE.getBooleanValue())
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.player != null && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK))
            {
                cir.setReturnValue(new BlockPos(mc.player.getPositionVector()));
                cir.cancel();
            }
        }
    }
}
