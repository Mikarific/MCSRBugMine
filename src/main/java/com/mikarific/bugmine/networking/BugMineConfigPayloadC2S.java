package com.mikarific.bugmine.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BugMineConfigPayloadC2S(String option, String value) implements CustomPayload {
    public static final CustomPayload.Id<BugMineConfigPayloadC2S> ID = new CustomPayload.Id<>(Identifier.of("bugmine", "config"));
    public static final PacketCodec<RegistryByteBuf, BugMineConfigPayloadC2S> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BugMineConfigPayloadC2S::option, PacketCodecs.STRING, BugMineConfigPayloadC2S::value, BugMineConfigPayloadC2S::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}