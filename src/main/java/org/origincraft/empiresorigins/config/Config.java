package org.origincraft.empiresorigins.config;


import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {

    private static HashMap<String, Empire> empires;

    public static void onStart(MinecraftServer minecraftServer) {
        empires = new HashMap<>();
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

