package net.mat0u5.functioneditor;

import net.fabricmc.api.ModInitializer;

import net.mat0u5.functioneditor.config.ConfigManager;
import net.mat0u5.functioneditor.utils.ModRegistries;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main implements ModInitializer {
	public static final String MOD_ID = "functioneditor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager config;
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		config = new ConfigManager("./config/"+MOD_ID+".properties");

		ModRegistries.registerModStuff();
		LOGGER.info("Initializing Function Editor...");
	}
}