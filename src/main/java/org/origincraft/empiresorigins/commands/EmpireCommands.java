package org.origincraft.empiresorigins.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.origincraft.empiresorigins.config.Config;
import org.origincraft.empiresorigins.config.Empire;
import org.origincraft.empiresorigins.config.EmpireHelper;

import static org.origincraft.empiresorigins.EmpiresOrigins.log;

public class EmpireCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("empire")
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(EmpireCommands::createEmpire)))
                .then(CommandManager.literal("info")
                        .executes(EmpireCommands::info))
                .then(CommandManager.literal("invite")
                        .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                        .executes(EmpireCommands::invitePlayerToEmpire)))
                .then(CommandManager.literal("join")
                        .then(CommandManager.argument("empire", StringArgumentType.string())
                                .executes(EmpireCommands::joinEmpire)))
                .then(CommandManager.literal("sethome")
                        .executes(EmpireCommands::setEmpireHome))
                .then(CommandManager.literal("home")
                        .executes(EmpireCommands::empireHome))
                        .then(CommandManager.literal("save")
                                .executes(EmpireCommands::saveConfig))
                .then(CommandManager.literal("claim")
                        .executes(EmpireCommands::claim))
                );
    }

    public static int claim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ChunkPos chunk = new ChunkPos(player.getBlockPos());
        Empire empire = EmpireHelper.getEmpireByPlayer(player);

        if (empire == null) {
            player.sendMessage(new LiteralText("You need to be in an Empire to claim land."), false);
            return 1;
        }

        Empire chunkEmpire = EmpireHelper.getChunkEmpire(chunk);
        if (chunkEmpire != null) {
            if (chunkEmpire.name.equals(empire.name)) {
                player.sendMessage(new LiteralText("Your Empire has already claimed this area!"), false);
            } else {
                player.sendMessage(new LiteralText(chunkEmpire.name + " controls this area already!"), false);
            }
            return 1;
        }

        empire.addClaim(chunk);
        Config.addEmpire(empire);

        player.sendMessage(new LiteralText("You claimed this chunk for " + empire.name), false);


        return 1;
    }

    public static int saveConfig(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        log("Running save...");
        player.sendMessage(new LiteralText("Saving plugins configuration manually..."), false);
        try {
            Config.save();
            player.sendMessage(new LiteralText("Configuration saved."), false);
            return 1;
        } catch (Error e) {
            log(e.toString());
            return -1;
        }
    }

    public static int setEmpireHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Empire empire = EmpireHelper.getEmpireByPlayer(player);
        Empire chunkEmpire = EmpireHelper.getChunkEmpire(new ChunkPos(player.getBlockPos()));

        if (empire == null) {
            player.sendMessage(new LiteralText("You need to be in your Empire to set its home."), false);
            return 1;
        }

        if (chunkEmpire != null) {
            if (!chunkEmpire.equals(empire)) {
                player.sendMessage(new LiteralText("You can't set your Empire's home in another Empire's land."), false);
                return 1;
            }
        }

        empire.home = player.getPos();
        empire.homeWorld = player.getServerWorld();
        Config.addEmpire(empire);

        player.sendMessage(new LiteralText("Your Empire's home has been set to your location."), false);



        return 1;
    }

    public static int empireHome(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        Empire empire = EmpireHelper.getEmpireByPlayer(player);

        if (empire == null) {
            player.sendMessage(new LiteralText("You need an Empire to teleport home to."), false);
            return 1;
        }

        Vec3d home = empire.home;

        if (!player.getServerWorld().equals(empire.homeWorld)) {
            player.moveToWorld(empire.homeWorld); // This check stops a weird bug where inventory is cleared client side til you relog
        }

        player.teleport(home.x, home.y, home.z);
        player.sendMessage(new LiteralText("Wooosh"), false);

        return 1;
    }


        public static int createEmpire(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        String name = StringArgumentType.getString(context, "name");

        Empire existingEmpire = EmpireHelper.getEmpireByName(name);
        if (existingEmpire != null) {
            player.sendMessage(new LiteralText("There's already an Empire named " + existingEmpire), false);
            return 1;
        }

        Empire createdEmpire = new Empire(player.getUuid(), name);
        Config.addEmpire(createdEmpire);

        player.sendMessage(new LiteralText("You created an Empire called " + name), false);

        return 1;
    }

    public static int invitePlayerToEmpire(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Empire empire = EmpireHelper.getEmpireByPlayer(player);

        if (empire == null) {
            player.sendMessage(new LiteralText("You need to be in an Empire to invite someone to it."), false);
            return 1;
        }

        // Player is in an Empire and can invite other players

        for (GameProfile prof : GameProfileArgumentType.getProfileArgument(context, "player")) {
            empire.invited.add(prof.getId()); // maybe add a notif so players know they've been invited
            player.sendMessage(new LiteralText("You invited " + prof.getName() + " to " + empire.name), false);
        }

        Config.addEmpire(empire); // update empire in config with new invites

        return 1;
    }

    public static int joinEmpire(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "empire");
        Empire empireCheck = EmpireHelper.getEmpireByPlayer(player);

        if (empireCheck != null) {
            player.sendMessage(new LiteralText("You're already in an Empire!"), false);
            return 1;
        }

        Empire empire = EmpireHelper.getEmpireByName(name);
        if (empire == null) {
            player.sendMessage(new LiteralText("There isn't an Empire with that name to join."), false);
            return 1;
        }

        if (empire.invited.contains(player.getUuid())) {

            empire.invited.remove(player.getUuid());
            empire.members.add(player.getUuid());

            player.sendMessage(new LiteralText("You joined " + empire.name), false);
        } else {
            player.sendMessage(new LiteralText("You need to be invited to the Empire to join it"), false);
            return 1;
        }

        Config.addEmpire(empire);

        return 1;
    }

    public static int info(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        try {
            Empire empire = EmpireHelper.getEmpireByPlayer(player);
            player.sendMessage(new LiteralText("You're a member of the Empire " + empire.name), false);
        } catch (Exception e) {
            player.sendMessage(new LiteralText("You're not in an Empire!"), false);
            return 1;
        }

        return 1;
    }

}
