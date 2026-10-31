package com.thegods.client;

import com.thegods.ability.Ability;
import com.thegods.network.SyncDivinePacket;

public final class ClientDivineData {
    public static float energy = 0.0F;
    public static float maxEnergy = 100.0F;
    public static boolean godForm;
    public static boolean unlocked;
    public static int[] cooldowns = new int[Ability.values().length];

    private ClientDivineData() {
    }

    public static void set(SyncDivinePacket packet) {
        energy = packet.energy();
        maxEnergy = packet.maxEnergy();
        godForm = packet.godForm();
        unlocked = packet.unlocked();
        cooldowns = packet.cooldowns().clone();
    }
}
