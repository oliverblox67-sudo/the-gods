package com.thegods.network;

import com.thegods.capability.DivineData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ToggleFormPacket() {
    public static void encode(ToggleFormPacket packet, FriendlyByteBuf buffer) {
    }

    public static ToggleFormPacket decode(FriendlyByteBuf buffer) {
        return new ToggleFormPacket();
    }

    public static void handle(ToggleFormPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            DivineData data = DivineData.get(player);
            if (data != null) {
                data.toggleGodForm(player);
            }
        });
        context.setPacketHandled(true);
    }
}
