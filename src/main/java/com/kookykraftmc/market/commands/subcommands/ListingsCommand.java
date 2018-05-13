package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.ui.UIManager;
import com.kookykraftmc.market.service.MarketService;
import org.spongepowered.api.Sponge;
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
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        MarketService pl = Sponge.getServiceManager().provide(MarketService.class).get();
        Player player = (Player) src;
        if (pl.isHuskyUILoaded()) {
            if (pl.isChestGUIDefault()) {
                if (args.hasAny("g")) pl.getListingsPagination(player.getUniqueId(), player.hasPermission("market.command.staff.show.all.listing")).sendTo(src);
                else UIManager.getStateContainer(pl.getListings(player.getUniqueId(), player.hasPermission("market.command.staff.show.all.listing"))).launchFor((Player) src);
            } else {
                if (args.hasAny("g")) UIManager.getStateContainer(pl.getListings(player.getUniqueId(), player.hasPermission("market.command.staff.show.all.listing"))).openState((Player) src, "0");
                else pl.getListingsPagination(player.getUniqueId(), player.hasPermission("market.command.staff.show.all.listing")).sendTo(src);
            }
        } else {
            pl.getListingsPagination(player.getUniqueId(), player.hasPermission("market.command.staff.show.all.listing")).sendTo(src);
        }

        return CommandResult.success();
    }
}
