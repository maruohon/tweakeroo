package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CameraEntity extends OtherClientPlayerEntity
{
    @Nullable private static CameraEntity camera;
    private static float forwardRamped;
    private static float strafeRamped;
    private static float verticalRamped;

    public CameraEntity(ClientWorld worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
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
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerEntity player = mc.player;

            float forward = 0;
            float vertical = 0;
            float strafe = 0;

            GameOptions options = mc.options;
            if (options.keyForward.isPressed()) { forward++;  }
            if (options.keyBack.isPressed())    { forward--;  }
            if (options.keyLeft.isPressed())    { strafe++;   }
            if (options.keyRight.isPressed())   { strafe--;   }
            if (options.keyJump.isPressed())    { vertical++; }
            if (options.keySneak.isPressed())   { vertical--; }

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
        double xFactor = Math.sin(this.yaw * Math.PI / 180D);
        double zFactor = Math.cos(this.yaw * Math.PI / 180D);
        double scale = getMoveSpeed();

        double x = (double) (strafe * zFactor - forward * xFactor) * scale;
        double y = (double) up * scale;
        double z = (double) (forward * zFactor + strafe * xFactor) * scale;
        this.setVelocity(new Vec3d(x, y, z));

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void updateLastTickPosition()
    {
        this.prevRenderX = this.x;
        this.prevRenderY = this.y;
        this.prevRenderZ = this.z;

        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;

        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;

        this.prevHeadYaw = this.headYaw;
    }

    public void setRotations(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;

        this.headYaw = this.yaw;

        //this.prevRotationYaw = this.rotationYaw;
        //this.prevRotationPitch = this.rotationPitch;

        //this.prevRotationYawHead = this.rotationYaw;
        //this.setRenderYawOffset(this.rotationYaw);
    }

    private static CameraEntity create(MinecraftClient mc)
    {
        CameraEntity camera = new CameraEntity(mc.world, mc.player.getGameProfile());
        camera.noClip = true;

        ClientPlayerEntity player = mc.player;

        if (player != null)
        {
            camera.setPositionAndAngles(player.x, player.y, player.z, player.yaw, player.pitch);
            camera.setRotations(player.yaw, player.pitch);
        }

        return camera;
    }

    public static void createCamera(MinecraftClient mc)
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
