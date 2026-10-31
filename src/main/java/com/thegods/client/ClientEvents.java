package com.thegods.client;

import com.thegods.TheGods;
import com.thegods.ability.Ability;
import com.thegods.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheGods.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        while (KeyBindings.TIDAL_SURGE.consumeClick()) {
            ModNetwork.sendAbility(Ability.TIDAL_SURGE.ordinal());
        }
        while (KeyBindings.TRIDENT_STORM.consumeClick()) {
            ModNetwork.sendAbility(Ability.TRIDENT_STORM.ordinal());
        }
        while (KeyBindings.ABYSSAL_BARRIER.consumeClick()) {
            ModNetwork.sendAbility(Ability.ABYSSAL_BARRIER.ordinal());
        }
        while (KeyBindings.TSUNAMI.consumeClick()) {
            ModNetwork.sendAbility(Ability.TSUNAMI.ordinal());
        }
        while (KeyBindings.TOGGLE_FORM.consumeClick()) {
            ModNetwork.sendToggleForm();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        DivineHUD.render(event.getGuiGraphics());
    }
}
