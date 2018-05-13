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

import java.util.UUID;

/**
 * Created by TimeTheCat on 3/18/2017.
 */
public class ListingsCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        MarketService pl = Sponge.getServiceManager().provide(MarketService.class).get();
        UUID uniqueId = src instanceof Player ? ((Player) src).getUniqueId() : UUID.randomUUID();
        boolean staff = src instanceof Player ? ((Player) src).hasPermission("market.command.staff.show.all.listing") : true;
        if (pl.isHuskyUILoaded()) {
            if (pl.isChestGUIDefault()) {
                if (args.hasAny("g")) pl.getListingsPagination(uniqueId, staff).sendTo(src);
                else UIManager.getStateContainer(pl.getListings(uniqueId, staff)).launchFor((Player) src);
            } else {
                if (args.hasAny("g")) UIManager.getStateContainer(pl.getListings(uniqueId, staff)).openState((Player) src, "0");
                else pl.getListingsPagination(uniqueId, staff).sendTo(src);
            }
        } else {
            pl.getListingsPagination(uniqueId, staff).sendTo(src);
        }

        return CommandResult.success();
    }
}
