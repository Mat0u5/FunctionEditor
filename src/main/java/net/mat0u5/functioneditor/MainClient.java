package net.mat0u5.functioneditor;

import net.fabricmc.api.ClientModInitializer;
import net.mat0u5.functioneditor.network.NetworkHandler;
import net.mat0u5.functioneditor.utils.ModRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClient implements ClientModInitializer {
    public static final String MOD_ID = "functioneditor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitializeClient() {
        //config = new ConfigManager("./config/"+MOD_ID+".properties");
        LOGGER.info("[CLIENT] Initializing Function Editor...");
        NetworkHandler.registerClientPackets();
    }
}
