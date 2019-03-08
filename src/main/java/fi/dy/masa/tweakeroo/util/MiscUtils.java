package fi.dy.masa.tweakeroo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;

public class MiscUtils
{
    private static ITextComponent[] previousSignText;
    private static String previousChatText = "";
    private static final Date DATE = new Date();

    public static String getChatTimestamp()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Configs.Generic.CHAT_TIME_FORMAT.getStringValue());
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }

    public static void setLastChatText(String text)
    {
        previousChatText = text;
    }

    public static String getLastChatText()
    {
        return previousChatText;
    }

    public static int getChatBackgroundColor(int colorOrig)
    {
        int newColor = Configs.Generic.CHAT_BACKGROUND_COLOR.getIntegerValue();
        return (newColor & 0x00FFFFFF) | ((int) (((newColor >>> 24) / 255.0) * ((colorOrig >>> 24) / 255.0) / 0.5 * 255) << 24);
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
