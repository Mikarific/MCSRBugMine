package com.mikarific.bugmine;

import com.mikarific.bugmine.config.Config;
import com.mikarific.bugmine.networking.ServerNetworkingHandler;
import net.fabricmc.api.ModInitializer;

public class BugMine implements ModInitializer {

    @Override
    public void onInitialize() {
        Config.load();
        ServerNetworkingHandler.register();
    }
}
