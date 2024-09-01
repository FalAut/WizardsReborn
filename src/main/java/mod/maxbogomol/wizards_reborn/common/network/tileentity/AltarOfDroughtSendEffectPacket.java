package mod.maxbogomol.wizards_reborn.common.network.tileentity;

import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.fluffy_fur.registry.client.FluffyFurParticles;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class AltarOfDroughtSendEffectPacket {
    private final BlockPos pos;
    private static final Random random = new Random();

    public AltarOfDroughtSendEffectPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static AltarOfDroughtSendEffectPacket decode(FriendlyByteBuf buf) {
        return new AltarOfDroughtSendEffectPacket(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static void handle(AltarOfDroughtSendEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    Level world = WizardsReborn.proxy.getLevel();
                    ParticleBuilder.create(FluffyFurParticles.WISP)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB()).build())
                            .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                            .setScaleData(GenericParticleData.create(0.05f, 0).build())
                            .setLifetime(20)
                            .addVelocity(((random.nextDouble() - 0.5D) / 100), -(random.nextDouble() / 40), ((random.nextDouble() - 0.5D) / 100))
                            .spawn(world, msg.pos.getX() + 0.5F, msg.pos.getY() + 0.625F, msg.pos.getZ() + 0.5F);
                    ParticleBuilder.create(FluffyFurParticles.WISP)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB()).build())
                            .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                            .setScaleData(GenericParticleData.create(0.05f, 0).build())
                            .randomSpin(0.2f)
                            .setLifetime(30)
                            .addVelocity(((random.nextDouble() - 0.5D) / 100), -(random.nextDouble() / 40), ((random.nextDouble() - 0.5D) / 100))
                            .spawn(world, msg.pos.getX() + 0.5F, msg.pos.getY() + 0.625F, msg.pos.getZ() + 0.5F);
                    ctx.get().setPacketHandled(true);
                }
            });
        }
    }
}
