package net.mat0u5.functioneditor.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.functioneditor.network.NetworkHandler;
import net.mat0u5.functioneditor.utils.FunctionDataGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;


public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("test")
                .executes(context -> Command.execute(
                    context.getSource())
                )
        );
    }
    public static int execute(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        String functionData = FunctionDataGetter.getFunctionDataJson();
        NetworkHandler.sendFunctionDataToClient(source.getPlayer(), "send","dom:test2", List.of(functionData));

        self.sendMessage(Text.of("Command Worked!"));
        return 1;
    }
}
