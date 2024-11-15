package net.mat0u5.functioneditor.network;

import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.functioneditor.Main;
import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.mat0u5.functioneditor.network.packets.FileDataPayload;
import net.mat0u5.functioneditor.network.packets.FunctionDataPayload;
import net.mat0u5.functioneditor.network.packets.ListFileDataPayload;
import net.mat0u5.functioneditor.network.packets.RequestDataPayload;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkHandlerClient {
    //private static final Map<UUID, CompletableFuture<Void>> serverResponseVoidMap = new ConcurrentHashMap<>();
    private static final Map<UUID, CompletableFuture<ClientFile>> serverResponseFileMap = new ConcurrentHashMap<>();
    private static final Map<UUID, CompletableFuture<List<ClientFile>>> serverResponseListFileMap = new ConcurrentHashMap<>();

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                Main.LOGGER.info("[PACKET] Received function data payload");
                //GuiBase.openGui(new GuiFileBrowser());
                //serverResponseFuture.complete(payload);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(FileDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                Main.LOGGER.info("[PACKET] Received ClientFile");
                ClientFile file = new ClientFile(payload);
                UUID requestId = UUID.fromString(payload.packetInfo().get(0)); // Get the unique request ID from the payload
                CompletableFuture<ClientFile> future = serverResponseFileMap.remove(requestId);
                if (future != null) {
                    future.complete(file);
                } else {
                    Main.LOGGER.warn("Received FileDataPayload with unknown request ID: " + requestId);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ListFileDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                Main.LOGGER.info("[PACKET] Received List<ClientFile>");
                List<ClientFile> result = new ArrayList<>();
                List<FileDataPayload> filePayloads = payload.files();
                for (FileDataPayload filePayload : filePayloads) {
                    ClientFile file = new ClientFile(filePayload);
                    result.add(file);
                }
                UUID requestId = UUID.fromString(payload.packetInfo().get(0)); // Get the unique request ID from the payload
                CompletableFuture<List<ClientFile>> future = serverResponseListFileMap.remove(requestId);
                if (future != null) {
                    future.complete(result);
                } else {
                    Main.LOGGER.warn("Received ListFileDataPayload with unknown request ID: " + requestId);
                }
            });
        });
    }
    public static void sendFunctionDataToServer(String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        Main.LOGGER.info("Sending data to server: " + payload.lines().toString());
        ClientPlayNetworking.send(payload);
    }

    //public static CompletableFuture<Void> sendReturnablePacketRequestToServer(UUID requestId, String requestInfo, List<String> additionalInfo) {
    //    RequestDataPayload payload = new RequestDataPayload(requestId.toString(), requestInfo, additionalInfo); // Include request ID
    //    CompletableFuture<Void> future = new CompletableFuture<>();
    //    serverResponseVoidMap.put(requestId, future);
    //    ClientPlayNetworking.send(payload);
    //    Main.LOGGER.info("[PACKET] Requested returnable packet (" + requestInfo + "), (" + additionalInfo + ") with ID: " + requestId);
    //}

    public static void sendPacketRequestToServer(UUID requestId, String requestInfo, List<String> additionalInfo) {
        RequestDataPayload payload = new RequestDataPayload(requestId.toString(), requestInfo, additionalInfo); // Include request ID
        ClientPlayNetworking.send(payload);
        Main.LOGGER.info("[PACKET] Requested packet (" + requestInfo + "), (" + additionalInfo + ") with ID: " + requestId);
    }

    public static CompletableFuture<ClientFile> requestServerFileAsync(String requestInfo, List<String> additionalInfo) {
        UUID requestId = UUID.randomUUID(); // Generate unique ID for the request
        CompletableFuture<ClientFile> future = new CompletableFuture<>();
        serverResponseFileMap.put(requestId, future);
        sendPacketRequestToServer(requestId, requestInfo, additionalInfo);
        return future;
    }

    public static CompletableFuture<List<ClientFile>> requestServerListFileAsync(String requestInfo, List<String> additionalInfo) {
        UUID requestId = UUID.randomUUID(); // Generate unique ID for the request
        CompletableFuture<List<ClientFile>> future = new CompletableFuture<>();
        serverResponseListFileMap.put(requestId, future);
        sendPacketRequestToServer(requestId, requestInfo, additionalInfo);
        return future;
    }
}
