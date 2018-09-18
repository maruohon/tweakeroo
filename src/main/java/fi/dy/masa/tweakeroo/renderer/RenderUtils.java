package fi.dy.masa.tweakeroo.renderer;

import org.lwjgl.opengl.GL11;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.mixin.IMixinAbstractHorse;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks.HitPart;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RenderUtils
{
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };

    public static void renderBlockPlacementOverlay(Entity entity, BlockPos pos, EnumFacing side, Vec3d hitVec, double dx, double dy, double dz)
    {
        EnumFacing playerFacing = entity.getHorizontalFacing();
        HitPart part = PlacementTweaks.getHitPart(side, playerFacing, pos, hitVec);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        switch (side)
        {
            case DOWN:
                GlStateManager.rotate(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                GlStateManager.rotate( 90f, 1f, 0, 0);
                break;
            case UP:
                GlStateManager.rotate(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                GlStateManager.rotate(-90f, 1f, 0, 0);
                break;
            case NORTH:
                GlStateManager.rotate(180f, 0, 1f, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(   0, 0, 1f, 0);
                break;
            case WEST:
                GlStateManager.rotate(-90f, 0, 1f, 0);
                break;
            case EAST:
                GlStateManager.rotate( 90f, 0, 1f, 0);
                break;
        }

        GlStateManager.translate(-x, -y, -z + 0.501);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float quadAlpha = 0.18f;
        int color = Configs.Generic.FLEXIBLE_PLACEMENT_OVERLAY_COLOR.getIntegerValue();
        float ha = ((color >>> 24) & 0xFF) / 255f;
        float hr = ((color >>> 16) & 0xFF) / 255f;
        float hg = ((color >>>  8) & 0xFF) / 255f;
        float hb = ((color       ) & 0xFF) / 255f;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // White full block background
        buffer.pos(x - 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x + 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x + 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x - 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();

        switch (part)
        {
            case CENTER:
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                break;
            case LEFT:
                buffer.pos(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case RIGHT:
                buffer.pos(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case TOP:
                buffer.pos(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case BOTTOM:
                buffer.pos(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            default:
        }

        tessellator.draw();

        GlStateManager.glLineWidth(1.6f);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        // Middle small rectangle
        buffer.pos(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        // Bottom left
        buffer.pos(x - 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Top left
        buffer.pos(x - 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Bottom right
        buffer.pos(x + 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Top right
        buffer.pos(x + 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

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

            HudAlignment align = (HudAlignment) Configs.Generic.HOTBAR_SWAP_OVERLAY_ALIGNMENT.getOptionListValue();

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

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GuiInventory.INVENTORY_BACKGROUND);
            mc.ingameGUI.drawTexturedModalRect(x - 1, y - 1, 7, 83, 9 * 18, 3 * 18);

            for (int row = 1; row <= 3; row++)
            {
                mc.fontRenderer.drawString(String.valueOf(row), x - 10, y + 4, 0xFFFFFF);

                for (int column = 0; column < 9; column++)
                {
                    ItemStack stack = player.inventory.getStackInSlot(row * 9 + column);

                    if (stack.isEmpty() == false)
                    {
                        fi.dy.masa.malilib.gui.RenderUtils.renderStackAt(stack, x, y, 1, mc);
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
        World world = WorldUtils.getBestWorld(mc);

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
            final int totalSlots = (entityLivingBase instanceof AbstractHorse) ? inv.getSizeInventory() - 2 : inv.getSizeInventory();
            final int firstSlot = (entityLivingBase instanceof AbstractHorse) ? 2 : 0;
            final int slotConfig = fi.dy.masa.malilib.gui.RenderUtils.getInventorySlotConfiguration(inv, totalSlots);
            final int slotsPerRow = (slotConfig >>> 16) & 0xFF;
            final int slotOffsetX = (slotConfig >>> 8) & 0xFF;
            final int slotOffsetY = slotConfig & 0xFF;
            final int wh = fi.dy.masa.malilib.gui.RenderUtils.getInventoryBackgroundWidthHeight(inv, totalSlots, slotsPerRow);
            final int rows = (int) Math.ceil(totalSlots / slotsPerRow);
            final int width = (wh >>> 16) & 0xFFFF;
            final int height = wh & 0xFFFF;
            int xInv = xCenter - (width / 2);
            int yInv = yCenter - height - 6;

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

            if (entityLivingBase instanceof AbstractHorse)
            {
                fi.dy.masa.malilib.gui.RenderUtils.renderInventoryBackground(xInv, yInv, 1, 2, inv, mc);
                fi.dy.masa.malilib.gui.RenderUtils.renderInventoryStacks(inv, xInv + slotOffsetX, yInv + slotOffsetY, 1, 0, 2, mc);
                xInv += 32 + 4;
            }

            if (totalSlots > 0)
            {
                fi.dy.masa.malilib.gui.RenderUtils.renderInventoryBackground(xInv, yInv, slotsPerRow, totalSlots, inv, mc);
                fi.dy.masa.malilib.gui.RenderUtils.renderInventoryStacks(inv, xInv + slotOffsetX, yInv + slotOffsetY, slotsPerRow, firstSlot, totalSlots, mc);
            }
        }

        if (entityLivingBase != null)
        {
            renderEquipmentOverlayBackground(mc, x, y, entityLivingBase);
            renderEquipmentStacks(entityLivingBase, x, y, mc);
        }
    }

    public static void renderPlayerInventoryOverlay(Minecraft mc)
    {
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 - 176 / 2;
        int y = res.getScaledHeight() / 2 + 10;
        int slotOffsetX =  8;
        int slotOffsetY = 18;

        fi.dy.masa.malilib.gui.RenderUtils.renderInventoryBackground(x, y, 9, 27, mc.player.inventory, mc);
        fi.dy.masa.malilib.gui.RenderUtils.renderInventoryStacks(mc.player.inventory, x + slotOffsetX, y + slotOffsetY, 9, 9, 27, mc);
    }

    private static void renderEquipmentOverlayBackground(Minecraft mc, int x, int y, EntityLivingBase entity)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        mc.getTextureManager().bindTexture(fi.dy.masa.malilib.gui.RenderUtils.TEXTURE_DISPENSER);

        fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0, 50, 83, buffer); // top-left (main part)
        fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x + 50, y     , 173,   0,  3, 83, buffer); // right edge top
        fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x     , y + 83,   0, 163, 50,  3, buffer); // bottom edge left
        fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x + 50, y + 83, 173, 163,  3,  3, buffer); // bottom right corner

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }

        // Main hand and offhand
        fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x + 28, y + 2 * 18 + 7, 61, 16, 18, 18, buffer);
        fi.dy.masa.malilib.gui.RenderUtils.drawTexturedRectBatched(x + 28, y + 3 * 18 + 7, 61, 16, 18, 18, buffer);

        tessellator.draw();

        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).isEmpty())
        {
            String texture = "minecraft:items/empty_armor_slot_shield";
            renderSprite(mc, x + 28 + 1, y + 3 * 18 + 7 + 1, texture, 16, 16);
        }

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EntityEquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];

            if (entity.getItemStackFromSlot(eqSlot).isEmpty())
            {
                String texture = ItemArmor.EMPTY_SLOT_NAMES[eqSlot.getIndex()];
                renderSprite(mc, x + xOff + 1, y + yOff + 1, texture, 16, 16);
            }
        }
    }

    public static void renderHotbarScrollOverlay(Minecraft mc)
    {
        IInventory inv = mc.player.inventory;
        ScaledResolution res = new ScaledResolution(mc);
        final int xCenter = res.getScaledWidth() / 2;
        final int yCenter = res.getScaledHeight() / 2;
        final int x = xCenter - 176 / 2;
        final int y = yCenter + 10;

        fi.dy.masa.malilib.gui.RenderUtils.renderInventoryBackground(x, y, 9, 36, inv, mc);

        // Main inventory
        fi.dy.masa.malilib.gui.RenderUtils.renderInventoryStacks(inv, x + 8, y + 18, 9, 9, 27, mc);
        // Hotbar
        fi.dy.masa.malilib.gui.RenderUtils.renderInventoryStacks(inv, x + 8, y + 72, 9, 0,  9, mc);

        int currentRow = Configs.Generic.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
        fi.dy.masa.malilib.gui.RenderUtils.drawOutline(x + 7, y + currentRow * 18 + 17, 9 * 18, 18, 2, 0xFFFF2020);
    }

    private static void renderSprite(Minecraft mc, int x, int y, String texture, int width, int height)
    {
        if (texture != null)
        {
            TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(texture);
            GlStateManager.disableLighting();
            mc.ingameGUI.drawTexturedModalRect(x, y, sprite, width, height);
        }
    }

    private static void renderEquipmentStacks(EntityLivingBase entity, int x, int y, Minecraft mc)
    {
        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EntityEquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = entity.getItemStackFromSlot(eqSlot);

            if (stack.isEmpty() == false)
            {
                fi.dy.masa.malilib.gui.RenderUtils.renderStackAt(stack, x + xOff + 1, y + yOff + 1, 1, mc);
            }
        }

        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (stack.isEmpty() == false)
        {
            fi.dy.masa.malilib.gui.RenderUtils.renderStackAt(stack, x + 28, y + 2 * 18 + 7 + 1, 1, mc);
        }

        stack = entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);

        if (stack.isEmpty() == false)
        {
            fi.dy.masa.malilib.gui.RenderUtils.renderStackAt(stack, x + 28, y + 3 * 18 + 7 + 1, 1, mc);
        }
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

    public static void renderMapPreview(ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof ItemMap && GuiScreen.isShiftKeyDown())
        {
            Minecraft mc = Minecraft.getMinecraft();

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(fi.dy.masa.malilib.gui.RenderUtils.TEXTURE_MAP_BACKGROUND);

            int size = Configs.Generic.MAP_PREVIEW_SIZE.getIntegerValue();
            int y1 = y - size - 20;
            int y2 = y1 + size;
            int x1 = x + 8;
            int x2 = x1 + size;
            int z = 300;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x1, y2, z).tex(0.0D, 1.0D).endVertex();
            buffer.pos(x2, y2, z).tex(1.0D, 1.0D).endVertex();
            buffer.pos(x2, y1, z).tex(1.0D, 0.0D).endVertex();
            buffer.pos(x1, y1, z).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();

            MapData mapdata = Items.FILLED_MAP.getMapData(stack, mc.world);

            if (mapdata != null)
            {
                x1 += 8;
                y1 += 8;
                z = 310;
                double scale = (double) (size - 16) / 128.0D;
                GlStateManager.translate(x1, y1, z);
                GlStateManager.scale(scale, scale, 0);
                mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
