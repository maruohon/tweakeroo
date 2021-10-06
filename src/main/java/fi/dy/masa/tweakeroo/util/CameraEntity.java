package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class CameraEntity extends EntityPlayerSP
{
    public CameraEntity(Minecraft mc, World world, NetHandlerPlayClient nethandler,
            StatisticsManager stats, RecipeBook recipeBook)
    {
        super(mc, world, nethandler, stats, recipeBook);
    }

    @Nullable private static Entity originalRenderViewEntity;
    @Nullable private static CameraEntity camera;
    private static boolean cullChunksOriginal;
    private static float forwardRamped;
    private static float strafeRamped;
    private static float verticalRamped;
    private static boolean sprinting;

    @Override
    public boolean isSpectator()
    {
        return true;
    }

    public static void movementTick(boolean sneak, boolean jump)
    {
        CameraEntity camera = getCamera();

        if (camera != null && Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT.getBooleanValue() == false)
        {
            camera.updateLastTickPosition();

            float forward = 0;
            float vertical = 0;
            float strafe = 0;

            GameSettings options = GameUtils.getClient().gameSettings;
            if (options.keyBindForward.isKeyDown()) { forward++;  }
            if (options.keyBindBack.isKeyDown())    { forward--;  }
            if (options.keyBindLeft.isKeyDown())    { strafe++;   }
            if (options.keyBindRight.isKeyDown())   { strafe--;   }
            if (options.keyBindJump.isKeyDown())    { vertical++; }
            if (options.keyBindSneak.isKeyDown())   { vertical--; }

            if (options.keyBindSprint.isKeyDown())
            {
                sprinting = true;
            }
            else if (forward == 0)
            {
                sprinting = false;
            }

            float rampAmount = 0.15f;
            float speed = strafe * strafe + forward * forward;

            if (forward != 0 && strafe != 0)
            {
                speed = (float) Math.sqrt(speed * 0.6);
            }
            else
            {
                speed = 1;
            }

            forwardRamped  = getRampedMotion(forwardRamped , forward , rampAmount) / speed;
            verticalRamped = getRampedMotion(verticalRamped, vertical, rampAmount);
            strafeRamped   = getRampedMotion(strafeRamped  , strafe  , rampAmount) / speed;

            forward = sprinting ? forwardRamped * 3 : forwardRamped;

            camera.handleMotion(forward, verticalRamped, strafeRamped);
        }
    }

    private static float getRampedMotion(float current, float input, float rampAmount)
    {
        if (input != 0)
        {
            if (input < 0)
            {
                rampAmount *= -1f;
            }

            // Immediately kill the motion when changing direction to the opposite
            if ((input < 0) != (current < 0))
            {
                current = 0;
            }

            current = MathHelper.clamp(current + rampAmount, -1f, 1f);
        }
        else
        {
            current *= 0.5f;
        }

        return current;
    }

    private static double getMoveSpeed()
    {
        double base = 0.07;

        if (FeatureToggle.TWEAK_FLY_SPEED.getBooleanValue())
        {
            base = Configs.getActiveFlySpeedConfig().getDoubleValue();
        }

        return base * 10;
    }

    private void handleMotion(float forward, float up, float strafe)
    {
        double xFactor = Math.sin(this.rotationYaw * Math.PI / 180D);
        double zFactor = Math.cos(this.rotationYaw * Math.PI / 180D);
        double scale = getMoveSpeed();

        this.motionX = (double) (strafe * zFactor - forward * xFactor) * scale;
        this.motionY = (double) up * scale;
        this.motionZ = (double) (forward * zFactor + strafe * xFactor) * scale;

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        this.chunkCoordX = (int) Math.floor(this.posX) >> 4;
        this.chunkCoordY = (int) Math.floor(this.posY) >> 4;
        this.chunkCoordZ = (int) Math.floor(this.posZ) >> 4;
    }

    private void updateLastTickPosition()
    {
        this.prevPosX = this.lastTickPosX = this.posX;
        this.prevPosY = this.lastTickPosY = this.posY;
        this.prevPosZ = this.lastTickPosZ = this.posZ;
    }

    public void setCameraRotations(float yaw, float pitch)
    {
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.setRotationYawHead(this.rotationYaw);
        this.setRenderYawOffset(this.rotationYaw);
    }

    public void updateCameraRotations(float yawChange, float pitchChange)
    {
        this.rotationYaw += yawChange * 0.15F;
        this.rotationPitch = MathHelper.clamp(this.rotationPitch - pitchChange * 0.15F, -90F, 90F);

        this.setCameraRotations(this.rotationYaw, this.rotationPitch);
    }

    private static CameraEntity createCameraEntity(Minecraft mc)
    {
        EntityPlayerSP player = mc.player;
        CameraEntity camera = new CameraEntity(mc, mc.world, player.connection, player.getStatFileWriter(), player.getRecipeBook());

        camera.noClip = true;
        camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

        camera.prevRotationYaw = camera.rotationYaw;
        camera.prevRotationPitch = camera.rotationPitch;
        camera.setRotationYawHead(camera.rotationYaw);
        camera.setRenderYawOffset(camera.rotationYaw);

        return camera;
    }

    @Nullable
    public static CameraEntity getCamera()
    {
        return camera;
    }

    public static void setCameraState(boolean enabled)
    {
        Minecraft mc = GameUtils.getClient();

        if (mc.world != null && mc.player != null)
        {
            if (enabled)
            {
                createAndSetCamera(mc);
            }
            else
            {
                removeCamera(mc);
            }
        }
    }

    private static void createAndSetCamera(Minecraft mc)
    {
        camera = createCameraEntity(mc);
        originalRenderViewEntity = mc.getRenderViewEntity();
        cullChunksOriginal = mc.renderChunksMany;

        mc.setRenderViewEntity(camera);
        mc.renderChunksMany = false; // Disable chunk culling

        // Disable the motion option when entering camera mode
        Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT.setValue(false);
    }

    private static void removeCamera(Minecraft mc)
    {
        if (mc.world != null && camera != null)
        {
            mc.setRenderViewEntity(originalRenderViewEntity);
            mc.renderChunksMany = cullChunksOriginal;
            CameraUtils.markChunksForRebuildOnDeactivation(camera.chunkCoordX, camera.chunkCoordZ);
        }

        originalRenderViewEntity = null;
        camera = null;
    }
}
