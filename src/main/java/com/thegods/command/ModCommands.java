package com.thegods.command;

import com.mojang.brigadier.Command;
import com.thegods.TheGods;
import com.thegods.capability.DivineData;
import com.thegods.network.ModNetwork;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TheGods.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModCommands {
    private ModCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("thegods")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("unlock").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    DivineData data = DivineData.get(player);
                    if (data != null) {
                        data.unlock(player);
                        ModNetwork.sync(player);
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("commands.thegods.unlocked"), false);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("form").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    DivineData data = DivineData.get(player);
                    if (data != null) {
                        data.toggleGodForm(player);
                        ModNetwork.sync(player);
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("commands.thegods.form"), false);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("energy")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D, 100.0D))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    float value = (float) DoubleArgumentType.getDouble(context, "value");
                                    DivineData data = DivineData.get(player);
                                    if (data != null) {
                                        data.setEnergy(value);
                                        ModNetwork.sync(player);
                                    }
                                    context.getSource().sendSuccess(() -> Component.translatable(
                                            "commands.thegods.energy", String.format("%.1f", value)), false);
                                    return Command.SINGLE_SUCCESS;
                                }))));
    }
}
