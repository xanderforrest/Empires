package org.origincraft.empiresorigins.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockInteraction {

    public static boolean breakBlock(World world, PlayerEntity p, BlockPos pos, BlockState state, BlockEntity tile) {

        if (world.isClient || p.isSpectator()) return true;

        ServerPlayerEntity player = (ServerPlayerEntity) p;
        player.sendMessage(new LiteralText("This block is locked with a magical spell"), false);

        return false;

    }

}
