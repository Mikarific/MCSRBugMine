package com.mikarific.bugmine;

import com.mikarific.bugmine.config.Config;
import net.fabricmc.api.ModInitializer;
import net.minecraft.screen.MineCrafterScreenHandler;

public class BugMine implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.save();
    }
}
