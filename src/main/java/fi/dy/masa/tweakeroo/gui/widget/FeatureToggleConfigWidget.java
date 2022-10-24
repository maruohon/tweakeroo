package fi.dy.masa.tweakeroo.gui.widget;

import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.config.BaseHotkeyedBooleanConfigWidget;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class FeatureToggleConfigWidget extends BaseHotkeyedBooleanConfigWidget<FeatureToggle>
{
    public FeatureToggleConfigWidget(FeatureToggle config,
                                     DataListEntryWidgetData constructData,
                                     ConfigWidgetContext ctx)
    {
        super(config, config.getBooleanConfig(), config.getKeyBind(), constructData, ctx);
    }
}
