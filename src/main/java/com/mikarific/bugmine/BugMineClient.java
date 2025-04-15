package com.mikarific.bugmine;

import com.mikarific.bugmine.networking.ClientNetworkingHandler;
import net.fabricmc.api.ClientModInitializer;
public class BugMineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientNetworkingHandler.register();
    }
}
