package fi.dy.masa.tweakeroo.gui.widget.info;

import malilib.overlay.widget.sub.HotkeyedBooleanConfigStatusWidget;
import malilib.util.data.ConfigOnTab;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class TweakConfigStatusWidget extends HotkeyedBooleanConfigStatusWidget
{
    public TweakConfigStatusWidget(FeatureToggle config, ConfigOnTab configOnTab)
    {
        super(config.getBooleanConfig(), config::getKeyBind, configOnTab, "tweakeroo:csi_value_tweak_toggle");
    }
}
