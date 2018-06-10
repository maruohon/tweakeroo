package fi.dy.masa.tweakeroo.config.gui;

import javax.annotation.Nullable;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.tweakeroo.config.gui.button.ConfigButtonHotkey;

public abstract class ConfigPanelHotkeysBase extends ConfigPanelSub
{
    private ConfigButtonHotkey activeButton;

    public ConfigPanelHotkeysBase(String title, TweakerooConfigPanel parent)
    {
        super(title, parent);
    }

    public void setActiveButton(@Nullable ConfigButtonHotkey button)
    {
        if (this.activeButton != null)
        {
            this.activeButton.onClearSelection();
        }

        this.activeButton = button;

        if (this.activeButton != null)
        {
            this.activeButton.onSelected();
        }
    }

    @Override
    protected boolean mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        boolean handled = super.mousePressed(mouseX, mouseY, mouseButton);

        // When clicking on not-a-button, clear the selection
        if (handled == false && this.activeButton != null)
        {
            this.activeButton.onClearSelection();
            this.setActiveButton(null);
            return true;
        }

        return handled;
    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode)
    {
        if (this.activeButton != null)
        {
            this.activeButton.onKeyPressed(keyCode);
        }
        else
        {
            super.keyPressed(host, keyChar, keyCode);
        }
    }
}
