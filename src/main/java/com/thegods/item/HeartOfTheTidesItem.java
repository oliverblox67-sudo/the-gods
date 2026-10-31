package com.thegods.item;

import com.thegods.capability.DivineData;
import com.thegods.network.ModNetwork;
import com.thegods.util.ModAdvancements;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeartOfTheTidesItem extends Item {
    public HeartOfTheTidesItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            DivineData data = DivineData.get(serverPlayer);
            if (data != null) {
                if (!data.isUnlocked()) {
                    data.unlock(serverPlayer);
                    if (!serverPlayer.isCreative()) {
                        stack.shrink(1);
                    }
                    ModAdvancements.grantPoseidon(serverPlayer);
                } else {
                    data.toggleGodForm(serverPlayer);
                }
                ModNetwork.sync(serverPlayer);
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.thegods.heart_of_the_tides").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.thegods.heart_of_the_tides.use").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
