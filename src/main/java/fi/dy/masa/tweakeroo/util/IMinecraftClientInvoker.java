package fi.dy.masa.tweakeroo.util;

public interface IMinecraftClientInvoker
{
    void tweakeroo_setItemUseCooldown(int value);

    boolean tweakeroo_invokeDoAttack();

    void tweakeroo_invokeDoItemUse();
}
