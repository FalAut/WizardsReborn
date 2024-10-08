package mod.maxbogomol.wizards_reborn.common.block.salt_torch;

import mod.maxbogomol.fluffy_fur.FluffyFur;
import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.LightParticleData;
import mod.maxbogomol.fluffy_fur.common.block.entity.ExposedBlockSimpleInventory;
import mod.maxbogomol.fluffy_fur.common.block.entity.TickableBlockEntity;
import mod.maxbogomol.fluffy_fur.common.easing.Easing;
import mod.maxbogomol.fluffy_fur.util.RenderUtils;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.common.block.ArcaneLumosBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;

public class SaltTorchBlockEntity extends ExposedBlockSimpleInventory implements TickableBlockEntity {

    public static Color colorFirst = new Color(255, 170, 65);
    public static Color colorSecond = new Color(231, 71, 101);

    public SaltTorchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SaltTorchBlockEntity(BlockPos pos, BlockState state) {
        this(WizardsReborn.SALT_TORCH_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            Color colorF = colorFirst;
            Color color = colorSecond;
            Vec3 pos = new Vec3(0.5f, 0.6875f, 0.5f);
            boolean isCosmic = false;

            if (getBlockState().getBlock() instanceof SaltWallTorchBlock) {
                BlockPos blockPos = new BlockPos(0, 0, 0).relative(getBlockState().getValue(SaltWallTorchBlock.FACING));
                pos = pos.add(blockPos.getX() * -0.25f, 0.09375f, blockPos.getZ() * -0.25f);
            }

            if (!getItemHandler().getItem(0).isEmpty()) {
                if (getItemHandler().getItem(0).getItem() instanceof BlockItem) {
                    BlockItem blockItem = (BlockItem) getItemHandler().getItem(0).getItem();
                    if (blockItem.getBlock() instanceof ArcaneLumosBlock) {
                        ArcaneLumosBlock lumos = (ArcaneLumosBlock) blockItem.getBlock();
                        color = ArcaneLumosBlock.getColor(lumos.color);

                        if (lumos.color == ArcaneLumosBlock.Colors.COSMIC) {
                            isCosmic = true;
                        }
                    }
                }
            }
            if (!getItemHandler().getItem(1).isEmpty()) {
                if (getItemHandler().getItem(1).getItem() instanceof BlockItem) {
                    BlockItem blockItem = (BlockItem) getItemHandler().getItem(1).getItem();
                    if (blockItem.getBlock() instanceof ArcaneLumosBlock) {
                        ArcaneLumosBlock lumos = (ArcaneLumosBlock) blockItem.getBlock();
                        colorF = ArcaneLumosBlock.getColor(lumos.color);

                        if (lumos.color == ArcaneLumosBlock.Colors.COSMIC) {
                            isCosmic = true;
                        }
                    }
                }
            }

            if (random.nextFloat() < 0.5) {
                ParticleBuilder.create(FluffyFur.SPARKLE_PARTICLE)
                        .setColorData(ColorParticleData.create(colorF, color).build())
                        .setTransparencyData(GenericParticleData.create(0.25f, 0).build())
                        .setScaleData(GenericParticleData.create(0.35f, 0).setEasing(Easing.CUBIC_IN_OUT).build())
                        .randomSpin(0.005f)
                        .setLifetime(30)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }
            if (random.nextFloat() < 0.45) {
                ParticleBuilder.create(random.nextFloat() < 0.3 ? FluffyFur.TINY_STAR_PARTICLE : FluffyFur.SPARKLE_PARTICLE)
                        .setColorData(ColorParticleData.create(colorF, color).build())
                        .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                        .setScaleData(GenericParticleData.create(0.25f, 0).setEasing(Easing.SINE_IN_OUT).build())
                        .randomSpin(0.01f)
                        .setLifetime(60)
                        .randomVelocity(0.007f)
                        .addVelocity(0, 0.015f, 0)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }
            if (random.nextFloat() < 0.45) {
                ParticleBuilder.create(FluffyFur.TINY_WISP_PARTICLE)
                        .setColorData(ColorParticleData.create(colorF, color).build())
                        .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                        .setScaleData(GenericParticleData.create(0.15f, 0).setEasing(Easing.SINE_OUT).build())
                        .setLifetime(30)
                        .randomVelocity(0.015f)
                        .addVelocity(0, 0.03f, 0)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }
            if (random.nextFloat() < 0.3) {
                ParticleBuilder.create(FluffyFur.SMOKE_PARTICLE)
                        .setRenderType(RenderUtils.DELAYED_PARTICLE)
                        .setColorData(ColorParticleData.create(Color.BLACK).build())
                        .setTransparencyData(GenericParticleData.create(0.2f, 0).build())
                        .setScaleData(GenericParticleData.create(0.25f, 0).build())
                        .setLightData(LightParticleData.DEFAULT)
                        .randomSpin(0.1f)
                        .setLifetime(60)
                        .randomVelocity(0.0035f)
                        .addVelocity(0, 0.03f, 0)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }

            if (isCosmic) {
                if (random.nextFloat() < 0.1) {
                    ParticleBuilder.create(FluffyFur.STAR_PARTICLE)
                            .setColorData(ColorParticleData.create(color).build())
                            .setTransparencyData(GenericParticleData.create(0.75f, 0).build())
                            .setScaleData(GenericParticleData.create(0, 0.1f, 0).setEasing(Easing.SINE_IN_OUT).build())
                            .randomSpin(0.1f)
                            .setLifetime(10)
                            .randomVelocity(0.015f)
                            .flatRandomOffset(0.2f, 0.2f, 0.2f)
                            .addVelocity(0, 0.025f, 0)
                            .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y() + 0.1f, worldPosition.getZ() + pos.z());
                }
                if (random.nextFloat() < 0.1) {
                    ParticleBuilder.create(FluffyFur.STAR_PARTICLE)
                            .setColorData(ColorParticleData.create(Color.WHITE).build())
                            .setTransparencyData(GenericParticleData.create(0.75f, 0).build())
                            .setScaleData(GenericParticleData.create(0, 0.1f, 0).setEasing(Easing.SINE_IN_OUT).build())
                            .randomSpin(0.1f)
                            .setLifetime(10)
                            .randomVelocity(0.015f)
                            .flatRandomOffset(0.2f, 0.2f, 0.2f)
                            .addVelocity(0, 0.025f, 0)
                            .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y() + 0.1f, worldPosition.getZ() + pos.z());
                }
            }
        }
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new SimpleContainer(2) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        return new AABB(pos.getX() - 0.5f, pos.getY() - 0.5f, pos.getZ() - 0.5f, pos.getX() + 1.5f, pos.getY() + 1.5f, pos.getZ() + 1.5f);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        if (stack.is(WizardsReborn.ARCANE_LUMOS_ITEM_TAG)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return true;
    }

    public int getInventorySize() {
        int size = 0;

        for (int i = 0; i < getItemHandler().getContainerSize(); i++) {
            if (!getItemHandler().getItem(i).isEmpty()) {
                size++;
            }
        }

        return size;
    }
}
