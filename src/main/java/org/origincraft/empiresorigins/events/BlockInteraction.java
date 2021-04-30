package org.origincraft.empiresorigins.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.origincraft.empiresorigins.config.Empire;
import org.origincraft.empiresorigins.config.EmpireHelper;

public class BlockInteraction {

    public static boolean breakBlock(World world, PlayerEntity p, BlockPos pos, BlockState state, BlockEntity tile) {

        if (world.isClient || p.isSpectator()) return true;

        ServerPlayerEntity player = (ServerPlayerEntity) p;


        Empire empire = EmpireHelper.getChunkEmpire(new ChunkPos(pos));
        if (empire != null) {
            if (empire.members.contains(player.getUuid())) {
                return true;
            } else {
                player.sendMessage(new LiteralText("This block is locked with a magical spell"), false);
                return false;
            }
        }
        return true;
    }

    public static ActionResult useBlocks(PlayerEntity p, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient) return ActionResult.PASS;

        ServerPlayerEntity player = (ServerPlayerEntity) p;
        Empire empire = EmpireHelper.getChunkEmpire(new ChunkPos(player.getBlockPos()));

        if (empire == null) return ActionResult.PASS;

        boolean emptyHand = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        boolean cancelBlockInteract = player.shouldCancelInteraction() && emptyHand;

        if (!cancelBlockInteract) {
            BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
            if (blockEntity != null) {
                if (blockEntity instanceof Inventory || blockEntity instanceof InventoryProvider) {
                    if (empire.members.contains(player.getUuid())) {
                        return ActionResult.PASS;
                    } else {
                        player.sendMessage(new LiteralText("This block is locked with a magical spell"), false);
                        return ActionResult.FAIL;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
