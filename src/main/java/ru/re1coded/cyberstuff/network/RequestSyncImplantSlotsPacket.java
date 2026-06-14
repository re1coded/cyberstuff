package ru.re1coded.cyberstuff.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import ru.re1coded.cyberstuff.CyberStuff;

public record RequestSyncImplantSlotsPacket() implements CustomPacketPayload {
    public static final Type<RequestSyncImplantSlotsPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(CyberStuff.MODID, "request_sync_implant_slots"));

    public static final StreamCodec<ByteBuf, RequestSyncImplantSlotsPacket> STREAM_CODEC =
            StreamCodec.unit(new RequestSyncImplantSlotsPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
