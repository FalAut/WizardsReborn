package mod.maxbogomol.wizards_reborn.common.block.wissen_cell;

import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.fluffy_fur.common.block.entity.ExposedBlockSimpleInventory;
import mod.maxbogomol.fluffy_fur.common.block.entity.TickableBlockEntity;
import mod.maxbogomol.fluffy_fur.common.easing.Easing;
import mod.maxbogomol.fluffy_fur.registry.client.FluffyFurParticles;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.wissen.IWissenBlockEntity;
import mod.maxbogomol.wizards_reborn.api.wissen.IWissenItem;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenItemUtil;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenUtils;
import mod.maxbogomol.wizards_reborn.common.config.Config;
import mod.maxbogomol.wizards_reborn.common.network.PacketHandler;
import mod.maxbogomol.wizards_reborn.common.network.tileentity.WissenCellSendEffectPacket;
import mod.maxbogomol.wizards_reborn.registry.common.WizardsRebornBlockEntities;
import mod.maxbogomol.wizards_reborn.registry.common.WizardsRebornSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WissenCellBlockEntity extends ExposedBlockSimpleInventory implements TickableBlockEntity, IWissenBlockEntity {
    public int wissen = 0;

    public WissenCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public WissenCellBlockEntity(BlockPos pos, BlockState state) {
        this(WizardsRebornBlockEntities.WISSEN_CELL.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            boolean update = false;
            if (wissen > 0) {
                if (!getItemHandler().getItem(0).isEmpty()) {
                    ItemStack stack = getItemHandler().getItem(0);
                    if (stack.getItem() instanceof IWissenItem) {
                        IWissenItem item = (IWissenItem) stack.getItem();
                        int wissenRemain = WissenUtils.getRemoveWissenRemain(wissen, getWissenPerReceive());
                        wissenRemain = getWissenPerReceive() - wissenRemain;
                        WissenItemUtil.existWissen(stack);
                        int itemWissenRemain = WissenItemUtil.getAddWissenRemain(stack, wissenRemain, item.getMaxWissen());
                        wissenRemain = wissenRemain - itemWissenRemain;
                        if (wissenRemain > 0) {
                            WissenItemUtil.addWissen(stack, wissenRemain, item.getMaxWissen());
                            wissen = wissen - wissenRemain;
                            if (random.nextFloat() < 0.5) {
                                PacketHandler.sendToTracking(level, getBlockPos(), new WissenCellSendEffectPacket(getBlockPos()));
                            }
                            if (random.nextFloat() < 0.1) {
                                level.playSound(WizardsReborn.proxy.getPlayer(), getBlockPos(), WizardsRebornSounds.WISSEN_BURST.get(), SoundSource.BLOCKS, 0.15f, (float) (0.5f + ((random.nextFloat() - 0.5D) / 4)));
                            }

                            update = true;
                        }
                    }
                }
            }

            if (update) setChanged();
        }

        if (level.isClientSide()) {
            if (getWissen() > 0) {
                if (random.nextFloat() < 0.5) {
                    ParticleBuilder.create(FluffyFurParticles.WISP)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB()).build())
                            .setTransparencyData(GenericParticleData.create(0.25f, 0).build())
                            .setScaleData(GenericParticleData.create(0.3f * getStage(), 0).build())
                            .setLifetime(20)
                            .randomVelocity(0.025f * getStage(), 0.025f * getStage(), 0.025f * getStage())
                            .spawn(level, getBlockPos().getX() + 0.5F, getBlockPos().getY() + 0.9375F, getBlockPos().getZ() + 0.5F);
                }
                if (random.nextFloat() < 0.4) {
                    boolean square = random.nextFloat() < 0.2;
                    float i = square ? 0.5f : 1f;
                    ParticleBuilder.create(square ? FluffyFurParticles.SQUARE : FluffyFurParticles.SPARKLE)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB(), 0.5f, 0.5f, 0).build())
                            .setTransparencyData(GenericParticleData.create(0.25f, 0).build())
                            .setScaleData(GenericParticleData.create(0.1f * getStage() * i, 0.2f * getStage() * i, 0).setEasing(Easing.QUINTIC_IN_OUT).build())
                            .randomSpin(0.1f)
                            .setLifetime(100)
                            .randomVelocity(0.025f * getStage() * i)
                            .spawn(level, getBlockPos().getX() + 0.5F, getBlockPos().getY() + 0.9375F, getBlockPos().getZ() + 0.5F);
                }
            }
        }
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new SimpleContainer(1) {
            @Override
            public int getMaxStackSize() {
                return 64;
            }
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        if (stack.getItem() instanceof IWissenItem) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("wissen", wissen);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        wissen = tag.getInt("wissen");
    }

    public float getBlockRotate() {
        switch (this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH:
                return 0F;
            case SOUTH:
                return 180F;
            case WEST:
                return 90F;
            case EAST:
                return 270F;
            default:
                return 0F;
        }
    }

    public float getStage() {
        return ((float) getWissen() / (float) getMaxWissen());
    }

    public int getWissenPerReceive() {
        return 250;
    }

    @Override
    public int getWissen() {
        return wissen;
    }

    @Override
    public int getMaxWissen() {
        return 100000;
    }

    @Override
    public boolean canSendWissen() {
        return true;
    }

    @Override
    public boolean canReceiveWissen() {
        return true;
    }

    @Override
    public boolean canConnectSendWissen() {
        return true;
    }

    @Override
    public boolean canConnectReceiveWissen() {
        return true;
    }

    @Override
    public void setWissen(int wissen) {
        this.wissen = wissen;
    }

    @Override
    public void addWissen(int wissen) {
        this.wissen = this.wissen + wissen;
        if (this.wissen > getMaxWissen()) {
            this.wissen = getMaxWissen();
        }
    }

    @Override
    public void removeWissen(int wissen) {
        this.wissen = this.wissen - wissen;
        if (this.wissen < 0) {
            this.wissen = 0;
        }
    }
}