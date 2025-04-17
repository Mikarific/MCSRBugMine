package com.mikarific.bugmine.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.bugmine.config.ServerConfig;
import net.minecraft.aprilfools.UnlockCondition;
import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.aprilfools.WorldEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(WorldEffects.class)
public class WorldEffectsMixin {
    @WrapOperation(method = "method_69980", at = @At(value = "FIELD", target = "net/minecraft/entity/EntityType.PARROT:Lnet/minecraft/entity/EntityType;"))
    private static EntityType<?> rabbitsSpawnsRabbits(Operation<EntityType<ParrotEntity>> original) {
        return ServerConfig.rabbitsSpawnsRabbits ? EntityType.RABBIT : original.call();
    }

    @WrapOperation(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=no_drops")), at = @At(value = "INVOKE", target = "net/minecraft/aprilfools/WorldEffect$Builder.buildAndRegister()Lnet/minecraft/aprilfools/WorldEffect;", ordinal = 0))
    private static WorldEffect obtainableNoDrops(WorldEffect.Builder builder, Operation<WorldEffect> original) {
        return original.call(builder.unlockedByCondition(UnlockCondition.method_69651((world, player, entity) -> ServerConfig.obtainableNoDrops && world.getRandom().nextFloat() < 0.05F)));
    }
}
