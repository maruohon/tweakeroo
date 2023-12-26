package tweakeroo.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.event.ClickEvent;

import malilib.util.game.wrap.GameUtils;
import tweakeroo.Tweakeroo;
import tweakeroo.config.DisableToggle;
import tweakeroo.config.FeatureToggle;
import tweakeroo.tweaks.PlacementTweaks;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient
{
    @Inject(method = "handleSetSlot", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Container;putStackInSlot(ILnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    private void onHandleSetSlot(SPacketSetSlot packet, CallbackInfo ci)
    {
        if (PlacementTweaks.shouldSkipSlotSync(packet.getSlot(), packet.getStack()))
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleCombatEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlayerDeath(SPacketCombatEvent packetIn, CallbackInfo ci, Entity entity)
    {
        if (FeatureToggle.TWEAK_PRINT_DEATH_COORDINATES.getBooleanValue())
        {
            BlockPos pos = entity.getPosition();
            String str = String.format("You died @ %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            net.minecraft.util.text.TextComponentString message = new net.minecraft.util.text.TextComponentString(str);
            message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, pos.getX() + " " + pos.getY() + " " + pos.getZ()));
            GameUtils.getClient().ingameGUI.getChatGUI().printChatMessage(message);
            Tweakeroo.LOGGER.info(str);
        }
    }

    @Redirect(method = "handleChangeGameState",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"),
              slice = @Slice(to = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD,
                                      target = "Lnet/minecraft/client/Minecraft;gameSettings:Lnet/minecraft/client/settings/GameSettings;")))
    private void preventPortalGuiClosing1(Minecraft mc, net.minecraft.client.gui.GuiScreen gui)
    {
        if (DisableToggle.DISABLE_PORTAL_GUI_CLOSING.getBooleanValue() == false)
        {
            mc.displayGuiScreen(gui);
        }
    }

    @Redirect(method = "handleRespawn",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    private void preventPortalGuiClosing2(Minecraft mc, net.minecraft.client.gui.GuiScreen gui)
    {
        if (DisableToggle.DISABLE_PORTAL_GUI_CLOSING.getBooleanValue() == false)
        {
            mc.displayGuiScreen(gui);
        }
    }

    @Redirect(method = "handlePlayerPosLook",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    private void preventPortalGuiClosing3(Minecraft mc, net.minecraft.client.gui.GuiScreen gui)
    {
        // Allow clearing the download terrain screen, for example when first logging in,
        // but don't close the GUI otherwise.
        if (DisableToggle.DISABLE_PORTAL_GUI_CLOSING.getBooleanValue() == false ||
            mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain)
        {
            mc.displayGuiScreen(gui);
        }
    }
}
