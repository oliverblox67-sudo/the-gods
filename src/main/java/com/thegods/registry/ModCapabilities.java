package com.thegods.registry;

import com.thegods.TheGods;
import com.thegods.capability.DivineData;
import com.thegods.capability.DivineProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModCapabilities {
    public static final Capability<DivineData> DIVINE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final ResourceLocation DIVINE_ID = new ResourceLocation(TheGods.MOD_ID, "divine");

    private ModCapabilities() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ModCapabilities::registerCapabilities);
        MinecraftForge.EVENT_BUS.register(ModCapabilities.class);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(DivineData.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player && !player.getCapability(DIVINE).isPresent()) {
            event.addCapability(DIVINE_ID, new DivineProvider());
        }
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(DIVINE).ifPresent(oldData ->
                event.getEntity().getCapability(DIVINE).ifPresent(newData -> newData.copyFrom(oldData)));
        event.getOriginal().invalidateCaps();
    }
}
