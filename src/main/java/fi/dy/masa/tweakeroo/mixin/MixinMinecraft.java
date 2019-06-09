package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraftAccessor
{
    @Shadow
    private int rightClickDelayTimer;

    @Shadow
    private void clickMouse() {}

    @Shadow
    private void rightClickMouse() {}

    @Override
    public void setRightClickDelayTimer(int value)
    {
        this.rightClickDelayTimer = value;
    }

    @Override
    public void leftClickMouseAccessor()
    {
        this.clickMouse();
    }

    @Override
    public void rightClickMouseAccessor()
    {
        this.rightClickMouse();
    }

    @Inject(method = "runGameLoop", at = @At("RETURN"))
    private void onGameLoop(boolean renderWorld, CallbackInfo ci)
    {
        Tweakeroo.onGameLoop((Minecraft) (Object) this);
    }

    @Inject(method = "clickMouse", at = {
            @At(value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(" +
                         "Lnet/minecraft/entity/player/EntityPlayer;" +
                         "Lnet/minecraft/entity/Entity;)V"),
            @At(value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;clickBlock(" +
                         "Lnet/minecraft/util/math/BlockPos;" +
                         "Lnet/minecraft/util/EnumFacing;)Z")
            })
    private void onLeftClickMousePre(CallbackInfo ci)
    {
        PlacementTweaks.onLeftClickMousePre();
    }

    @Inject(method = "clickMouse", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingArm(Lnet/minecraft/util/EnumHand;)V"))
    private void onLeftClickMousePost(CallbackInfo ci)
    {
        PlacementTweaks.onLeftClickMousePost();
    }

    @Redirect(method = "rightClickMouse()V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClickBlock(" +
                "Lnet/minecraft/client/entity/EntityPlayerSP;" +
                "Lnet/minecraft/client/multiplayer/WorldClient;" +
                "Lnet/minecraft/util/math/BlockPos;" +
                "Lnet/minecraft/util/EnumFacing;" +
                "Lnet/minecraft/util/math/Vec3d;" +
                "Lnet/minecraft/util/EnumHand;" +
                ")Lnet/minecraft/util/EnumActionResult;"))
    private EnumActionResult onProcessRightClickBlock(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos pos,
            EnumFacing side,
            Vec3d hitVec,
            EnumHand hand)
    {
        return PlacementTweaks.onProcessRightClickBlock(controller, player, world, pos, side, hitVec, hand);
    }

    @Inject(method = "processKeyBinds", at = @At("HEAD"))
    private void onProcessKeybindsPre(CallbackInfo ci)
    {
        Minecraft mc = (Minecraft) (Object) this;

        if (FeatureToggle.TWEAK_HOLD_ATTACK.getBooleanValue() && GuiUtils.getCurrentScreen() == null)
        {
            KeyBinding.setKeyBindState(InputMappings.getInputByName(mc.gameSettings.keyBindAttack.getTranslationKey()), true);
        }

        if (FeatureToggle.TWEAK_HOLD_USE.getBooleanValue() && GuiUtils.getCurrentScreen() == null)
        {
            KeyBinding.setKeyBindState(InputMappings.getInputByName(mc.gameSettings.keyBindUseItem.getTranslationKey()), true);
        }
    }
}
