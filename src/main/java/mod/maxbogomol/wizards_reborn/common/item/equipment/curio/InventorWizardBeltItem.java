package mod.maxbogomol.wizards_reborn.common.item.equipment.curio;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.registry.common.WizardsRebornAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import java.util.UUID;

public class InventorWizardBeltItem extends BaseCurioItem {

    private static final ResourceLocation BELT_TEXTURE = new ResourceLocation(WizardsReborn.MOD_ID,"textures/entity/curio/inventor_wizard_belt.png");

    public InventorWizardBeltItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();
        atts.put(WizardsRebornAttributes.WISSEN_DISCOUNT.get(), new AttributeModifier(uuid, "bonus", 10, AttributeModifier.Operation.ADDITION));
        atts.put(WizardsRebornAttributes.MAGIC_ARMOR.get(), new AttributeModifier(uuid, "bonus", 10, AttributeModifier.Operation.ADDITION));
        return atts;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, LivingEntity entity) {
        return BELT_TEXTURE;
    }
}
