package com.mikarific.bugmine.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.bugmine.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.MineCrafterScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MineCrafterScreenHandler.class)
public class MineCrafterScreenHandlerMixin {
    @WrapOperation(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "net/minecraft/screen/ScreenHandler.internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void preventCtrlQFreeze(MineCrafterScreenHandler screenHandler, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, Operation<Void> original) {
        if (Config.preventCtrlQFreeze && actionType == SlotActionType.THROW) {
            original.call(screenHandler, slotIndex, 0, actionType, player);
        } else {
            original.call(screenHandler, slotIndex, button, actionType, player);
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void preventIngredientThrowingAndSwapping(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (Config.preventIngredientThrowing && actionType == SlotActionType.THROW) ci.cancel();
        if (Config.preventIngredientSwapping && actionType == SlotActionType.SWAP) ci.cancel();
    }
}
