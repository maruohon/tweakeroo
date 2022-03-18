package fi.dy.masa.tweakeroo.renderer;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.config.value.VerticalAlignment;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.ItemRenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.inventory.BuiltinInventoryRenderDefinitions;
import fi.dy.masa.malilib.render.inventory.InventoryRenderDefinition;
import fi.dy.masa.malilib.render.inventory.InventoryRenderUtils;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.inventory.InventoryView;
import fi.dy.masa.malilib.util.inventory.SlicedInventoryView;
import fi.dy.masa.malilib.util.inventory.VanillaInventoryView;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class RenderUtils
{
    private static long lastRotationChangeTime;

    public static void renderHotbarSwapOverlay(Minecraft mc)
    {
        EntityPlayer player = mc.player;

        if (player != null)
        {
            final int scaledWidth = GuiUtils.getScaledWindowWidth();
            final int scaledHeight = GuiUtils.getScaledWindowHeight();
            final int offX = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET_X.getIntegerValue();
            final int offY = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET_Y.getIntegerValue();
            int startX = offX;
            int startY = offY;

            HudAlignment alignment = Configs.Generic.HOTBAR_SWAP_OVERLAY_ALIGNMENT.getValue();

            if (alignment == HudAlignment.TOP_RIGHT)
            {
                startX = scaledWidth - offX - 9 * 18;
            }
            else if (alignment == HudAlignment.BOTTOM_LEFT)
            {
                startY = scaledHeight - offY - 3 * 18;
            }
            else if (alignment == HudAlignment.BOTTOM_RIGHT)
            {
                startX = scaledWidth - offX - 9 * 18;
                startY = scaledHeight - offY - 3 * 18;
            }
            else if (alignment == HudAlignment.CENTER)
            {
                startX = scaledWidth / 2 - offX - 9 * 18 / 2;
                startY = scaledHeight / 2 - offY - 3 * 18 / 2;
            }

            int x = startX;
            int y = startY;
            int z = 0;
            FontRenderer textRenderer = mc.fontRenderer;

            fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
            fi.dy.masa.malilib.render.RenderUtils.bindTexture(GuiInventory.INVENTORY_BACKGROUND);
            ShapeRenderUtils.renderTexturedRectangle256(x - 1, y - 1, z, 7, 83, 9 * 18, 3 * 18);

            for (int row = 1; row <= 3; row++)
            {
                textRenderer.drawStringWithShadow(String.valueOf(row), x - 10, y + 4, 0xFFFFFF);

                for (int column = 0; column < 9; column++)
                {
                    ItemStack stack = player.inventory.getStackInSlot(row * 9 + column);

                    if (stack.isEmpty() == false)
                    {
                        ItemRenderUtils.renderStackAt(stack, x, y, z, 1f, mc);
                    }

                    x += 18;
                }

                y += 18;
                x = startX;
            }
        }
    }

    public static void renderPointedInventoryOverlay(Minecraft mc)
    {
        Pair<InventoryView, InventoryRenderDefinition> pair = InventoryRenderUtils.getPointedInventory(mc);

        if (pair != null)
        {
            final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
            final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
            InventoryView inv = pair.getLeft();
            InventoryRenderDefinition renderer = pair.getRight();
            InventoryRenderUtils.renderInventoryPreview(inv, renderer, xCenter, yCenter, 300, 0xFFFFFFFF,
                                                        HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        }
    }

    public static void renderPlayerInventoryPeekOverlay(Minecraft mc)
    {
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        InventoryView inv = new SlicedInventoryView(new VanillaInventoryView(mc.player.inventory), 9, 27);

        InventoryRenderDefinition renderer = BuiltinInventoryRenderDefinitions.GENERIC_27;
        InventoryRenderUtils.renderInventoryPreview(inv, renderer, xCenter, yCenter, 300, 0xFFFFFFFF,
                                                    HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    }

    public static void renderHotbarScrollOverlay(Minecraft mc)
    {
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        final int x = xCenter - 176 / 2;
        final int y = yCenter + 6;
        final int z = 300;

        InventoryView inv = new VanillaInventoryView(mc.player.inventory);

        InventoryRenderDefinition renderer = BuiltinInventoryRenderDefinitions.PLAYER_INVENTORY;
        InventoryRenderUtils.renderInventoryPreview(inv, renderer, xCenter, y, z, 0xFFFFFFFF,
                                                    HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
        ShapeRenderUtils.renderOutline(x + 5, y + currentRow * 18 + 5, z + 1f, 9 * 18 + 4, 22, 2, 0xFFFF2020);
    }

    public static float getLavaFog(Entity entity, float originalFog)
    {
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase living = (EntityLivingBase) entity;
            final int resp = EnchantmentHelper.getRespirationModifier(living);
            // The original fog value of 2.0F is way too much to reduce gradually from.
            // You would only be able to see meaningfully with the full reduction.
            final float baseFog = 0.6F;
            final float respDecrement = (baseFog * 0.75F) / 3F - 0.02F;
            float fog = baseFog;

            if (living.isPotionActive(MobEffects.WATER_BREATHING))
            {
                fog -= baseFog * 0.4F;
            }

            if (resp > 0)
            {
                fog -= (float) resp * respDecrement;
                fog = Math.max(0.12F,  fog);
            }

            return fog < baseFog ? fog : originalFog;
        }

        return originalFog;
    }

    public static void overrideLavaFog(Entity entity)
    {
        float fog = getLavaFog(entity, 2.0F);

        if (fog < 2.0F)
        {
            GlStateManager.setFogDensity(fog);
        }
    }

    public static void renderDirectionsCursor(float zLevel, float partialTicks)
    {
        GlStateManager.pushMatrix();

        int width = GuiUtils.getScaledWindowWidth();
        int height = GuiUtils.getScaledWindowHeight();
        GlStateManager.translate(width / 2.0F, height / 2.0F, zLevel);
        Entity entity = GameUtils.getClient().getRenderViewEntity();
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, -1.0F);
        OpenGlHelper.renderDirections(10);

        GlStateManager.popMatrix();
    }

    public static void notifyRotationChanged()
    {
        lastRotationChangeTime = System.currentTimeMillis();
    }

    public static void renderSnapAimAngleIndicator()
    {
        long current = System.currentTimeMillis();

        if (current - lastRotationChangeTime < 750)
        {
            final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
            final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
            SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getValue();
            FontRenderer textRenderer = GameUtils.getClient().fontRenderer;

            if (mode != SnapAimMode.PITCH)
            {
                renderSnapAimAngleIndicatorYaw(xCenter, yCenter, 80, 12, textRenderer);
            }

            if (mode != SnapAimMode.YAW)
            {
                renderSnapAimAngleIndicatorPitch(xCenter, yCenter, 12, 50, textRenderer);
            }
        }
    }

    private static void renderSnapAimAngleIndicatorYaw(int xCenter, int yCenter, int width, int height, FontRenderer textRenderer)
    {
        double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
        double realYaw = MathHelper.positiveModulo(MiscUtils.getLastRealYaw(), 360.0D);
        double snappedYaw = MiscUtils.calculateSnappedAngle(realYaw, step);
        double startYaw = snappedYaw - (step / 2.0);
        final int x = xCenter - width / 2;
        final int y = yCenter + 10;
        final int z = 0;
        int lineX = x + (int) ((MathHelper.wrapDegrees(realYaw - startYaw)) / step * width);

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        int bgColor = Configs.Generic.SNAP_AIM_INDICATOR_COLOR.getIntegerValue();

        // Draw the main box
        ShapeRenderUtils.renderOutlinedRectangle(x, y, z, width, height, bgColor, 0xFFFFFFFF);

        String str = MathHelper.wrapDegrees(snappedYaw) + "°";
        textRenderer.drawString(str, xCenter - textRenderer.getStringWidth(str) / 2, y + height + 2, 0xFFFFFFFF);

        str = "<  " + MathHelper.wrapDegrees(snappedYaw - step) + "°";
        textRenderer.drawString(str, x - textRenderer.getStringWidth(str), y + height + 2, 0xFFFFFFFF);

        str = MathHelper.wrapDegrees(snappedYaw + step) + "°  >";
        textRenderer.drawString(str, x + width, y + height + 2, 0xFFFFFFFF);

        if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue())
        {
            double threshold = Configs.Generic.SNAP_AIM_THRESHOLD_YAW.getDoubleValue();
            int snapThreshOffset = (int) (width * threshold / step);

            // Draw the middle region
            ShapeRenderUtils.renderRectangle(xCenter - snapThreshOffset, y + 1, z, snapThreshOffset * 2, height - 2, 0x6050FF50);

            if (threshold < (step / 2.0))
            {
                ShapeRenderUtils.renderRectangle(xCenter - snapThreshOffset, y + 1, z, 2, height - 2, 0xFF20FF20);
                ShapeRenderUtils.renderRectangle(xCenter + snapThreshOffset, y + 1, z, 2, height - 2, 0xFF20FF20);
            }
        }

        // Draw the current angle indicator
        ShapeRenderUtils.renderRectangle(lineX, y, z, 2, height, 0xFFFFFFFF);
    }

    private static void renderSnapAimAngleIndicatorPitch(int xCenter, int yCenter, int width, int height, FontRenderer textRenderer)
    {
        double step = Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue();
        int limit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;
        //double realPitch = MathHelper.clamp(MathHelper.wrapDegrees(MiscUtils.getLastRealPitch()), -limit, limit);
        double realPitch = MathHelper.wrapDegrees(MiscUtils.getLastRealPitch());
        double snappedPitch;

        if (realPitch < 0)
        {
            snappedPitch = -MiscUtils.calculateSnappedAngle(-realPitch, step);
        }
        else
        {
            snappedPitch = MiscUtils.calculateSnappedAngle(realPitch, step);
        }

        snappedPitch = MathHelper.clamp(MathHelper.wrapDegrees(snappedPitch), -limit, limit);

        int x = xCenter - width / 2;
        int y = yCenter - height - 10;

        renderPitchIndicator(x, y, width, height, realPitch, snappedPitch, step, true, textRenderer);
    }

    public static void renderPitchLockIndicator(Minecraft mc)
    {
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        int width = 12;
        int height = 50;
        int x = xCenter - width / 2;
        int y = yCenter - height - 10;
        double currentPitch = mc.player.rotationPitch;
        double centerPitch = 0;
        double indicatorRange = 180;

        renderPitchIndicator(x, y, width, height, currentPitch, centerPitch, indicatorRange, false, mc.fontRenderer);
    }

    private static void renderPitchIndicator(int x, int y, int width, int height,
            double currentPitch, double centerPitch, double indicatorRange, boolean isSnapRange, FontRenderer textRenderer)
    {
        double startPitch = centerPitch - (indicatorRange / 2.0);
        double printedRange = isSnapRange ? indicatorRange : indicatorRange / 2;
        int lineY = y + (int) ((MathHelper.wrapDegrees(currentPitch) - startPitch) / indicatorRange * height);
        int z = 0;
        double angleUp = centerPitch - printedRange;
        double angleDown = centerPitch + printedRange;

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        if (isSnapRange)
        {
            String strUp   = String.format("%6.1f° ^", MathHelper.wrapDegrees(angleUp));
            String strDown = String.format("%6.1f° v", MathHelper.wrapDegrees(angleDown));
            textRenderer.drawString(strUp, x + width + 4, y - 4, 0xFFFFFFFF);
            textRenderer.drawString(strDown, x + width + 4, y + height - 4, 0xFFFFFFFF);

            String str = String.format("%6.1f°", MathHelper.wrapDegrees(isSnapRange ? centerPitch : currentPitch));
            textRenderer.drawString(str, x + width + 4, y + height / 2 - 4, 0xFFFFFFFF);
        }
        else
        {
            String str = String.format("%4.1f°", MathHelper.wrapDegrees(isSnapRange ? centerPitch : currentPitch));
            textRenderer.drawString(str, x + width + 4, lineY - 4, 0xFFFFFFFF);
        }

        int bgColor = Configs.Generic.SNAP_AIM_INDICATOR_COLOR.getIntegerValue();
        // Draw the main box
        ShapeRenderUtils.renderOutlinedRectangle(x, y, z, width, height, bgColor, 0xFFFFFFFF);

        int yCenter = y + height / 2 - 1;

        if (isSnapRange && Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue())
        {
            double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
            double threshold = Configs.Generic.SNAP_AIM_THRESHOLD_PITCH.getDoubleValue();
            int snapThreshOffset = (int) ((double) height * threshold / indicatorRange);

            ShapeRenderUtils.renderRectangle(x + 1, yCenter - snapThreshOffset, z, width - 2, snapThreshOffset * 2, 0x6050FF50);

            if (threshold < (step / 2.0))
            {
                ShapeRenderUtils.renderRectangle(x + 1, yCenter - snapThreshOffset, z, width - 2, 2, 0xFF20FF20);
                ShapeRenderUtils.renderRectangle(x + 1, yCenter + snapThreshOffset, z, width - 2, 2, 0xFF20FF20);
            }
        }
        else if (isSnapRange == false)
        {
            ShapeRenderUtils.renderRectangle(x + 1, yCenter - 1, z, width - 2, 2, 0xFFC0C0C0);
        }

        // Draw the current angle indicator
        ShapeRenderUtils.renderRectangle(x, lineY - 1, z, width, 2, 0xFFFFFFFF);
    }
}
