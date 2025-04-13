package com.mikarific.bugmine.mixins;

import com.mikarific.bugmine.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShieldItem.class)
public class ShieldItemMixin extends Item {
    public ShieldItemMixin(Settings settings) { super(settings); }

    @Inject(method = "use", at = @At("RETURN"), cancellable = true)
    private void functionalShields(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (Config.functionalShields) {
            cir.setReturnValue(super.use(world, player, hand));
        }
    }
}
