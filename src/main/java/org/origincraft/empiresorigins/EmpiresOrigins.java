package org.origincraft.empiresorigins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.origincraft.empiresorigins.commands.EmpireCommands;
import org.origincraft.empiresorigins.config.Config;
import org.origincraft.empiresorigins.config.ConfigHelper;
import org.origincraft.empiresorigins.events.BlockInteraction;

public class EmpiresOrigins implements ModInitializer {

    public static final Logger logger = LogManager.getLogger("EmpiresOrigins");

    @Override
    public void onInitialize() {
        log("Starting up...");
        PlayerBlockBreakEvents.BEFORE.register(BlockInteraction::breakBlock);
        UseBlockCallback.EVENT.register(BlockInteraction::useBlocks);
        ServerLifecycleEvents.SERVER_STARTING.register(ConfigHelper::serverLoad);
        CommandRegistrationCallback.EVENT.register(EmpireCommands::register);
    }

    public static void log(String msg, Object... o) {
        logger.info("[EO] " + msg, o);
    }

}
