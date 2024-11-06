package net.mat0u5.functioneditor.network;

import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class NetworkHandler {

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                System.out.println("Client received custom packet: " + payload.lines().toString());
                //client.setScreen(new FunctionEditScreen(payload.sendInfo(), payload.function(), payload.lines()));
                GuiBase.openGui(new GuiFileBrowser());
            });
        });
    }
    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> {
                System.out.println("Server received custom packet (from "+player.getNameForScoreboard()+"): " + payload.lines().toString());
                //server.setScreen(new FunctionEditScreen(payload.sendInfo(), payload.function(), payload.lines()));
            });
        });
    }
    public static void sendFunctionDataToClient(ServerPlayerEntity player, String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        System.out.println("Sending data to client: " + payload.lines().toString());
        ServerPlayNetworking.send(player, payload);
    }
    public static void sendFunctionDataToServer(String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        System.out.println("Sending data to server: " + payload.lines().toString());
        ClientPlayNetworking.send(payload);
    }

    /*
    public static void sendEditedFunctionDataToServer(String editedFunctionDataJson) {
        EditedFunctionDataPayload payload = new EditedFunctionDataPayload(editedFunctionDataJson);
        ClientPlayNetworking.send(payload);
    }
    */
}