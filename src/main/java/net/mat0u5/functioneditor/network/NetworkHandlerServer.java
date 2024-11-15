package net.mat0u5.functioneditor.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.functioneditor.Main;
import net.mat0u5.functioneditor.files.DataManagerServer;
import net.mat0u5.functioneditor.files.FileFilters;
import net.mat0u5.functioneditor.network.packets.FileDataPayload;
import net.mat0u5.functioneditor.network.packets.FunctionDataPayload;
import net.mat0u5.functioneditor.network.packets.ListFileDataPayload;
import net.mat0u5.functioneditor.network.packets.RequestDataPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkHandlerServer {

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FileDataPayload.ID, FileDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ListFileDataPayload.ID, ListFileDataPayload.CODEC);

        //PayloadTypeRegistry.playC2S().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestDataPayload.ID, RequestDataPayload.CODEC);
    }
    public static void registerServerReceiver() {
        /*
        ServerPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> {
                System.out.println("Server received custom packet (from "+player.getNameForScoreboard()+"): " + payload.lines().toString());
            });
        });
        */
        ServerPlayNetworking.registerGlobalReceiver(RequestDataPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> {
                Main.LOGGER.info("[PACKET] Server received packet request (from "+player.getNameForScoreboard()+"): ("+payload.requestInfo()+"), ("+String.join("__", payload.additionalInfo())+")");
                handlePacketRequest(player, payload);
            });
        });
    }
    public static void sendFunctionDataToClient(ServerPlayerEntity player, String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        Main.LOGGER.info("[PACKET] Sending data to client: " + payload.lines().toString());
        ServerPlayNetworking.send(player, payload);
    }
    public static void handlePacketRequest(ServerPlayerEntity player, RequestDataPayload payload) {
        UUID requestId = UUID.fromString(payload.requestUUID());
        String requestInfo = payload.requestInfo();
        List<String> additionalInfo = payload.additionalInfo();
        if (additionalInfo.isEmpty()) return;
        if (requestInfo.equalsIgnoreCase("file_data") ||
            requestInfo.equalsIgnoreCase("file_data_getparent")) {
            String filePath = additionalInfo.get(0);
            File file = new File(filePath);
            if (filePath.equalsIgnoreCase("root")) {
                file = DataManagerServer.getRootDirectory();
            }
            else {
                if (requestInfo.equalsIgnoreCase("file_data_getparent")) {
                    file = file.getParentFile();
                }
                try {
                    file = file.getCanonicalFile();
                } catch (Exception e){}
            }
            FileDataPayload fileDataPayload = FileDataPayload.getFromFile(requestId, requestInfo, file);
            ServerPlayNetworking.send(player, fileDataPayload);
        }
        else if (requestInfo.equalsIgnoreCase("file_list_dir") ||
                requestInfo.equalsIgnoreCase("file_list_files")) {
            FileFilter filter = requestInfo.equalsIgnoreCase("file_list_dir") ? FileFilters.FILE_FILTER_DIRECTORIES : FileFilters.FILE_FILTER_SUPPORTED;
            String filePath = additionalInfo.get(0);
            File mainFile = new File(filePath);
            File[] files = mainFile.listFiles(filter);
            if (files == null) return;
            ListFileDataPayload listFileDataPayload = ListFileDataPayload.getFromFiles(requestId, requestInfo,files);
            ServerPlayNetworking.send(player, listFileDataPayload);
        }
        else if (requestInfo.equalsIgnoreCase("create_dir")) {
            if (additionalInfo.size() < 2) return;

            String currentDirPath = additionalInfo.get(0);
            String dirName = additionalInfo.get(1);
            File dir = new File(currentDirPath+"\\"+dirName);
            if (dir.exists()) return;
            if (!dir.mkdirs()) return;

            try {
                dir = dir.getCanonicalFile();
            } catch (Exception e){}
            FileDataPayload fileDataPayload = FileDataPayload.getFromFile(requestId, requestInfo, dir);
            ServerPlayNetworking.send(player, fileDataPayload);
        }
    }
}