package net.mat0u5.functioneditor.network;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record FunctionDataPayload(String sendInfo, String function, List<String> lines) implements CustomPayload {

    public static final CustomPayload.Id<FunctionDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "function_data"));
    public static final PacketCodec<RegistryByteBuf, FunctionDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, FunctionDataPayload::sendInfo,
            PacketCodecs.STRING, FunctionDataPayload::function,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), FunctionDataPayload::lines,
            FunctionDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}