package fi.dy.masa.tweakeroo.renderer;

import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.mixin.IMixinHorseBaseEntity;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks.HitPart;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RenderUtils
{
    public static void renderBlockPlacementOverlay(Entity entity, BlockPos pos, Direction side, Vec3d hitVec, double dx, double dy, double dz)
    {
        Direction playerFacing = entity.getHorizontalFacing();
        HitPart part = PlacementTweaks.getHitPart(side, playerFacing, pos, hitVec);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        switch (side)
        {
            case DOWN:
                GlStateManager.rotatef(180f - playerFacing.asRotation(), 0, 1f, 0);
                GlStateManager.rotatef( 90f, 1f, 0, 0);
                break;
            case UP:
                GlStateManager.rotatef(180f - playerFacing.asRotation(), 0, 1f, 0);
                GlStateManager.rotatef(-90f, 1f, 0, 0);
                break;
            case NORTH:
                GlStateManager.rotatef(180f, 0, 1f, 0);
                break;
            case SOUTH:
                GlStateManager.rotatef(   0, 0, 1f, 0);
                break;
            case WEST:
                GlStateManager.rotatef(-90f, 0, 1f, 0);
                break;
            case EAST:
                GlStateManager.rotatef( 90f, 0, 1f, 0);
                break;
        }

        GlStateManager.translated(-x, -y, -z + 0.501);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        float quadAlpha = 0.18f;
        int color = Configs.Generic.FLEXIBLE_PLACEMENT_OVERLAY_COLOR.getIntegerValue();
        float ha = ((color >>> 24) & 0xFF) / 255f;
        float hr = ((color >>> 16) & 0xFF) / 255f;
        float hg = ((color >>>  8) & 0xFF) / 255f;
        float hb = ((color       ) & 0xFF) / 255f;

        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

        // White full block background
        buffer.vertex(x - 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).next();
        buffer.vertex(x + 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).next();
        buffer.vertex(x + 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).next();
        buffer.vertex(x - 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).next();

        switch (part)
        {
            case CENTER:
                buffer.vertex(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                break;
            case LEFT:
                buffer.vertex(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                break;
            case RIGHT:
                buffer.vertex(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                break;
            case TOP:
                buffer.vertex(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                break;
            case BOTTOM:
                buffer.vertex(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                break;
            default:
        }

        tessellator.draw();

        GlStateManager.lineWidth(1.6f);

        buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);

        // Middle small rectangle
        buffer.vertex(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();
        tessellator.draw();

        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
        // Bottom left
        buffer.vertex(x - 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();

        // Top left
        buffer.vertex(x - 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();

        // Bottom right
        buffer.vertex(x + 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();

        // Top right
        buffer.vertex(x + 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public static void renderHotbarSwapOverlay(MinecraftClient mc)
    {
        PlayerEntity player = mc.player;

        if (player != null)
        {
            Window win = mc.window;
            final int offX = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET_X.getIntegerValue();
            final int offY = Configs.Generic.HOTBAR_SWAP_OVERLAY_OFFSET_Y.getIntegerValue();
            int startX = offX;
            int startY = offY;

            fi.dy.masa.malilib.config.HudAlignment align = (fi.dy.masa.malilib.config.HudAlignment) Configs.Generic.HOTBAR_SWAP_OVERLAY_ALIGNMENT.getOptionListValue();

            switch (align)
            {
                case TOP_RIGHT:
                    startX = (int) win.getScaledWidth() - offX - 9 * 18;
                    break;
                case BOTTOM_LEFT:
                    startY = (int) win.getScaledHeight() - offY - 3 * 18;
                    break;
                case BOTTOM_RIGHT:
                    startX = (int) win.getScaledWidth() - offX - 9 * 18;
                    startY = (int) win.getScaledHeight() - offY - 3 * 18;
                    break;
                case CENTER:
                    startX = (int) win.getScaledWidth() / 2 - offX - 9 * 18 / 2;
                    startY = (int) win.getScaledHeight() / 2 - offY - 3 * 18 / 2;
                    break;
                default:
            }

            int x = startX;
            int y = startY;

            GlStateManager.color4f(1f, 1f, 1f, 1f);
            mc.getTextureManager().bindTexture(AbstractContainerScreen.BACKGROUND_TEXTURE);
            mc.inGameHud.blit(x - 1, y - 1, 7, 83, 9 * 18, 3 * 18);

            for (int row = 1; row <= 3; row++)
            {
                mc.textRenderer.draw(String.valueOf(row), x - 10, y + 4, 0xFFFFFF);

                for (int column = 0; column < 9; column++)
                {
                    ItemStack stack = player.inventory.getInvStack(row * 9 + column);

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

    public static void renderInventoryOverlay(MinecraftClient mc)
    {
        World world = fi.dy.masa.malilib.util.WorldUtils.getBestWorld(mc);

        // We need to get the player from the server world, so that the player itself won't be included in the ray trace
        PlayerEntity player = world.getPlayerByUuid(mc.player.getUuid());

        if (player == null)
        {
            player = mc.player;
        }

        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, player, false);

        if (trace == null)
        {
            return;
        }

        Inventory inv = null;
        ShulkerBoxBlock block = null;
        LivingEntity entityLivingBase = null;

        if (trace.getType() == HitResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockHitResult) trace).getBlockPos();
            BlockEntity te1 = world.getWorldChunk(pos).getBlockEntity(pos);

            if (te1 instanceof Inventory)
            {
                inv = (Inventory) te1;
                BlockState state = world.getBlockState(pos);

                if (state.getBlock() instanceof ChestBlock && te1 instanceof ChestBlockEntity)
                {
                    ChestType type = state.get(ChestBlock.CHEST_TYPE);

                    if (type != ChestType.SINGLE)
                    {
                        BlockPos posAdj = pos.offset(ChestBlock.getFacing(state));
                        BlockState stateAdj = world.getBlockState(posAdj);
                        BlockEntity te2 = world.getWorldChunk(posAdj).getBlockEntity(posAdj);

                        if (stateAdj.getBlock() == state.getBlock() &&
                            te2 instanceof ChestBlockEntity &&
                            stateAdj.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE &&
                            stateAdj.get(ChestBlock.FACING) == state.get(ChestBlock.FACING))
                        {
                            Inventory invRight = type == ChestType.RIGHT ?             inv : (Inventory) te2;
                            Inventory invLeft  = type == ChestType.RIGHT ? (Inventory) te2 :             inv;
                            inv = new DoubleInventory(invRight, invLeft);
                        }
                    }
                }

                Block blockTmp = world.getBlockState(pos).getBlock();

                if (blockTmp instanceof ShulkerBoxBlock)
                {
                    block = (ShulkerBoxBlock) blockTmp;
                }
            }
        }
        else if (trace.getType() == HitResult.Type.ENTITY)
        {
            Entity entity = ((EntityHitResult) trace).getEntity();

            if (entity instanceof LivingEntity)
            {
                entityLivingBase = (LivingEntity) entity;
            }

            if (entity instanceof Inventory)
            {
                inv = (Inventory) entity;
            }
            else if (entity instanceof VillagerEntity)
            {
                inv = ((VillagerEntity) entity).getInventory();
            }
            else if (entity instanceof HorseBaseEntity)
            {
                inv = ((IMixinHorseBaseEntity) entity).getHorseInventory();
            }
        }

        Window win = mc.window;
        final int xCenter = win.getScaledWidth() / 2;
        final int yCenter = win.getScaledHeight() / 2;
        int x = xCenter - 52 / 2;
        int y = yCenter - 92;

        if (inv != null && inv.getInvSize() > 0)
        {
            final boolean isHorse = (entityLivingBase instanceof HorseBaseEntity);
            final int totalSlots = isHorse ? inv.getInvSize() - 2 : inv.getInvSize();
            final int firstSlot = isHorse ? 2 : 0;

            final fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = (entityLivingBase instanceof VillagerEntity) ? fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType.VILLAGER : fi.dy.masa.malilib.render.InventoryOverlay.getInventoryType(inv);
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

            setShulkerboxBackgroundTintColor(block);

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

    public static void renderPlayerInventoryOverlay(MinecraftClient mc)
    {
        Window win = mc.window;
        int x = win.getScaledWidth() / 2 - 176 / 2;
        int y = win.getScaledHeight() / 2 + 10;
        int slotOffsetX = 8;
        int slotOffsetY = 8;
        fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType.GENERIC;

        GlStateManager.color4f(1f, 1f, 1f, 1f);

        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, x, y, 9, 27, mc);
        fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, mc.player.inventory, x + slotOffsetX, y + slotOffsetY, 9, 9, 27, mc);
    }

    public static void renderHotbarScrollOverlay(MinecraftClient mc)
    {
        Inventory inv = mc.player.inventory;
        Window win = mc.window;
        final int xCenter = win.getScaledWidth() / 2;
        final int yCenter = win.getScaledHeight() / 2;
        final int x = xCenter - 176 / 2;
        final int y = yCenter + 6;
        fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType.GENERIC;

        GlStateManager.color4f(1f, 1f, 1f, 1f);

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
        if (entity instanceof LivingEntity)
        {
            LivingEntity living = (LivingEntity) entity;
            final int resp = EnchantmentHelper.getRespiration(living);
            // The original fog value of 2.0F is way too much to reduce gradually from.
            // You would only be able to see meaningfully with the full reduction.
            final float baseFog = 0.6F;
            final float respDecrement = (baseFog * 0.75F) / 3F - 0.02F;
            float fog = baseFog;

            if (living.hasStatusEffect(StatusEffects.WATER_BREATHING))
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
            GlStateManager.fogDensity(fog);
        }
    }

    public static void renderMapPreview(ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof FilledMapItem && Screen.hasShiftDown())
        {
            MinecraftClient mc = MinecraftClient.getInstance();

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.color4f(1f, 1f, 1f, 1f);
            mc.getTextureManager().bindTexture(fi.dy.masa.malilib.render.RenderUtils.TEXTURE_MAP_BACKGROUND);

            int size = Configs.Generic.MAP_PREVIEW_SIZE.getIntegerValue();
            int y1 = y - size - 20;
            int y2 = y1 + size;
            int x1 = x + 8;
            int x2 = x1 + size;
            int z = 300;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBufferBuilder();
            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
            buffer.vertex(x1, y2, z).texture(0.0D, 1.0D).next();
            buffer.vertex(x2, y2, z).texture(1.0D, 1.0D).next();
            buffer.vertex(x2, y1, z).texture(1.0D, 0.0D).next();
            buffer.vertex(x1, y1, z).texture(0.0D, 0.0D).next();
            tessellator.draw();

            MapState mapdata = FilledMapItem.getMapState(stack, mc.world);

            if (mapdata != null)
            {
                x1 += 8;
                y1 += 8;
                z = 310;
                double scale = (double) (size - 16) / 128.0D;
                GlStateManager.translated(x1, y1, z);
                GlStateManager.scaled(scale, scale, 0);
                mc.gameRenderer.getMapRenderer().draw(mapdata, false);
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void renderShulkerBoxPreview(ItemStack stack, int x, int y)
    {
        if (Screen.hasShiftDown() && stack.hasTag())
        {
            DefaultedList<ItemStack> items = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(stack, -1);

            if (items.size() == 0)
            {
                return;
            }

            GlStateManager.pushMatrix();
            GuiLighting.disable();
            GlStateManager.translatef(0F, 0F, 700F);

            fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType type = fi.dy.masa.malilib.render.InventoryOverlay.getInventoryType(stack);
            fi.dy.masa.malilib.render.InventoryOverlay.InventoryProperties props = fi.dy.masa.malilib.render.InventoryOverlay.getInventoryPropsTemp(type, items.size());

            x += 8;
            y -= (props.height + 18);

            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                setShulkerboxBackgroundTintColor((ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock());
            }
            else
            {
                GlStateManager.color4f(1f, 1f, 1f, 1f);
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, items.size(), mc);

            GuiLighting.enable();
            GlStateManager.enableDepthTest();
            GlStateManager.enableRescaleNormal();

            Inventory inv = fi.dy.masa.malilib.util.InventoryUtils.getAsInventory(items);
            fi.dy.masa.malilib.render.InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc);

            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }
    }

    private static void setShulkerboxBackgroundTintColor(@Nullable ShulkerBoxBlock block)
    {
        if (block != null && Configs.Generic.SHULKER_DISPLAY_BACKGROUND_COLOR.getBooleanValue())
        {
            final DyeColor dye = block.getColor() != null ? block.getColor() : DyeColor.PURPLE;
            final float[] colors = dye.getColorComponents();
            GlStateManager.color3f(colors[0], colors[1], colors[2]);
        }
        else
        {
            GlStateManager.color4f(1f, 1f, 1f, 1f);
        }
    }
}
