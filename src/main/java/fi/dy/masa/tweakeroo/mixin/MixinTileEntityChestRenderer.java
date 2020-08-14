package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.DisableToggle;

@Mixin(net.minecraft.client.renderer.tileentity.TileEntityChestRenderer.class)
public abstract class MixinTileEntityChestRenderer extends
       net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer<net.minecraft.tileentity.TileEntityChest>
{
    @Shadow private boolean isChristmas;

    @Redirect(method = "render", require = 0, at = @At(value = "FIELD",
              target = "Lnet/minecraft/client/renderer/tileentity/TileEntityChestRenderer;isChristmas:Z"))
    private boolean disableChristmasTexture(net.minecraft.client.renderer.tileentity.TileEntityChestRenderer renderer)
    {
        return DisableToggle.DISABLE_CHRISTMAS_CHESTS.getBooleanValue() ? false : this.isChristmas;
    }
}
