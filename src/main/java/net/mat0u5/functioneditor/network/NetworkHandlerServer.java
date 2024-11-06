package net.mat0u5.functioneditor.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class NetworkHandlerServer {

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FunctionDataPayload.ID, FunctionDataPayload.CODEC);
    }
    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(FunctionDataPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> {
                System.out.println("Server received custom packet (from "+player.getNameForScoreboard()+"): " + payload.lines().toString());
            });
        });
    }
    public static void sendFunctionDataToClient(ServerPlayerEntity player, String sendType, String function, List<String> lines) {
        FunctionDataPayload payload = new FunctionDataPayload(sendType, function, lines);
        System.out.println("Sending data to client: " + payload.lines().toString());
        ServerPlayNetworking.send(player, payload);
    }
}