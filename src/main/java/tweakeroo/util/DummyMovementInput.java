package tweakeroo.util;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;

public class DummyMovementInput extends MovementInputFromOptions
{
    public DummyMovementInput(GameSettings gameSettingsIn)
    {
        super(gameSettingsIn);
    }

    @Override
    public void updatePlayerMoveState()
    {
        // NO-OP
    }
}
