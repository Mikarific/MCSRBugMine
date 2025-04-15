package com.mikarific.bugmine.networking;

import com.mikarific.bugmine.config.Config;
import com.mikarific.bugmine.networking.payloads.BugMineConfigPayloadS2C;
import com.mikarific.bugmine.networking.payloads.BugMineInitPayloadC2S;
import com.mikarific.bugmine.networking.payloads.BugMineInitPayloadS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Arrays;

public class ClientNetworkingHandler {
    private static final String VERSION = FabricLoader.getInstance().getModContainer("bugmine").map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
    private static boolean MATCHING_SERVER = false;

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> ClientPlayNetworking.send(new BugMineInitPayloadC2S(VERSION)));

        ClientPlayNetworking.registerGlobalReceiver(BugMineInitPayloadS2C.ID, (payload, context) -> context.client().execute(() -> MATCHING_SERVER = true));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            MATCHING_SERVER = false;
            Config.load();
        });

        ClientPlayNetworking.registerGlobalReceiver(BugMineConfigPayloadS2C.ID, (payload, context) -> context.client().execute(() -> {
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
        }));
    }

    public static boolean isOnServer() {
        return MATCHING_SERVER;
    }
}
