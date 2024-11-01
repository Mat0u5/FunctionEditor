package net.mat0u5.functioneditor.network;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FunctionDataPayload(String dataJson) implements CustomPayload {

    public static final CustomPayload.Id<FunctionDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "function_data"));
    public static final PacketCodec<RegistryByteBuf, FunctionDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, FunctionDataPayload::dataJson
            , FunctionDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}