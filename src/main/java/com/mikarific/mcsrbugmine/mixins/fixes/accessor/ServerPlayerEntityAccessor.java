package com.mikarific.mcsrbugmine.mixins.fixes.accessor;

import net.minecraft.class_10959;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {
    @Accessor
    class_10959 getField_58300();
}
