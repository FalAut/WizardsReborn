package mod.maxbogomol.wizards_reborn.common.network;

import com.mojang.datafixers.util.Pair;
import mod.maxbogomol.wizards_reborn.WizardsReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {
    private static final String PROTOCOL = "10";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WizardsReborn.MOD_ID,"network"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void init() {
        int id = 0;

        HANDLER.registerMessage(id++, SetCrystalPacket.class, SetCrystalPacket::encode, SetCrystalPacket::decode, SetCrystalPacket::handle);
        HANDLER.registerMessage(id++, DeleteCrystalPacket.class, DeleteCrystalPacket::encode, DeleteCrystalPacket::decode, DeleteCrystalPacket::handle);
        HANDLER.registerMessage(id++, SetSpellPacket.class, SetSpellPacket::encode, SetSpellPacket::decode, SetSpellPacket::handle);

        HANDLER.registerMessage(id++, WissenAltarBurstEffectPacket.class, WissenAltarBurstEffectPacket::encode, WissenAltarBurstEffectPacket::decode, WissenAltarBurstEffectPacket::handle);
        HANDLER.registerMessage(id++, WissenCrystallizerBurstEffectPacket.class, WissenCrystallizerBurstEffectPacket::encode, WissenCrystallizerBurstEffectPacket::decode, WissenCrystallizerBurstEffectPacket::handle);
        HANDLER.registerMessage(id++, ArcaneWorkbenchBurstEffectPacket.class, ArcaneWorkbenchBurstEffectPacket::encode, ArcaneWorkbenchBurstEffectPacket::decode, ArcaneWorkbenchBurstEffectPacket::handle);
        HANDLER.registerMessage(id++, WissenAltarSendEffectPacket.class, WissenAltarSendEffectPacket::encode, WissenAltarSendEffectPacket::decode, WissenAltarSendEffectPacket::handle);

        HANDLER.registerMessage(id++, WissenTranslatorBurstEffectPacket.class, WissenTranslatorBurstEffectPacket::encode, WissenTranslatorBurstEffectPacket::decode, WissenTranslatorBurstEffectPacket::handle);
        HANDLER.registerMessage(id++, WissenTranslatorSendEffectPacket.class, WissenTranslatorSendEffectPacket::encode, WissenTranslatorSendEffectPacket::decode, WissenTranslatorSendEffectPacket::handle);
        HANDLER.registerMessage(id++, WissenSendEffectPacket.class, WissenSendEffectPacket::encode, WissenSendEffectPacket::decode, WissenSendEffectPacket::handle);

        HANDLER.registerMessage(id++, SpellBurstEffectPacket.class, SpellBurstEffectPacket::encode, SpellBurstEffectPacket::decode, SpellBurstEffectPacket::handle);
        HANDLER.registerMessage(id++, SpellProjectileRayEffectPacket.class, SpellProjectileRayEffectPacket::encode, SpellProjectileRayEffectPacket::decode, SpellProjectileRayEffectPacket::handle);

        HANDLER.registerMessage(id++, WissenDustBurstEffectPacket.class, WissenDustBurstEffectPacket::encode, WissenDustBurstEffectPacket::decode, WissenDustBurstEffectPacket::handle);

        HANDLER.registerMessage(id++, KnowledgeUpdatePacket.class, KnowledgeUpdatePacket::encode, KnowledgeUpdatePacket::decode, KnowledgeUpdatePacket::handle);

        HANDLER.registerMessage(id++, TESyncPacket.class, TESyncPacket::encode, TESyncPacket::decode, TESyncPacket::handle);
    }

    private static final PacketDistributor<Pair<Level, BlockPos>> TRACKING_CHUNK_AND_NEAR = new PacketDistributor<>(
            (_d, pairSupplier) -> {
                var pair = pairSupplier.get();
                var level = pair.getFirst();
                var blockpos = pair.getSecond();
                var chunkpos = new ChunkPos(blockpos);
                return packet -> {
                    var players = ((ServerChunkCache) level.getChunkSource()).chunkMap
                            .getPlayers(chunkpos, false);
                    for (var player : players) {
                        if (player.distanceToSqr(blockpos.getX(), blockpos.getY(), blockpos.getZ()) < 64 * 64) {
                            player.connection.send(packet);
                        }
                    }
                };
            },
            NetworkDirection.PLAY_TO_CLIENT
    );

    //public static void sendToNearby(Level world, BlockPos pos, Object toSend) {
    //    sendToNearby(world, pos, toSend);
    //}

    //public static void sendToNearby(Level world, Entity e, Object toSend) {
    //    sendToNearby(world, e.blockPosition(), toSend);
    //}

    public static void sendTo(ServerPlayer playerMP, Object toSend) {
        HANDLER.sendTo(toSend, playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendNonLocal(ServerPlayer playerMP, Object toSend) {
        if (playerMP.server.isDedicatedServer() || !playerMP.getGameProfile().getName().equals(playerMP.server.getLocalIp())) {
            sendTo(playerMP, toSend);
        }
    }

    public static void sendToTracking(Level world, BlockPos pos, Object msg) {
        //HANDLER.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), msg);
        HANDLER.send(TRACKING_CHUNK_AND_NEAR.with(() -> Pair.of(world, pos)), msg);
    }

    public static void sendTo(Player entity, Object msg) {
        HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)entity), msg);
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    private PacketHandler() {}
}