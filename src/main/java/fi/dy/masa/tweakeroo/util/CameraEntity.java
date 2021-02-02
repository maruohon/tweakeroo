package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class CameraEntity extends ClientPlayerEntity
{
    @Nullable private static Entity originalCameraEntity;
    @Nullable private static CameraEntity camera;
    private static boolean cullChunksOriginal;
    private static float forwardRamped;
    private static float strafeRamped;
    private static float verticalRamped;
    private static boolean sprinting;
    private static boolean originalCameraWasPlayer;

    public CameraEntity(MinecraftClient mc, ClientWorld world,
                        ClientPlayNetworkHandler nethandler, StatHandler stats,
                        ClientRecipeBook recipeBook)
    {
        super(mc, world, nethandler, stats, recipeBook, false, false);
    }

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
            MinecraftClient mc = MinecraftClient.getInstance();

            camera.updateLastTickPosition();

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

            if (options.keySprint.isPressed())
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
        double xFactor = Math.sin(this.yaw * Math.PI / 180D);
        double zFactor = Math.cos(this.yaw * Math.PI / 180D);
        double scale = getMoveSpeed();

        double x = (double) (strafe * zFactor - forward * xFactor) * scale;
        double y = (double) up * scale;
        double z = (double) (forward * zFactor + strafe * xFactor) * scale;
        this.setVelocity(new Vec3d(x, y, z));

        this.move(MovementType.SELF, this.getVelocity());

        this.chunkX = (int) Math.floor(this.getX()) >> 4;
        this.chunkY = (int) Math.floor(this.getY()) >> 4;
        this.chunkZ = (int) Math.floor(this.getZ()) >> 4;
    }

    private void updateLastTickPosition()
    {
        this.lastRenderX = this.getX();
        this.lastRenderY = this.getY();
        this.lastRenderZ = this.getZ();

        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();

        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;

        this.prevHeadYaw = this.headYaw;
    }

    public void setCameraRotations(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;

        this.headYaw = this.yaw;

        //this.prevRotationYaw = this.rotationYaw;
        //this.prevRotationPitch = this.rotationPitch;

        //this.prevRotationYawHead = this.rotationYaw;
        //this.setRenderYawOffset(this.rotationYaw);
    }

    public void updateCameraRotations(float yawChange, float pitchChange)
    {
        this.yaw += yawChange * 0.15F;
        this.pitch = MathHelper.clamp(this.pitch + pitchChange * 0.15F, -90F, 90F);

        this.setCameraRotations(this.yaw, this.pitch);
    }

    private static CameraEntity createCameraEntity(MinecraftClient mc)
    {
        ClientPlayerEntity player = mc.player;
        CameraEntity camera = new CameraEntity(mc, mc.world, player.networkHandler, player.getStatHandler(), player.getRecipeBook());
        camera.noClip = true;

        camera.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch);
        camera.setRotation(player.yaw, player.pitch);

        return camera;
    }

    @Nullable
    public static CameraEntity getCamera()
    {
        return camera;
    }

    public static void setCameraState(boolean enabled)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

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

    public static boolean originalCameraWasPlayer()
    {
        return originalCameraWasPlayer;
    }

    private static void createAndSetCamera(MinecraftClient mc)
    {
        camera = createCameraEntity(mc);
        originalCameraEntity = mc.getCameraEntity();
        originalCameraWasPlayer = originalCameraEntity == mc.player;
        cullChunksOriginal = mc.chunkCullingEnabled;

        mc.setCameraEntity(camera);
        mc.chunkCullingEnabled = false; // Disable chunk culling

        // Disable the motion option when entering camera mode
        Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT.setBooleanValue(false);
    }

    private static void removeCamera(MinecraftClient mc)
    {
        // Re-fetch the player entity, in case the player died while in Free Camera mode and the instance changed
        mc.setCameraEntity(originalCameraWasPlayer ? mc.player : originalCameraEntity);
        mc.chunkCullingEnabled = cullChunksOriginal;
        originalCameraEntity = null;

        if (mc.world != null && camera != null)
        {
            CameraUtils.markChunksForRebuildOnDeactivation(camera.chunkX, camera.chunkZ);
        }

        camera = null;
    }
}
