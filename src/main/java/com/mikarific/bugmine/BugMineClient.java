package com.mikarific.bugmine;

import com.mikarific.bugmine.config.Config;
import com.mikarific.bugmine.networking.BugMineConfigPayloadS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Arrays;

public class BugMineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BugMineConfigPayloadS2C.ID, (payload, context) -> {
            context.client().execute(() -> {
                try {
                    Object parsedValue = null;
                    if (Config.class.getField(payload.option()).getType() == boolean.class) parsedValue = Arrays.asList(Config.getValues(payload.option())).contains(payload.value().toLowerCase()) ? Boolean.parseBoolean(payload.value()) : null;
                    if (parsedValue != null) {
                        Config.class.getField(payload.option()).set(null, parsedValue);
                        Config.save();
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
