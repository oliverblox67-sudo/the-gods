package com.thegods.network;

import com.thegods.TheGods;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(TheGods.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(AbilityPacket.class, id++, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                .encoder(AbilityPacket::encode)
                .decoder(AbilityPacket::decode)
                .consumerMainThread(AbilityPacket::handle)
                .add();

        CHANNEL.messageBuilder(ToggleFormPacket.class, id++, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                .encoder(ToggleFormPacket::encode)
                .decoder(ToggleFormPacket::decode)
                .consumerMainThread(ToggleFormPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncDivinePacket.class, id++, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncDivinePacket::encode)
                .decoder(SyncDivinePacket::decode)
                .consumerMainThread(SyncDivinePacket::handle)
                .add();
    }

    public static void sendAbility(int abilityId) {
        CHANNEL.sendToServer(new AbilityPacket(abilityId));
    }

    public static void sendToggleForm() {
        CHANNEL.sendToServer(new ToggleFormPacket());
    }

    public static void sync(net.minecraft.server.level.ServerPlayer player) {
        com.thegods.capability.DivineData data = com.thegods.capability.DivineData.get(player);
        if (data == null) {
            return;
        }
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncDivinePacket(data.getEnergy(), data.isGodForm(), data.isUnlocked(),
                        data.getCooldowns(), com.thegods.capability.DivineData.MAX_ENERGY));
    }
}
