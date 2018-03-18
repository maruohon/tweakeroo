package fi.dy.masa.tweakeroo.config.interfaces;

public interface IConfigHotkey extends IConfig
{
    IKeybind getKeybind();

    void setKeybind(IKeybind keybind);
}
