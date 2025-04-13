package com.mikarific.bugmine.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.bugmine.config.Config;
import net.minecraft.aprilfools.WorldEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldEffects.class)
public class WorldEffectsMixin {
    @WrapOperation(method = "method_69980", at = @At(value = "FIELD", target = "net/minecraft/entity/EntityType.PARROT:Lnet/minecraft/entity/EntityType;"))
    private static EntityType<?> rabbitsSpawnsRabbits(Operation<EntityType<ParrotEntity>> original) {
        return Config.rabbitsSpawnsRabbits ? EntityType.RABBIT : original.call();
    }
}
