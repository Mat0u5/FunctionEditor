package net.mat0u5.functioneditor.network;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestDataPayload(String requestInfo) implements CustomPayload {

    public static final CustomPayload.Id<RequestDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "request_data"));
    public static final PacketCodec<RegistryByteBuf, RequestDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, RequestDataPayload::requestInfo,
            RequestDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}