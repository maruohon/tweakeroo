package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CameraEntity extends EntityOtherPlayerMP
{
    @Nullable private static CameraEntity camera;

    public CameraEntity(World worldIn, GameProfile gameProfileIn)
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

        if (camera != null)
        {
            camera.updateLastTickPosition();

            float up = (float) getFlySpeed() * 0.01f;

            if (sneak)
            {
                camera.handleMotion(0, -up, 0);
            }
            else if (jump)
            {
                camera.handleMotion(0, up, 0);
            }
        }
    }

    private static double getFlySpeed()
    {
        return FeatureToggle.TWEAK_FLY_SPEED.getBooleanValue() ? Configs.getActiveFlySpeedConfig().getDoubleValue() * 128 : 16;
    }

    public void handleMotion(float strafe, float up, float forward)
    {
        double xFactor = Math.sin(this.rotationYaw * Math.PI / 180D);
        double zFactor = Math.cos(this.rotationYaw * Math.PI / 180D);
        double scale = getFlySpeed();

        this.motionX = (double) (strafe * zFactor - forward * xFactor) * scale;
        this.motionY = (double) up * scale;
        this.motionZ = (double) (forward * zFactor + strafe * xFactor) * scale;

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    public void updateLastTickPosition()
    {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
    }

    public void setRotations(float yaw, float pitch)
    {
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.setRotationYawHead(this.rotationYaw);
        this.setRenderYawOffset(this.rotationYaw);
    }

    private static CameraEntity create(Minecraft mc)
    {
        CameraEntity camera = new CameraEntity(mc.world, mc.player.getGameProfile());
        camera.noClip = true;

        EntityPlayer player = mc.player;

        if (player != null)
        {
            camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

            camera.prevRotationYaw = camera.rotationYaw;
            camera.prevRotationPitch = camera.rotationPitch;
            camera.setRotationYawHead(camera.rotationYaw);
            camera.setRenderYawOffset(camera.rotationYaw);
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
