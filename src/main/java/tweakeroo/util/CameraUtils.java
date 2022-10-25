package tweakeroo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import malilib.util.game.wrap.GameUtils;
import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;

public class CameraUtils
{
    private static float cameraYaw;
    private static float cameraPitch;
    private static boolean freeCameraSpectator;

    public static void setFreeCameraSpectator(boolean isSpectator)
    {
        freeCameraSpectator = isSpectator;
    }

    public static boolean getFreeCameraSpectator()
    {
        return freeCameraSpectator;
    }

    public static boolean shouldPreventPlayerInputs()
    {
        return FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
               Configs.Generic.FREE_CAMERA_PLAYER_INPUTS.getBooleanValue() == false;
    }

    public static boolean shouldPreventPlayerMovement()
    {
        return FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
               Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT.getBooleanValue() == false;
    }

    public static float getCameraYaw()
    {
        return MathHelper.wrapDegrees(cameraYaw);
    }

    public static float getCameraPitch()
    {
        return MathHelper.wrapDegrees(cameraPitch);
    }

    public static void setCameraYaw(float yaw)
    {
        cameraYaw = yaw;
    }

    public static void setCameraPitch(float pitch)
    {
        cameraPitch = pitch;
    }

    public static void setCameraRotations(float yaw, float pitch)
    {
        CameraEntity camera = CameraEntity.getCamera();

        if (camera != null)
        {
            camera.setCameraRotations(yaw, pitch);
        }
    }

    public static void updateCameraRotations(float yawChange, float pitchChange)
    {
        CameraEntity camera = CameraEntity.getCamera();

        if (camera != null)
        {
            camera.updateCameraRotations(yawChange, pitchChange);
        }
    }

    public static void markChunksForRebuild(ViewFrustum storage,
                                            int chunkX, int chunkZ, int lastChunkX, int lastChunkZ)
    {
        if (chunkX == lastChunkX && chunkZ == lastChunkZ)
        {
            return;
        }

        Minecraft mc = GameUtils.getClient();
        ChunkProviderClient provider = mc.world.getChunkProvider();
        final int viewDistance = GameUtils.getRenderDistanceChunks();

        if (chunkX != lastChunkX)
        {
            final int minCX = chunkX > lastChunkX ? lastChunkX + viewDistance : chunkX     - viewDistance;
            final int maxCX = chunkX > lastChunkX ? chunkX     + viewDistance : lastChunkX - viewDistance;

            for (int cx = minCX; cx <= maxCX; ++cx)
            {
                for (int cz = chunkZ - viewDistance; cz <= chunkZ + viewDistance; ++cz)
                {
                    if (provider.isChunkGeneratedAt(cx, cz))
                    {
                        int x = cx << 4;
                        int z = cz << 4;
                        storage.markBlocksForUpdate(x, 0, z, x, 255, z, false);
                    }
                }
            }
        }

        if (chunkZ != lastChunkZ)
        {
            final int minCZ = chunkZ > lastChunkZ ? lastChunkZ + viewDistance : chunkZ     - viewDistance;
            final int maxCZ = chunkZ > lastChunkZ ? chunkZ     + viewDistance : lastChunkZ - viewDistance;

            for (int cz = minCZ; cz <= maxCZ; ++cz)
            {
                for (int cx = chunkX - viewDistance; cx <= chunkX + viewDistance; ++cx)
                {
                    if (provider.isChunkGeneratedAt(cx, cz))
                    {
                        int x = cx << 4;
                        int z = cz << 4;
                        storage.markBlocksForUpdate(x, 0, z, x, 255, z, false);
                    }
                }
            }
        }
    }

    public static void markChunksForRebuildOnDeactivation(int lastChunkX, int lastChunkZ)
    {
        Minecraft mc = GameUtils.getClient();
        Entity entity = GameUtils.getCameraEntity();
        ChunkProviderClient provider = mc.world.getChunkProvider();
        final int viewDistance = GameUtils.getRenderDistanceChunks();
        final int chunkX = entity.chunkCoordX;
        final int chunkZ = entity.chunkCoordZ;

        final int minCameraCX = lastChunkX - viewDistance;
        final int maxCameraCX = lastChunkX + viewDistance;
        final int minCameraCZ = lastChunkZ - viewDistance;
        final int maxCameraCZ = lastChunkZ + viewDistance;
        final int minCX = chunkX - viewDistance;
        final int maxCX = chunkX + viewDistance;
        final int minCZ = chunkZ - viewDistance;
        final int maxCZ = chunkZ + viewDistance;

        for (int cz = minCZ; cz <= maxCZ; ++cz)
        {
            for (int cx = minCX; cx <= maxCX; ++cx)
            {
                // Mark all chunks that were not in free camera range
                if ((cx < minCameraCX || cx > maxCameraCX || cz < minCameraCZ || cz > maxCameraCZ) &&
                    provider.isChunkGeneratedAt(cx, cz))
                {
                    int x = cx << 4;
                    int z = cz << 4;
                    mc.world.markBlockRangeForRenderUpdate(x, 0, z, x, 255, z);
                }
            }
        }
    }
}
