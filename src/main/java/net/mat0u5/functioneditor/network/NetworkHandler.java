package net.mat0u5.functioneditor.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.functioneditor.gui.FunctionEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;

public class NetworkHandler {

    // Register packets for server-to-client (S2C) and client-to-server (C2S)
    public static void registerPackets() {
        // Server to Client
        PayloadTypeRegistry.playS2C().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);

        // Client to Server
        PayloadTypeRegistry.playC2S().register(EditedFunctionDataPayload.ID, EditedFunctionDataPayload.CODEC);
    }

    // Client-side registration for handling incoming S2C payload
    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                // Open the client screen with received function data
                System.out.println("test0_"+payload);
                client.setScreen(new FunctionEditScreen(payload.dataJson()));
            });
        });
    }
    public static void sendFunctionDataToClient(ServerPlayerEntity player, String functionDataJson) {
        FunctionDataPayload payload = new FunctionDataPayload(functionDataJson);
        ServerPlayNetworking.send(player, payload);
    }
    public static void sendEditedFunctionDataToServer(String editedFunctionDataJson) {
        EditedFunctionDataPayload payload = new EditedFunctionDataPayload(editedFunctionDataJson);
        ClientPlayNetworking.send(payload);
    }
}