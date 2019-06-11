package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CameraEntity extends EntityPlayerSP
{
    @Nullable private static CameraEntity camera;
    private static float forwardRamped;
    private static float strafeRamped;
    private static float verticalRamped;

    public CameraEntity(Minecraft mc, World world, NetHandlerPlayClient nethandler,
            StatisticsManager stats, RecipeBookClient recipeBook)
    {
        super(mc, world, nethandler, stats, recipeBook);
    }

    @Override
    public boolean isSpectator()
    {
        return true;
    }

    public static void movementTick(boolean sneak, boolean jump)
    {
        CameraEntity camera = getCamera();

        if (camera != null && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
        {
            Minecraft mc = Minecraft.getInstance();
            EntityPlayerSP player = mc.player;

            float forward = 0;
            float vertical = 0;
            float strafe = 0;

            GameSettings options = mc.gameSettings;
            if (options.keyBindForward.isKeyDown()) { forward++;  }
            if (options.keyBindBack.isKeyDown())    { forward--;  }
            if (options.keyBindLeft.isKeyDown())    { strafe++;   }
            if (options.keyBindRight.isKeyDown())   { strafe--;   }
            if (options.keyBindJump.isKeyDown())    { vertical++; }
            if (options.keyBindSneak.isKeyDown())   { vertical--; }

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

            forward = player.isSprinting() ? forwardRamped * 2 : forwardRamped;

            camera.handleMotion(forward, verticalRamped, strafeRamped);

            camera.updateLastTickPosition();
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

        return base * 6;
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
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        this.prevRotationYawHead = this.rotationYawHead;
    }

    public void setRotations(float yaw, float pitch)
    {
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;

        this.rotationYawHead = this.rotationYaw;

        //this.prevRotationYaw = this.rotationYaw;
        //this.prevRotationPitch = this.rotationPitch;

        //this.prevRotationYawHead = this.rotationYaw;
        //this.setRenderYawOffset(this.rotationYaw);
    }

    private static CameraEntity create(Minecraft mc)
    {
        CameraEntity camera = new CameraEntity(mc, mc.world, mc.player.connection, mc.player.getStats(), mc.player.getRecipeBook());
        camera.noClip = true;

        EntityPlayerSP player = mc.player;

        if (player != null)
        {
            camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            camera.setRotations(player.rotationYaw, player.rotationPitch);
        }

        return camera;
    }

    public static void createCamera(Minecraft mc)
    {
        camera = create(mc);
    }

    @Nullable
    public static CameraEntity getCamera()
    {
        return camera;
    }

    public static void removeCamera()
    {
        camera = null;
    }
}
