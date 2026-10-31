package com.thegods.registry;

import com.thegods.TheGods;
import com.thegods.item.HeartOfTheTidesItem;
import com.thegods.item.TridentOfTheDeepItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TheGods.MOD_ID);

    public static final RegistryObject<Item> HEART_OF_THE_TIDES = ITEMS.register("heart_of_the_tides",
            () -> new HeartOfTheTidesItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()));

    public static final RegistryObject<Item> TRIDENT_OF_THE_DEEP = ITEMS.register("trident_of_the_deep",
            () -> new TridentOfTheDeepItem(new Item.Properties()
                    .durability(1500)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final RegistryObject<Item> ALTAR_OF_THE_TIDES = ITEMS.register("altar_of_the_tides",
            () -> new BlockItem(ModBlocks.ALTAR_OF_THE_TIDES.get(), new Item.Properties().rarity(Rarity.RARE)));

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
