package com.thegods.util;

import com.thegods.TheGods;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ModAdvancements {
    private ModAdvancements() {
    }

    public static void grantPoseidon(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        Advancement advancement = server.getAdvancements()
                .getAdvancement(new ResourceLocation(TheGods.MOD_ID, "poseidon"));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "unlock");
        }
    }
}
