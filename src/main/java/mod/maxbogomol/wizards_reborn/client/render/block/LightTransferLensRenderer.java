package mod.maxbogomol.wizards_reborn.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.maxbogomol.fluffy_fur.client.render.LevelRenderHandler;
import mod.maxbogomol.wizards_reborn.WizardsRebornClient;
import mod.maxbogomol.wizards_reborn.api.light.ILightBlockEntity;
import mod.maxbogomol.wizards_reborn.api.light.LightUtil;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenUtils;
import mod.maxbogomol.wizards_reborn.client.event.ClientTickHandler;
import mod.maxbogomol.wizards_reborn.common.block.light_transfer_lens.LightTransferLensBlockEntity;
import mod.maxbogomol.wizards_reborn.util.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Random;

public class LightTransferLensRenderer implements BlockEntityRenderer<LightTransferLensBlockEntity> {

    public LightTransferLensRenderer() {}

    @Override
    public void render(LightTransferLensBlockEntity lens, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
        Random random = new Random();
        random.setSeed(lens.getBlockPos().asLong());

        double ticks = (ClientTickHandler.ticksInGame + partialTicks) * 0.4f;
        double ticksAlpha = (ClientTickHandler.ticksInGame + partialTicks);
        float alpha = (float) (0.35f + Math.abs(Math.sin(Math.toRadians(random.nextFloat() * 360f + ticksAlpha)) * 0.3f));

        MultiBufferSource bufferDelayed = LevelRenderHandler.getDelayedRender();

        ms.pushPose();
        ms.translate(0.5F, 0.5F, 0.5F);
        ms.mulPose(Axis.YP.rotationDegrees((float) (random.nextFloat() * 360 + ticks)));
        ms.mulPose(Axis.XP.rotationDegrees((float) (random.nextFloat() * 360 + ticks)));
        ms.mulPose(Axis.ZP.rotationDegrees((float) (random.nextFloat() * 360 + ticks)));
        RenderUtils.renderCustomModel(WizardsRebornClient.HOVERING_LENS_MODEL, ItemDisplayContext.FIXED, false, ms, buffers, light, overlay);
        RenderUtils.ray(ms, bufferDelayed, 0.075f, 0.075f, 1f, 0.564f, 0.682f, 0.705f, alpha, 0.564f, 0.682f, 0.705f, alpha);
        ms.popPose();

        if (lens.isToBlock && lens.canWork() && lens.getLight() > 0) {
            BlockPos pos = new BlockPos(lens.blockToX, lens.blockToY, lens.blockToZ);
            if (lens.getLevel().getBlockEntity(pos) instanceof ILightBlockEntity lightTile) {
                Vec3 from = LightUtil.getLightLensPos(lens.getBlockPos(), lens.getLightLensPos());
                Vec3 to = LightUtil.getLightLensPos(pos, lightTile.getLightLensPos());

                ms.pushPose();
                ms.translate(0.5F, 0.5F, 0.5F);
                Color color = LightUtil.getRayColorFromLumos(lens.getColor(), lens.getLumos(), lens.getBlockPos(), partialTicks);
                LightUtil.renderLightRay(lens.getLevel(), lens.getBlockPos(), from, to, 25f, color, partialTicks, ms);
                ms.popPose();
            }
        }

        if (WissenUtils.isCanRenderWissenWand()) {
            if (lens.isToBlock) {
                ms.pushPose();
                Vec3 lensPos = lens.getLightLensPos();
                ms.translate(lensPos.x(), lensPos.y(), lensPos.z());
                BlockPos pos = new BlockPos(lens.blockToX, lens.blockToY, lens.blockToZ);
                if (lens.getLevel().getBlockEntity(pos) instanceof ILightBlockEntity lightTile) {
                    RenderUtils.renderConnectLine(LightUtil.getLightLensPos(lens.getBlockPos(), lens.getLightLensPos()), LightUtil.getLightLensPos(pos, lightTile.getLightLensPos()), RenderUtils.colorConnectTo, partialTicks, ms);
                }
                ms.popPose();
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(LightTransferLensBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(LightTransferLensBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return true;
    }
}
