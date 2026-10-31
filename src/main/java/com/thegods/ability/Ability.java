package com.thegods.ability;

public enum Ability {
    TIDAL_SURGE("tidal_surge", 20.0F, 100),
    TRIDENT_STORM("trident_storm", 35.0F, 160),
    ABYSSAL_BARRIER("abyssal_barrier", 30.0F, 300),
    TSUNAMI("tsunami", 60.0F, 900);

    private final String id;
    private final float cost;
    private final int cooldownTicks;

    Ability(String id, float cost, int cooldownTicks) {
        this.id = id;
        this.cost = cost;
        this.cooldownTicks = cooldownTicks;
    }

    public String getTranslationKey() {
        return "ability.thegods." + id;
    }

    public String getKeyId() {
        return "key.thegods." + id;
    }

    public float getCost() {
        return cost;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public static Ability byId(int id) {
        return id >= 0 && id < values().length ? values()[id] : null;
    }
}
