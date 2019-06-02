package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.village.MerchantRecipe;

@Mixin(MerchantRecipe.class)
public abstract class MixinMerchantRecipe
{
    @Shadow private int maxTradeUses;

    @Inject(method = "incrementToolUses", at = @At("RETURN"))
    private void preventTradeLocking(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_VILLAGER_TRADE_LOCKING.getBooleanValue())
        {
            // Prevents the trade from getting locked, by also incrementing
            // the max uses every time the trade use count is incremented.
            ++this.maxTradeUses;
        }
    }
}
