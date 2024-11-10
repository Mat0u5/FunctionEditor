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
import java.util.concurrent.CompletableFuture;

public class NetworkHandlerClient {
    private static CompletableFuture<ClientFile> serverResponseFutureFile = new CompletableFuture<>();
    private static CompletableFuture<List<ClientFile>> serverResponseFutureListFile = new CompletableFuture<>();

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                System.out.println("[PACKET] Received function data payload");
                //GuiBase.openGui(new GuiFileBrowser());
                //serverResponseFuture.complete(payload);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(FileDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                ClientFile file = new ClientFile(payload);
                serverResponseFutureFile.complete(file);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ListFileDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                List<ClientFile> result = new ArrayList<>();
                List<FileDataPayload> filePayloads = payload.files();
                for (FileDataPayload filePayload : filePayloads) {
                    ClientFile file = new ClientFile(filePayload);
                    result.add(file);
                }
                serverResponseFutureListFile.complete(result);
            });
        });
    }
    public static void sendFunctionDataToServer(String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        System.out.println("Sending data to server: " + payload.lines().toString());
        ClientPlayNetworking.send(payload);
    }

    public static void sendPacketRequestToServer(String requestInfo, String additionalInfo) {
        RequestDataPayload payload = new RequestDataPayload(requestInfo, additionalInfo);
        ClientPlayNetworking.send(payload);
        Main.LOGGER.info("[PACKET] Requested packet ("+requestInfo+"), ("+additionalInfo+")");
    }

    public static CompletableFuture<ClientFile> requestServerFileAsync(String requestInfo, String additionalInfo) {
        sendPacketRequestToServer(requestInfo, additionalInfo);
        return serverResponseFutureFile;
    }
    public static CompletableFuture<List<ClientFile>> requestServerListFileAsync(String requestInfo, String additionalInfo) {
        sendPacketRequestToServer(requestInfo, additionalInfo);
        return serverResponseFutureListFile;
    }
}
