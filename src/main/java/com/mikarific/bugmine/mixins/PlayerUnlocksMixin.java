package com.mikarific.bugmine.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mikarific.bugmine.config.Config;
import net.minecraft.aprilfools.PlayerUnlocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

import static net.minecraft.aprilfools.PlayerUnlocks.SORCERER_SUPREME;

@Mixin(PlayerUnlocks.class)
public class PlayerUnlocksMixin {
    @WrapMethod(method = "method_69247")
    private static Boolean dragonFireVisibility(ServerWorld world, ServerPlayerEntity player, DamageSource damageSource, Float float_, Operation<Boolean> original) {
        boolean originalReturn = original.call(world, player, damageSource, float_);
        if (Config.obtainableDragonFire) {
            if (originalReturn) return true;
            if (player.method_69135(SORCERER_SUPREME)) {
                if (damageSource.getSource() != null && damageSource.getSource().getType() == EntityType.AREA_EFFECT_CLOUD) {
                    if (damageSource.getAttacker() != null && (damageSource.getAttacker().getType() == EntityType.ENDER_DRAGON || damageSource.getAttacker().getType() == EntityType.DRAGON_FIREBALL)) {
                        return true;
                    }
                }
            }
        }
        return originalReturn;
    }
}
