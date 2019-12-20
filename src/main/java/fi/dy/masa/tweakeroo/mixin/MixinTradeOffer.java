package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.village.TradeOffer;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(TradeOffer.class)
public abstract class MixinTradeOffer
{
    @Shadow @Mutable @Final private int maxUses;

    @Inject(method = "use", at = @At("RETURN"))
    private void preventTradeLocking(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_VILLAGER_TRADE_LOCKING.getBooleanValue())
        {
            // Prevents the trade from getting locked, by also incrementing
            // the max uses every time the trade use count is incremented.
            ++this.maxUses;
        }
    }
}
