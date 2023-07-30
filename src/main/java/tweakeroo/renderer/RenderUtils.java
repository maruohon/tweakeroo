package tweakeroo.renderer;

import org.apache.commons.lang3.tuple.Pair;

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

import malilib.config.value.HorizontalAlignment;
import malilib.config.value.HudAlignment;
import malilib.config.value.VerticalAlignment;
import malilib.gui.util.GuiUtils;
import malilib.render.ItemRenderUtils;
import malilib.render.RenderContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.inventory.BuiltinInventoryRenderDefinitions;
import malilib.render.inventory.InventoryRenderDefinition;
import malilib.render.inventory.InventoryRenderUtils;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.inventory.InventoryView;
import malilib.util.inventory.SlicedInventoryView;
import malilib.util.inventory.VanillaInventoryView;
import malilib.util.position.Vec2i;
import tweakeroo.config.Configs;
import tweakeroo.util.MiscUtils;
import tweakeroo.util.SnapAimMode;

public class RenderUtils
{
    private static long lastRotationChangeTime;

    public static void renderHotbarSwapOverlay(RenderContext ctx)
    {
        EntityPlayer player = GameUtils.getClientPlayer();

        if (player != null)
        {
            final int scaledWidth = GuiUtils.getScaledWindowWidth();
            final int scaledHeight = GuiUtils.getScaledWindowHeight();
            Vec2i val = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET.getValue();
            final int offX = val.x;
            final int offY = val.y;
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
            FontRenderer textRenderer = GameUtils.getClient().fontRenderer;

            malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
            malilib.render.RenderUtils.bindTexture(GuiInventory.INVENTORY_BACKGROUND);
            ShapeRenderUtils.renderTexturedRectangle256(x - 1, y - 1, z, 7, 83, 9 * 18, 3 * 18, ctx);

            for (int row = 1; row <= 3; row++)
            {
                textRenderer.drawStringWithShadow(String.valueOf(row), x - 10, y + 4, 0xFFFFFF);

                for (int column = 0; column < 9; column++)
                {
                    ItemStack stack = player.inventory.getStackInSlot(row * 9 + column);

                    if (ItemWrap.notEmpty(stack))
                    {
                        ItemRenderUtils.renderStackAt(stack, x, y, z, 1f, RenderContext.DUMMY);
                    }

                    x += 18;
                }

                y += 18;
                x = startX;
            }
        }
    }

    public static void renderPointedInventoryOverlay()
    {
        Pair<InventoryView, InventoryRenderDefinition> pair = InventoryRenderUtils.getPointedInventory();

        if (pair != null)
        {
            final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
            final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
            InventoryView inv = pair.getLeft();
            InventoryRenderDefinition renderer = pair.getRight();
            InventoryRenderUtils.renderInventoryPreview(inv, renderer, xCenter, yCenter, 300, 0xFFFFFFFF,
                                                        HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, RenderContext.DUMMY);
        }
    }

    public static void renderPlayerInventoryPeekOverlay()
    {
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        InventoryView inv = new SlicedInventoryView(new VanillaInventoryView(GameUtils.getPlayerInventory()), 9, 27);

        InventoryRenderDefinition renderer = BuiltinInventoryRenderDefinitions.GENERIC_27;
        InventoryRenderUtils.renderInventoryPreview(inv, renderer, xCenter, yCenter, 300, 0xFFFFFFFF,
                                                    HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, RenderContext.DUMMY);
    }

    public static void renderHotbarScrollOverlay(RenderContext ctx)
    {
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        final int x = xCenter - 176 / 2;
        final int y = yCenter + 6;
        final int z = 300;

        InventoryView inv = new VanillaInventoryView(GameUtils.getPlayerInventory());

        InventoryRenderDefinition renderer = BuiltinInventoryRenderDefinitions.PLAYER_INVENTORY;
        InventoryRenderUtils.renderInventoryPreview(inv, renderer, xCenter, y, z, 0xFFFFFFFF,
                                                    HorizontalAlignment.CENTER, VerticalAlignment.TOP, RenderContext.DUMMY);

        int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
        ShapeRenderUtils.renderOutline(x + 5, y + currentRow * 18 + 5, z + 1f, 9 * 18 + 4, 22, 2, 0xFFFF2020, ctx);
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
        Entity entity = GameUtils.getCameraEntity();
        GlStateManager.rotate(entity.prevRotationPitch + (EntityWrap.getPitch(entity) - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationYaw + (EntityWrap.getYaw(entity) - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, -1.0F);
        OpenGlHelper.renderDirections(10);

        GlStateManager.popMatrix();
    }

    public static void notifyRotationChanged()
    {
        lastRotationChangeTime = System.nanoTime();
    }

    public static void renderSnapAimAngleIndicator(RenderContext ctx)
    {
        long current = System.nanoTime();

        if (current - lastRotationChangeTime < 750000000L)
        {
            final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
            final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
            SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getValue();
            FontRenderer textRenderer = GameUtils.getClient().fontRenderer;

            if (mode != SnapAimMode.PITCH)
            {
                renderSnapAimAngleIndicatorYaw(xCenter, yCenter, 80, 12, textRenderer, ctx);
            }

            if (mode != SnapAimMode.YAW)
            {
                renderSnapAimAngleIndicatorPitch(xCenter, yCenter, 12, 50, textRenderer, ctx);
            }
        }
    }

    private static void renderSnapAimAngleIndicatorYaw(int xCenter, int yCenter, int width, int height,
                                                       FontRenderer textRenderer, RenderContext ctx)
    {
        double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
        double realYaw = MathHelper.positiveModulo(MiscUtils.getLastRealYaw(), 360.0D);
        double snappedYaw = MiscUtils.calculateSnappedAngle(realYaw, step);
        double startYaw = snappedYaw - (step / 2.0);
        final int x = xCenter - width / 2;
        final int y = yCenter + 10;
        final int z = 0;
        int lineX = x + (int) ((MathHelper.wrapDegrees(realYaw - startYaw)) / step * width);

        malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        String str = MathHelper.wrapDegrees(snappedYaw) + "°";
        textRenderer.drawString(str, xCenter - textRenderer.getStringWidth(str) / 2, y + height + 2, 0xFFFFFFFF);

        str = "<  " + MathHelper.wrapDegrees(snappedYaw - step) + "°";
        textRenderer.drawString(str, x - textRenderer.getStringWidth(str), y + height + 2, 0xFFFFFFFF);

        str = MathHelper.wrapDegrees(snappedYaw + step) + "°  >";
        textRenderer.drawString(str, x + width, y + height + 2, 0xFFFFFFFF);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        int bgColor = Configs.Generic.SNAP_AIM_INDICATOR_COLOR.getIntegerValue();

        // Draw the main box
        ShapeRenderUtils.renderOutlinedRectangle(x, y, z, width, height, bgColor, 0xFFFFFFFF, builder);

        if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue())
        {
            double threshold = Configs.Generic.SNAP_AIM_THRESHOLD_YAW.getDoubleValue();
            int snapThreshOffset = (int) (width * threshold / step);

            // Draw the middle region
            ShapeRenderUtils.renderRectangle(xCenter - snapThreshOffset, y + 1, z, snapThreshOffset * 2, height - 2, 0x6050FF50, builder);

            if (threshold < (step / 2.0))
            {
                ShapeRenderUtils.renderRectangle(xCenter - snapThreshOffset, y + 1, z, 2, height - 2, 0xFF20FF20, builder);
                ShapeRenderUtils.renderRectangle(xCenter + snapThreshOffset, y + 1, z, 2, height - 2, 0xFF20FF20, builder);
            }
        }

        // Draw the current angle indicator
        ShapeRenderUtils.renderRectangle(lineX, y, z, 2, height, 0xFFFFFFFF, builder);

        builder.draw();
    }

    private static void renderSnapAimAngleIndicatorPitch(int xCenter, int yCenter, int width, int height,
                                                         FontRenderer textRenderer, RenderContext ctx)
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

        renderPitchIndicator(x, y, width, height, realPitch, snappedPitch, step, true, textRenderer, ctx);
    }

    public static void renderPitchLockIndicator(RenderContext ctx)
    {
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        int width = 12;
        int height = 50;
        int x = xCenter - width / 2;
        int y = yCenter - height - 10;
        double currentPitch = EntityWrap.getPitch(GameUtils.getClientPlayer());
        double centerPitch = 0;
        double indicatorRange = 180;

        renderPitchIndicator(x, y, width, height, currentPitch, centerPitch, indicatorRange,
                             false, GameUtils.getClient().fontRenderer, ctx);
    }

    private static void renderPitchIndicator(int x, int y, int width, int height,
                                             double currentPitch, double centerPitch,
                                             double indicatorRange, boolean isSnapRange,
                                             FontRenderer textRenderer, RenderContext ctx)
    {
        double startPitch = centerPitch - (indicatorRange / 2.0);
        double printedRange = isSnapRange ? indicatorRange : indicatorRange / 2;
        int lineY = y + (int) ((MathHelper.wrapDegrees(currentPitch) - startPitch) / indicatorRange * height);
        int z = 0;
        double angleUp = centerPitch - printedRange;
        double angleDown = centerPitch + printedRange;

        malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

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
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        // Draw the main box
        ShapeRenderUtils.renderOutlinedRectangle(x, y, z, width, height, bgColor, 0xFFFFFFFF, builder);

        int yCenter = y + height / 2 - 1;

        if (isSnapRange && Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue())
        {
            double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
            double threshold = Configs.Generic.SNAP_AIM_THRESHOLD_PITCH.getDoubleValue();
            int snapThreshOffset = (int) ((double) height * threshold / indicatorRange);

            ShapeRenderUtils.renderRectangle(x + 1, yCenter - snapThreshOffset, z, width - 2, snapThreshOffset * 2, 0x6050FF50, builder);

            if (threshold < (step / 2.0))
            {
                ShapeRenderUtils.renderRectangle(x + 1, yCenter - snapThreshOffset, z, width - 2, 2, 0xFF20FF20, builder);
                ShapeRenderUtils.renderRectangle(x + 1, yCenter + snapThreshOffset, z, width - 2, 2, 0xFF20FF20, builder);
            }
        }
        else if (isSnapRange == false)
        {
            ShapeRenderUtils.renderRectangle(x + 1, yCenter - 1, z, width - 2, 2, 0xFFC0C0C0, builder);
        }

        // Draw the current angle indicator
        ShapeRenderUtils.renderRectangle(x, lineY - 1, z, width, 2, 0xFFFFFFFF, builder);

        builder.draw();
    }
}
