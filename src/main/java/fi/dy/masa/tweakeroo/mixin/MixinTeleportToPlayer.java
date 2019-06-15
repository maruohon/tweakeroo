package fi.dy.masa.tweakeroo.mixin;

import java.util.Collection;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.common.collect.Ordering;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;

@Mixin(TeleportToPlayer.class)
public abstract class MixinTeleportToPlayer
{
    @Shadow @Final private static Ordering<NetworkPlayerInfo> PROFILE_ORDER;
    @Shadow @Final private List<ISpectatorMenuObject> items;

    @Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("RETURN"))
    private void allowSpectatorTeleport(Collection<NetworkPlayerInfo> profiles, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SPECTATOR_TELEPORT.getBooleanValue())
        {
            this.items.clear();

            for (NetworkPlayerInfo info : PROFILE_ORDER.sortedCopy(profiles))
            {
                this.items.add(new PlayerMenuObject(info.getGameProfile()));
            }
        }
    }
}
