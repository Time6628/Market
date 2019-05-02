package co.nytro.market.commands.subcommands;

import co.nytro.market.datastores.UIBuilder;
import co.nytro.market.Market;
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
        if (pl.isHuskyUILoaded() && pl.isChestGUIDefault()) {
            if (args.hasAny("g")) pl.getDataStore().getListingsPagination().sendTo(src);
            else UIBuilder.getStateContainer(pl.getDataStore().getListings()).launchFor((Player) src);
        } else {
            pl.getDataStore().getListingsPagination().sendTo(src);
        }

        return CommandResult.success();
    }
}
