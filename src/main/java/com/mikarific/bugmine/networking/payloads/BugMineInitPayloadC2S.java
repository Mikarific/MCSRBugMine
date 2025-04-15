package com.mikarific.bugmine.networking.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BugMineInitPayloadC2S(String version) implements CustomPayload {
    public static final CustomPayload.Id<BugMineInitPayloadC2S> ID = new CustomPayload.Id<>(Identifier.of("bugmine", "init"));
    public static final PacketCodec<RegistryByteBuf, BugMineInitPayloadC2S> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BugMineInitPayloadC2S::version, BugMineInitPayloadC2S::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}