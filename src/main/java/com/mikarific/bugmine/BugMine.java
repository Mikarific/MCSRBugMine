package com.mikarific.bugmine;

import com.mikarific.bugmine.commands.Commands;
import com.mikarific.bugmine.config.Config;
import net.fabricmc.api.ModInitializer;

public class BugMine implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.load();
        Commands.register();
    }
}
