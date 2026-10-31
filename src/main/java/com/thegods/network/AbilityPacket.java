package com.thegods.network;

import com.thegods.ability.Ability;
import com.thegods.capability.DivineData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AbilityPacket(int abilityId) {
    public static void encode(AbilityPacket packet, FriendlyByteBuf buffer) {
        buffer.writeByte(packet.abilityId);
    }

    public static AbilityPacket decode(FriendlyByteBuf buffer) {
        return new AbilityPacket(buffer.readByte());
    }

    public static void handle(AbilityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            Ability ability = Ability.byId(packet.abilityId);
            DivineData data = DivineData.get(player);
            if (ability != null && data != null) {
                data.cast(ability, player);
            }
        });
        context.setPacketHandled(true);
    }
}
