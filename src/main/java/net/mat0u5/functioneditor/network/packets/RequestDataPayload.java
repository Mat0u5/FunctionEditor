package net.mat0u5.functioneditor.network.packets;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record RequestDataPayload(String requestUUID, String requestInfo, List<String> additionalInfo) implements CustomPayload {

    public static final CustomPayload.Id<RequestDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "request_data"));
    public static final PacketCodec<RegistryByteBuf, RequestDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, RequestDataPayload::requestUUID,
            PacketCodecs.STRING, RequestDataPayload::requestInfo,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), RequestDataPayload::additionalInfo,
            RequestDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}