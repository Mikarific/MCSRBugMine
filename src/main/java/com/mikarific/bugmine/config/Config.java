package com.mikarific.bugmine.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
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
    public static boolean functionalShields = true;

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
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("bugmine.options.functional_shields.name"))
                    .description(OptionDescription.of(Text.translatable("bugmine.options.functional_shields.description")))
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
                .build()
            )
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("bugmine.category.qol.name"))
                .tooltip(Text.translatable("bugmine.category.qol.tooltip"))
                .build()
            )
            .build()
            .generateScreen(parent);
    }
}
