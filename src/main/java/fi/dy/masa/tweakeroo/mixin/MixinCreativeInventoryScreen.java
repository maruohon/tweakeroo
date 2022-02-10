package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.util.CreativeExtraItems;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler>
{
    private MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler,
                                         PlayerInventory playerInventory, Text text)
    {
        super(screenHandler, playerInventory, text);
    }

    // This needs to happen before the `this.handler.scrollItems(0.0F);` call.
    @Inject(method = "search", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/util/collection/DefaultedList;addAll(Ljava/util/Collection;)Z"))
    private void tweakeroo_removeInfestedStoneFromCreativeSearchInventory(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_CREATIVE_INFESTED_BLOCKS.getBooleanValue())
        {
            CreativeExtraItems.removeInfestedBlocks(this.handler.itemList);
        }
    }
}
