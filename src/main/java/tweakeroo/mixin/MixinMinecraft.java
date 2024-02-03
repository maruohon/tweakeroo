package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import malilib.gui.util.GuiUtils;
import malilib.util.position.Direction;
import tweakeroo.config.FeatureToggle;
import tweakeroo.tweaks.MiscTweaks;
import tweakeroo.tweaks.PlacementTweaks;
import tweakeroo.util.IMinecraftAccessor;
import tweakeroo.util.MiscUtils;

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
        return PlacementTweaks.onProcessRightClickBlock(controller, player, world,
                                                        malilib.util.position.BlockPos.of(pos),
                                                        Direction.of(side),
                                                        malilib.util.position.Vec3d.of(hitVec), hand);
    }

    @Inject(method = "processKeyBinds", at = @At("HEAD"))
    private void onProcessKeybindsPre(CallbackInfo ci)
    {
        Minecraft mc = (Minecraft) (Object) this;

        if (FeatureToggle.TWEAK_HOLD_ATTACK.getBooleanValue() && GuiUtils.noScreenOpen())
        {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
        }

        if (FeatureToggle.TWEAK_HOLD_USE.getBooleanValue() && GuiUtils.noScreenOpen())
        {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        }
    }

    @Inject(method = "runTick",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTickKeyboard()V")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;world:Lnet/minecraft/client/multiplayer/WorldClient;", ordinal = 0))
    private void onRunTick(CallbackInfo ci)
    {
        MiscTweaks.onTick();
    }

    @Inject(method = "displayDebugInfo", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;glLineWidth(F)V"))
    private void scaleDebugPieChart(long elapsedTicksTime, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_DEBUG_PIE_CHART_SCALE.getBooleanValue())
        {
            MiscUtils.applyDebugPieChartScale();
        }
    }
}
