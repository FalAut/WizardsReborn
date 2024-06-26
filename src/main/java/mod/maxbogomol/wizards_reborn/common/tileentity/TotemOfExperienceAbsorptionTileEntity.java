package mod.maxbogomol.wizards_reborn.common.tileentity;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.api.wissen.IExperienceTileEntity;
import mod.maxbogomol.wizards_reborn.api.wissen.IWissenTileEntity;
import mod.maxbogomol.wizards_reborn.api.wissen.WissenUtils;
import mod.maxbogomol.wizards_reborn.client.particle.ExperienceTotemBurst;
import mod.maxbogomol.wizards_reborn.client.particle.Particles;
import mod.maxbogomol.wizards_reborn.client.sound.TotemOfExperienceAbsorptionSoundInstance;
import mod.maxbogomol.wizards_reborn.common.config.Config;
import mod.maxbogomol.wizards_reborn.common.network.PacketHandler;
import mod.maxbogomol.wizards_reborn.common.network.tileentity.ExperienceTotemBurstEffectPacket;
import mod.maxbogomol.wizards_reborn.utils.PacketUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TotemOfExperienceAbsorptionTileEntity extends BlockEntity implements TickableBlockEntity, IWissenTileEntity, IExperienceTileEntity {

    public int wissen = 0;
    public int experience = 0;
    public int cooldown = 0;
    public int tick = 0;

    public Random random = new Random();

    public List<ExperienceTotemBurst> bursts = new ArrayList<>();

    public TotemOfExperienceAbsorptionSoundInstance sound;

    public TotemOfExperienceAbsorptionTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TotemOfExperienceAbsorptionTileEntity(BlockPos pos, BlockState state) {
        this(WizardsReborn.TOTEM_OF_EXPERIENCE_ABSORPTION_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            boolean update = false;

            if (getExperience() > 0 && getWissen() < getMaxWissen()) {
                removeExperience(1);
                addWissen(8);
                if (random.nextFloat() < 0.01) {
                    level.playSound(WizardsReborn.proxy.getPlayer(), getBlockPos(), WizardsReborn.SPELL_BURST_SOUND.get(), SoundSource.BLOCKS, 0.25f, (float) (0.5f + ((random.nextFloat() - 0.5D) / 4)));
                }
                update = true;
            }

            if (getExperience() < getMaxExperience() && cooldown <= 0) {
                List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(getBlockPos().getX() - 1, getBlockPos().getY() - 1, getBlockPos().getZ() - 1, getBlockPos().getX() + 2, getBlockPos().getY() + 2, getBlockPos().getZ() + 2));
                for (ExperienceOrb orb : orbs) {
                    int remain = WissenUtils.getRemoveWissenRemain(orb.value, 100);
                    remain = 100 - remain;
                    int remainAdd = WissenUtils.getAddWissenRemain(getExperience(), remain, getMaxExperience());
                    remainAdd = remain - remainAdd;
                    if (remainAdd > 0 && remain > 0) {
                        addExperience(remainAdd);
                        orb.value = orb.value - remainAdd;
                        if (orb.value <= 0) {
                            orb.kill();
                        }
                        level.playSound(null, orb.getOnPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1.2f);
                        PacketHandler.sendToTracking(level, getBlockPos(), new ExperienceTotemBurstEffectPacket(getBlockPos(), orb.getPosition(0)));
                        update = true;
                    }
                }
                cooldown = 20;
            }

            if (cooldown > 0) {
                cooldown--;
            }

            if (update) {
                PacketUtils.SUpdateTileEntityPacket(this);
            }
        }

        if (level.isClientSide()) {
            List<ExperienceTotemBurst> newBursts = new ArrayList<>();
            newBursts.addAll(bursts);
            for (ExperienceTotemBurst burst : newBursts) {
                burst.tick();
                if (burst.end) {
                    bursts.remove(burst);
                }
            }

            if (getWissen() > 0) {
                if (random.nextFloat() < 0.3) {
                    Particles.create(WizardsReborn.WISP_PARTICLE)
                            .addVelocity(((random.nextDouble() - 0.5D) / 30) * getStage(), ((random.nextDouble() - 0.5D) / 30) * getStage(), ((random.nextDouble() - 0.5D) / 30) * getStage())
                            .setAlpha(0.25f, 0).setScale(0.2f * getStage(), 0)
                            .setColor(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB())
                            .setLifetime(20)
                            .spawn(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F);
                }
                if (random.nextFloat() < 0.1) {
                    Particles.create(WizardsReborn.SPARKLE_PARTICLE)
                            .addVelocity(((random.nextDouble() - 0.5D) / 30) * getStage(), ((random.nextDouble() - 0.5D) / 30) * getStage(), ((random.nextDouble() - 0.5D) / 30) * getStage())
                            .setAlpha(0.25f, 0).setScale(0.075f * getStage(), 0)
                            .setColor(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB())
                            .setLifetime(30)
                            .setSpin((0.5f * (float) ((random.nextDouble() - 0.5D) * 2)))
                            .spawn(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F);
                }
            }

            if (getExperience() > 0) {
                if (random.nextFloat() < 0.1) {
                    Particles.create(WizardsReborn.SPARKLE_PARTICLE)
                            .addVelocity(((random.nextDouble() - 0.5D) / 6), (((random.nextDouble() - 0.5D) / 10)) + 0.2f, ((random.nextDouble() - 0.5D) / 6))
                            .setAlpha(0.25f, 0).setScale(0.05f, 0)
                            .setColor(0.784f, 1f, 0.560f)
                            .setLifetime(40)
                            .setSpin((0.1f * (float) ((random.nextDouble() - 0.5D) * 2)))
                            .enableGravity()
                            .spawn(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F);
                }
            }

            if (getExperience() > 0 && getWissen() < getMaxWissen()) {
                if (random.nextFloat() < 0.5) {
                    Particles.create(WizardsReborn.WISP_PARTICLE)
                            .addVelocity(((random.nextDouble() - 0.5D) / 30) * getStage(), (((random.nextDouble() - 0.5D) / 30) + 0.05f) * getStage(), ((random.nextDouble() - 0.5D) / 30) * getStage())
                            .setAlpha(0.25f, 0).setScale(0.2f * getStage(), 0)
                            .setColor(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB(), 0.784f, 1f, 0.560f)
                            .setLifetime(60)
                            .spawn(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F);
                }

                if (sound == null) {
                    sound = TotemOfExperienceAbsorptionSoundInstance.getSound(this);
                    sound.playSound();
                } else if (sound.isStopped()) {
                    sound = TotemOfExperienceAbsorptionSoundInstance.getSound(this);
                    sound.playSound();
                }

                if (tick < 20) {
                    tick++;
                }
            } else {
                if (tick > 0) {
                    tick--;
                    if (tick <= 0) {
                        getLevel().playSound(WizardsReborn.proxy.getPlayer(), getBlockPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 0.2f);
                    }
                }
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (e) -> e.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getTag());
    }

    @NotNull
    @Override
    public final CompoundTag getUpdateTag() {
        var tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            PacketUtils.SUpdateTileEntityPacket(this);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("wissen", wissen);
        tag.putInt("experience", experience);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        wissen = tag.getInt("wissen");
        experience = tag.getInt("experience");
    }

    @Override
    public AABB getRenderBoundingBox() {
        return IForgeBlockEntity.INFINITE_EXTENT_AABB;
    }

    public float getStage() {
        return ((float) getWissen() / (float) getMaxWissen());
    }

    @Override
    public int getWissen() {
        return wissen;
    }

    @Override
    public int getMaxWissen() {
        return 5000;
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

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public int getMaxExperience() {
        return 500;
    }

    @Override
    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public void addExperience(int experience) {
        this.experience = this.experience + experience;
        if (this.experience > getMaxExperience()) {
            this.experience = getMaxExperience();
        }
    }

    @Override
    public void removeExperience(int experience) {
        this.experience = this.experience - experience;
        if (this.experience < 0) {
            this.experience = 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addBurst(Vec3 startPos, Vec3 endPos) {
        bursts.add(new ExperienceTotemBurst(level, (float) startPos.x(), (float) startPos.y(), (float) startPos.z(),
                (float) endPos.x(), (float) endPos.y(), (float) endPos.z(), 0.1f, 5, 200,
                0.784f, 1f, 0.560f));
    }
}
