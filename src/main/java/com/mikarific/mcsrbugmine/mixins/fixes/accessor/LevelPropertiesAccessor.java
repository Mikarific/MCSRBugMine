package com.mikarific.mcsrbugmine.mixins.fixes.accessor;

import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LevelProperties.class)
public interface LevelPropertiesAccessor {
    @Accessor
    List<WorldEffect> getField_59280();
}
