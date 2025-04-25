package com.mikarific.mcsrbugmine.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.mcsrbugmine.MCSRBugMine;
import net.minecraft.aprilfools.UnlockCondition;
import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.aprilfools.WorldEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(WorldEffects.class)
public class WorldEffectsMixin {
    @WrapOperation(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=no_drops")), at = @At(value = "INVOKE", target = "net/minecraft/aprilfools/WorldEffect$Builder.buildAndRegister()Lnet/minecraft/aprilfools/WorldEffect;", ordinal = 0))
    private static WorldEffect obtainableNoDrops(WorldEffect.Builder builder, Operation<WorldEffect> original) {
        return original.call(builder.unlockedByCondition(UnlockCondition.method_69651((world, player, entity) -> MCSRBugMine.config.obtainableNoDrops && world.getRandom().nextFloat() < 0.05F)));
    }
}
