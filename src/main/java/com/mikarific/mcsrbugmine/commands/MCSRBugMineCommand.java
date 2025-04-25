package com.mikarific.mcsrbugmine.commands;

import com.mikarific.mcsrbugmine.MCSRBugMine;
import com.mikarific.mcsrbugmine.config.Config;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import me.contaria.speedrunapi.config.api.annotations.Config.Access;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MCSRBugMineCommand {
    private static Field getFieldFromOptionName(String option) {
        try {
            return Config.class.getField(option);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static Object getValueFromField(Field field) {
        if (field == null) return null;
        try {
            return field.get(MCSRBugMine.config);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static String[] getValues(String option) {
        Field field = getFieldFromOptionName(option);
        if (field == null) return new String[]{};
        if (field.getType() == boolean.class) return new String[]{"true", "false"};
        return new String[]{};
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("mcsrbugmine")
            .then(argument("option", StringArgumentType.word())
                .suggests((context, builder) -> suggestMatching(Config.getOptions(), builder))
                .executes(context -> showOption(context.getSource(), StringArgumentType.getString(context, "option")))
                .then(argument("value", StringArgumentType.word())
                    .suggests((context, builder) -> suggestMatching(getValues(StringArgumentType.getString(context, "option")), builder))
                    .executes(context -> setOption(context.getSource(), StringArgumentType.getString(context, "option"), StringArgumentType.getString(context, "value")))
                )
            )
        );
    }

    private static int setOption(ServerCommandSource source, String option, String value) throws CommandSyntaxException {
        if (!List.of(Config.getOptions()).contains(option)) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("MCSR BugMine config option not found");

        Field field = getFieldFromOptionName(option);
        if (field == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("MCSR BugMine config option not found");

        Object parsedValue = null;
        if (field.getType() == boolean.class) parsedValue = Arrays.asList(getValues(option)).contains(value.toLowerCase()) ? Boolean.parseBoolean(value) : null;
        if (parsedValue == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Can't set config option to that value");
        try {
            field.set(MCSRBugMine.config, parsedValue);
        } catch (IllegalAccessException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Can't set config option to that value");
        }

        if (field.isAnnotationPresent(Access.class) && !field.getAnnotation(Access.class).setter().isEmpty()) {
            //noinspection resource
            source.method_69818().reloadResources(source.method_69818().getDataPackManager().getEnabledIds()).exceptionally((throwable) -> {
                LogUtils.getLogger().warn("Failed to execute reload", throwable);
                return null;
            });
        }

        MCSRBugMine.config.save();

        Object finalParsedValue = parsedValue;
        source.sendFeedback(() -> Text.literal("")
                .append(Text.translatable("speedrunapi.config.mcsrbugmine.option." + option).setStyle(Style.EMPTY.withBold(true)))
                .append(Text.literal(": ").setStyle(Style.EMPTY))
                .append(Text.literal(finalParsedValue.toString()).setStyle(getValueStyle(finalParsedValue))), true);
        return 1;
    }

    private static int showOption(ServerCommandSource source, String option) throws CommandSyntaxException {
        if (!List.of(Config.getOptions()).contains(option)) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("MCSR BugMine config option not found");

        Object value = getValueFromField(getFieldFromOptionName(option));
        if (value == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("MCSR BugMine config option not found");
        source.sendFeedback(() -> Text.literal("")
                .append(Text.translatable("speedrunapi.config.mcsrbugmine.option." + option).setStyle(Style.EMPTY.withBold(true)))
                .append(Text.literal("\n").setStyle(Style.EMPTY))
                .append(Text.translatable("speedrunapi.config.mcsrbugmine.option." + option + ".description"))
                .append(Text.literal("\n"))
                .append(Text.literal("Current Value: "))
                .append(Text.literal(value.toString()).setStyle(getValueStyle(value))), false);
        return 1;
    }

    private static Style getValueStyle(Object value) {
        if (value.toString().equals("true")) return Style.EMPTY.withBold(true).withColor(Formatting.GREEN);
        if (value.toString().equals("false")) return Style.EMPTY.withBold(true).withColor(Formatting.RED);
        return Style.EMPTY.withBold(true).withColor(Formatting.WHITE);
    }
}
