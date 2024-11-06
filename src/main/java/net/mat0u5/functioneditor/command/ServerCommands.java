package net.mat0u5.functioneditor.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.functioneditor.network.NetworkHandlerServer;
import net.mat0u5.functioneditor.utils.FunctionDataGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;


public class ServerCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("testserver")
                .executes(context -> ServerCommands.execute(
                    context.getSource())
                )
        );
    }
    public static int execute(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        ServerPlayerEntity testPlayer = server.getPlayerManager().getPlayerList().get(0);
        if (testPlayer == null) return -1;

        String functionData = FunctionDataGetter.getFunctionDataJson();
        NetworkHandlerServer.sendFunctionDataToClient(testPlayer, "send","dom:test2", List.of(functionData));

        source.sendMessage(Text.of("Test 2"));
        source.sendMessage(Text.of("Command Worked!"));
        return 1;
    }
}
