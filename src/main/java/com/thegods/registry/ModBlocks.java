package com.thegods.registry;

import com.thegods.TheGods;
import com.thegods.block.AltarOfTheTidesBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TheGods.MOD_ID);

    public static final RegistryObject<Block> ALTAR_OF_THE_TIDES = BLOCKS.register("altar_of_the_tides",
            () -> new AltarOfTheTidesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 10)
                    .noOcclusion()));

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
