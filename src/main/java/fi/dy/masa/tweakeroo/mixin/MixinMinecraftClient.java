package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.profiler.ProfilerTiming;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.IMinecraftClientInvoker;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClientInvoker
{
    @Shadow
    public GameOptions options;

    @Shadow
    private int itemUseCooldown;

    @Shadow
    private void doAttack() {}

    @Shadow
    private void doItemUse() {}

    @Override
    public void setItemUseCooldown(int value)
    {
        this.itemUseCooldown = value;
    }

    @Override
    public void leftClickMouseAccessor()
    {
        this.doAttack();
    }

    @Override
    public void rightClickMouseAccessor()
    {
        this.doItemUse();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onGameLoop(boolean renderWorld, CallbackInfo ci)
    {
        MiscTweaks.onGameLoop();
    }

    @Inject(method = "doAttack", at = {
            @At(value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(" +
                         "Lnet/minecraft/entity/player/PlayerEntity;" +
                         "Lnet/minecraft/entity/Entity;)V"),
            @At(value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackBlock(" +
                         "Lnet/minecraft/util/math/BlockPos;" +
                         "Lnet/minecraft/util/math/Direction;)Z")
            })
    private void onLeftClickMousePre(CallbackInfo ci)
    {
        PlacementTweaks.onLeftClickMousePre();
    }

    @Inject(method = "doAttack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void onLeftClickMousePost(CallbackInfo ci)
    {
        PlacementTweaks.onLeftClickMousePost();
    }

    @Redirect(method = "doItemUse()V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(" +
                         "Lnet/minecraft/client/network/ClientPlayerEntity;" +
                         "Lnet/minecraft/client/world/ClientWorld;" +
                         "Lnet/minecraft/util/Hand;" +
                         "Lnet/minecraft/util/hit/BlockHitResult;" +
                         ")Lnet/minecraft/util/ActionResult;"))
    private ActionResult onProcessRightClickBlock(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            Hand hand,
            BlockHitResult hitResult)
    {
        return PlacementTweaks.onProcessRightClickBlock(controller, player, world, hand, hitResult);
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void onProcessKeybindsPre(CallbackInfo ci)
    {
        if (((MinecraftClient) (Object) this).currentScreen == null)
        {
            if (FeatureToggle.TWEAK_HOLD_ATTACK.getBooleanValue())
            {
                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.options.keyAttack.getBoundKeyTranslationKey()), true);
            }

            if (FeatureToggle.TWEAK_HOLD_USE.getBooleanValue())
            {
                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.options.keyUse.getBoundKeyTranslationKey()), true);
            }
        }
    }

    @ModifyConstant(method = "handleProfilerKeyPress", constant = @Constant(intValue = 46), require = 0, allow = 1)
    private int fixProfiler1(int orig)
    {
        // ProfileResultImpl switched to using '\u001e' ie. ASCII "record separator", but this method wasn't updated
        return Configs.Fixes.PROFILER_CHART_FIX.getBooleanValue() ? 30 : orig;
    }

    @ModifyConstant(method = "handleProfilerKeyPress", constant = @Constant(stringValue = "."), require = 0, allow = 1)
    private String fixProfiler2(String orig)
    {
        // ProfileResultImpl switched to using '\u001e' ie. ASCII "record separator", but this method wasn't updated
        return Configs.Fixes.PROFILER_CHART_FIX.getBooleanValue() ? "\u001e" : orig;
    }

    @Redirect(method = "drawProfilerResults", require = 0, allow = 5, at = @At(value = "FIELD",
                        target = "Lnet/minecraft/util/profiler/ProfilerTiming;name:Ljava/lang/String;"))
    private String fixProfilerSectionDisplayName(ProfilerTiming timing)
    {
        // ProfileResultImpl switched to using '\u001e', which looks horrible when printed, so return back to the old dot
        return Configs.Fixes.PROFILER_CHART_FIX.getBooleanValue() ? timing.name.replace('\u001e', '.') : timing.name;
    }
}
