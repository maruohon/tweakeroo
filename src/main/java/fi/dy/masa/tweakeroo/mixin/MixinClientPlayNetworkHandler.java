package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Inject(method = "onGuiSlotUpdate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/container/Container;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    private void onHandleSetSlot(GuiSlotUpdateS2CPacket packet, CallbackInfo ci)
    {
        if (PlacementTweaks.shouldSkipSlotSync(packet.getSlot(), packet.getItemStack()))
        {
            ci.cancel();
        }
    }

    @Inject(method = "onCombatEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void onPlayerDeath(CombatEventS2CPacket packetIn, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PRINT_DEATH_COORDINATES.getBooleanValue())
        {
            BlockPos pos = new BlockPos(MinecraftClient.getInstance().player);
            String str = String.format("You died @ %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            LiteralText message = new LiteralText(str);
            message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, pos.getX() + " " + pos.getY() + " " + pos.getZ()));
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
            Tweakeroo.logger.info(str);
        }
    }
}
