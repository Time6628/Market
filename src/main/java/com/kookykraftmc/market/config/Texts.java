package com.kookykraftmc.market.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import static org.spongepowered.api.text.TextTemplate.arg;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
@ConfigSerializable
public class Texts {
    @Setting("Base")
    public static Text MARKET_BASE = Text.builder().color(TextColors.GREEN).append(Text.of("Market")).build();

    @Setting("Can't Buy Item")
    public static Text NO_BUY_ITEM = Text.builder().color(TextColors.RED).append(Text.of("Unable to buy item. Be sure you have enough money to buy it.")).build();

    @Setting("Not Enough Items")
    public static Text NOT_ENOUGH_ITEMS = Text.builder().color(TextColors.RED).append(Text.of("You cannot set the quantity to more than what you have in your hand.")).build();

    @Setting("Too Enough Items")
    public static Text TOO_ENOUGH_ITEMS = Text.builder().color(TextColors.RED).append(Text.of("The item in your hand is over the maximum normal amount, please separate them and add the extra items as stock using the /market addstock command.")).build();

    @Setting("Invalid Listing")
    public static Text INVALID_LISTING = Text.builder().color(TextColors.RED).append(Text.of("Unable to get listing.")).build();

    @Setting("Inventory Full")
    public static Text INV_FULL = Text.builder().color(TextColors.RED).append(Text.of("Unable to add the item to your inventory. Please make sure it is not full. Will try to add the item to your inventory again in 30 seconds.")).build();

    @Setting("Purchase Successful")
    public static Text PURCHASE_SUCCESSFUL = Text.builder().color(TextColors.GREEN).append(Text.of("Purchase successful and the item has been added to your inventory.")).build();

    @Setting("Use Add Stock")
    public static Text USE_ADD_STOCK = Text.builder().color(TextColors.RED).append(Text.of("You already have a listing of a similar item, please use /market addstock <listing id>.")).build();

    @Setting("Market Listing")
    public static TextTemplate MARKET_LISTING = TextTemplate.of(
            TextColors.GREEN, "Market Listing ", arg("id")
    );

    @Setting("Listings")
    public static Text MARKET_LISTINGS = Text.builder().color(TextColors.GREEN).append(Text.of("Market Listings")).build();

    @Setting("Search")
    public static Text MARKET_SEARCH = Text.builder().color(TextColors.GREEN).append(Text.of("Search Results")).build();

    @Setting("Air Item")
    public static Text AIR_ITEM = Text.builder().color(TextColors.RED).append(Text.of("Please hold something in your hand.")).build();

    @Setting("Could Not Make Listing")
    public static Text COULD_NOT_MAKE_LISTING = Text.builder().color(TextColors.RED).append(Text.of("Could not make listing, sorry.")).build();

    @Setting("Could Not Add Stock")
    public static Text COULD_NOT_ADD_STOCK = Text.builder().color(TextColors.RED).append(Text.of("Unable to add stock. This means the item you are holding has different data then the item you listed before.")).build();

    @Setting("Air Blacklist")
    public static Text BLACKLIST_NO_ADD = Text.builder().color(TextColors.RED).append(Text.of("Could not add to blacklist.. maybe try holding something?")).build();

    @Setting("Could Not Add to Blacklist")
    public static Text BLACKLIST_NO_ADD_2 = Text.builder().color(TextColors.RED).append(Text.of("Could not add to blacklist.")).build();

    @Setting("Remove From Blacklist")
    public static Text BLACKLIST_REMOVED = Text.builder().color(TextColors.GREEN).append(Text.of("Successfully removed from the blacklist.")).build();

    @Setting("Failed To Remove From Blacklist")
    public static Text BLACKLIST_REMOVED_FAIL = Text.builder().color(TextColors.RED).append(Text.of("Failed to remove from the blacklist, please make sure the id is correct.")).build();
    @Setting("Add To Blacklist")
    public static TextTemplate ADD_TO_BLACKLIST = TextTemplate.of(
            TextColors.GREEN, "Added ",
            TextColors.WHITE, arg("id"),
            TextColors.GREEN, " to the market blacklist."
    );
    @Setting("GUI Item Lore")
    public static TextTemplate guiListing = TextTemplate.of(
            TextColors.GREEN, "$", arg("Price").color(TextColors.GREEN),
            TextColors.WHITE, " for ",
            TextColors.GREEN, arg("Quantity").color(TextColors.GREEN), "x"
    );

    public static Text quickItemFormat(ItemStack value) {
        return Text.builder()
                .color(TextColors.AQUA)
                .append(Text.of("["))
                .append(Text.of(value.getTranslation()))
                .append(Text.of("]"))
                .onHover(TextActions.showItem(value.createSnapshot()))
                .build();
    }
}
