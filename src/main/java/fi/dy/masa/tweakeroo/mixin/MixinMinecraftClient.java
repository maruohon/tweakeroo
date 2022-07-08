package fi.dy.masa.tweakeroo.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.IMinecraftClientInvoker;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClientInvoker
{
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;
    @Shadow @Nullable public Screen currentScreen;
    @Shadow @Final public GameOptions options;
    @Shadow private int itemUseCooldown;
    @Shadow protected int attackCooldown;

    @Shadow
    private boolean doAttack() { return false; }

    @Shadow
    private void doItemUse() {}

    @Override
    public void tweakeroo_setItemUseCooldown(int value)
    {
        this.itemUseCooldown = value;
    }

    @Override
    public boolean tweakeroo_invokeDoAttack()
    {
        return this.doAttack();
    }

    @Override
    public void tweakeroo_invokeDoItemUse()
    {
        this.doItemUse();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onGameLoop(boolean renderWorld, CallbackInfo ci)
    {
        if (this.player != null && this.world != null)
        {
            MiscTweaks.onGameLoop((MinecraftClient) (Object) this);
        }
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
    private void onLeftClickMousePre(CallbackInfoReturnable<Boolean> cir)
    {
        PlacementTweaks.onLeftClickMousePre();
    }

    @Inject(method = "doAttack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void onLeftClickMousePost(CallbackInfoReturnable<Boolean> cir)
    {
        PlacementTweaks.onLeftClickMousePost();
    }

    @Redirect(method = "doItemUse()V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(" +
                         "Lnet/minecraft/client/network/ClientPlayerEntity;" +
                         "Lnet/minecraft/util/Hand;" +
                         "Lnet/minecraft/util/hit/BlockHitResult;" +
                         ")Lnet/minecraft/util/ActionResult;"))
    private ActionResult onProcessRightClickBlock(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            Hand hand,
            BlockHitResult hitResult)
    {
        return PlacementTweaks.onProcessRightClickBlock(controller, player, this.world, hand, hitResult);
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void onProcessKeybindsPre(CallbackInfo ci)
    {
        if (this.currentScreen == null)
        {
            if (FeatureToggle.TWEAK_HOLD_ATTACK.getBooleanValue())
            {
                // Opening a GUI sets the cooldown to 10000, and it won't have a chance
                // to get reset normally when this tweak is active.
                if (this.attackCooldown >= 10000)
                {
                    this.attackCooldown = 0;
                }

                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.options.attackKey.getBoundKeyTranslationKey()), true);
            }

            if (FeatureToggle.TWEAK_HOLD_USE.getBooleanValue())
            {
                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.options.useKey.getBoundKeyTranslationKey()), true);
            }
        }
    }
}
