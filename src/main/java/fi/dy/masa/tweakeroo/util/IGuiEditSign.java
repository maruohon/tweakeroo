package fi.dy.masa.tweakeroo.util;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;

public interface IGuiEditSign
{
    SignBlockEntity getTile();

    void applyText(SignText text);
}
