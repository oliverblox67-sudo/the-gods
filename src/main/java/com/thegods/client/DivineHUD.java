package com.thegods.client;

import com.thegods.ability.Ability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class DivineHUD {
    private DivineHUD() {
    }

    public static void render(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui || !ClientDivineData.unlocked) {
            return;
        }

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int barWidth = 140;
        int barX = (screenWidth - barWidth) / 2;
        int barY = screenHeight - 62;

        graphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + 9, 0xB0000000);
        int fill = Math.round(barWidth * (ClientDivineData.energy / Math.max(1.0F, ClientDivineData.maxEnergy)));
        graphics.fill(barX, barY, barX + fill, barY + 8, 0xFF1E90FF);
        graphics.fill(barX, barY, barX + fill, barY + 3, 0xFF8BE9FD);

        Component label = Component.translatable("hud.thegods.energy",
                (int) ClientDivineData.energy, (int) ClientDivineData.maxEnergy);
        graphics.drawString(minecraft.font, label,
                barX + barWidth / 2 - minecraft.font.width(label) / 2, barY - 11, 0xFFBDEFFF, true);

        if (!ClientDivineData.godForm) {
            Component dormant = Component.translatable("hud.thegods.dormant");
            graphics.drawString(minecraft.font, dormant,
                    barX + barWidth / 2 - minecraft.font.width(dormant) / 2, barY - 23, 0xFF7F8C9A, true);
            return;
        }

        Component name = Component.translatable("hud.thegods.poseidon");
        graphics.drawString(minecraft.font, name,
                barX + barWidth / 2 - minecraft.font.width(name) / 2, barY - 23, 0xFF6FE7FF, true);

        int iconSize = 24;
        int gap = 4;
        int total = Ability.values().length * iconSize + (Ability.values().length - 1) * gap;
        int iconX = (screenWidth - total) / 2;
        int iconY = barY - 36;

        for (Ability ability : Ability.values()) {
            int cooldown = ability.ordinal() < ClientDivineData.cooldowns.length
                    ? ClientDivineData.cooldowns[ability.ordinal()] : 0;
            int border = cooldown > 0 ? 0xFF4A5560 : 0xFF2E8BC0;

            graphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0xCC06121F);
            graphics.fill(iconX, iconY, iconX + iconSize, iconY + 1, border);
            graphics.fill(iconX, iconY + iconSize - 1, iconX + iconSize, iconY + iconSize, border);
            graphics.fill(iconX, iconY, iconX + 1, iconY + iconSize, border);
            graphics.fill(iconX + iconSize - 1, iconY, iconX + iconSize, iconY + iconSize, border);

            String letter = switch (ability) {
                case TIDAL_SURGE -> "R";
                case TRIDENT_STORM -> "F";
                case ABYSSAL_BARRIER -> "G";
                case TSUNAMI -> "V";
            };
            graphics.drawString(minecraft.font, letter,
                    iconX + iconSize / 2 - minecraft.font.width(letter) / 2, iconY + 8, 0xFFE6F7FF, true);

            if (cooldown > 0) {
                graphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0x99000000);
                String seconds = String.valueOf(Math.max(1, (cooldown + 19) / 20));
                graphics.drawString(minecraft.font, seconds,
                        iconX + iconSize / 2 - minecraft.font.width(seconds) / 2, iconY + 8, 0xFFFF6B6B, true);
            }

            iconX += iconSize + gap;
        }
    }
}
