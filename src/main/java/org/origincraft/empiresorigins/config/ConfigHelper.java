package org.origincraft.empiresorigins.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;

public class ConfigHelper {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Config config;

    public static void serverLoad(MinecraftServer server) {
        config = new Config(server);
        reloadConfigs();
    }

    public static void reloadConfigs() {
        config.load();
    }
}
