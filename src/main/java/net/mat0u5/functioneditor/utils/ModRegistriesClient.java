package net.mat0u5.functioneditor.utils;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.mat0u5.functioneditor.command.ClientCommands;

public class ModRegistriesClient {
    public static void registerModStuff() {
        registerCommands();
    }
    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);
    }
}
