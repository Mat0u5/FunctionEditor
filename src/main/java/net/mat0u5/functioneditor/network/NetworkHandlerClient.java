package net.mat0u5.functioneditor.network;

import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class NetworkHandlerClient {

    public static void registerPackets() {
        //Register the same packets as the server
        //NetworkHandlerServer.registerPackets();
    }
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                System.out.println("Client received custom packet: " + payload.lines().toString());
                GuiBase.openGui(new GuiFileBrowser());
            });
        });
    }
    public static void sendFunctionDataToServer(String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        System.out.println("Sending data to server: " + payload.lines().toString());
        ClientPlayNetworking.send(payload);
    }
}
