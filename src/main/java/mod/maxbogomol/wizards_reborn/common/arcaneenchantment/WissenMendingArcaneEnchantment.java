package mod.maxbogomol.wizards_reborn.common.arcaneenchantment;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.arcaneenchantment.ArcaneEnchantment;
import mod.maxbogomol.wizards_reborn.api.arcaneenchantment.ArcaneEnchantmentType;
import mod.maxbogomol.wizards_reborn.api.arcaneenchantment.ArcaneEnchantmentUtil;
import mod.maxbogomol.wizards_reborn.api.arcaneenchantment.IArcaneItem;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.List;

public class WissenMendingArcaneEnchantment extends ArcaneEnchantment {

    public WissenMendingArcaneEnchantment(String id, int maxLevel) {
        super(id, maxLevel);
    }

    @Override
    public Color getColor() {
        return new Color(87, 127, 184);
    }

    @Override
    public boolean canEnchantItem(ItemStack stack) {
        if (stack.getItem() instanceof IArcaneItem item) {
            if (ArcaneEnchantmentUtil.getArcaneEnchantment(stack, WizardsReborn.LIFE_MENDING_ARCANE_ENCHANTMENT) > 0) return false;
            return item.getArcaneEnchantmentTypes().contains(ArcaneEnchantmentType.BREAKABLE);
        }
        return false;
    }

    public static void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected) {
        if (!world.isClientSide()) {
            if (entity instanceof Player player) {
                int enchantmentLevel = ArcaneEnchantmentUtil.getArcaneEnchantment(stack, WizardsReborn.WISSEN_MENDING_ARCANE_ENCHANTMENT);

                if (enchantmentLevel > 0 && stack.getDamageValue() > 0) {
                    int tick = 100 - ((enchantmentLevel - 1) * 50);
                    if (enchantmentLevel >= 3) {
                        tick = 20;
                    }
                    if (entity.tickCount % tick == 0) {
                        float costModifier = WissenUtils.getWissenCostModifierWithDiscount(player);
                        List<ItemStack> items = WissenUtils.getWissenItemsNoneAndStorage(WissenUtils.getWissenItemsCurios(player));
                        int wissen = WissenUtils.getWissenInItems(items);
                        int cost = (int) (5 * (1 - costModifier));
                        if (cost <= 0) {
                            cost = 1;
                        }

                        if (WissenUtils.canRemoveWissen(wissen, cost)) {
                            WissenUtils.removeWissenFromWissenItems(items, cost);
                            stack.setDamageValue(stack.getDamageValue() - 1);
                        }
                    }
                }
            }
        }
    }

    public static int damageItem(ItemStack stack, int amount, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            if (amount > 0) {
                if (entity instanceof Player player) {
                    int enchantmentLevel = ArcaneEnchantmentUtil.getArcaneEnchantment(stack, WizardsReborn.WISSEN_MENDING_ARCANE_ENCHANTMENT);
                    float costModifier = WissenUtils.getWissenCostModifierWithDiscount(player);
                    List<ItemStack> items = WissenUtils.getWissenItemsNoneAndStorage(WissenUtils.getWissenItemsCurios(player));
                    int wissen = WissenUtils.getWissenInItems(items);
                    int cost = (int) (5 * (1 - costModifier));
                    if (cost <= 0) {
                        cost = 1;
                    }

                    if (enchantmentLevel >= 3 && WissenUtils.canRemoveWissen(wissen, cost)) {
                        WissenUtils.removeWissenFromWissenItems(items, cost);
                        amount--;
                    }
                }
            }
        }

        return amount;
    }
}
