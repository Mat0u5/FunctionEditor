package net.mat0u5.functioneditor.network.packets;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.List;
import java.util.UUID;

public record FileDataPayload(
        List<String> packetInfo,
        String path, String name,
        List<Boolean> fileInfo
) implements CustomPayload {

    public static final CustomPayload.Id<FileDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "file_data"));
    public static final PacketCodec<RegistryByteBuf, FileDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList()), FileDataPayload::packetInfo,
            PacketCodecs.STRING, FileDataPayload::path,
            PacketCodecs.STRING, FileDataPayload::name,
            PacketCodecs.BOOL.collect(PacketCodecs.toList()), FileDataPayload::fileInfo,
            FileDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static FileDataPayload getFromFile(UUID requestId, String requestInfo, File file) {
        FileDataPayload fileDataPayload = new FileDataPayload(
                List.of(requestId.toString(), requestInfo),
                file.getPath(),file.getName(),
                List.of(file.canRead(), file.isFile(), file.exists(), file.isDirectory())
        );
        return fileDataPayload;
    }
}