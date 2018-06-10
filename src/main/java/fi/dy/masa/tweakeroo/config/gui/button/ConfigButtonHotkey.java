package fi.dy.masa.tweakeroo.config.gui.button;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.tweakeroo.config.gui.ConfigPanelHotkeysBase;
import fi.dy.masa.tweakeroo.config.interfaces.IHotkey;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;
import net.minecraft.util.text.TextFormatting;

public class ConfigButtonHotkey extends ConfigButtonBase
{
    private final ConfigPanelHotkeysBase host;
    private final IHotkey hotkey;
    private boolean selected;
    private boolean firstKey;

    public ConfigButtonHotkey(int id, int x, int y, int width, int height, IHotkey hotkey, ConfigPanelHotkeysBase host)
    {
        super(id, x, y, width, height);

        this.host = host;
        this.hotkey = hotkey;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        if (this.selected)
        {
            this.addKey(mouseButton - 100);
            this.updateDisplayString();
        }
        else if (mouseButton == 0)
        {
            this.selected = true;
            this.host.setActiveButton(this);
        }
    }

    public void onKeyPressed(int keyCode)
    {
        if (this.selected)
        {
            IKeybind keybind = this.hotkey.getKeybind();

            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                keybind.clearKeys();
                this.host.setActiveButton(null);
            }
            else
            {
                this.addKey(keyCode);
            }

            this.updateDisplayString();
        }
    }

    private void addKey(int keyCode)
    {
        IKeybind keybind = this.hotkey.getKeybind();

        if (this.firstKey)
        {
            keybind.clearKeys();
            this.firstKey = false;
        }

        keybind.addKey(keyCode);
    }

    public void onSelected()
    {
        this.selected = true;
        this.firstKey = true;
        this.updateDisplayString();
    }

    public void onClearSelection()
    {
        this.selected = false;
        this.updateDisplayString();
    }

    private void updateDisplayString()
    {
        String valueStr = this.hotkey.getKeybind().getKeysDisplayString();

        if (this.hotkey.getKeybind().isValid() == false || StringUtils.isBlank(valueStr))
        {
            valueStr = "NONE";
        }

        if (this.selected)
        {
            this.displayString = "> " + TextFormatting.YELLOW + valueStr + TextFormatting.RESET + " <";
        }
        /*
        else if (isConflicted(this.hotkey.getKeybind()))
        {
            this.displayString = TextFormatting.RED + valueStr + TextFormatting.RESET;
        }
        */
        else
        {
            this.displayString = valueStr;
        }
    }
}
