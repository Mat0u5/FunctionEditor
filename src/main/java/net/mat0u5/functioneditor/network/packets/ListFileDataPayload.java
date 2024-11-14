package net.mat0u5.functioneditor.network.packets;

import net.mat0u5.functioneditor.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ListFileDataPayload(List<String> packetInfo, List<FileDataPayload> files) implements CustomPayload {

    public static final CustomPayload.Id<ListFileDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "list_file_data"));
    public static final PacketCodec<RegistryByteBuf, ListFileDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList()), ListFileDataPayload::packetInfo,
            FileDataPayload.CODEC.collect(PacketCodecs.toList()), ListFileDataPayload::files,
            ListFileDataPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static ListFileDataPayload getFromFiles(UUID requestId, String requestInfo, File[] files) {
        List<FileDataPayload> listFilePayload = new ArrayList<>();
        for (File file : files) {
            try {
                file = file.getCanonicalFile();
            } catch (Exception e){}
            FileDataPayload fileDataPayload = FileDataPayload.getFromFile(requestId, requestInfo, file);
            listFilePayload.add(fileDataPayload);
        }

        return new ListFileDataPayload(List.of(requestId.toString(), requestInfo), listFilePayload);
    }
}