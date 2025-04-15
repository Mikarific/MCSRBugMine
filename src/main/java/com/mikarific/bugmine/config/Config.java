package com.mikarific.bugmine.config;

import com.google.gson.GsonBuilder;
import com.mikarific.bugmine.config.annotations.Client;
import com.mikarific.bugmine.config.annotations.Server;
import com.mikarific.bugmine.config.annotations.TriggerReload;
import com.mikarific.bugmine.networking.ClientNetworkingHandler;
import com.mikarific.bugmine.networking.payloads.BugMineConfigPayloadC2S;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;

public class Config {
    private static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
        .id(new Identifier("bugmine", "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("bugmine.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    @Client @SerialEntry
    public static boolean allowSoulLinkOnLanServers = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    @Client(required = false) @Server @SerialEntry
    public static boolean functionalShields = true;

    @Server @SerialEntry
    public static boolean obtainableDragonFire = true;

    @Server @TriggerReload @SerialEntry
    public static boolean obtainableInItTogether = true;

    @Server @SerialEntry
    public static boolean obtainableNoDrops = true;

    @Client @SerialEntry
    public static boolean preventCtrlQFreeze = true;

    @Server @SerialEntry
    public static boolean preventIngredientSwapping = true;

    @Server @SerialEntry
    public static boolean preventIngredientThrowing = true;

    @Server @SerialEntry
    public static boolean preventPacketDisconnect = true;

    @Server @SerialEntry
    public static boolean preventSoulLinkCrash = true;

    @Server @SerialEntry
    public static boolean rabbitsSpawnsRabbits = true;

    public static void save() {
        HANDLER.save();
    }

    public static void load() {
        HANDLER.load();
    }

    private static boolean isAvailableOnServer() {
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            return MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.hasPermissionLevel(2);
        } else {
            return true;
        }
    }

    public static Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("bugmine.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("bugmine.category.bug_fixes.name"))
                        .tooltip(Text.translatable("bugmine.category.bug_fixes.tooltip"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("bugmine.group.client.name"))
                                .description(OptionDescription.of(Text.translatable("bugmine.group.client.description")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.allowSoulLinkOnLanServers.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.allowSoulLinkOnLanServers.description")))
                                        .available(isAvailableOnServer() && (MinecraftClient.getInstance().getServer() != null || MinecraftClient.getInstance().getCurrentServerEntry() == null))
                                        .binding(
                                                allowSoulLinkOnLanServers,
                                                () -> MinecraftClient.getInstance().getCurrentServerEntry() == null && allowSoulLinkOnLanServers,
                                                newVal -> {
                                                    allowSoulLinkOnLanServers = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() == null) {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventCtrlQFreeze.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventCtrlQFreeze.description")))
                                        .binding(
                                                preventCtrlQFreeze,
                                                () -> preventCtrlQFreeze,
                                                newVal -> {
                                                    preventCtrlQFreeze = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() == null) {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .build()
                        )
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("bugmine.group.server.name"))
                                .description(OptionDescription.of(Text.translatable("bugmine.group.server.description")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.functionalShields.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.functionalShields.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                functionalShields,
                                                () -> functionalShields,
                                                newVal -> {
                                                    functionalShields = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("functionalShields", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.obtainableDragonFire.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableDragonFire.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                obtainableDragonFire,
                                                () -> obtainableDragonFire,
                                                newVal -> {
                                                    obtainableDragonFire = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("obtainableDragonFire", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.obtainableInItTogether.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableInItTogether.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                obtainableInItTogether,
                                                () -> obtainableInItTogether,
                                                newVal -> {
                                                    obtainableInItTogether = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("obtainableInItTogether", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.obtainableNoDrops.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableNoDrops.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                obtainableNoDrops,
                                                () -> obtainableNoDrops,
                                                newVal -> {
                                                    obtainableNoDrops = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("obtainableInItTogether", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventIngredientSwapping.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventIngredientSwapping.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                preventIngredientSwapping,
                                                () -> preventIngredientSwapping,
                                                newVal -> {
                                                    preventIngredientSwapping = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventIngredientSwapping", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventIngredientThrowing.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventIngredientThrowing.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                preventIngredientThrowing,
                                                () -> preventIngredientThrowing,
                                                newVal -> {
                                                    preventIngredientThrowing = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventIngredientThrowing", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventPacketDisconnect.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventPacketDisconnect.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                preventPacketDisconnect,
                                                () -> preventPacketDisconnect,
                                                newVal -> {
                                                    preventPacketDisconnect = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventPacketDisconnect", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.preventSoulLinkCrash.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventSoulLinkCrash.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                preventSoulLinkCrash,
                                                () -> preventSoulLinkCrash,
                                                newVal -> {
                                                    preventSoulLinkCrash = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("preventSoulLinkCrash", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("bugmine.options.rabbitsSpawnsRabbits.name"))
                                        .description(OptionDescription.of(Text.translatable("bugmine.options.rabbitsSpawnsRabbits.description")))
                                        .available(ClientNetworkingHandler.isOnServer() && isAvailableOnServer())
                                        .binding(
                                                rabbitsSpawnsRabbits,
                                                () -> rabbitsSpawnsRabbits,
                                                newVal -> {
                                                    rabbitsSpawnsRabbits = newVal;
                                                    if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                                                        ClientPlayNetworking.send(new BugMineConfigPayloadC2S("rabbitsSpawnsRabbits", newVal.toString()));
                                                    } else {
                                                        save();
                                                    }
                                                }
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                                .name(Text.translatable("bugmine.category.qol.name"))
                                .tooltip(Text.translatable("bugmine.category.qol.tooltip"))
//                .group(OptionGroup.createBuilder()
//                    .name(Text.translatable("bugmine.group.client.name"))
//                    .description(OptionDescription.of(Text.translatable("bugmine.group.client.description")))
//                    .build()
//                )
//                .group(OptionGroup.createBuilder()
//                    .name(Text.translatable("bugmine.group.server.name"))
//                    .description(OptionDescription.of(Text.translatable("bugmine.group.server.description")))
//                    .build()
//                )
                                .build()
                )
                .build()
                .generateScreen(parent);
    }

    public static String[] getOptions() {
        return Arrays.stream(Config.class.getFields()).filter((field) -> {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                return field.isAnnotationPresent(SerialEntry.class);
            } else {
                return field.isAnnotationPresent(SerialEntry.class) && field.isAnnotationPresent(Server.class);
            }
        }).map(Field::getName).toArray(String[]::new);
    }

    public static String[] getValues(String option) {
        try {
            if (Config.class.getField(option).getType() == boolean.class) return new String[]{"true", "false"};
            return new String[]{};
        } catch (NoSuchFieldException e) {
            return new String[]{};
        }
    }
}
