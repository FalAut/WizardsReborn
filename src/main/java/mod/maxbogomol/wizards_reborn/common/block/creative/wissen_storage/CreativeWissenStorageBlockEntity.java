package mod.maxbogomol.wizards_reborn.common.block.creative.wissen_storage;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.common.block.wissen_cell.WissenCellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeWissenStorageBlockEntity extends WissenCellBlockEntity {

    public CreativeWissenStorageBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public CreativeWissenStorageBlockEntity(BlockPos pos, BlockState state) {
        this(WizardsReborn.CREATIVE_WISSEN_STORAGE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        wissen = getMaxWissen();
        super.tick();
    }

    @Override
    public int getWissenPerReceive() {
        return 1000000;
    }

    @Override
    public int getWissen() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxWissen() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setWissen(int wissen) {

    }

    @Override
    public void addWissen(int wissen) {

    }

    @Override
    public void removeWissen(int wissen) {

    }
}
