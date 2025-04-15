package com.mikarific.bugmine.config;

import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.GameInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Config {
    private static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
        .id(new Identifier("bugmine", "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("bugmine.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    @SerialEntry
    public static boolean allowSoulLinkOnLanServers = true;

    @SerialEntry
    public static boolean functionalShields = true;

    @SerialEntry
    public static boolean obtainableDragonFire = true;

    @SerialEntry
    public static boolean obtainableInItTogether = true;

    @SerialEntry
    public static boolean obtainableNoDrops = true;

    @SerialEntry
    public static boolean preventCtrlQFreeze = true;

    @SerialEntry
    public static boolean preventIngredientSwapping = true;

    @SerialEntry
    public static boolean preventIngredientThrowing = true;

    @SerialEntry
    public static boolean preventPacketDisconnect = true;

    @SerialEntry
    public static boolean preventSoulLinkCrash = true;

    @SerialEntry
    public static boolean rabbitsSpawnsRabbits = true;

    public static void save() {
        HANDLER.save();
    }

    public static void load() {
        HANDLER.load();
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
                        .binding(
                            allowSoulLinkOnLanServers,
                            () -> allowSoulLinkOnLanServers,
                            newVal -> {
                                allowSoulLinkOnLanServers = newVal;
                                save();
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
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.preventIngredientSwapping.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventIngredientSwapping.description")))
                        .binding(
                            preventIngredientSwapping,
                            () -> preventIngredientSwapping,
                            newVal -> {
                                preventIngredientSwapping = newVal;
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.preventIngredientThrowing.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventIngredientThrowing.description")))
                        .binding(
                            preventIngredientThrowing,
                            () -> preventIngredientThrowing,
                            newVal -> {
                                preventIngredientThrowing = newVal;
                                save();
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
                        .binding(
                            functionalShields,
                            () -> functionalShields,
                            newVal -> {
                                functionalShields = newVal;
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.obtainableDragonFire.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableDragonFire.description")))
                        .binding(
                            obtainableDragonFire,
                            () -> obtainableDragonFire,
                            newVal -> {
                                obtainableDragonFire = newVal;
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.obtainableInItTogether.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableInItTogether.description")))
                        .binding(
                            obtainableInItTogether,
                            () -> obtainableInItTogether,
                            newVal -> {
                                obtainableInItTogether = newVal;
                                GameInstance gameInstance = MinecraftClient.getInstance().method_70242();
                                if (gameInstance != null) gameInstance.reloadResources(gameInstance.getDataPackManager().getEnabledIds()).exceptionally((throwable) -> {
                                    LogUtils.getLogger().warn("Failed to execute reload", throwable);
                                    return null;
                                });
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.obtainableNoDrops.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.obtainableNoDrops.description")))
                        .binding(
                            obtainableNoDrops,
                            () -> obtainableNoDrops,
                            newVal -> {
                                obtainableNoDrops = newVal;
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.preventPacketDisconnect.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventPacketDisconnect.description")))
                        .binding(
                            preventPacketDisconnect,
                            () -> preventPacketDisconnect,
                            newVal -> {
                                preventPacketDisconnect = newVal;
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.preventSoulLinkCrash.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.preventSoulLinkCrash.description")))
                        .binding(
                            preventSoulLinkCrash,
                            () -> preventSoulLinkCrash,
                            newVal -> {
                                preventSoulLinkCrash = newVal;
                                save();
                            }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("bugmine.options.rabbitsSpawnsRabbits.name"))
                        .description(OptionDescription.of(Text.translatable("bugmine.options.rabbitsSpawnsRabbits.description")))
                        .binding(
                            rabbitsSpawnsRabbits,
                            () -> rabbitsSpawnsRabbits,
                            newVal -> {
                                rabbitsSpawnsRabbits = newVal;
                                save();
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
}
