package mod.maxbogomol.wizards_reborn.common.spell.projectile;

import mod.maxbogomol.wizards_reborn.api.crystal.CrystalUtil;
import mod.maxbogomol.wizards_reborn.common.entity.SpellEntity;
import mod.maxbogomol.wizards_reborn.common.item.equipment.arcane.ArcaneArmorItem;
import mod.maxbogomol.wizards_reborn.common.network.PacketHandler;
import mod.maxbogomol.wizards_reborn.common.network.spell.ProjectileSpellHeartsPacket;
import mod.maxbogomol.wizards_reborn.common.network.spell.ProjectileSpellSkullsPacket;
import mod.maxbogomol.wizards_reborn.registry.common.WizardsRebornCrystals;
import mod.maxbogomol.wizards_reborn.registry.common.WizardsRebornSpells;
import mod.maxbogomol.wizards_reborn.registry.common.damage.WizardsRebornDamage;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.awt.*;

public class HolyProjectileSpell extends ProjectileSpell {

    public HolyProjectileSpell(String id, int points) {
        super(id, points);
        addCrystalType(WizardsRebornCrystals.EARTH);
        addCrystalType(WizardsRebornCrystals.AIR);
    }

    @Override
    public Color getColor() {
        return WizardsRebornSpells.holySpellColor;
    }

    @Override
    public void onImpact(Level level, SpellEntity entity, HitResult hitResult, Entity target) {
        super.onImpact(level, entity, hitResult, target);

        if (!level.isClientSide()) {
            if (target instanceof LivingEntity livingEntity) {
                int focusLevel = CrystalUtil.getStatLevel(entity.getStats(), WizardsRebornCrystals.FOCUS);
                float magicModifier = ArcaneArmorItem.getPlayerMagicModifier(entity.getOwner());
                float damage = (float) (1.5f + (focusLevel * 0.5)) + magicModifier;
                if (livingEntity.isInvertedHealAndHarm()) {
                    DamageSource damageSource = getDamage(WizardsRebornDamage.create(target.level(), WizardsRebornDamage.ARCANE_MAGIC).typeHolder(), entity, entity.getOwner());
                    target.hurt(damageSource, damage);
                    PacketHandler.sendToTracking(level, entity.blockPosition(), new ProjectileSpellSkullsPacket(entity.position().add(0, 0.2f, 0), getColor()));
                } else {
                    livingEntity.heal(damage);
                    PacketHandler.sendToTracking(level, entity.blockPosition(), new ProjectileSpellHeartsPacket(entity.position().add(0, 0.2f, 0), getColor()));
                }
            }
        }
    }

    @Override
    public void burstEffectEntity(Level level, SpellEntity entity) {

    }
}