package net.mat0u5.functioneditor.events;



import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.functioneditor.network.NetworkHandlerServer;
import net.mat0u5.functioneditor.utils.FunctionDataGetter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class Events {

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Events.onPlayerJoin(server, handler.getPlayer()));
    }
    private static void onServerStopping(MinecraftServer server) {
    }
    private static void onServerStart(MinecraftServer server) {
    }
    static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        try {
            if (player.hasPermissionLevel(2)) {
                /*String functionData = FunctionDataGetter.getFunctionDataJson();
                NetworkHandlerServer.sendFunctionDataToClient(player, "send","dom:test", List.of(functionData));*/

            }
        }catch (Exception e) {}
    }
}
