package org.origincraft.empiresorigins.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    private static HashMap<String, Empire> empires;
    private static File config;

    public static void onStart(MinecraftServer minecraftServer) {

        File configDir = FabricLoader.getInstance().getConfigDir().resolve("eo").toFile();
        try {
            if (!configDir.exists())
                configDir.mkdirs();
            config = new File(configDir, "eo_config.json");
            if (config.exists()) {
                config.createNewFile();
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    public static void load() {
        String initialLoad = "";
        try {
            FileReader reader = new FileReader(config);

            StringBuilder sb = new StringBuilder(); // i hate java??!
            while (reader.read() != -1) {
                sb.append(reader.read());
            }
            initialLoad = sb.toString();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (initialLoad.equals("")) {
            empires = new HashMap<>();
        } else {
            empires = new HashMap<>();

            Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();

            // https://stackoverflow.com/questions/2779251/how-can-i-convert-json-to-a-hashmap-using-gson
            // shout out to the first reply which says don't do this

            ArrayList<String> serealisedEmpires = GSON.fromJson(initialLoad, type);

            for (String se : serealisedEmpires) {
                Empire e = GSON.fromJson(se, Empire.class);
                empires.put(e.name, e);
            }

        }

    }

    public static void save() {
        Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        ArrayList<String> serealisedEmpires = new ArrayList<>();
        for (Empire e : empires.values()) {
            String serealised = GSON.toJson(e);
            serealisedEmpires.add(serealised);
        }

        String finalSave = GSON.toJson(serealisedEmpires);

        try {
            FileWriter writer = new FileWriter(config);
            writer.write(finalSave);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addEmpire(Empire e) {
        empires.put(e.name, e);
    }

    public static void removeEmpire(Empire e) {
        empires.remove(e.name);
    }

    public static HashMap<String, Empire> getEmpires() {
        return empires;
    }

}

