package mod.maxbogomol.wizards_reborn.api.alchemy;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidBlockEntity {
    FluidStack getFluidStack();
    int getFluidAmount();
    int getFluidMaxAmount();
}
