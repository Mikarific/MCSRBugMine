package com.mikarific.bugmine.config;

import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

public class SpeedrunIntegration implements SpeedrunConfig {
    @Override
    public String modID() {
        return "bugmine";
    }

    @Override
    public @NotNull Screen createConfigScreen(Screen parent) {
        return Config.getScreen(parent);
    }
}
