package mod.maxbogomol.wizards_reborn.common.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.WizardsRebornClient;
import mod.maxbogomol.wizards_reborn.api.skin.Skin;
import mod.maxbogomol.wizards_reborn.client.event.ClientTickHandler;
import mod.maxbogomol.wizards_reborn.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;

public class SkinTrimItem extends Item implements IGuiParticleItem {
    public Skin skin;

    public SkinTrimItem(Properties properties, Skin skin) {
        super(properties);
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flags) {
        list.add(getSkin().getSkinComponent());
    }

    @Override
    public Component getHighlightTip(ItemStack stack, Component displayName) {
        return displayName.copy().append(Component.literal(" (")).append(getSkin().getSkinName()).append(Component.literal(")"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderParticle(PoseStack pose, LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset) {
        float ticks = ClientTickHandler.ticksInGame + Minecraft.getInstance().getPartialTick();
        Color color = getSkin().getColor();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(WizardsRebornClient::getGlowingShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        TextureAtlasSprite sparkle = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(WizardsReborn.MOD_ID, "particle/sparkle"));

        pose.pushPose();
        pose.translate(x + 8, y + 8, 100);
        pose.mulPose(Axis.ZP.rotationDegrees(ticks));
        RenderUtils.spriteGlowQuadCenter(pose, buffersource, 0, 0, 20f, 20f, sparkle.getU0(), sparkle.getU1(), sparkle.getV0(), sparkle.getV1(), r, g, b, 0.5F);
        pose.mulPose(Axis.ZP.rotationDegrees(45f));
        RenderUtils.spriteGlowQuadCenter(pose, buffersource, 0, 0, 20f, 20f, sparkle.getU0(), sparkle.getU1(), sparkle.getV0(), sparkle.getV1(), r, g, b, 0.5F);
        buffersource.endBatch();
        pose.popPose();

        pose.pushPose();
        pose.translate(x + 8, y + 8, 100);
        pose.mulPose(Axis.ZP.rotationDegrees(-ticks));
        RenderUtils.spriteGlowQuadCenter(pose, buffersource, 0, 0, 18f, 18f, sparkle.getU0(), sparkle.getU1(), sparkle.getV0(), sparkle.getV1(), r, g, b, 0.5F);
        pose.mulPose(Axis.ZP.rotationDegrees(45f));
        RenderUtils.spriteGlowQuadCenter(pose, buffersource, 0, 0, 18f, 18f, sparkle.getU0(), sparkle.getU1(), sparkle.getV0(), sparkle.getV1(), r, g, b, 0.5F);
        buffersource.endBatch();
        pose.popPose();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }
}
