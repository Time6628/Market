package com.kookykraftmc.market;

import com.google.inject.Inject;
import com.kookykraftmc.market.commands.MarketCommand;
import com.kookykraftmc.market.commands.subcommands.*;
import com.kookykraftmc.market.commands.subcommands.blacklist.BlacklistAddCommand;
import com.kookykraftmc.market.commands.subcommands.blacklist.BlacklistRemoveCommand;
import com.kookykraftmc.market.service.MarketService;
import com.kookykraftmc.market.service.UUIDCacheService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(id = "market",
        name = "Market",
        description = "Market",
        url = "https://kookykraftmc.net",
        authors = {"TimeTheCat"},
        version = "@VERSION@",
        dependencies = @Dependency(id = "huskyui", optional = true)
)
public class Market {

    @Inject
    private Logger logger;
    @Inject
    private Game game;
    @Inject
    private MarketService marketService;
    @Inject
    private UUIDCacheService uuidCacheService;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        logger.info("Loading config...");
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        game.getServiceManager().setProvider(this, MarketService.class, marketService);
        game.getServiceManager().setProvider(this, UUIDCacheService.class, uuidCacheService);

        CommandSpec createMarketCmd = CommandSpec.builder()
                .executor(new CreateCommand())
                .arguments(GenericArguments.integer(Text.of("quantity")), GenericArguments.integer(Text.of("price")))
                .permission("market.command.createlisting")
                .description(Text.of("Create a market listing."))
                .build();

        CommandSpec listingsCmd = CommandSpec.builder()
                .executor(new ListingsCommand())
                .permission("market.command.listings")
                .description(Text.of("List all market listings."))
                .arguments(GenericArguments.flags().flag("g").buildWith(GenericArguments.none()))
                .build();

        CommandSpec listingInfoCmd = CommandSpec.builder()
                .executor(new ListingInfoCommand())
                .permission("market.command.check")
                .arguments(GenericArguments.string(Text.of("id")))
                .description(Text.of("Get info about a listing."))
                .build();

        CommandSpec buyCmd = CommandSpec.builder()
                .executor(new BuyCommand())
                .permission("market.command.buy")
                .arguments(GenericArguments.string(Text.of("id")))
                .description(Text.of("Buy an Item from the market."))
                .build();

        CommandSpec addStockCmd = CommandSpec.builder()
                .executor(new AddStockCommand())
                .permission("market.command.addstock")
                .arguments(GenericArguments.string(Text.of("id")))
                .description(Text.of("Add more stock to your market listing."))
                .build();

        CommandSpec removeListingCmd = CommandSpec.builder()
                .executor(new RemoveListingCommand())
                .permission("market.command.removelisting")
                .arguments(GenericArguments.string(Text.of("id")))
                .description(Text.of("Remove an item from the market."))
                .build();

        CommandSpec blacklistAddCmd = CommandSpec.builder()
                .executor(new BlacklistAddCommand())
                .permission("market.command.staff.blacklist.add")
                .description(Text.of("Add an item to the market blacklist."))
                .build();

        CommandSpec blacklistRmCmd = CommandSpec.builder()
                .executor(new BlacklistRemoveCommand())
                .permission("market.command.staff.blacklist.remove")
                .description(Text.of("Remove an item to the market blacklist."))
                .arguments(GenericArguments.string(Text.of("id")))
                .build();

        CommandSpec blacklistCmd = CommandSpec.builder()
                .executor(new BlackListCommand())
                .permission("market.command.blacklist")
                .description(Text.of("List all blacklisted items."))
                .child(blacklistAddCmd, "add")
                .child(blacklistRmCmd, "remove")
                .build();

        CommandSpec itemSearch = CommandSpec.builder()
                .executor(new SearchCommand.ItemSearch())
                .permission("market.command.search")
                .arguments(GenericArguments.catalogedElement(Text.of("item"), ItemType.class))
                .description(Text.of("List all market listings for a specific item."))
                .build();

        CommandSpec nameSearch = CommandSpec.builder()
                .executor(new SearchCommand.NameSearch())
                .permission("market.command.search")
                .arguments(GenericArguments.user(Text.of("user")))
                .description(Text.of("List all market listings for a specific name."))
                .build();

        CommandSpec search = CommandSpec.builder()
                .executor(new SearchCommand())
                .permission("market.command.search")
                .description(Text.of("List all search options."))
                .child(itemSearch, "item")
                .child(nameSearch, "name")
                .build();

        CommandSpec marketCmd = CommandSpec.builder()
                .executor(new MarketCommand())
                .permission("market.command.base")
                .description(Text.of("Market base command."))
                .child(createMarketCmd, "create")
                .child(listingsCmd, "listings")
                .child(listingInfoCmd, "check")
                .child(buyCmd, "buy")
                .child(addStockCmd, "addstock")
                .child(removeListingCmd, "removelisting")
                .child(blacklistCmd, "blacklist")
                .child(search, "search")
                .build();
        Sponge.getCommandManager().register(this, marketCmd, "market");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        marketService.updateUUIDCache(player);
    }
}