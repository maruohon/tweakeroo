package fi.dy.masa.tweakeroo.gui.widget;

import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseHotkeyedBooleanConfigWidget;
import fi.dy.masa.tweakeroo.config.DisableToggle;

public class DisableToggleConfigWidget extends BaseHotkeyedBooleanConfigWidget
{
    public DisableToggleConfigWidget(int x, int y, int width, int height, int listIndex,
                                     int originalListIndex, DisableToggle config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, config.getBooleanConfig(), config.getKeyBind(), ctx);
    }
}
