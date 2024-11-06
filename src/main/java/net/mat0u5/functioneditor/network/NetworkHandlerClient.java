package net.mat0u5.functioneditor.network;

import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.functioneditor.Main;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.mat0u5.functioneditor.network.packets.FileDataPayload;
import net.mat0u5.functioneditor.network.packets.FunctionDataPayload;
import net.mat0u5.functioneditor.network.packets.ListFileDataPayload;
import net.mat0u5.functioneditor.network.packets.RequestDataPayload;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetworkHandlerClient {
    private static CompletableFuture<Object> serverResponseFuture = new CompletableFuture<>();

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                System.out.println("[PACKET] Received function data payload");
                //GuiBase.openGui(new GuiFileBrowser());
                serverResponseFuture.complete(payload);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(FileDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                serverResponseFuture.complete(payload);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ListFileDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                serverResponseFuture.complete(payload);
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

    public static CompletableFuture<Object> requestServerDataAsync(String requestInfo, String additionalInfo) {
        sendPacketRequestToServer(requestInfo, additionalInfo);
        return serverResponseFuture;
    }
}
