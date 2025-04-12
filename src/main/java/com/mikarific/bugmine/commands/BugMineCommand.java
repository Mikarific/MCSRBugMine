package com.mikarific.bugmine.commands;

import com.mikarific.bugmine.config.Config;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class BugMineCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("bugmine")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(literal("reload").executes(context -> reload(context.getSource())))
        );
    }

    private static int reload(ServerCommandSource source) {
        Config.load();
        source.sendMessage(Text.translatable("bugmine.config.reloaded"));
        return 1;
    }
}
