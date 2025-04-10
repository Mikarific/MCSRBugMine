package com.mikarific.bugmine.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            IngredientCommand.register(dispatcher, registryAccess);
        });
    }
}
