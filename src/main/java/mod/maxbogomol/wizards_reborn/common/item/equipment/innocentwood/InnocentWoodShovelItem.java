package mod.maxbogomol.wizards_reborn.common.item.equipment.innocentwood;

import mod.maxbogomol.wizards_reborn.common.item.equipment.arcanewood.ArcaneWoodShovelItem;
import mod.maxbogomol.wizards_reborn.common.item.equipment.arcanewood.ArcaneWoodTools;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class InnocentWoodShovelItem extends ArcaneWoodShovelItem {

    public InnocentWoodShovelItem(Tier tier, float attackDamageModifier, float attackSpeedModifier, Properties properties, Item repairItem) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties, repairItem);
    }

    @Override
    public ArcaneWoodTools getTools(Item repairItem) {
        return new InnocentWoodTools(repairItem);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!slotChanged) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, true);
    }
}
