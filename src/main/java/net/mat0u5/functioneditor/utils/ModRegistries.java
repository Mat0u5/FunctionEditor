package net.mat0u5.functioneditor.utils;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.functioneditor.command.Command;
import net.mat0u5.functioneditor.events.Events;

public class ModRegistries {
    public static void registerModStuff() {
        registerCommands();
        registerEvents();
    }
    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(Command::register);
    }
    private static void registerEvents() {
        Events.register();
    }
}
