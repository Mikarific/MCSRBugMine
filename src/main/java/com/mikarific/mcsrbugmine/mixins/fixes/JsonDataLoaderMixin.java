package com.mikarific.mcsrbugmine.mixins.fixes;

import com.mikarific.mcsrbugmine.MCSRBugMine;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(JsonDataLoader.class)
public abstract class JsonDataLoaderMixin<T> extends SinglePreparationResourceReloader<Map<Identifier, T>> {
    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/Map;", at = @At("RETURN"))
    private void obtainableInItTogether(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<Map<Identifier, Advancement>> cir) {
        if (MCSRBugMine.config.obtainableInItTogether) {
            if (((Object)this) instanceof ServerAdvancementLoader) {
                Map<Identifier, Advancement> advancements = cir.getReturnValue();
                Advancement inItTogether = advancements.get(new Identifier("minecraft", "unlocks/in_it_together"));

                AdvancementDisplay display = inItTogether.display().get();
                AdvancementDisplay fixedDisplay = new AdvancementDisplay(display.getIcon(), display.getTitle(), Text.translatable("mcsrbugmine.advancements.unlocks.in_it_together.description"), Text.translatable("mcsrbugmine.advancements.unlocks.in_it_together.description"), display.getBackground(), display.getFrame(), display.shouldShowToast(), display.shouldAnnounceToChat(), display.isHidden());
                fixedDisplay.setPos(display.getX(), display.getY());

                Map<String, AdvancementCriterion<?>> fixedCriteria =  Map.of("complete_level", Criteria.LEVEL_COMPLETED.create((TickCriterion.Conditions) inItTogether.criteria().get("complete_level").conditions()));

                Advancement fixedAdvancement = new Advancement(inItTogether.parent(), Optional.ofNullable(fixedDisplay), inItTogether.rewards(), fixedCriteria, inItTogether.requirements(), inItTogether.sendsTelemetryEvent());
                advancements.replace(new Identifier("minecraft", "unlocks/in_it_together"), fixedAdvancement);
            }
        }
    }
}
