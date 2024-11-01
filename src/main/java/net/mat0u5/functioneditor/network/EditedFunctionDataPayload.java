package net.mat0u5.functioneditor.network;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EditedFunctionDataPayload(String editedDataJson) implements CustomPayload {

    public static final CustomPayload.Id<EditedFunctionDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "edited_function_data"));
    public static final PacketCodec<RegistryByteBuf, EditedFunctionDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, EditedFunctionDataPayload::editedDataJson
            , EditedFunctionDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}