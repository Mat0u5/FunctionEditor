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
                Main.LOGGER.info("[PACKET] Server received packet request (from "+player.getNameForScoreboard()+"): ("+payload.requestInfo()+"), ("+payload.additionalInfo()+")");
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
        String requestInfo = payload.requestInfo();
        String additionalInfo = payload.additionalInfo();
        if (requestInfo.equalsIgnoreCase("file_data") ||
            requestInfo.equalsIgnoreCase("file_data_getparent")) {
            String filePath = additionalInfo;
            File file = new File(filePath);
            if (additionalInfo.equalsIgnoreCase("root")) {
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
            FileDataPayload fileDataPayload = FileDataPayload.getFromFile(requestInfo, file);
            ServerPlayNetworking.send(player, fileDataPayload);
        }
        else if (requestInfo.equalsIgnoreCase("file_list_dir") ||
                requestInfo.equalsIgnoreCase("file_list_files")) {
            FileFilter filter = requestInfo.equalsIgnoreCase("file_list_dir") ? FileFilters.FILE_FILTER_DIRECTORIES : FileFilters.FILE_FILTER_SUPPORTED;
            String filePath = additionalInfo;
            File mainFile = new File(filePath);
            File[] files = mainFile.listFiles(filter);

            if (files == null) return;
            ListFileDataPayload listFileDataPayload = ListFileDataPayload.getFromFiles(requestInfo,files);
            ServerPlayNetworking.send(player, listFileDataPayload);
        }
    }
}