package com.mikarific.bugmine.networking.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BugMineInitPayloadS2C(String version) implements CustomPayload {
    public static final CustomPayload.Id<BugMineInitPayloadS2C> ID = new CustomPayload.Id<>(Identifier.of("bugmine", "init"));
    public static final PacketCodec<RegistryByteBuf, BugMineInitPayloadS2C> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BugMineInitPayloadS2C::version, BugMineInitPayloadS2C::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}