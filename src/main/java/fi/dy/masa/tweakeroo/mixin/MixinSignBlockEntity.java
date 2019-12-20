package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.nbt.CompoundTag;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IGuiEditSign;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(SignBlockEntity.class)
public abstract class MixinSignBlockEntity
{
    @Inject(method = "fromTag", at = @At("RETURN"))
    private void restoreCopiedText(CompoundTag nbt, CallbackInfo ci)
    {
        // Restore the copied/pasted text after the TileEntity sync overrides it with empty lines
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            MinecraftClient mc = MinecraftClient.getInstance();

            if ((mc.currentScreen instanceof SignEditScreen) && ((IGuiEditSign) mc.currentScreen).getTile() == (Object) this)
            {
                MiscUtils.applyPreviousTextToSign((SignBlockEntity) (Object) this);
            }
        }
    }
}
