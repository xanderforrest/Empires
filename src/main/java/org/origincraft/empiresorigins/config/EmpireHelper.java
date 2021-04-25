package org.origincraft.empiresorigins.config;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

public class EmpireHelper {

    public static Empire getEmpireByPlayer(ServerPlayerEntity player) throws Exception {

        HashMap<String, Empire> empires = Config.getEmpires();

        for (Empire e : empires.values()) {
            if (e.members.contains(player.getUuid())) {
                return e;
            }
        }
        throw new Exception("Player does not belong to an Empire");
    }

    public static Empire getEmpireByName(String name) throws Exception {

        HashMap<String, Empire> empires = Config.getEmpires();

        if (empires.containsKey(name)) {
            return empires.get(name);
        } else {
            throw new Exception("That Empire does not exist");
        }
    }

}
