package mod.maxbogomol.wizards_reborn.common.spell.block;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.crystal.CrystalUtil;
import mod.maxbogomol.wizards_reborn.common.item.equipment.arcane.ArcaneArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DirtBlockSpell extends BlockPlaceSpell {
    List<Block> blockList = new ArrayList<>();

    public DirtBlockSpell(String id, int points) {
        super(id, points);
        addCrystalType(WizardsReborn.EARTH_CRYSTAL_TYPE);
        blockList.add(Blocks.GRASS_BLOCK);
        blockList.add(Blocks.COARSE_DIRT);
        blockList.add(Blocks.ROOTED_DIRT);
        blockList.add(Blocks.PODZOL);
        blockList.add(Blocks.MYCELIUM);
    }

    @Override
    public Color getColor() {
        return WizardsReborn.earthSpellColor;
    }

    @Override
    public InteractionResult placeBlock(ItemStack stack, UseOnContext context, BlockPos blockPos) {
        Level level = context.getLevel();
        BlockPos blockPos1 = blockPos.relative(context.getClickedFace());
        BlockState blockState = Blocks.DIRT.defaultBlockState();

        CompoundTag stats = getStats(stack);
        int focusLevel = CrystalUtil.getStatLevel(stats, WizardsReborn.FOCUS_CRYSTAL_STAT);
        float magicModifier = ArcaneArmorItem.getPlayerMagicModifier(context.getPlayer());
        if (random.nextFloat() < 0.15f + (0.05f* (focusLevel + magicModifier))) {
            blockState = blockList.get(random.nextInt(blockList.size())).defaultBlockState();
        }
        setBlock(level, blockPos1, blockState, context.getPlayer());
        return InteractionResult.SUCCESS;
    }
}
