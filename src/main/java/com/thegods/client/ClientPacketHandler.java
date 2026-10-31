package com.thegods.client;

import com.thegods.network.SyncDivinePacket;

public final class ClientPacketHandler {
    private ClientPacketHandler() {
    }

    public static void handleSync(SyncDivinePacket packet) {
        ClientDivineData.set(packet);
    }
}
