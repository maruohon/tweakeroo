package fi.dy.masa.tweakeroo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;

public class MiscUtils
{
    private static ITextComponent[] previousSignText;
    private static final Date DATE = new Date();

    public static String getChatTimestamp()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Configs.Generic.CHAT_TIME_FORMAT.getStringValue());
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }

    public static void copyTextFromSign(TileEntitySign te)
    {
        int size = te.signText.length;
        previousSignText = new ITextComponent[size];

        for (int i = 0; i < size; ++i)
        {
            previousSignText[i] = te.signText[i];
        }
    }

    public static void applyPreviousTextToSign(TileEntitySign te)
    {
        if (previousSignText != null)
        {
            int size = Math.min(te.signText.length, previousSignText.length);

            for (int i = 0; i < size; ++i)
            {
                te.signText[i] = previousSignText[i];
            }
        }
    }
}
