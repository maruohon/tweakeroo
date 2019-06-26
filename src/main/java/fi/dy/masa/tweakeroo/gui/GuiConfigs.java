package fi.dy.masa.tweakeroo.gui;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.resource.language.I18n;

public class GuiConfigs extends GuiConfigsBase
{
    private static ConfigGuiTab tab = ConfigGuiTab.TWEAK_TOGGLES;
    private int id;

    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, "tweakeroo.gui.title.configs");
    }

    @Override
    public void init()
    {
        super.init();
        this.clearOptions();

        this.id = 0;
        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values())
        {
            x += this.createButton(x, y, -1, tab) + 4;
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonListener listener = new ButtonListener(tab, this);
        boolean enabled = GuiConfigs.tab != tab;
        String label = tab.getDisplayName();

        if (width < 0)
        {
            width = this.textRenderer.getStringWidth(label) + 10;
        }

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);
        button.setEnabled(enabled);
        this.addButton(button, listener);

        return width;
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = GuiConfigs.tab;

        if (tab == ConfigGuiTab.GENERIC || tab == ConfigGuiTab.FIXES || tab == ConfigGuiTab.TWEAK_TOGGLES)
        {
            return 120;
        }
        else if (tab == ConfigGuiTab.LISTS)
        {
            return 200;
        }

        return super.getConfigWidth();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = GuiConfigs.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            configs = Configs.Generic.OPTIONS;
        }
        else if (tab == ConfigGuiTab.FIXES)
        {
            configs = Configs.Fixes.OPTIONS;
        }
        else if (tab == ConfigGuiTab.LISTS)
        {
            configs = Configs.Lists.OPTIONS;
        }
        else if (tab == ConfigGuiTab.TWEAK_TOGGLES)
        {
            configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values()));
        }
        else if (tab == ConfigGuiTab.TWEAK_HOTKEYS)
        {
            configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values()));
        }
        else if (tab == ConfigGuiTab.GENERIC_HOTKEYS)
        {
            configs = Hotkeys.HOTKEY_LIST;
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, GuiConfigs parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase control, int mouseButton)
        {
            GuiConfigs.tab = this.tab;

            if (this.tab != ConfigGuiTab.PLACEMENT)
            {
                this.parent.reCreateListWidget(); // apply the new config width
                this.parent.getListWidget().resetScrollbarPosition();
                this.parent.init();
            }
            else
            {
                //MinecraftClient.getInstance().displayScreen(new GuiPlacementSettings());
            }
        }
    }

    public enum ConfigGuiTab
    {
        GENERIC         ("tweakeroo.gui.button.config_gui.generic"),
        FIXES           ("tweakeroo.gui.button.config_gui.fixes"),
        LISTS           ("tweakeroo.gui.button.config_gui.lists"),
        TWEAK_TOGGLES   ("tweakeroo.gui.button.config_gui.tweak_toggle"),
        TWEAK_HOTKEYS   ("tweakeroo.gui.button.config_gui.tweak_hotkeys"),
        GENERIC_HOTKEYS ("tweakeroo.gui.button.config_gui.generic_hotkeys"),
        PLACEMENT       ("tweakeroo.gui.button.config_gui.placement");

        private final String translationKey;

        private ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return I18n.translate(this.translationKey);
        }
    }
}
