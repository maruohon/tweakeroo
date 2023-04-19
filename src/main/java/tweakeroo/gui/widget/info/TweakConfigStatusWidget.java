package tweakeroo.gui.widget.info;

import malilib.overlay.widget.sub.HotkeyedBooleanConfigStatusWidget;
import malilib.util.data.ConfigOnTab;
import tweakeroo.config.FeatureToggle;

public class TweakConfigStatusWidget extends HotkeyedBooleanConfigStatusWidget
{
    public TweakConfigStatusWidget(FeatureToggle config, ConfigOnTab configOnTab)
    {
        super(config.getBooleanConfig(), config::getKeyBind, configOnTab, "tweakeroo:csi_value_tweak_toggle");
    }
}
