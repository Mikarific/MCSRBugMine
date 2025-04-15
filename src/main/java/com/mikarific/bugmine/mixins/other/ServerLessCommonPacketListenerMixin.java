package com.mikarific.bugmine.mixins.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mikarific.bugmine.networking.ServerNetworkingHandler;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.class_10972;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(class_10972.class)
public abstract class ServerLessCommonPacketListenerMixin {
    @WrapOperation(method = "method_69158", at = @At(value = "INVOKE", target = "net/minecraft/network/ClientConnection.send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V"))
    private void bypassExpensiveCalculationIfNecessary(ClientConnection connection, Packet<?> packet, PacketCallbacks callbacks, boolean flush, Operation<Void> original) {
        if ((Object) this instanceof ServerPlayNetworkHandler) {
            ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).player;
            if (!ServerNetworkingHandler.isOnClientForPlayer(player)) {
                if (packet instanceof GameMessageS2CPacket messagePacket) {
                    original.call(connection, new GameMessageS2CPacket(ServerNetworkingHandler.translate(messagePacket.content()), messagePacket.overlay()), callbacks, flush);
                    return;
                }

                if (packet instanceof AdvancementUpdateS2CPacket advancementPacket) {
                    List<AdvancementEntry> advancementsToEarn = new ArrayList<>();
                    for (AdvancementEntry advancementEntry : advancementPacket.getAdvancementsToEarn()) {
                        if (advancementEntry.value().display().isPresent()) {
                            AdvancementDisplay display = advancementEntry.value().display().get();
                            AdvancementDisplay fixedDisplay = new AdvancementDisplay(display.getIcon(), ServerNetworkingHandler.translate(display.getTitle()), ServerNetworkingHandler.translate(display.getDescription()), ServerNetworkingHandler.translate(display.getHint()), display.getBackground(), display.getFrame(), display.shouldShowToast(), display.shouldAnnounceToChat(), display.isHidden());
                            fixedDisplay.setPos(display.getX(), display.getY());

                            Advancement fixedAdvancement = new Advancement(advancementEntry.value().parent(), Optional.ofNullable(fixedDisplay), advancementEntry.value().rewards(), advancementEntry.value().criteria(), advancementEntry.value().requirements(), advancementEntry.value().sendsTelemetryEvent());
                            advancementsToEarn.add(new AdvancementEntry(advancementEntry.id(), fixedAdvancement));
                        } else {
                            advancementsToEarn.add(advancementEntry);
                        }
                    }

                    original.call(connection, new AdvancementUpdateS2CPacket(advancementPacket.shouldClearCurrent(), advancementsToEarn, advancementPacket.getAdvancementIdsToRemove(), advancementPacket.getAdvancementsToProgress(), advancementPacket.shouldShowToast()), callbacks, flush);
                    return;
                }

                original.call(connection, packet, callbacks, flush);
            } else {
                original.call(connection, packet, callbacks, flush);
            }
        } else {
            original.call(connection, packet, callbacks, flush);
        }
    }
}
