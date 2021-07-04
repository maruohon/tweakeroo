package fi.dy.masa.tweakeroo.gui.widget.info;

import fi.dy.masa.malilib.overlay.widget.sub.HotkeyedBooleanConfigStatusWidget;
import fi.dy.masa.malilib.util.data.ConfigOnTab;
import fi.dy.masa.tweakeroo.config.DisableToggle;

public class DisableConfigStatusWidget extends HotkeyedBooleanConfigStatusWidget
{
    public DisableConfigStatusWidget(DisableToggle config, ConfigOnTab configOnTab)
    {
        super(config.getBooleanConfig(), config::getKeyBind, configOnTab, "tweakeroo:csi_value_disable_toggle");
    }
}
