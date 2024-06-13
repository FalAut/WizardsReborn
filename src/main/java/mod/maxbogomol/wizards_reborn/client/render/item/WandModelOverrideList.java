package mod.maxbogomol.wizards_reborn.client.render.item;

import mod.maxbogomol.wizards_reborn.api.skin.Skin;
import mod.maxbogomol.wizards_reborn.common.item.equipment.ArcaneWandItem;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class WandModelOverrideList extends CustomModelOverrideList {

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int pSeed) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains("crystal") && nbt.getBoolean("crystal")) {
            SimpleContainer stack_inv = ArcaneWandItem.getInventory(stack);
            String string = stack_inv.getItem(0).getDescriptionId();

            int i = string.indexOf(".");
            string = string.substring(i + 1);
            i = string.indexOf(".");
            String modId = string.substring(0, i);
            String crystalId = string.substring(i + 1);

            Skin skin = Skin.getSkinFromItem(stack);
            if (skin != null) {
                return WandCrystalsModels.getModel(skin.getItemModelName(stack), modId + ":" + crystalId);
            } else {
                string = stack.getDescriptionId();
                i = string.indexOf(".");
                string = string.substring(i + 1);
                i = string.indexOf(".");
                String modIdW = string.substring(0, i);
                String wandId = string.substring(i + 1);

                return WandCrystalsModels.getModel(modIdW + ":" + wandId, modId + ":" + crystalId);
            }
        }

        Skin skin = Skin.getSkinFromItem(stack);
        if (skin != null) {
            return WandCrystalsModels.getModel(skin.getItemModelName(stack), "");
        }

        return originalModel;
    }
}