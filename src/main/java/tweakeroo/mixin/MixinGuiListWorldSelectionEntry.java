package tweakeroo.mixin;

import java.text.DateFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.GuiListWorldSelectionEntry;

import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.MiscUtils;

@Mixin(GuiListWorldSelectionEntry.class)
public abstract class MixinGuiListWorldSelectionEntry
{
    @Shadow @Final private static DateFormat DATE_FORMAT;

    @Redirect(method = "drawEntry", at = @At(value = "FIELD",
              target = "Lnet/minecraft/client/gui/GuiListWorldSelectionEntry;DATE_FORMAT:Ljava/text/DateFormat;"))
    private DateFormat overrideModificationDateFormat()
    {
        if (FeatureToggle.TWEAK_WORLD_LIST_DATE_FORMAT.getBooleanValue())
        {
            return MiscUtils.getDateFormatFor(Configs.Generic.WORLD_LIST_DATE_FORMAT.getValue());
        }

        return DATE_FORMAT;
    }
}
