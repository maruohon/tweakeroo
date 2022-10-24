package fi.dy.masa.tweakeroo.gui;

import malilib.gui.config.liteloader.RedirectingConfigPanel;

public class TweakerooConfigPanel extends RedirectingConfigPanel
{
    public TweakerooConfigPanel()
    {
        super(ConfigScreen::create);
    }
}
