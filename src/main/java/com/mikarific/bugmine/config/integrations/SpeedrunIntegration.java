package com.mikarific.bugmine.config.integrations;

import com.mikarific.bugmine.config.screen.ConfigScreen;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SpeedrunIntegration implements SpeedrunConfig {
    @Override
    public String modID() {
        return "bugmine";
    }

    @Override
    public @NotNull Screen createConfigScreen(Screen parent) {
        return ConfigScreen.getScreen(parent);
    }
}
