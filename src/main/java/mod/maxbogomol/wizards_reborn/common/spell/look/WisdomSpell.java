package mod.maxbogomol.wizards_reborn.common.spell.look;

import mod.maxbogomol.fluffy_fur.FluffyFur;
import mod.maxbogomol.fluffy_fur.client.animation.ItemAnimation;
import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.crystal.CrystalUtil;
import mod.maxbogomol.wizards_reborn.client.animation.StrikeSpellItemAnimation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class WisdomSpell extends LookSpell {
    public static StrikeSpellItemAnimation animation = new StrikeSpellItemAnimation();

    public WisdomSpell(String id, int points) {
        super(id, points);
        addCrystalType(WizardsReborn.VOID_CRYSTAL_TYPE);
    }

    @Override
    public Color getColor() {
        return WizardsReborn.experienceSpellColor;
    }

    @Override
    public int getCooldown() {
        return 50;
    }

    @Override
    public int getWissenCost() {
        return 300;
    }

    @Override
    public int getMinimumPolishingLevel() {
        return 1;
    }

    @Override
    public void useSpell(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide) {
            player.startUsingItem(hand);
        }
    }

    @Override
    public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            if (player.getTicksUsingItem() % 20 == 0 && player.getTicksUsingItem() > 0) {
                if (!world.isClientSide) {
                    CompoundTag stats = getStats(stack);
                    removeWissen(stack, stats, player);
                    awardStat(player, stack);
                    spellSound(player, world);
                    lookSpell(world, player, player.getUsedItemHand());
                } else {
                    Vec3 pos = getHitPos(world, player, player.getUsedItemHand()).getPosHit();
                    Color color = getColor();
                    ParticleBuilder.create(FluffyFur.SPARKLE_PARTICLE)
                            .setColorData(ColorParticleData.create(color).build())
                            .setTransparencyData(GenericParticleData.create(0.5f).build())
                            .setScaleData(GenericParticleData.create(0.2f, 0f).build())
                            .setLifetime(15)
                            .spawn(world, pos.x(), pos.y(), pos.z());
                    ParticleBuilder.create(FluffyFur.SPARKLE_PARTICLE)
                            .setColorData(ColorParticleData.create(color).build())
                            .setTransparencyData(GenericParticleData.create(0.5f).build())
                            .setScaleData(GenericParticleData.create(0.2f, 0f).build())
                            .setLifetime(10)
                            .spawn(world, pos.x(), pos.y(), pos.z());
                }
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {
        if (!world.isClientSide) {
            CompoundTag stats = getStats(stack);
            setCooldown(stack, stats);
        }
    }

    @Override
    public void lookSpell(Level world, Player player, InteractionHand hand) {
        Vec3 pos = getHitPos(world, player, hand).getPosHit();

        ItemStack stack = player.getItemInHand(hand);
        CompoundTag stats = getStats(stack);
        int focusLevel = CrystalUtil.getStatLevel(stats, WizardsReborn.FOCUS_CRYSTAL_STAT);
        int exp = 5 + focusLevel;

        world.addFreshEntity(new ExperienceOrb(world, pos.x, pos.y, pos.z, exp));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public boolean hasCustomAnimation(ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemAnimation getAnimation(ItemStack stack) {
        return animation;
    }
}
