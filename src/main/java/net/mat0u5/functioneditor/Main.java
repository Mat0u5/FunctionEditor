package net.mat0u5.functioneditor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.mat0u5.functioneditor.config.ConfigManager;
import net.mat0u5.functioneditor.utils.ModRegistries;
import net.mat0u5.functioneditor.network.NetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main implements ModInitializer {
	public static final String MOD_ID = "functioneditor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager config;

	@Override
	public void onInitialize() {
		LOGGER.info("[SERVER] Initializing Function Editor...");
		ModRegistries.registerModStuff();
		NetworkHandler.registerPackets();
		NetworkHandler.registerServerReceiver();
	}


}