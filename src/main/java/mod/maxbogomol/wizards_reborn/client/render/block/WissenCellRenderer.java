package mod.maxbogomol.wizards_reborn.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.maxbogomol.wizards_reborn.common.block.wissen_cell.WissenCellBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class WissenCellRenderer implements BlockEntityRenderer<WissenCellBlockEntity> {

    public WissenCellRenderer() {}

    @Override
    public void render(WissenCellBlockEntity cell, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();

        ms.pushPose();
        ms.translate(0.5F, 0.703125F, 0.5F);
        ms.mulPose(Axis.YP.rotationDegrees(cell.getBlockRotate()));
        ms.mulPose(Axis.XP.rotationDegrees(90F));
        ms.scale(0.5F,0.5F,0.5F);
        mc.getItemRenderer().renderStatic(cell.getItemHandler().getItem(0), ItemDisplayContext.FIXED, light, overlay, ms, buffers, cell.getLevel(), 0);
        ms.popPose();
    }
}
