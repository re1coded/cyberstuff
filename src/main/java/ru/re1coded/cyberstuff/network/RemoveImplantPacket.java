package ru.re1coded.cyberstuff.network;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import ru.re1coded.cyberstuff.CyberStuff;

public record RemoveImplantPacket(int slotIndex, InteractionHand hand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RemoveImplantPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CyberStuff.MODID, "remove_implant"));

    public static final StreamCodec<ByteBuf, RemoveImplantPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, RemoveImplantPacket::slotIndex,
                    ByteBufCodecs.fromCodec(
                            Codec.STRING.xmap(InteractionHand::valueOf, InteractionHand::name)
                    ), RemoveImplantPacket::hand,
                    RemoveImplantPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
