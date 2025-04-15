package com.mikarific.bugmine.commands;

import com.mikarific.bugmine.config.Config;
import com.mikarific.bugmine.config.annotations.TriggerReload;
import com.mikarific.bugmine.networking.BugMineConfigPayloadS2C;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BugMineCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("bugmine")
            .requires((source) -> source.hasPermissionLevel(2))
            .then(argument("option", StringArgumentType.word())
                .suggests((context, builder) -> suggestMatching(Config.getOptions(), builder))
                .executes(context -> showOption(context.getSource(), StringArgumentType.getString(context, "option")))
                .then(argument("value", StringArgumentType.word())
                    .suggests((context, builder) -> suggestMatching(Config.getValues(StringArgumentType.getString(context, "option")), builder))
                    .executes(context -> setOption(context.getSource(), StringArgumentType.getString(context, "option"), StringArgumentType.getString(context, "value")))
                )
            )
        );
    }

    private static int setOption(ServerCommandSource source, String option, String value) throws CommandSyntaxException {
        try {
            Object parsedValue = null;
            if (Config.class.getField(option).getType() == boolean.class) parsedValue = Arrays.asList(Config.getValues(option)).contains(value.toLowerCase()) ? Boolean.parseBoolean(value) : null;
            if (parsedValue != null) {
                Config.class.getField(option).set(null, parsedValue);
                if (Config.class.getField(option).isAnnotationPresent(TriggerReload.class)) {
                    source.method_69818().reloadResources(source.method_69818().getDataPackManager().getEnabledIds()).exceptionally((throwable) -> {
                        LogUtils.getLogger().warn("Failed to execute reload", throwable);
                        return null;
                    });
                }
                Config.save();
                for (ServerPlayerEntity player : source.method_69818().getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(player, new BugMineConfigPayloadS2C(option, parsedValue.toString()));
                }
                Object finalParsedValue = parsedValue;
                source.sendFeedback(() -> Text.literal("Set " + option + " to " + finalParsedValue), true);
            } else {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Can't set config option to that value");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("BugMine config option not found");
        }
        return 1;
    }

    private static int showOption(ServerCommandSource source, String option) throws CommandSyntaxException {
        try {
            Object value = Config.class.getField(option).get(null);
            source.sendFeedback(() -> Text.literal(option + " = " + value), false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("BugMine config option not found");
        }
        return 1;
    }
}
