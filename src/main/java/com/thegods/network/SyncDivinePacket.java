package com.thegods.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncDivinePacket(float energy, boolean godForm, boolean unlocked, int[] cooldowns, float maxEnergy) {
    public static void encode(SyncDivinePacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat(packet.energy);
        buffer.writeBoolean(packet.godForm);
        buffer.writeBoolean(packet.unlocked);
        buffer.writeVarInt(packet.cooldowns.length);
        for (int cooldown : packet.cooldowns) {
            buffer.writeVarInt(cooldown);
        }
        buffer.writeFloat(packet.maxEnergy);
    }

    public static SyncDivinePacket decode(FriendlyByteBuf buffer) {
        float energy = buffer.readFloat();
        boolean godForm = buffer.readBoolean();
        boolean unlocked = buffer.readBoolean();
        int length = buffer.readVarInt();
        int[] cooldowns = new int[length];
        for (int i = 0; i < length; i++) {
            cooldowns[i] = buffer.readVarInt();
        }
        return new SyncDivinePacket(energy, godForm, unlocked, cooldowns, buffer.readFloat());
    }

    public static void handle(SyncDivinePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.thegods.client.ClientPacketHandler.handleSync(packet)));
        context.setPacketHandled(true);
    }
}
