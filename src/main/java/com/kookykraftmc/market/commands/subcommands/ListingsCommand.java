package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.Market;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by TimeTheCat on 3/18/2017.
 */
public class ListingsCommand implements CommandExecutor {
    private Market pl = Market.instance;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (pl.isChestGUIDefault()) {
            if (args.hasAny("g")) pl.getDataStore().getListings().sendTo(src);
            else pl.getDataStore().getListingsGUI().copy().launchFor((Player) src);
        } else {
            if (args.hasAny("g")) pl.getDataStore().getListingsGUI().copy().launchFor((Player) src);
            else pl.getDataStore().getListings().sendTo(src);
        }

        return CommandResult.success();
    }
}
