package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Inject(method = "onScreenHandlerSlotUpdate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setStackInSlot(IILnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    private void onHandleSetSlot(net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci)
    {
        if (PlacementTweaks.shouldSkipSlotSync(packet.getSlot(), packet.getItemStack()))
        {
            ci.cancel();
        }
    }

    @Inject(method = "onDeathMessage", at = @At(value = "INVOKE", // onCombatEvent
            target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void onPlayerDeath(DeathMessageS2CPacket packetIn, CallbackInfo ci)
    {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_PRINT_DEATH_COORDINATES.getBooleanValue() && mc.player != null)
        {
            net.minecraft.util.math.BlockPos pos = fi.dy.masa.malilib.util.PositionUtils.getEntityBlockPos(mc.player);
            String str = String.format("You died @ %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            net.minecraft.text.LiteralText message = new net.minecraft.text.LiteralText(str);
            message.getStyle().withClickEvent(new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.SUGGEST_COMMAND, pos.getX() + " " + pos.getY() + " " + pos.getZ()));
            message.formatted(net.minecraft.util.Formatting.UNDERLINE);
            net.minecraft.client.MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
            Tweakeroo.logger.info(str);
        }
    }
}
