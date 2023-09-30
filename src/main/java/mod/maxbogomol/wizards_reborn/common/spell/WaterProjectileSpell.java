package mod.maxbogomol.wizards_reborn.common.spell;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.crystal.CrystalUtils;
import mod.maxbogomol.wizards_reborn.common.entity.SpellProjectileEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.awt.*;

public class WaterProjectileSpell extends ProjectileSpell {
    public WaterProjectileSpell(String id) {
        super(id);
        addCrystalType(WizardsReborn.WATER_CRYSTAL_TYPE);
    }

    @Override
    public Color getColor() {
        return new Color(152, 180, 223);
    }

    @Override
    public void onImpact(HitResult ray, Level world, SpellProjectileEntity projectile, Player player, Entity target) {
        super.onImpact(ray, world, projectile, player, target);

        int focusLevel = CrystalUtils.getStatLevel(projectile.getStats(), WizardsReborn.FOCUS_CRYSTAL_STAT);

        target.hurt(new DamageSource(projectile.damageSources().drown().typeHolder(), projectile, player),  (float) (2.0f + (focusLevel * 0.5)));
        target.clearFire();
        int frost = target.getTicksFrozen() + 10;
        if (frost > 250) {
            frost = 250;
        }
        target.setTicksFrozen(frost);
    }
}
