package mod.maxbogomol.wizards_reborn.client.sound;

import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.common.block.light_emitter.LightEmitterBlockEntity;
import net.minecraft.client.Minecraft;

public class LightEmitterSoundInstance extends TileEntitySoundInstance<LightEmitterBlockEntity> {
    public LightEmitterSoundInstance(LightEmitterBlockEntity blockEntity, float volume, float pitch) {
        super(blockEntity, WizardsReborn.ARCANUM_LENS_RESONATE_SOUND.get(), volume, pitch);
        this.x = blockEntity.getBlockPos().getX() + 0.5f;
        this.y = blockEntity.getBlockPos().getY() + 0.5f;
        this.z = blockEntity.getBlockPos().getZ() + 0.5f;
    }

    @Override
    public void tick() {
        if (blockEntity.getLight() <= 0) {
            stop();
        }
        super.tick();
    }

    public static LightEmitterSoundInstance getSound(LightEmitterBlockEntity tileEntity) {
        return new LightEmitterSoundInstance(tileEntity, 1, 1);
    }

    public void playSound() {
        Minecraft.getInstance().getSoundManager().queueTickingSound(this);
    }
}