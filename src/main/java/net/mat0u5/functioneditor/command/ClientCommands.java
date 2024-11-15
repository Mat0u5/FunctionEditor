package net.mat0u5.functioneditor.command;


import com.mojang.brigadier.CommandDispatcher;
import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.mat0u5.functioneditor.Main;
import net.mat0u5.functioneditor.MainClient;
import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.files.DataManagerClient;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.mat0u5.functioneditor.network.NetworkHandlerClient;
import net.mat0u5.functioneditor.network.packets.FileDataPayload;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;


public class ClientCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
            ClientCommandManager.literal("files")
                .executes(context -> ClientCommands.execute(
                    context.getSource())
                )
        );
    }

    public static int execute(FabricClientCommandSource source)  {
        final PlayerEntity self = source.getPlayer();
        if (!DataManagerClient.hasRootDirectory()) {
            NetworkHandlerClient.requestServerFileAsync("file_data", List.of("root")).thenAccept(payload -> {
                DataManagerClient.setClientRootDirectory(payload);
                //GuiBase.openGui(MainClient.guiFileBrowser);
                GuiBase.openGui(new GuiFileBrowser());
            });
        }
        else {
            //GuiBase.openGui(MainClient.guiFileBrowser);
            GuiBase.openGui(new GuiFileBrowser());
        }
        self.sendMessage(Text.of("Command Worked!"));
        return 1;
    }
}
