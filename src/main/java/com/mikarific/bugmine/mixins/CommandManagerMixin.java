package com.mikarific.bugmine.mixins;

import com.mikarific.bugmine.commands.BugMineCommand;
import com.mikarific.bugmine.commands.IngredientCommand;
import com.mikarific.bugmine.commands.UnlockCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.class_10963;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/server/command/AbstractServerCommandSource.asResultConsumer()Lcom/mojang/brigadier/ResultConsumer;"))
    private void register(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess registryAccess, CallbackInfo ci) {
        BugMineCommand.register(this.dispatcher);
        IngredientCommand.register(this.dispatcher, registryAccess);
        UnlockCommand.register(this.dispatcher, registryAccess);
        class_10963.method_69020(this.dispatcher, registryAccess);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/class_10963.method_69020(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/command/CommandRegistryAccess;)V"))
    private void disableLevelCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {}

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/class_10964.method_69036(Lcom/mojang/brigadier/CommandDispatcher;)V"))
    private void disableRoomCommand(CommandDispatcher<ServerCommandSource> dispatcher) {}

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/class_10965.method_69048(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/command/CommandRegistryAccess;)V"))
    private void disableUnlockCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {}

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/class_10966.method_69052(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/command/CommandRegistryAccess;)V"))
    private void disableUnlockWorldEffectCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {}
}
