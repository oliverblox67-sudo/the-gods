package com.thegods.registry;

import com.thegods.TheGods;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheGods.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thegods"))
            .icon(() -> new ItemStack(ModItems.HEART_OF_THE_TIDES.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.HEART_OF_THE_TIDES.get());
                output.accept(ModItems.TRIDENT_OF_THE_DEEP.get());
                output.accept(ModItems.ALTAR_OF_THE_TIDES.get());
            })
            .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
