package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IGuiEditSign;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;

@Mixin(TileEntitySign.class)
public abstract class MixinTileEntitySign
{
    @Shadow
    public abstract void func_212365_a(int line, ITextComponent text);

    @Inject(method = "read", at = @At("RETURN"))
    private void restoreCopiedText(NBTTagCompound nbt, CallbackInfo ci)
    {
        // Restore the copied/pasted text after the TileEntity sync overrides it with empty lines
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            Minecraft mc = Minecraft.getInstance();

            if ((mc.currentScreen instanceof GuiEditSign) && ((IGuiEditSign) mc.currentScreen).getTile() == (Object) this)
            {
                MiscUtils.applyPreviousTextToSign((TileEntitySign) (Object) this);
            }
        }
    }
}
