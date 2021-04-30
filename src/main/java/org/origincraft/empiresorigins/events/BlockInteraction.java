package org.origincraft.empiresorigins.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.origincraft.empiresorigins.config.EmpireHelper;

public class BlockInteraction {

    public static boolean breakBlock(World world, PlayerEntity p, BlockPos pos, BlockState state, BlockEntity tile) {

        if (world.isClient || p.isSpectator()) return true;

        ServerPlayerEntity player = (ServerPlayerEntity) p;

        try {
            EmpireHelper.getChunkEmpire(new ChunkPos(pos));
            player.sendMessage(new LiteralText("This block is locked with a magical spell"), false);
            return false;
        } catch (Exception e) {
            return true;
        }

    }

}
