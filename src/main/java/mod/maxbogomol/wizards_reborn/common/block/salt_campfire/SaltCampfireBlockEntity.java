package mod.maxbogomol.wizards_reborn.common.block.salt_campfire;

import mod.maxbogomol.fluffy_fur.FluffyFur;
import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.LightParticleData;
import mod.maxbogomol.fluffy_fur.common.block.entity.ExposedBlockSimpleInventory;
import mod.maxbogomol.fluffy_fur.common.block.entity.TickableBlockEntity;
import mod.maxbogomol.fluffy_fur.common.easing.Easing;
import mod.maxbogomol.fluffy_fur.common.network.BlockEntityUpdate;
import mod.maxbogomol.fluffy_fur.util.RenderUtils;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.common.block.ArcaneLumosBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

public class SaltCampfireBlockEntity extends ExposedBlockSimpleInventory implements TickableBlockEntity {

    public static Color colorFirst = new Color(255, 170, 65);
    public static Color colorSecond = new Color(231, 71, 101);

    public static final int BURN_COOL_SPEED = 2;
    public static final int NUM_SLOTS = 4;
    public final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    public final int[] cookingProgress = new int[4];
    public final int[] cookingTime = new int[4];
    public final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public SaltCampfireBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SaltCampfireBlockEntity(BlockPos pos, BlockState state) {
        this(WizardsReborn.SALT_CAMPFIRE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            cookTick(level, getBlockPos(), getBlockState(), this);
        }

        if (level.isClientSide()) {
            Color colorF = colorFirst;
            Color color = colorSecond;
            Vec3 pos = new Vec3(0.5f, 0.26f, 0.5f);
            boolean isCosmic = false;

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
                        .setScaleData(GenericParticleData.create(0.55f, 0).setEasing(Easing.CUBIC_IN_OUT).build())
                        .randomSpin(0.005f)
                        .setLifetime(30)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }
            if (random.nextFloat() < 0.45) {
                ParticleBuilder.create(random.nextFloat() < 0.3 ? FluffyFur.TINY_STAR_PARTICLE : FluffyFur.SPARKLE_PARTICLE)
                        .setColorData(ColorParticleData.create(colorF, color).build())
                        .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                        .setScaleData(GenericParticleData.create(0.45f, 0).setEasing(Easing.SINE_IN_OUT).build())
                        .randomSpin(0.01f)
                        .setLifetime(60)
                        .randomVelocity(0.0025f)
                        .addVelocity(0, 0.025f, 0)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }
            if (random.nextFloat() < 0.45) {
                ParticleBuilder.create(FluffyFur.TINY_WISP_PARTICLE)
                        .setColorData(ColorParticleData.create(colorF, color).build())
                        .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                        .setScaleData(GenericParticleData.create(0.35f, 0).setEasing(Easing.SINE_OUT).build())
                        .setLifetime(30)
                        .randomVelocity(0.005f)
                        .addVelocity(0, 0.04f, 0)
                        .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
            }
            if (random.nextFloat() < 0.3) {
                ParticleBuilder.create(FluffyFur.SMOKE_PARTICLE)
                        .setRenderType(RenderUtils.DELAYED_PARTICLE)
                        .setColorData(ColorParticleData.create(Color.BLACK).build())
                        .setTransparencyData(GenericParticleData.create(0.4f, 0).build())
                        .setScaleData(GenericParticleData.create(0.45f, 0).build())
                        .setLightData(LightParticleData.DEFAULT)
                        .randomSpin(0.1f)
                        .setLifetime(60)
                        .randomVelocity(0.005f)
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

            Direction direction = getBlockState().getValue(CampfireBlock.FACING);
            NonNullList<ItemStack> nonnulllist = getItems();
            for(int j = 0; j < nonnulllist.size(); ++j) {
                ItemStack itemstack = nonnulllist.get(j);
                if (itemstack != ItemStack.EMPTY && (cookingTime[j] - cookingProgress[j] > 0)) {
                    pos = new Vec3(0.5F, 0.44921875F, 0.5F);

                    Direction direction1 = Direction.from2DDataValue((j + direction.get2DDataValue()) % 4);
                    float f = -direction1.toYRot();

                    float distance = 0.53125F;
                    double yaw = Math.toRadians(f + 45f);
                    double pitch = 90;

                    double X = Math.sin(pitch) * Math.cos(yaw) * distance;
                    double Y = Math.cos(pitch);
                    double Z = Math.sin(pitch) * Math.sin(yaw) * distance;

                    pos = pos.add(X, Y, Z);

                    if (random.nextFloat() < 0.1) {
                        ParticleBuilder.create(FluffyFur.SMOKE_PARTICLE)
                                .setRenderType(RenderUtils.DELAYED_PARTICLE)
                                .setColorData(ColorParticleData.create(Color.BLACK).build())
                                .setTransparencyData(GenericParticleData.create(0.4f, 0).build())
                                .setScaleData(GenericParticleData.create(0.45f, 0.3f).build())
                                .setLightData(LightParticleData.DEFAULT)
                                .randomSpin(0.1f)
                                .setLifetime(50)
                                .randomVelocity(0.005f)
                                .addVelocity(0, 0.03f, 0)
                                .spawn(level, worldPosition.getX() + pos.x(), worldPosition.getY() + pos.y(), worldPosition.getZ() + pos.z());
                    }
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

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveAllItems(tag, this.items, true);
        tag.putIntArray("CookingTimes", this.cookingProgress);
        tag.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadAllItems(tag, this.items);
        if (tag.contains("CookingTimes", 11)) {
            int[] aint = tag.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (tag.contains("CookingTotalTimes", 11)) {
            int[] aint1 = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }
    }

    public static void cookTick(Level pLevel, BlockPos pPos, BlockState pState, SaltCampfireBlockEntity pBlockEntity) {
        boolean flag = false;

        for(int i = 0; i < pBlockEntity.items.size(); ++i) {
            ItemStack itemstack = pBlockEntity.items.get(i);
            if (!itemstack.isEmpty()) {
                flag = true;
                int j = pBlockEntity.cookingProgress[i]++;
                if (pBlockEntity.cookingProgress[i] >= pBlockEntity.cookingTime[i]) {
                    Container container = new SimpleContainer(itemstack);
                    ItemStack itemstack1 = pBlockEntity.quickCheck.getRecipeFor(container, pLevel).map((p_270054_) -> {
                        return p_270054_.assemble(container, pLevel.registryAccess());
                    }).orElse(itemstack);
                    if (itemstack1.isItemEnabled(pLevel.enabledFeatures())) {
                        Containers.dropItemStack(pLevel, (double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), itemstack1);
                        pBlockEntity.items.set(i, ItemStack.EMPTY);
                        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                        pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(pState));
                    }
                }
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
            BlockEntityUpdate.packet(pBlockEntity);
        }

    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack pStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.quickCheck.getRecipeFor(new SimpleContainer(pStack), this.level);
    }

    public boolean placeFood(@Nullable Entity pEntity, ItemStack pStack, int pCookTime) {
        for(int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.cookingTime[i] = (pCookTime / 2);
                this.cookingProgress[i] = 0;
                this.items.set(i, pStack.split(1));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(pEntity, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void clearContent() {
        this.items.clear();
    }

    public static CompoundTag saveAllItems(CompoundTag pTag, NonNullList<ItemStack> pList, boolean pSaveEmpty) {
        ListTag listtag = new ListTag();

        for(int i = 0; i < pList.size(); ++i) {
            ItemStack itemstack = pList.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }

        if (!listtag.isEmpty() || pSaveEmpty) {
            pTag.put("ItemsCook", listtag);
        }

        return pTag;
    }

    public static void loadAllItems(CompoundTag pTag, NonNullList<ItemStack> pList) {
        ListTag listtag = pTag.getList("ItemsCook", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < pList.size()) {
                pList.set(j, ItemStack.of(compoundtag));
            }
        }

    }
}
