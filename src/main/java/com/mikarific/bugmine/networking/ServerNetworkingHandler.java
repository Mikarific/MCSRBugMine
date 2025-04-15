package com.mikarific.bugmine.networking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikarific.bugmine.config.Config;
import com.mikarific.bugmine.networking.payloads.BugMineConfigPayloadC2S;
import com.mikarific.bugmine.networking.payloads.BugMineConfigPayloadS2C;
import com.mikarific.bugmine.networking.payloads.BugMineInitPayloadC2S;
import com.mikarific.bugmine.networking.payloads.BugMineInitPayloadS2C;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerNetworkingHandler {
    private static final String VERSION = FabricLoader.getInstance().getModContainer("bugmine").map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
    private static final Map<String, String> LANG_MAP = new HashMap<>();
    private static final Set<UUID> MATCHING_PLAYERS = new HashSet<>();

    public static void register() {
        PayloadTypeRegistry.playC2S().register(BugMineInitPayloadC2S.ID, BugMineInitPayloadC2S.CODEC);
        PayloadTypeRegistry.playS2C().register(BugMineInitPayloadS2C.ID, BugMineInitPayloadS2C.CODEC);

        PayloadTypeRegistry.playS2C().register(BugMineConfigPayloadS2C.ID, BugMineConfigPayloadS2C.CODEC);
        PayloadTypeRegistry.playC2S().register(BugMineConfigPayloadC2S.ID, BugMineConfigPayloadC2S.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BugMineInitPayloadC2S.ID, (payload, context) -> context.server().execute(() -> {
            MATCHING_PLAYERS.add(context.player().getUuid());
            ServerPlayNetworking.send(context.player(), new BugMineInitPayloadS2C(VERSION));
            for (String option : Config.getOptions()) {
                try {
                    Object value = Config.class.getField(option).get(null);
                    ServerPlayNetworking.send(context.player(), new BugMineConfigPayloadS2C(option, value.toString()));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> MATCHING_PLAYERS.remove(handler.getPlayer().getUuid()));

        ServerPlayNetworking.registerGlobalReceiver(BugMineConfigPayloadC2S.ID, (payload, context) -> context.server().execute(() -> {
            if (context.player().hasPermissionLevel(2)) {
                try {
                    Object parsedValue = null;
                    if (Config.class.getField(payload.option()).getType() == boolean.class) parsedValue = Arrays.asList(Config.getValues(payload.option())).contains(payload.value().toLowerCase()) ? Boolean.parseBoolean(payload.value()) : null;
                    if (parsedValue != null) {
                        Config.class.getField(payload.option()).set(null, parsedValue);
                        Config.save();
                        if (context.server().getGameInstance() != null) {
                            for (ServerPlayerEntity player : getPlayersWithClientMod(Objects.requireNonNull(context.server().getGameInstance()).getPlayerManager())) {
                                ServerPlayNetworking.send(player, new BugMineConfigPayloadS2C(payload.option(), parsedValue.toString()));
                            }
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }));

        try (InputStream in = ServerNetworkingHandler.class.getResourceAsStream("/assets/bugmine/lang/en_us.json")) {
            if (in == null) {
                throw new FileNotFoundException("Translation file not found");
            }
            JsonObject json = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                LANG_MAP.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Text translate(Text text) {
        if (!text.getSiblings().isEmpty()) {
            MutableText copy = text.copy();
            List<Text> replacedSiblings = text.getSiblings().stream().map(ServerNetworkingHandler::translate).toList();
            copy.getSiblings().clear();
            copy.getSiblings().addAll(replacedSiblings);
            return copy;
        } else {
            if (text.getContent() instanceof TranslatableTextContent translatable) {
                String pattern = LANG_MAP.getOrDefault(translatable.getKey(), null);
                if (pattern == null) return text;

                Text result = Text.literal("").setStyle(text.getStyle());
                Matcher matcher = Pattern.compile("%(\\d+\\$)?s|%%").matcher(pattern);

                int lastIndex = 0;
                while (matcher.find()) {
                    if (matcher.start() > lastIndex) {
                        String literal = pattern.substring(lastIndex, matcher.start());
                        result = result.copy().append(Text.literal(literal));
                    }

                    String match = matcher.group();
                    if (match.equals("%%")) {
                        result = result.copy().append(Text.literal("%"));
                    } else {
                        String indexGroup = matcher.group(1);

                        int replacementIndex;
                        if (indexGroup != null) {
                            replacementIndex = Integer.parseInt(indexGroup.substring(0, indexGroup.length() - 1)) - 1;
                        } else {
                            replacementIndex = -1;
                        }

                        Text replacement;
                        if (replacementIndex == -1) {
                            int next = 0;
                            while (next < translatable.getArgs().length && translatable.getArgs()[next] == null) next++;
                            replacement = next < translatable.getArgs().length ? (Text) translatable.getArgs()[next++] : Text.literal("%s");
                            if (next - 1 < translatable.getArgs().length) translatable.getArgs()[next - 1] = null;
                        } else if (replacementIndex >= 0 && replacementIndex < translatable.getArgs().length) {
                            replacement = (Text) translatable.getArgs()[replacementIndex];
                        } else {
                            replacement = Text.literal("%s");
                        }

                        result = result.copy().append(replacement);
                    }

                    lastIndex = matcher.end();
                }

                if (lastIndex < pattern.length()) {
                    result = result.copy().append(Text.literal(pattern.substring(lastIndex)));
                }

                return result;
            } else {
                return text;
            }
        }
    }

    public static boolean isOnClientForPlayer(ServerPlayerEntity player) {
        return MATCHING_PLAYERS.contains(player.getUuid());
    }

    public static List<ServerPlayerEntity> getPlayersWithClientMod(PlayerManager playerManager) {
        return MATCHING_PLAYERS.stream().map(playerManager::getPlayer).toList();
    }
}
