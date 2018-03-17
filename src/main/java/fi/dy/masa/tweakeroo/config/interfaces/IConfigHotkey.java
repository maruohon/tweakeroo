package fi.dy.masa.tweakeroo.config.interfaces;

public interface IConfigHotkey extends IConfig
{
    int getBitMask();

    IKeybind getKeybind();

    void setKeybind(IKeybind keybind);
}
