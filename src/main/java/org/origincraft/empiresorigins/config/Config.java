package org.origincraft.empiresorigins.config;


import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.origincraft.empiresorigins.EmpiresOrigins.log;


public class Config {

    private static HashMap<String, Empire> empires;
    private static File config;
    public static MinecraftServer server;

    public Config(MinecraftServer minecraftServer) {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve("eo").toFile();
        server = minecraftServer;

        try {
            if (!configDir.exists())
                configDir.mkdirs();
            config = new File(configDir, "eo_config.json");
            if (!config.exists()) {
                config.createNewFile();
                //save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void load() {
        empires = new HashMap<String, Empire>();
        JsonArray jsonEmpires = new JsonArray();
        try {
            FileReader reader = new FileReader(config);

            if (reader.ready()) {
                jsonEmpires = ConfigHelper.GSON.fromJson(reader, JsonArray.class);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (JsonElement empireElement : jsonEmpires) {
            JsonObject jsonEmpire = empireElement.getAsJsonObject();
            String name = jsonEmpire.get("name").getAsString();

            List<UUID> members = new ArrayList<>();
            for (JsonElement memberElement : jsonEmpire.get("members").getAsJsonArray()) {
                members.add(UUID.fromString(memberElement.getAsString()));
            }

            List<UUID> invited = new ArrayList<>();
            for (JsonElement invitedElement : jsonEmpire.get("invited").getAsJsonArray()) {
                invited.add(UUID.fromString(invitedElement.getAsString()));
            }

            Empire empire = new Empire(members.get(0), name);
            empire.members = members;
            empire.invited = invited;

            JsonArray homeCoords = jsonEmpire.get("homeCoords").getAsJsonArray();
            empire.home = new Vec3d(homeCoords.get(0).getAsDouble(),
                    homeCoords.get(1).getAsDouble(), homeCoords.get(2).getAsDouble());

            empire.homeWorld = server.getWorld(RegistryKey.of(Registry.DIMENSION,
                    new Identifier(jsonEmpire.get("homeWorld").getAsString()))); // Registry#DIMENSION may be WORLD_KEY

            empires.put(empire.name, empire);
        }

    }

    public static void save() {
        JsonArray jsonEmpires = new JsonArray();

        for (Empire e : empires.values()) {
            JsonObject jsonEmpire = new JsonObject();
            JsonArray members = new JsonArray();
            for (UUID uuid : e.members) {
                members.add(uuid.toString());
            }

            JsonArray invited = new JsonArray();
            for (UUID uuid : e.invited) {
                invited.add(uuid.toString());
            }

            JsonArray homeCoords = new JsonArray(); // TODO make sure people can save/load empires with no homes
            homeCoords.add(e.home.x);
            homeCoords.add(e.home.y);
            homeCoords.add(e.home.z);

            jsonEmpire.addProperty("name", e.name);
            jsonEmpire.add("members", members);
            jsonEmpire.add("invited", invited);
            jsonEmpire.add("homeCoords", homeCoords);

            String worldIdentifier = e.homeWorld.getRegistryKey().getValue().toString();
            jsonEmpire.addProperty("homeWorld", worldIdentifier);

            jsonEmpires.add(jsonEmpire);
        }

        String finalSave = ConfigHelper.GSON.toJson(jsonEmpires);

        try {
            FileWriter writer = new FileWriter(config);
            writer.write(finalSave);
            writer.close();
        } catch (IOException e) {
            log(e.toString());
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

