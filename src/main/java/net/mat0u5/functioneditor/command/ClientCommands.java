package net.mat0u5.functioneditor.command;


import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.mat0u5.functioneditor.Main;
import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.network.NetworkHandlerClient;
import net.mat0u5.functioneditor.network.packets.FileDataPayload;
import net.mat0u5.functioneditor.utils.FunctionDataGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;


public class ClientCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
            ClientCommandManager.literal("testclient")
                .executes(context -> ClientCommands.execute(
                    context.getSource())
                )
        );
    }

    public static int execute(FabricClientCommandSource source)  {
        final PlayerEntity self = source.getPlayer();
        String functionData = FunctionDataGetter.getFunctionDataJson();
        //NetworkHandlerClient.sendFunctionDataToServer("send","tefs", List.of(functionData));
        NetworkHandlerClient.requestServerDataAsync("file_data","root").thenAccept(payload -> {
            if (payload instanceof FileDataPayload) {
                FileDataPayload fileData = (FileDataPayload) payload;
                ClientFile file = new ClientFile(fileData);
                Main.LOGGER.info("Received data: " + file.getName());
                Main.LOGGER.info("Received data: " + file.getAbsolutePath());
                Main.LOGGER.info("Received data: " + file.isFile());
            }
        });
        self.sendMessage(Text.of("Test 1"));
        self.sendMessage(Text.of("Command Worked!"));
        return 1;
    }
}
