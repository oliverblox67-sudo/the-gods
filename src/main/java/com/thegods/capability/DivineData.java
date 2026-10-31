package com.thegods.capability;

import com.thegods.TheGods;
import com.thegods.ability.Ability;
import com.thegods.ability.PoseidonAbilities;
import com.thegods.network.ModNetwork;
import com.thegods.registry.ModCapabilities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public class DivineData implements INBTSerializable<CompoundTag> {
    public static final float MAX_ENERGY = 100.0F;

    private float energy = MAX_ENERGY;
    private boolean godForm;
    private boolean unlocked;
    private final int[] cooldowns = new int[Ability.values().length];

    public static DivineData get(Player player) {
        return player.getCapability(ModCapabilities.DIVINE).orElse(null);
    }

    public void tick(Player player) {
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
            }
        }

        if (!unlocked) {
            energy = 0.0F;
            godForm = false;
            return;
        }

        if (godForm) {
            energy -= 0.03F;
            if (energy <= 0.0F) {
                energy = 0.0F;
                godForm = false;
                if (!player.level().isClientSide) {
                    player.displayClientMessage(Component.translatable("message.thegods.energy_depleted"), true);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.CONDUIT_DEACTIVATE,
                            SoundSource.PLAYERS, 1.0F, 0.7F);
                }
            }

            if (!player.level().isClientSide && player.tickCount % 20 == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 120, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 120, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 120, 0, false, false, true));
            }

            if (!player.level().isClientSide && player.isInWaterOrBubble() && player.tickCount % 100 == 0
                    && player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
        }

        float regen = player.isInWaterOrBubble() ? 0.20F : 0.05F;
        energy = Math.min(MAX_ENERGY, energy + regen);
    }

    public boolean cast(Ability ability, ServerPlayer player) {
        if (!unlocked) {
            player.displayClientMessage(Component.translatable("message.thegods.not_unlocked"), true);
            return false;
        }
        if (!godForm) {
            player.displayClientMessage(Component.translatable("message.thegods.form_required"), true);
            return false;
        }

        int cooldown = cooldowns[ability.ordinal()];
        if (cooldown > 0) {
            player.displayClientMessage(Component.translatable("message.thegods.cooldown",
                    Component.translatable(ability.getTranslationKey()),
                    Math.max(1, (cooldown + 19) / 20)), true);
            return false;
        }

        if (energy < ability.getCost()) {
            player.displayClientMessage(Component.translatable("message.thegods.no_energy"), true);
            return false;
        }

        energy -= ability.getCost();
        cooldowns[ability.ordinal()] = ability.getCooldownTicks();
        PoseidonAbilities.cast(ability, player);
        ModNetwork.sync(player);
        return true;
    }

    public void unlock(ServerPlayer player) {
        if (unlocked) {
            return;
        }
        unlocked = true;
        godForm = true;
        energy = MAX_ENERGY;
        player.displayClientMessage(Component.translatable("message.thegods.form_on"), false);
        player.level().playSound(null, player.blockPosition(), SoundEvents.CONDUIT_ACTIVATE,
                SoundSource.PLAYERS, 1.5F, 0.7F);
        spawnRing(player, ParticleTypes.SPLASH, 2.0D, 80);
        spawnRing(player, ParticleTypes.BUBBLE, 1.4D, 60);
    }

    public void toggleGodForm(ServerPlayer player) {
        if (!unlocked) {
            player.displayClientMessage(Component.translatable("message.thegods.not_unlocked"), true);
            return;
        }

        godForm = !godForm;
        if (godForm) {
            player.displayClientMessage(Component.translatable("message.thegods.form_on"), true);
            player.level().playSound(null, player.blockPosition(), SoundEvents.CONDUIT_ACTIVATE,
                    SoundSource.PLAYERS, 1.2F, 0.8F);
            spawnRing(player, ParticleTypes.SPLASH, 1.8D, 60);
        } else {
            player.displayClientMessage(Component.translatable("message.thegods.form_off"), true);
            player.level().playSound(null, player.blockPosition(), SoundEvents.CONDUIT_DEACTIVATE,
                    SoundSource.PLAYERS, 1.0F, 0.8F);
            spawnRing(player, ParticleTypes.CLOUD, 1.2D, 30);
        }
        ModNetwork.sync(player);
    }

    private static void spawnRing(ServerPlayer player, net.minecraft.core.particles.SimpleParticleType particle,
                                  double radius, int count) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2.0D) * i / count;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            level.sendParticles(particle, x, player.getY() + 0.2D, z, 1, 0.0D, 0.25D, 0.0D, 0.05D);
        }
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = Math.max(0.0F, Math.min(MAX_ENERGY, energy));
    }

    public boolean isGodForm() {
        return godForm;
    }

    public void setGodForm(boolean godForm) {
        this.godForm = godForm;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public int[] getCooldowns() {
        return cooldowns.clone();
    }

    public void copyFrom(DivineData other) {
        energy = other.energy;
        godForm = other.godForm;
        unlocked = other.unlocked;
        System.arraycopy(other.cooldowns, 0, cooldowns, 0, cooldowns.length);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Energy", energy);
        tag.putBoolean("GodForm", godForm);
        tag.putBoolean("Unlocked", unlocked);
        tag.putIntArray("Cooldowns", cooldowns);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        energy = tag.getFloat("Energy");
        godForm = tag.getBoolean("GodForm");
        unlocked = tag.getBoolean("Unlocked");
        int[] saved = tag.getIntArray("Cooldowns");
        for (int i = 0; i < cooldowns.length; i++) {
            cooldowns[i] = i < saved.length ? saved[i] : 0;
        }
        if (tag.contains("Energy") && energy <= 0.0F && !unlocked) {
            energy = 0.0F;
        }
        TheGods.LOGGER.debug("Loaded divine data: unlocked={}, form={}, energy={}", unlocked, godForm, energy);
    }
}
