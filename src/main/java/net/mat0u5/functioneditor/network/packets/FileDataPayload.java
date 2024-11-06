package net.mat0u5.functioneditor.network.packets;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record FileDataPayload(
        String packetInfo,
        String path, String name,
        List<Boolean> fileInfo
) implements CustomPayload {

    public static final CustomPayload.Id<FileDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "file_data"));
    public static final PacketCodec<RegistryByteBuf, FileDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, FileDataPayload::packetInfo,
            PacketCodecs.STRING, FileDataPayload::path,
            PacketCodecs.STRING, FileDataPayload::name,
            PacketCodecs.BOOL.collect(PacketCodecs.toList()), FileDataPayload::fileInfo,
            FileDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}