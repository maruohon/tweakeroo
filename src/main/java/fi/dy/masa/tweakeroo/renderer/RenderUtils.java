package fi.dy.masa.tweakeroo.renderer;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.mixin.IMixinAbstractHorse;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class RenderUtils
{
    private static long lastRotationChangeTime;

    public static void renderHotbarSwapOverlay(Minecraft mc)
    {
        EntityPlayer player = mc.player;

        if (player != null)
        {
            ScaledResolution res = new ScaledResolution(mc);
            final int offX = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET_X.getIntegerValue();
            final int offY = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET_Y.getIntegerValue();
            int startX = offX;
            int startY = offY;

            fi.dy.masa.malilib.config.HudAlignment align = (fi.dy.masa.malilib.config.HudAlignment) Configs.Generic.HOTBAR_SWAP_OVERLAY_ALIGNMENT.getOptionListValue();

            switch (align)
            {
                case TOP_RIGHT:
                    startX = (int) res.getScaledWidth() - offX - 9 * 18;
                    break;
                case BOTTOM_LEFT:
                    startY = (int) res.getScaledHeight() - offY - 3 * 18;
                    break;
                case BOTTOM_RIGHT:
                    startX = (int) res.getScaledWidth() - offX - 9 * 18;
                    startY = (int) res.getScaledHeight() - offY - 3 * 18;
                    break;
                case CENTER:
                    startX = (int) res.getScaledWidth() / 2 - offX - 9 * 18 / 2;
                    startY = (int) res.getScaledHeight() / 2 - offY - 3 * 18 / 2;
                    break;
                default:
            }

            int x = startX;
            int y = startY;
            FontRenderer textRenderer = mc.fontRenderer;

            fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
            fi.dy.masa.malilib.render.RenderUtils.bindTexture(GuiInventory.INVENTORY_BACKGROUND);
            fi.dy.masa.malilib.render.RenderUtils.drawTexturedRect(x - 1, y - 1, 7, 83, 9 * 18, 3 * 18);

            for (int row = 1; row <= 3; row++)
            {
                textRenderer.drawStringWithShadow(String.valueOf(row), x - 10, y + 4, 0xFFFFFF);

                for (int column = 0; column < 9; column++)
                {
                    ItemStack stack = player.inventory.getStackInSlot(row * 9 + column);

                    if (stack.isEmpty() == false)
                    {
                        fi.dy.masa.malilib.render.InventoryOverlay.renderStackAt(stack, x, y, 1, mc);
                    }

                    x += 18;
                }

                y += 18;
                x = startX;
            }
        }
    }

    public static void renderInventoryOverlay(Minecraft mc)
    {
        World world = fi.dy.masa.malilib.util.WorldUtils.getBestWorld(mc);

        // We need to get the player from the server world, so that the player itself won't be included in the ray trace
        EntityPlayer player = world.getPlayerEntityByUUID(mc.player.getUniqueID());

        if (player == null)
        {
            player = mc.player;
        }

        RayTraceResult trace = RayTraceUtils.getRayTraceFromEntity(world, player, false);

        if (trace == null)
        {
            return;
        }

        IInventory inv = null;
        BlockShulkerBox block = null;
        EntityLivingBase entityLivingBase = null;

        if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = trace.getBlockPos();
            TileEntity te = world.getTileEntity(pos);

            if (te instanceof IInventory)
            {
                inv = (IInventory) te;
                IBlockState state = world.getBlockState(pos);

                if (state.getBlock() instanceof BlockChest)
                {
                    ILockableContainer cont = ((BlockChest) state.getBlock()).getLockableContainer(world, pos);

                    if (cont instanceof InventoryLargeChest)
                    {
                        inv = (InventoryLargeChest) cont;
                    }
                }

                Block blockTmp = world.getBlockState(pos).getBlock();

                if (blockTmp instanceof BlockShulkerBox)
                {
                    block = (BlockShulkerBox) blockTmp;
                }
            }
        }
        else if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
        {
            Entity entity = trace.entityHit;

            if (entity instanceof EntityLivingBase)
            {
                entityLivingBase = (EntityLivingBase) entity;
            }

            if (entity instanceof IInventory)
            {
                inv = (IInventory) entity;
            }
            else if (entity instanceof EntityVillager)
            {
                inv = ((EntityVillager) entity).getVillagerInventory();
            }
            else if (entity instanceof AbstractHorse)
            {
                inv = ((IMixinAbstractHorse) entity).getHorseChest();
            }
        }

        ScaledResolution res = new ScaledResolution(mc);
        final int xCenter = res.getScaledWidth() / 2;
        final int yCenter = res.getScaledHeight() / 2;
        int x = xCenter - 52 / 2;
        int y = yCenter - 92;

        if (inv != null && inv.getSizeInventory() > 0)
        {
            final boolean isHorse = (entityLivingBase instanceof AbstractHorse);
            final int totalSlots = isHorse ? inv.getSizeInventory() - 2 : inv.getSizeInventory();
            final int firstSlot = isHorse ? 2 : 0;

            final fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = (entityLivingBase instanceof EntityVillager) ? fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType.VILLAGER : fi.dy.masa.malilib.render.InventoryOverlay.getInventoryType(inv);
            final fi.dy.masa.malilib.render.InventoryOverlay.InventoryProperties props = fi.dy.masa.malilib.render.InventoryOverlay.getInventoryPropsTemp(type, totalSlots);
            final int rows = (int) Math.ceil(totalSlots / props.slotsPerRow);
            int xInv = xCenter - (props.width / 2);
            int yInv = yCenter - props.height - 6;

            if (rows > 6)
            {
                yInv -= (rows - 6) * 18;
                y -= (rows - 6) * 18;
            }

            if (entityLivingBase != null)
            {
                x = xCenter - 55;
                xInv = xCenter + 2;
                yInv = Math.min(yInv, yCenter - 92);
            }

            fi.dy.masa.malilib.render.RenderUtils.setShulkerboxBackgroundTintColor(block, Configs.Generic.SHULKER_DISPLAY_BACKGROUND_COLOR.getBooleanValue());

            if (isHorse)
            {
                fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
                fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc);
                xInv += 32 + 4;
            }

            if (totalSlots > 0)
            {
                fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc);
                fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, firstSlot, totalSlots, mc);
            }
        }

        if (entityLivingBase != null)
        {
            fi.dy.masa.malilib.render.InventoryOverlay.renderEquipmentOverlayBackground(x, y, entityLivingBase);
            fi.dy.masa.malilib.render.InventoryOverlay.renderEquipmentStacks(entityLivingBase, x, y, mc);
        }
    }

    public static void renderPlayerInventoryOverlay(Minecraft mc)
    {
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 - 176 / 2;
        int y = res.getScaledHeight() / 2 + 10;
        int slotOffsetX = 8;
        int slotOffsetY = 8;
        fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType.GENERIC;

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, x, y, 9, 27, mc);
        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, mc.player.inventory, x + slotOffsetX, y + slotOffsetY, 9, 9, 27, mc);
    }

    public static void renderHotbarScrollOverlay(Minecraft mc)
    {
        IInventory inv = mc.player.inventory;
        ScaledResolution res = new ScaledResolution(mc);
        final int xCenter = res.getScaledWidth() / 2;
        final int yCenter = res.getScaledHeight() / 2;
        final int x = xCenter - 176 / 2;
        final int y = yCenter + 6;
        fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType.GENERIC;

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, x, y     , 9, 27, mc);
        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, x, y + 70, 9,  9, mc);

        // Main inventory
        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, inv, x + 8, y +  8, 9, 9, 27, mc);
        // Hotbar
        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, inv, x + 8, y + 78, 9, 0,  9, mc);

        int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
        fi.dy.masa.malilib.render.RenderUtils.drawOutline(x + 5, y + currentRow * 18 + 5, 9 * 18 + 4, 22, 2, 0xFFFF2020);
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

    public static void renderDirectionsCursor(ScaledResolution sr, float zLevel, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();

        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        GlStateManager.translate(width / 2, height / 2, zLevel);
        Entity entity = mc.getRenderViewEntity();
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
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution res = new ScaledResolution(mc);
            final int xCenter = res.getScaledWidth() / 2;
            final int yCenter = res.getScaledHeight() / 2;
            SnapAimMode mode = (SnapAimMode) Configs.Generic.SNAP_AIM_MODE.getOptionListValue();

            if (mode != SnapAimMode.PITCH)
            {
                renderSnapAimAngleIndicatorYaw(xCenter, yCenter, 80, 10, mc);
            }

            if (mode != SnapAimMode.YAW)
            {
                renderSnapAimAngleIndicatorPitch(xCenter, yCenter, 10, 50, mc);
            }
        }
    }

    private static void renderSnapAimAngleIndicatorYaw(int xCenter, int yCenter, int width, int height, Minecraft mc)
    {
        double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
        double realYaw = MathHelper.positiveModulo(MiscUtils.getLastRealYaw(), 360.0D);
        double snappedYaw = MiscUtils.calculateSnappedAngle(realYaw, step);
        double startYaw = snappedYaw - (step / 2.0);
        int x = xCenter - width / 2;
        int y = yCenter + 10;
        int lineX = x + (int) ((MathHelper.wrapDegrees(realYaw - startYaw)) / step * width);
        FontRenderer textRenderer = mc.fontRenderer;

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

        int bgColor = Configs.Generic.SNAP_AIM_INDICATOR_COLOR.getIntegerValue();
        fi.dy.masa.malilib.render.RenderUtils.drawOutlinedBox(x, y, width, height, bgColor, 0xFFFFFFFF);

        fi.dy.masa.malilib.render.RenderUtils.drawRect(lineX, y, 2, height, 0xFFFFFFFF);

        String str = String.valueOf(MathHelper.wrapDegrees(snappedYaw)) + "°";
        textRenderer.drawString(str, xCenter - textRenderer.getStringWidth(str) / 2, y + height + 2, 0xFFFFFFFF);

        str = "<  " + String.valueOf(MathHelper.wrapDegrees(snappedYaw - step)) + "°";
        textRenderer.drawString(str, x - textRenderer.getStringWidth(str), y + height + 2, 0xFFFFFFFF);

        str = String.valueOf(MathHelper.wrapDegrees(snappedYaw + step)) + "°  >";
        textRenderer.drawString(str, x + width, y + height + 2, 0xFFFFFFFF);
    }

    private static void renderSnapAimAngleIndicatorPitch(int xCenter, int yCenter, int width, int height, Minecraft mc)
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

        renderPitchIndicator(x, y, width, height, realPitch, snappedPitch, step, true, mc);
    }

    public static void renderPitchLockIndicator(Minecraft mc)
    {
        ScaledResolution res = new ScaledResolution(mc);
        final int xCenter = res.getScaledWidth() / 2;
        final int yCenter = res.getScaledHeight() / 2;
        int width = 10;
        int height = 50;
        int x = xCenter - width / 2;
        int y = yCenter - height - 10;
        double currentPitch = mc.player.rotationPitch;
        double centerPitch = 0;
        double indicatorRange = 180;

        renderPitchIndicator(x, y, width, height, currentPitch, centerPitch, indicatorRange, false, mc);
    }

    private static void renderPitchIndicator(int x, int y, int width, int height,
            double currentPitch, double centerPitch, double indicatorRange, boolean isSnapRange, Minecraft mc)
    {
        double startPitch = centerPitch - (indicatorRange / 2.0);
        double printedRange = isSnapRange ? indicatorRange : indicatorRange / 2;
        int lineY = y + (int) ((MathHelper.wrapDegrees(currentPitch) - startPitch) / indicatorRange * height);
        double angleUp = centerPitch - printedRange;
        double angleDown = centerPitch + printedRange;
        String strUp, strDown;

        if (isSnapRange)
        {
            strUp   = String.format("%6.1f° ^", MathHelper.wrapDegrees(angleUp));
            strDown = String.format("%6.1f° v", MathHelper.wrapDegrees(angleDown));
        }
        else
        {
            strUp   = String.format("%6.1f°", angleUp);
            strDown = String.format("%6.1f°", angleDown);
        }

        fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);
        FontRenderer textRenderer = mc.fontRenderer;

        int bgColor = Configs.Generic.SNAP_AIM_INDICATOR_COLOR.getIntegerValue();
        fi.dy.masa.malilib.render.RenderUtils.drawOutlinedBox(x, y, width, height, bgColor, 0xFFFFFFFF);

        fi.dy.masa.malilib.render.RenderUtils.drawRect(x - 1, lineY - 1, width + 2, 2, 0xFFFFFFFF);

        String str = String.format("%6.1f°", MathHelper.wrapDegrees(isSnapRange ? centerPitch : currentPitch));
        textRenderer.drawString(str, x + width + 4, y + height / 2 - 4, 0xFFFFFFFF);

        //textRenderer.drawString(strUp, x - textRenderer.getStringWidth(strUp) - 4, y - 4, 0xFFFFFFFF);
        textRenderer.drawString(strUp, x + width + 4, y - 4, 0xFFFFFFFF);
        textRenderer.drawString(strDown, x + width + 4, y + height - 4, 0xFFFFFFFF);
    }
}
