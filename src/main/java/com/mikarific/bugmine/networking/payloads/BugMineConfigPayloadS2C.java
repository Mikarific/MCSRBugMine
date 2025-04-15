package com.mikarific.bugmine.networking.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BugMineConfigPayloadS2C(String option, String value) implements CustomPayload {
    public static final CustomPayload.Id<BugMineConfigPayloadS2C> ID = new CustomPayload.Id<>(Identifier.of("bugmine", "config"));
    public static final PacketCodec<RegistryByteBuf, BugMineConfigPayloadS2C> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BugMineConfigPayloadS2C::option, PacketCodecs.STRING, BugMineConfigPayloadS2C::value, BugMineConfigPayloadS2C::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}