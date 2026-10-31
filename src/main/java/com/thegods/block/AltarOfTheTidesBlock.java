package com.thegods.block;

import com.thegods.capability.DivineData;
import com.thegods.network.ModNetwork;
import com.thegods.registry.ModItems;
import com.thegods.util.ModAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AltarOfTheTidesBlock extends Block {
    public AltarOfTheTidesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        DivineData data = DivineData.get(serverPlayer);
        if (data == null) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!data.isUnlocked() && held.is(ModItems.HEART_OF_THE_TIDES.get())) {
            data.unlock(serverPlayer);
            if (!serverPlayer.isCreative()) {
                held.shrink(1);
            }
            ModAdvancements.grantPoseidon(serverPlayer);
            ModNetwork.sync(serverPlayer);
            serverPlayer.displayClientMessage(Component.translatable("message.thegods.ritual_complete"), false);

            level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 2.0F, 0.7F);
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 80; i++) {
                    double angle = (Math.PI * 2.0D) * i / 80.0D;
                    double radius = 1.5D + level.random.nextDouble() * 1.2D;
                    serverLevel.sendParticles(ParticleTypes.SPLASH,
                            pos.getX() + 0.5D + Math.cos(angle) * radius,
                            pos.getY() + 0.6D + level.random.nextDouble() * 1.5D,
                            pos.getZ() + 0.5D + Math.sin(angle) * radius,
                            1, 0.0D, 0.15D, 0.0D, 0.05D);
                }
            }
            return InteractionResult.CONSUME;
        }

        serverPlayer.displayClientMessage(Component.translatable(
                data.isUnlocked() ? "message.thegods.altar_already" : "message.thegods.altar_hint"), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
            double y = pos.getY() + 1.05D + random.nextDouble() * 0.25D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.9D;
            level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0D, 0.02D, 0.0D);
        }
    }
}
