package fi.dy.masa.tweakeroo.util;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;

public class DummyMovementInput extends KeyboardInput
{
    public DummyMovementInput(GameOptions options)
    {
        super(options);
    }

    @Override
    public void tick(boolean sneaking, float f)
    {
        // NO-OP
    }
}
