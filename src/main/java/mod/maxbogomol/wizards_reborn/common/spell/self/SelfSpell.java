package mod.maxbogomol.wizards_reborn.common.spell.self;

import mod.maxbogomol.wizards_reborn.api.spell.Spell;
import mod.maxbogomol.wizards_reborn.api.spell.SpellContext;
import mod.maxbogomol.wizards_reborn.common.spell.WandSpellContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SelfSpell extends Spell {

    public SelfSpell(String id, int points) {
        super(id, points);
    }

    @Override
    public void useSpell(Level level, SpellContext spellContext) {
        if (!level.isClientSide()) {
            spellContext.setCooldown(this);
            spellContext.removeWissen(this);
            spellContext.awardStat(this);
            spellContext.spellSound(this);
        }
        selfSpell(level, spellContext);
    }

    @Override
    public void useWand(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            WandSpellContext spellContext = WandSpellContext.getFromWand(player, stack);
            useSpell(level, spellContext);
        }
    }
/*
    public boolean canSpell(Level level, SpellContext spellContext) {
        if (super.canSpell(level, player, hand)) {
            return canSelfSpell(level, spellContext);
        }
        return false;
    }*/

    public boolean canSelfSpell(Level level, SpellContext spellContext) {
        return true;
    }

    public void selfSpell(Level level, SpellContext spellContext) {

    }
}
