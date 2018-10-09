package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.datastores.UIManager;
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
    private final Market pl = Market.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (pl.isHuskyUILoaded()) {
            if (pl.isChestGUIDefault()) {
                if (args.hasAny("g")) pl.getDataStore().getListingsPagination().sendTo(src);
                else UIManager.getStateContainer(pl.getDataStore().getListings()).launchFor((Player) src);
            } else {
                if (args.hasAny("g"))
                    UIManager.getStateContainer(pl.getDataStore().getListings()).openState((Player) src, "0");
                else pl.getDataStore().getListingsPagination().sendTo(src);
            }
        } else {
            pl.getDataStore().getListingsPagination().sendTo(src);
        }

        return CommandResult.success();
    }
}
