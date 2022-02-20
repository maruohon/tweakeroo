package fi.dy.masa.tweakeroo.gui;

import fi.dy.masa.malilib.gui.config.liteloader.RedirectingConfigPanel;

public class TweakerooConfigPanel extends RedirectingConfigPanel
{
    public TweakerooConfigPanel()
    {
        super(ConfigScreen::create);
    }
}
