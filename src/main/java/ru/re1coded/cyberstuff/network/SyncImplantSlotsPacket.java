package ru.re1coded.cyberstuff.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.data.ImplantSlots;

public record SyncImplantSlotsPacket(ImplantSlots slots) implements CustomPacketPayload {

    public static final Type<SyncImplantSlotsPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(CyberStuff.MODID, "sync_implant_slots"));

    public static final StreamCodec<ByteBuf, SyncImplantSlotsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(ImplantSlots.CODEC), SyncImplantSlotsPacket::slots,
                    SyncImplantSlotsPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
