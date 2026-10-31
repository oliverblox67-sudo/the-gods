package com.thegods.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class PoseidonAbilities {
    private PoseidonAbilities() {
    }

    public static void cast(Ability ability, ServerPlayer player) {
        switch (ability) {
            case TIDAL_SURGE -> tidalSurge(player);
            case TRIDENT_STORM -> tridentStorm(player);
            case ABYSSAL_BARRIER -> abyssalBarrier(player);
            case TSUNAMI -> tsunami(player);
        }
    }

    private static void tidalSurge(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        double range = 16.0D;

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(range), entity -> entity != player && entity.isAlive())) {
            if (target instanceof Player other && other.isCreative()) {
                continue;
            }

            Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 toTarget = center.subtract(start);
            double along = toTarget.dot(look);
            if (along < 0.0D || along > range) {
                continue;
            }

            Vec3 closest = start.add(look.scale(along));
            if (closest.distanceToSqr(center) > 9.0D) {
                continue;
            }

            float damage = target.isInWaterOrBubble() ? 12.0F : 8.0F;
            target.hurt(player.damageSources().indirectMagic(player, player), damage);
            Vec3 push = look.scale(1.4D).add(0.0D, 0.45D, 0.0D);
            target.setDeltaMovement(target.getDeltaMovement().add(push));
            target.hurtMarked = true;
        }

        for (double distance = 0.0D; distance <= range; distance += 0.45D) {
            Vec3 point = start.add(look.scale(distance));
            level.sendParticles(ParticleTypes.SPLASH, point.x, point.y, point.z, 3, 0.18D, 0.18D, 0.18D, 0.08D);
            if (distance % 1.35D < 0.45D) {
                level.sendParticles(ParticleTypes.BUBBLE, point.x, point.y, point.z, 2, 0.12D, 0.12D, 0.12D, 0.02D);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_2,
                SoundSource.PLAYERS, 1.2F, 0.8F);
    }

    private static void tridentStorm(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        LivingEntity target = findTarget(player, 24.0D);
        Vec3 impact = target != null ? target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D)
                : eye.add(look.scale(20.0D));

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(impact.x, impact.y, impact.z);
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }

        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(impact, impact).inflate(4.0D),
                entity -> entity != player && entity.isAlive());
        for (LivingEntity victim : victims) {
            if (victim instanceof Player other && other.isCreative()) {
                continue;
            }
            if (victim.position().distanceToSqr(impact) > 16.0D) {
                continue;
            }
            float damage = victim == target ? 16.0F : 9.0F;
            victim.hurt(player.damageSources().indirectMagic(player, player), damage);
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            victim.setDeltaMovement(victim.getDeltaMovement().add(0.0D, 0.5D, 0.0D));
        }

        for (int i = 0; i < 50; i++) {
            double offsetX = (level.random.nextDouble() - 0.5D) * 2.5D;
            double offsetY = level.random.nextDouble() * 2.0D;
            double offsetZ = (level.random.nextDouble() - 0.5D) * 2.5D;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, impact.x + offsetX, impact.y + offsetY,
                    impact.z + offsetZ, 1, 0.0D, 0.1D, 0.0D, 0.05D);
        }
        level.sendParticles(ParticleTypes.SPLASH, impact.x, impact.y, impact.z, 40, 1.2D, 1.0D, 1.2D, 0.2D);
        level.playSound(null, impact.x, impact.y, impact.z, SoundEvents.TRIDENT_THUNDER,
                SoundSource.PLAYERS, 1.6F, 0.9F);
    }

    private static LivingEntity findTarget(ServerPlayer player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        LivingEntity best = null;
        double bestDot = 0.965D;

        for (LivingEntity candidate : player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(range), entity -> entity != player && entity.isAlive())) {
            if (candidate instanceof Player other && other.isCreative()) {
                continue;
            }
            Vec3 toTarget = candidate.getEyePosition().subtract(eye);
            double distance = toTarget.length();
            if (distance > range || distance < 0.1D) {
                continue;
            }
            double dot = toTarget.normalize().dot(look);
            if (dot > bestDot) {
                bestDot = dot;
                best = candidate;
            }
        }
        return best;
    }

    private static void abyssalBarrier(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 260, 2, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0, false, false, true));

        for (int ring = 0; ring < 3; ring++) {
            double radius = 0.9D + ring * 0.55D;
            for (int i = 0; i < 36; i++) {
                double angle = (Math.PI * 2.0D) * i / 36.0D;
                level.sendParticles(ParticleTypes.BUBBLE,
                        player.getX() + Math.cos(angle) * radius,
                        player.getY() + 0.4D + ring * 0.45D,
                        player.getZ() + Math.sin(angle) * radius,
                        1, 0.0D, 0.08D, 0.0D, 0.01D);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE,
                SoundSource.PLAYERS, 1.2F, 1.1F);
    }

    private static void tsunami(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        double radius = 8.0D;

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius), entity -> entity != player && entity.isAlive())) {
            if (target instanceof Player other && other.isCreative()) {
                continue;
            }

            double distance = target.position().distanceTo(player.position());
            if (distance > radius) {
                continue;
            }

            Vec3 direction = target.position().subtract(player.position());
            if (direction.lengthSqr() < 0.01D) {
                direction = new Vec3(1.0D, 0.0D, 0.0D);
            }
            direction = direction.normalize();

            target.hurt(player.damageSources().indirectMagic(player, player), 18.0F);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
            target.setDeltaMovement(target.getDeltaMovement()
                    .add(direction.scale(2.2D - distance * 0.12D))
                    .add(0.0D, 0.8D, 0.0D));
            target.hurtMarked = true;
        }

        for (double ring = 0.5D; ring <= radius; ring += 0.6D) {
            int points = Math.max(18, (int) (ring * 10.0D));
            for (int i = 0; i < points; i++) {
                double angle = (Math.PI * 2.0D) * i / points;
                double x = player.getX() + Math.cos(angle) * ring;
                double z = player.getZ() + Math.sin(angle) * ring;
                level.sendParticles(ParticleTypes.SPLASH, x, player.getY() + 0.15D, z,
                        1, 0.0D, 0.25D, 0.0D, 0.12D);
                if (i % 3 == 0) {
                    level.sendParticles(ParticleTypes.CLOUD, x, player.getY() + 0.1D, z,
                            1, 0.0D, 0.15D, 0.0D, 0.04D);
                }
            }
        }

        level.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 0.5D, player.getZ(),
                8, 2.5D, 0.4D, 2.5D, 0.0D);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_3,
                SoundSource.PLAYERS, 2.0F, 0.6F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_SPLASH,
                SoundSource.PLAYERS, 1.5F, 0.5F);
    }
}
