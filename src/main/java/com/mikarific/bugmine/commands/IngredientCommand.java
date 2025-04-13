package com.mikarific.bugmine.commands;

import com.mikarific.bugmine.mixins.accessor.LevelPropertiesAccessor;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.aprilfools.UnlockCondition;
import net.minecraft.aprilfools.UnlockMode;
import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistrySelectorArgumentType;
import net.minecraft.network.packet.s2c.play.UpdateUnlockedEffectsS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IngredientCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("ingredient")
            .requires((source) -> source.hasPermissionLevel(2))
            .then(literal("grant")
                .then(literal("only")
                    .then(argument("ingredient", RegistrySelectorArgumentType.selector(registryAccess, RegistryKeys.WORLD_EFFECT))
                        .executes(context -> unlock(context.getSource(), RegistrySelectorArgumentType.getEntries(context, "ingredient", RegistryKeys.WORLD_EFFECT)))
                    )
                )
                .then(literal("everything")
                    .executes(context -> unlock(context.getSource(), context.getSource().getRegistryManager().getOrThrow(RegistryKeys.WORLD_EFFECT).streamEntries().toList()))
                )
            )
            .then(literal("revoke")
                .then(literal("only")
                    .then(argument("ingredient", RegistrySelectorArgumentType.selector(registryAccess, RegistryKeys.WORLD_EFFECT))
                        .executes(context -> lock(context.getSource(), RegistrySelectorArgumentType.getEntries(context, "ingredient", RegistryKeys.WORLD_EFFECT)))
                    )
                )
                .then(literal("everything")
                    .executes(context -> lock(context.getSource(), context.getSource().getRegistryManager().getOrThrow(RegistryKeys.WORLD_EFFECT).streamEntries().toList()))
                )
            )
        );
    }

    private static int unlock(ServerCommandSource source, Collection<RegistryEntry.Reference<WorldEffect>> worldEffects) {
        worldEffects.forEach((worldEffect) -> source.getWorld().method_69083(worldEffect.value()));
        return 1;
    }

    private static int lock(ServerCommandSource source, Collection<RegistryEntry.Reference<WorldEffect>> worldEffects) {
        worldEffects.forEach((worldEffect) -> {
            if (source.getWorld().method_69104(worldEffect.value()) && worldEffect.value().unlockMode() != UnlockMode.ALWAYS_UNLOCKED) {
                source.method_69818().getPlayerManager().broadcast(Text.translatable("world.effect.locked", worldEffect.value().name()), true);
                source.method_69818().getPlayerManager().broadcast(Text.translatable("world.effect.locked", worldEffect.value().name()), false);
                ((LevelPropertiesAccessor) source.getWorld().getLevelProperties()).getField_59280().remove(worldEffect.value());

                for (ServerPlayerEntity serverPlayerEntity : source.method_69818().getPlayerManager().getPlayerList()) {
                    UnlockCondition.method_69629(source.getWorld(), serverPlayerEntity, worldEffect.value());
                    serverPlayerEntity.networkHandler.sendPacket(new UpdateUnlockedEffectsS2CPacket(source.getWorld().method_69126()));
                }
            }
        });
        return 1;
    }
}
