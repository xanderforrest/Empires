package org.origincraft.empiresorigins.config;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Empire {
    public String name;
    public Vec3d home;
    public ServerWorld homeWorld;
    public List<UUID> members;
    public List<UUID> invited;

    public Empire(ServerPlayerEntity creator, String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.members.add(creator.getUuid());
        this.invited = new ArrayList<>();
    }

}

