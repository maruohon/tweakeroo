package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IGuiEditSign;
import fi.dy.masa.tweakeroo.util.ISignTextAccess;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(SignBlockEntity.class)
public abstract class MixinSignBlockEntity extends BlockEntity implements ISignTextAccess
{
    @Shadow @Final private Text[] texts;

    private MixinSignBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState)
    {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    private void restoreCopiedText(NbtCompound nbt, CallbackInfo ci)
    {
        // Restore the copied/pasted text after the TileEntity sync overrides it with empty lines
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue() && this.getWorld() != null && this.getWorld().isClient)
        {
            MinecraftClient mc = MinecraftClient.getInstance();

            if ((mc.currentScreen instanceof SignEditScreen) && ((IGuiEditSign) mc.currentScreen).getTile() == (Object) this)
            {
                MiscUtils.applyPreviousTextToSign((SignBlockEntity) (Object) this, null);
            }
        }
    }

    @Override
    public Text[] getText()
    {
        return this.texts;
    }
}
