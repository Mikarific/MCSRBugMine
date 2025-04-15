package com.mikarific.bugmine;

import com.mikarific.bugmine.config.Config;
import com.mikarific.bugmine.networking.BugMineConfigPayloadC2S;
import com.mikarific.bugmine.networking.BugMineConfigPayloadS2C;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.Arrays;

public class BugMine implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.load();
        PayloadTypeRegistry.playS2C().register(BugMineConfigPayloadS2C.ID, BugMineConfigPayloadS2C.CODEC);
        PayloadTypeRegistry.playC2S().register(BugMineConfigPayloadC2S.ID, BugMineConfigPayloadC2S.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (String option : Config.getOptions()) {
                try {
                    Object value = Config.class.getField(option).get(null);
                    ServerPlayNetworking.send(handler.player, new BugMineConfigPayloadS2C(option, value.toString()));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(BugMineConfigPayloadC2S.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player().hasPermissionLevel(2)) {
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
                }
            });
        });
    }
}
