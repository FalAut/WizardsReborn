package mod.maxbogomol.wizards_reborn.common.network.spell;

import mod.maxbogomol.fluffy_fur.FluffyFur;
import mod.maxbogomol.fluffy_fur.client.particle.ParticleBuilder;
import mod.maxbogomol.fluffy_fur.client.particle.data.ColorParticleData;
import mod.maxbogomol.fluffy_fur.client.particle.data.GenericParticleData;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class AuraSpellCastEffectPacket {
    private final float X, Y, Z;
    private final float colorR, colorG, colorB;

    private static final Random random = new Random();

    public AuraSpellCastEffectPacket(float X, float Y, float Z, float colorR, float colorG, float colorB) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;

        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
    }

    public static AuraSpellCastEffectPacket decode(FriendlyByteBuf buf) {
        return new AuraSpellCastEffectPacket(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(X);
        buf.writeFloat(Y);
        buf.writeFloat(Z);

        buf.writeFloat(colorR);
        buf.writeFloat(colorG);
        buf.writeFloat(colorB);
    }

    public static void handle(AuraSpellCastEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    Level world = WizardsReborn.proxy.getLevel();
                    ParticleBuilder.create(FluffyFur.SPARKLE_PARTICLE)
                            .setColorData(ColorParticleData.create(msg.colorR, msg.colorG, msg.colorB).build())
                            .setTransparencyData(GenericParticleData.create(0.4f, 0).build())
                            .setScaleData(GenericParticleData.create(0.2f, 0).build())
                            .setLifetime(100)
                            .setGravity(1f)
                            .randomVelocity(0.125f)
                            .addVelocity(msg.X, msg.Y, msg.Z)
                            .repeat(world, msg.X, msg.Y, msg.Z, 30, 0.8f);

                    ctx.get().setPacketHandled(true);
                }
            });
        }
    }
}