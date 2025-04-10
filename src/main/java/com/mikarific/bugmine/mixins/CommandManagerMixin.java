package com.mikarific.bugmine.mixins;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.class_10963;
import net.minecraft.class_10964;
import net.minecraft.class_10965;
import net.minecraft.class_10966;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CommandManager.RegistrationEnvironment registrationEnvironment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
        class_10963.method_69020(this.dispatcher, commandRegistryAccess);
        class_10964.method_69036(this.dispatcher);
        class_10965.method_69048(this.dispatcher, commandRegistryAccess);
        class_10966.method_69052(this.dispatcher, commandRegistryAccess);
    }
}
