package mod.maxbogomol.wizards_reborn.common.crystal;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.crystal.CrystalType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class VoidCrystalType extends CrystalType {
    public VoidCrystalType(String id) {
        super(id);
    }

    public Color getColor() {
        return WizardsReborn.voidCrystalColor;
    }

    public ResourceLocation getMiniIcon() {
        return new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/spell/void_mini_icon.png");
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(WizardsReborn.MOD_ID, "textures/gui/spell/void_icon.png");
    }

    public ItemStack getFracturedCrystal() {
        return WizardsReborn.FRACTURED_VOID_CRYSTAL.get().getDefaultInstance();
    }

    public ItemStack getCrystal() {
        return WizardsReborn.VOID_CRYSTAL.get().getDefaultInstance();
    }
}
