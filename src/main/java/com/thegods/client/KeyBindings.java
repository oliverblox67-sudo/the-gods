package com.thegods.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.thegods.ability.Ability;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class KeyBindings {
    public static final String CATEGORY = "key.categories.thegods";

    public static final KeyMapping TIDAL_SURGE = new KeyMapping(Ability.TIDAL_SURGE.getKeyId(),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping TRIDENT_STORM = new KeyMapping(Ability.TRIDENT_STORM.getKeyId(),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F, CATEGORY);
    public static final KeyMapping ABYSSAL_BARRIER = new KeyMapping(Ability.ABYSSAL_BARRIER.getKeyId(),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping TSUNAMI = new KeyMapping(Ability.TSUNAMI.getKeyId(),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping TOGGLE_FORM = new KeyMapping("key.thegods.toggle_form",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY);

    private KeyBindings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TIDAL_SURGE);
        event.register(TRIDENT_STORM);
        event.register(ABYSSAL_BARRIER);
        event.register(TSUNAMI);
        event.register(TOGGLE_FORM);
    }
}
