package mod.maxbogomol.wizards_reborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {

    public static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static final RenderState.TransparencyState NORMAL_TRANSPARENCY = new RenderState.TransparencyState("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType GLOWING_PARTICLE = RenderType.makeType(
        WizardsReborn.MOD_ID + ":glowing_particle",
        DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP,
        GL11.GL_QUADS, 256,
                RenderType.State.getBuilder()
                .shadeModel(new RenderState.ShadeModelState(true))
                .writeMask(new RenderState.WriteMaskState(true, false))
                .lightmap(new RenderState.LightmapState(false))
                .diffuseLighting(new RenderState.DiffuseLightingState(false))
                .transparency(ADDITIVE_TRANSPARENCY)
                        .texture(new RenderState.TextureState(AtlasTexture.LOCATION_PARTICLES_TEXTURE, false, false))
                .build(false));

    public static void renderItemModelInGui(ItemStack stack, int x, int y, int xSize, int ySize, int zSize) {
        Minecraft mc = Minecraft.getInstance();

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x+(xSize/2), (float)y+(ySize/2), 10.0F);
        RenderSystem.scalef((float)xSize/16, (float)ySize/16, (float)zSize/16);
        RenderSystem.translatef((float)-(x+(xSize/2)), (float)-(y+(ySize/2)), 0.0F);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(mc.player, stack, x, y);
        RenderSystem.popMatrix();
    }

    public static Vector3d followBodyRotation(LivingEntity living) {
        Vector3d rotate = new Vector3d(0, 0, 0);
        EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getRenderManager().getRenderer(living);
        if(render instanceof LivingRenderer) {
            LivingRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingRenderer<LivingEntity, EntityModel<LivingEntity>>) render;
            EntityModel<LivingEntity> entityModel = livingRenderer.getEntityModel();
            if (entityModel instanceof BipedModel) {
                BipedModel<LivingEntity> bipedModel = (BipedModel<LivingEntity>) entityModel;
                rotate = new Vector3d(bipedModel.bipedBody.rotateAngleX, bipedModel.bipedBody.rotateAngleY, bipedModel.bipedBody.rotateAngleZ);;
            }
        }
        return rotate;
    }

    public static void renderBoxBlockOutline(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, VoxelShape voxelShape, double originX, double originY, double originZ, Color color) {
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getLines());
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        voxelShape.forEachEdge((x0, y0, z0, x1, y1, z1) -> {
            builder.pos(matrix4f, (float)(x0 + originX), (float)(y0 + originY), (float)(z0 + originZ)).color(red, green, blue, alpha).endVertex();
            builder.pos(matrix4f, (float)(x1 + originX), (float)(y1 + originY), (float)(z1 + originZ)).color(red, green, blue, alpha).endVertex();
        });
    }

    public static void renderLine(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getLines());
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        builder.pos(matrix4f, (float) (x1), (float) (y1), (float) (z1)).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix4f, (float) (x2), (float) (y2), (float) (z2)).color(red, green, blue, alpha).endVertex();
    }
}