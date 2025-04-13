package com.mikarific.bugmine.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.bugmine.config.Config;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique private static ServerPlayerEntity lastDamaged = null;
    @Inject(method = "damage", at = @At("HEAD"))
    private void preventSoulLinkCrash(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (Config.preventSoulLinkCrash && lastDamaged == null) lastDamaged = (ServerPlayerEntity)(Object)this;
    }

    @WrapOperation(method = "damage", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerPlayerEntity.damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean preventSoulLinkCrash(ServerPlayerEntity player, ServerWorld world, DamageSource source, float amount, Operation<Boolean> original) {
        if (Config.preventSoulLinkCrash && player == lastDamaged) {
            lastDamaged = null;
            return false;
        }
        return original.call(player, world, source, amount);
    }
}
