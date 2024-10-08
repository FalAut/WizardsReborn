package mod.maxbogomol.wizards_reborn.api.light;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.maxbogomol.fluffy_fur.client.render.LevelRenderHandler;
import mod.maxbogomol.fluffy_fur.common.network.BlockEntityUpdate;
import mod.maxbogomol.wizards_reborn.client.event.ClientTickHandler;
import mod.maxbogomol.wizards_reborn.common.block.ArcaneLumosBlock;
import mod.maxbogomol.wizards_reborn.util.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class LightUtil {

    public static Vec3 getLightLensPos(BlockPos pos, Vec3 lensPos) {
        return new Vec3(pos.getX() + lensPos.x(), pos.getY() + lensPos.y(), pos.getZ() + lensPos.z());
    }

    public static LightRayHitResult getLightRayHitResult(Level level, BlockPos startPos, Vec3 from, Vec3 to, float rayDistance) {
        double dX = to.x() - from.x();
        double dY = to.y() - from.y();
        double dZ = to.z() - from.z();

        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;

        double X = Math.sin(pitch) * Math.cos(yaw) * rayDistance;
        double Y = Math.cos(pitch) * rayDistance;
        double Z = Math.sin(pitch) * Math.sin(yaw) * rayDistance;

        Vec3 newTo = new Vec3(-X, -Y, -Z).add(to);

        HitResult hitresult = level.clip(new ClipContext(from, newTo, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
        Vec3 end = hitresult.getLocation();

        float distance = (float) Math.sqrt(Math.pow(from.x() - end.x, 2) + Math.pow(from.y() - end.y, 2) + Math.pow(from.z() - end.z, 2));
        BlockEntity hitBlockEntity = null;

        BlockPos blockPosHit = BlockPos.containing(end.x(), end.y(), end.z());
        if (startPos.getX() != blockPosHit.getX() || startPos.getY() != blockPosHit.getY() || startPos.getZ() != blockPosHit.getZ()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPosHit);
            if (blockEntity instanceof ILightBlockEntity lightBlockEntityHit && lightBlockEntityHit.getLightLensSize() == 0) {
                distance = (float) Math.sqrt(Math.pow(from.x() - end.x, 2) + Math.pow(from.y() - end.y, 2) + Math.pow(from.z() - end.z, 2));
                hitBlockEntity = blockEntity;
            }
        }

        for (int i = 0; i < distance * 16; i++) {
            float v = (i / (distance * 16)) * distance;
            X = Math.sin(pitch) * Math.cos(yaw) * v;
            Y = Math.cos(pitch) * v;
            Z = Math.sin(pitch) * Math.sin(yaw) * v;

            Vec3 posHit = new Vec3(-X, -Y, -Z).add(from);
            blockPosHit = BlockPos.containing(posHit.x(), posHit.y(), posHit.z());
            if (startPos.getX() != blockPosHit.getX() || startPos.getY() != blockPosHit.getY() || startPos.getZ() != blockPosHit.getZ()) {
                BlockEntity blockEntity = level.getBlockEntity(blockPosHit);
                if (blockEntity instanceof ILightBlockEntity lightBlockEntityHit) {
                    float hitDistance = (float) Math.sqrt(Math.pow(posHit.x() - blockEntity.getBlockPos().getX() - lightBlockEntityHit.getLightLensPos().x(), 2) + Math.pow(posHit.y() - blockEntity.getBlockPos().getY() - lightBlockEntityHit.getLightLensPos().y(), 2) + Math.pow(posHit.z() - blockEntity.getBlockPos().getZ() - lightBlockEntityHit.getLightLensPos().z(), 2));

                    if (hitDistance <= lightBlockEntityHit.getLightLensSize()) {
                        end = posHit;
                        distance = (float) Math.sqrt(Math.pow(from.x() - end.x, 2) + Math.pow(from.y() - end.y, 2) + Math.pow(from.z() - end.z, 2));
                        hitBlockEntity = blockEntity;
                        break;
                    }
                }
            }
        }

        if (distance > rayDistance) {
            distance = rayDistance;
            end = newTo;
            hitBlockEntity = null;
        }

        return new LightRayHitResult(end, distance, hitBlockEntity);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderLightRay(Vec3 from, Vec3 to, float rayDistance, float maxRayDistance, Color color, float partialTicks, PoseStack ms) {
        MultiBufferSource bufferDelayed = LevelRenderHandler.getDelayedRender();

        double dX = to.x() - from.x();
        double dY = to.y() - from.y();
        double dZ = to.z() - from.z();

        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        ms.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(-yaw)));
        ms.mulPose(Axis.ZP.rotationDegrees((float) Math.toDegrees(-pitch) - 90f));
        float width = rayDistance - 0.05f;
        ms.translate(0.05f, 0, 0);
        ms.mulPose(Axis.XP.rotationDegrees((ClientTickHandler.ticksInGame + partialTicks) * 2.5f));
        RenderUtils.ray(ms, bufferDelayed, 0.05f, width, Mth.lerp(rayDistance / maxRayDistance, 1f, 0.75f), r, g, b, 0.3f);
        ms.translate(-0.01f, 0, 0);
        ms.mulPose(Axis.XP.rotationDegrees(-(ClientTickHandler.ticksInGame + partialTicks) * 5f));
        RenderUtils.ray(ms, bufferDelayed, 0.03f, width, Mth.lerp(rayDistance / maxRayDistance, 1f, 0.75f), 1f, 1f, 1f, 0.4f, 0.564f, 0.682f, 0.705f, 0.4f);
    }

    public static void renderLightRay(Level level, BlockPos startPos, Vec3 from, Vec3 to, float rayDistance, Color color, float partialTicks, PoseStack ms) {
        LightRayHitResult hitResult = getLightRayHitResult(level, startPos, from, to, rayDistance);
        renderLightRay(from, hitResult.getPosHit(), hitResult.getDistance(), rayDistance, color, partialTicks, ms);
    }

    public static void transferLight(BlockEntity from, BlockEntity to) {
        if (from != null && to != null) {
            if (from instanceof ILightBlockEntity fromLight) {
                if (to instanceof ILightBlockEntity toLight) {
                    int max = fromLight.getLight();
                    if (max > 1) {
                        if (max < toLight.getLight()) {
                            max = toLight.getLight();
                        }
                        toLight.setLight(max);
                        BlockEntityUpdate.packet(to);
                    }
                }
            }
        }
    }

    public static Color getRayColorFromLumos(Color color, ArcaneLumosBlock lumos, BlockPos pos, float partialTicks) {
        if (lumos != null) {
            if (lumos.color == ArcaneLumosBlock.Colors.RAINBOW) {
                double ticks = (ClientTickHandler.ticksInGame + partialTicks) * 0.1f;
                color = new Color((int)(Math.sin(ticks) * 127 + 128), (int)(Math.sin(ticks + Math.PI/2) * 127 + 128), (int)(Math.sin(ticks + Math.PI) * 127 + 128));
            }
        }

        return color;
    }
}
