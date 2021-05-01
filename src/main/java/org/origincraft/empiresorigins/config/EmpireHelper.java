package org.origincraft.empiresorigins.config;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;

public class EmpireHelper {
    public static Empire getEmpireByPlayer(ServerPlayerEntity player) {

        HashMap<String, Empire> empires = Config.getEmpires();

        for (Empire e : empires.values()) {
            if (e.members.contains(player.getUuid())) {
                return e;
            }
        }
        return null;
    }

    public static Empire getEmpireByName(String name) {

        HashMap<String, Empire> empires = Config.getEmpires();

        if (empires.containsKey(name)) {
            return empires.get(name);
        } else {
            return null;
        }
    }

    public static Empire getChunkEmpire(ChunkPos chunk) {
        for (Empire e : Config.getEmpires().values()) {
            for (ChunkPos c : e.claims) {
                if (chunk.equals(c)) {
                    return e;
                }
            }
        }
        return null;
    }


}
