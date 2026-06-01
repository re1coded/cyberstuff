package ru.re1coded.cyberstuff.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ActivateImplantPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActivateImplantPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("cyberstuff", "activate_implant"));

    public static final StreamCodec<ByteBuf, ActivateImplantPacket> STREAM_CODEC =
            StreamCodec.unit(new ActivateImplantPacket());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
