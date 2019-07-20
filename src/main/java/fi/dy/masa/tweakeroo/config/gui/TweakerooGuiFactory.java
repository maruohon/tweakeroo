package fi.dy.masa.tweakeroo.config.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.gui.GuiConfigs;

public class TweakerooGuiFactory extends DefaultGuiFactory
{
    public TweakerooGuiFactory()
    {
        super(Reference.MOD_ID, Reference.MOD_NAME + " configs");
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        GuiConfigs gui = new GuiConfigs();
        gui.setParent(parent);
        return gui;
    }
}
