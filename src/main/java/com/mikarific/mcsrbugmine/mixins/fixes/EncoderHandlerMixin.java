package com.mikarific.mcsrbugmine.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mikarific.mcsrbugmine.MCSRBugMine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.handler.EncoderHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EncoderHandler.class)
public class EncoderHandlerMixin<T extends PacketListener> {
    @SuppressWarnings({"CatchMayIgnoreException", "CallToPrintStackTrace"})
    @WrapMethod(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;Lio/netty/buffer/ByteBuf;)V")
    private void preventPacketDisconnect(ChannelHandlerContext channelHandlerContext, Packet<T> packet, ByteBuf byteBuf, Operation<Void> original) {
        try {
            original.call(channelHandlerContext, packet, byteBuf);
        } catch (Exception ignored) {
            if (MCSRBugMine.config.preventPacketDisconnect) {
                ignored.printStackTrace();
            } else {
                throw ignored;
            }
        }
    }
}
