package mod.maxbogomol.wizards_reborn.common.network.tileentity;

import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.fluffy_fur.common.easing.Easing;
import mod.maxbogomol.fluffy_fur.registry.client.FluffyFurParticles;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import mod.maxbogomol.wizards_reborn.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class JewelerTableBurstEffectPacket {
    private final BlockPos pos;
    private final float X, Y, Z;
    private final float velX, velY;
    private final float colorR, colorG, colorB;
    private final boolean isParticle;

    private static final Random random = new Random();

    public JewelerTableBurstEffectPacket(BlockPos pos, float X, float Y, float Z, float velX, float velY, float colorR, float colorG, float colorB, boolean isParticle) {
        this.pos = pos;

        this.X = X;
        this.Y = Y;
        this.Z = Z;

        this.velX = velX;
        this.velY = velY;

        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;

        this.isParticle = isParticle;
    }

    public JewelerTableBurstEffectPacket(BlockPos pos, float X, float Y, float Z, float velX, float velY, float colorR, float colorG, float colorB) {
        this.pos = pos;

        this.X = X;
        this.Y = Y;
        this.Z = Z;

        this.velX = velX;
        this.velY = velY;

        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;

        this.isParticle = true;
    }

    public JewelerTableBurstEffectPacket(BlockPos pos, float X, float Y, float Z) {
        this.pos = pos;

        this.X = X;
        this.Y = Y;
        this.Z = Z;

        this.velX = 0;
        this.velY = 0;

        this.colorR = 0;
        this.colorG = 0;
        this.colorB = 0;

        this.isParticle = false;
    }

    public static JewelerTableBurstEffectPacket decode(FriendlyByteBuf buf) {
        return new JewelerTableBurstEffectPacket(buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);

        buf.writeFloat(X);
        buf.writeFloat(Y);
        buf.writeFloat(Z);

        buf.writeFloat(velX);
        buf.writeFloat(velY);

        buf.writeFloat(colorR);
        buf.writeFloat(colorG);
        buf.writeFloat(colorB);
        buf.writeBoolean(isParticle);
    }

    public static void handle(JewelerTableBurstEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    Level world = WizardsReborn.proxy.getLevel();
                    ParticleBuilder.create(FluffyFurParticles.WISP)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB()).build())
                            .setTransparencyData(GenericParticleData.create(0.125f, 0).build())
                            .setScaleData(GenericParticleData.create(0.2f, 0).build())
                            .setLifetime(20)
                            .randomVelocity(0.05f)
                            .repeat(world, msg.pos.getX() + msg.X, msg.pos.getY() + msg.Y + 0.1875F, msg.pos.getZ() + msg.Z, 20);
                    ParticleBuilder.create(FluffyFurParticles.SPARKLE)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB()).build())
                            .setTransparencyData(GenericParticleData.create(0.25f, 0).build())
                            .setScaleData(GenericParticleData.create(0.1f, 0).build())
                            .randomSpin(0.5f)
                            .setLifetime(30)
                            .randomVelocity(0.05f)
                            .repeat(world, msg.pos.getX() + msg.X, msg.pos.getY() + msg.Y + 0.1875F, msg.pos.getZ() + msg.Z, 10);
                    ParticleBuilder.create(FluffyFurParticles.SQUARE)
                            .setColorData(ColorParticleData.create(Config.wissenColorR(), Config.wissenColorG(), Config.wissenColorB()).build())
                            .setTransparencyData(GenericParticleData.create(0.25f, 0).build())
                            .setScaleData(GenericParticleData.create(0, 0.1f, 0).setEasing(Easing.QUINTIC_IN_OUT).build())
                            .randomSpin(0.5f)
                            .setLifetime(30)
                            .randomVelocity(0.05f)
                            .repeat(world, msg.pos.getX() + msg.X, msg.pos.getY() + msg.Y + 0.1875F, msg.pos.getZ() + msg.Z, 10);

                    if (msg.isParticle) {
                        for (int i = 0; i < 25; i++) {
                            if (random.nextFloat() < 0.6) {
                                float x = 0F;
                                float y = 0F;

                                if (msg.velX == 0) {
                                    x = (float) ((random.nextDouble() - 0.5D) / 20);
                                } else {
                                    x = (float) ((random.nextDouble() / 20) * msg.velX);
                                }

                                if (msg.velY == 0) {
                                    y = (float) ((random.nextDouble() - 0.5D) / 20);
                                } else {
                                    y = (float) ((random.nextDouble() / 20) * msg.velY);
                                }

                                ParticleBuilder.create(FluffyFurParticles.SPARKLE)
                                        .setColorData(ColorParticleData.create(msg.colorR, msg.colorG, msg.colorB).build())
                                        .setTransparencyData(GenericParticleData.create(0.35f, 0).build())
                                        .setScaleData(GenericParticleData.create(0.2f, 0).build())
                                        .randomSpin(0.5f)
                                        .setLifetime(30)
                                        .addVelocity(x, (random.nextDouble() / 30), y)
                                        .spawn(world, msg.pos.getX() + msg.X, msg.pos.getY() + msg.Y - 0.125F, msg.pos.getZ() + msg.Z);
                            }
                        }
                    }
                    ctx.get().setPacketHandled(true);
                }
            });
        }
    }
}