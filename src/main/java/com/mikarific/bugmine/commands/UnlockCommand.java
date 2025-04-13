package com.mikarific.bugmine.commands;

import com.mikarific.bugmine.mixins.accessor.ServerPlayerEntityAccessor;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.aprilfools.PlayerUnlock;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.IndexedIterable;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class UnlockCommand {
    public static final Map<RegistryEntry<PlayerUnlock>, List<RegistryEntry<PlayerUnlock>>> unlockChildren = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        for (RegistryEntry<PlayerUnlock> playerUnlock : Registries.PLAYER_UNLOCK.getIndexedEntries()) {
            if (playerUnlock.value().parent().isPresent()) {
                RegistryEntry<PlayerUnlock> parent = playerUnlock.value().parent().get();
                unlockChildren.computeIfAbsent(parent, k -> new ArrayList<>()).add(playerUnlock);
            }
        }

        dispatcher.register(literal("unlock")
            .requires((source) -> source.hasPermissionLevel(2))
            .then(literal("grant")
                .then(argument("targets", EntityArgumentType.players())
                    .then(literal("only")
                        .then(argument("unlock", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.PLAYER_UNLOCK))
                            .executes(context -> unlock(EntityArgumentType.getPlayers(context, "targets"), RegistryEntryReferenceArgumentType.getRegistryEntry(context, "unlock", RegistryKeys.PLAYER_UNLOCK)))
                        )
                    )
                    .then(literal("everything")
                        .executes(context -> unlock(EntityArgumentType.getPlayers(context, "targets"), context.getSource().getRegistryManager().getOrThrow(RegistryKeys.PLAYER_UNLOCK).getIndexedEntries()))
                    )
                )
            )
            .then(literal("revoke")
                .then(argument("targets", EntityArgumentType.players())
                    .then(literal("only")
                        .then(argument("unlock", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.PLAYER_UNLOCK))
                            .executes(context -> lock(context.getSource().method_69819(), EntityArgumentType.getPlayers(context, "targets"), RegistryEntryReferenceArgumentType.getRegistryEntry(context, "unlock", RegistryKeys.PLAYER_UNLOCK)))
                        )
                    )
                    .then(literal("everything")
                        .executes(context -> lock(context.getSource().method_69819(), EntityArgumentType.getPlayers(context, "targets"), context.getSource().getRegistryManager().getOrThrow(RegistryKeys.PLAYER_UNLOCK).getIndexedEntries()))
                    )
                )
            )
        );
    }

    private static int unlock(Collection<ServerPlayerEntity> players, IndexedIterable<RegistryEntry<PlayerUnlock>> playerUnlocks) {
        for (RegistryEntry<PlayerUnlock> playerUnlock : playerUnlocks) {
            for (ServerPlayerEntity player : players) {
                if (!player.method_69135(playerUnlock)) player.method_69142(playerUnlock);
            }
        }
        return 1;
    }

    private static int unlock(Collection<ServerPlayerEntity> players, RegistryEntry<PlayerUnlock> playerUnlock) {
        if (playerUnlock.value().parent().isPresent()) {
            unlock(players, playerUnlock.value().parent().get());
        }
        for (ServerPlayerEntity player : players) {
            if (!player.method_69135(playerUnlock)) player.method_69142(playerUnlock);
        }
        return 1;
    }

    private static int lock(PlayerManager playerManager, Collection<ServerPlayerEntity> players, IndexedIterable<RegistryEntry<PlayerUnlock>> playerUnlocks) {
        for (RegistryEntry<PlayerUnlock> playerUnlock : playerUnlocks) {
            for (ServerPlayerEntity player : players) {
                if (player.method_69135(playerUnlock)) {
                    ((ServerPlayerEntityAccessor) player).getField_58300().method_68940(playerUnlock);

                    announceLock(playerManager, player, playerUnlock.value());
                }
            }
        }
        return 1;
    }

    private static int lock(PlayerManager playerManager, Collection<ServerPlayerEntity> players, RegistryEntry<PlayerUnlock> playerUnlock) {
        for (ServerPlayerEntity player : players) {
            if (player.method_69135(playerUnlock)) {
                List<RegistryEntry<PlayerUnlock>> children = unlockChildren.getOrDefault(playerUnlock, Collections.emptyList());
                for (RegistryEntry<PlayerUnlock> child : children) {
                    if (player.method_69135(child)) lock(playerManager, Collections.singleton(player), child);
                }

                ((ServerPlayerEntityAccessor) player).getField_58300().method_68940(playerUnlock);

                announceLock(playerManager, player, playerUnlock.value());
            }
        }
        return 1;
    }

    private static void announceLock(PlayerManager playerManager, ServerPlayerEntity player, PlayerUnlock playerUnlock) {
        AdvancementDisplay display = playerUnlock.display();
        if (display.shouldAnnounceToChat()) {
            Text description = playerUnlock.defaultVisibility() == PlayerUnlock.Visibility.VISIBLE ? display.getDescription() : Text.literal("You're not allowed to see the description of this unlock >:)").formatted(Formatting.OBFUSCATED);
            Text title = Texts.bracketed(display.getTitle()).styled((style) -> style.withColor(Formatting.GREEN).withHoverEvent(new HoverEvent.ShowText(description)));
            Text playerName = Texts.setStyleIfAbsent(player.getDisplayName().copy(), Style.EMPTY.withColor(Formatting.WHITE));
            playerManager.broadcast(Text.translatable("unlocks.player.locked", playerName, title).formatted(Formatting.GRAY), false);
        }
    }
}
