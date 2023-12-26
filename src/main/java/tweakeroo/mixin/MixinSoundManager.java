package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tweakeroo.Tweakeroo;
import tweakeroo.config.Configs;
import tweakeroo.config.DisableToggle;
import tweakeroo.tweaks.MiscTweaks;

@Mixin(net.minecraft.client.audio.SoundManager.class)
public abstract class MixinSoundManager
{
    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void disableSounds(net.minecraft.client.audio.ISound sound, CallbackInfo ci)
    {
        if (Configs.Generic.SOUND_NAME_OUTPUT.getBooleanValue())
        {
            Tweakeroo.LOGGER.info("Sound: '{}'", sound.getSoundLocation());
        }

        if (DisableToggle.DISABLE_SOUNDS_ALL.getBooleanValue() || MiscTweaks.shouldDisableSound(sound))
        {
            ci.cancel();
        }
    }

    @Inject(method = "playDelayedSound", at = @At("HEAD"), cancellable = true)
    private void disableDelayedSounds(net.minecraft.client.audio.ISound sound, int delay, CallbackInfo ci)
    {
        if (Configs.Generic.SOUND_NAME_OUTPUT.getBooleanValue())
        {
            Tweakeroo.LOGGER.info("Sound: '{}'", sound.getSoundLocation());
        }

        if (DisableToggle.DISABLE_SOUNDS_ALL.getBooleanValue() || MiscTweaks.shouldDisableSound(sound))
        {
            ci.cancel();
        }
    }
}
