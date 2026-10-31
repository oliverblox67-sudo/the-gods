package com.thegods;

import com.mojang.logging.LogUtils;
import com.thegods.network.ModNetwork;
import com.thegods.registry.ModBlocks;
import com.thegods.registry.ModCapabilities;
import com.thegods.registry.ModCreativeTabs;
import com.thegods.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TheGods.MOD_ID)
public final class TheGods {
    public static final String MOD_ID = "thegods";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TheGods(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModCreativeTabs.register(modBus);
        ModCapabilities.init(modBus);

        modBus.addListener(this::commonSetup);

        LOGGER.info("The Gods: the pantheon is waking up.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
