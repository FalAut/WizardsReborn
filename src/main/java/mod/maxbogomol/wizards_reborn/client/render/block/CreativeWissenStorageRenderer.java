package mod.maxbogomol.wizards_reborn.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.maxbogomol.wizards_reborn.common.block.creative.wissen_storage.CreativeWissenStorageBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class CreativeWissenStorageRenderer implements BlockEntityRenderer<CreativeWissenStorageBlockEntity> {

    public CreativeWissenStorageRenderer() {}

    @Override
    public void render(CreativeWissenStorageBlockEntity cell, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();

        ms.pushPose();
        ms.translate(0.5F, 0.84375F, 0.5F);
        ms.mulPose(Axis.YP.rotationDegrees(cell.getBlockRotate()));
        ms.mulPose(Axis.XP.rotationDegrees(90F));
        ms.scale(0.5F,0.5F,0.5F);
        mc.getItemRenderer().renderStatic(cell.getItemHandler().getItem(0), ItemDisplayContext.FIXED, light, overlay, ms, buffers, cell.getLevel(), 0);
        ms.popPose();
    }
}
