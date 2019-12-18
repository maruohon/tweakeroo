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
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(TeleportSpectatorMenu.class)
public abstract class MixinTeleportSpectatorMenu
{
    @Shadow @Final private static Ordering<PlayerListEntry> ORDERING;
    @Shadow @Final private List<SpectatorMenuCommand> elements;

    @Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("RETURN"))
    private void allowSpectatorTeleport(Collection<PlayerListEntry> profiles, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SPECTATOR_TELEPORT.getBooleanValue())
        {
            this.elements.clear();

            for (PlayerListEntry info : ORDERING.sortedCopy(profiles))
            {
                this.elements.add(new TeleportToSpecificPlayerSpectatorCommand(info.getProfile()));
            }
        }
    }
}
