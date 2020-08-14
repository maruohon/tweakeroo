package fi.dy.masa.tweakeroo.gui.widget;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseHotkeyedBooleanConfigWidget;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class FeatureToggleConfigWidget extends BaseHotkeyedBooleanConfigWidget<FeatureToggle>
{
    public FeatureToggleConfigWidget(int x, int y, int width, int height, int listIndex,
                                       int originalListIndex, FeatureToggle config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected BooleanConfig getBooleanConfig(FeatureToggle config)
    {
        return this.config.getBooleanConfig();
    }

    @Override
    protected KeyBind getKeyBind(FeatureToggle config)
    {
        return this.config.getKeyBind();
    }
}
