package com.kookykraftmc.market;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
public class Texts {
    public static Text MARKET_BASE = Text.builder().color(TextColors.GREEN).append(Text.of("Market")).build();
    public static Text NO_BUY_ITEM = Text.builder().color(TextColors.RED).append(Text.of("Unable to buy item. Be sure you have enough money to buy it.")).build();
    public static Text NOT_ENOUGH_ITEMS = Text.builder().color(TextColors.RED).append(Text.of("You cannot set the quantity to more than what you have in your hand.")).build();
    public static Text INVALID_LISTING = Text.builder().color(TextColors.RED).append(Text.of("Unable to get listing.")).build();
    public static Text INV_FULL = Text.builder().color(TextColors.RED).append(Text.of("Unable to add the item to your inventory. Please make sure it is not full. Will try to add the item to your inventory again in 30 seconds.")).build();
    public static Text PURCHASE_SUCCESSFUL = Text.builder().color(TextColors.GREEN).append(Text.of("Purchase successful and the item has been added to your inventory.")).build();
    public static Text USE_ADD_STOCK = Text.builder().color(TextColors.RED).append(Text.of("You already have a listing of a similar item, please use /market addstock <listing id>.")).build();

    public static Text MARKET_LISTING(String id) { return Text.builder().color(TextColors.GREEN).append(Text.of("Market Listing " + id)).build(); }
    public static Text MARKET_LISTINGS = Text.builder().color(TextColors.GREEN).append(Text.of("Market Listings")).build();
    public static Text MARKET_SEARCH = Text.builder().color(TextColors.GREEN).append(Text.of("Search Results")).build();
    public static Text AIR_ITEM = Text.builder().color(TextColors.RED).append(Text.of("Please hold something in your hand.")).build();
    public static Text COULD_NOT_MAKE_LISTNG = Text.builder().color(TextColors.RED).append(Text.of("Could not make listing, sorry.")).build();
    public static Text COULD_NOT_ADD_STOCK = Text.builder().color(TextColors.RED).append(Text.of("Unable to add stock. This means the item you are holding has different data then the item you listed before.")).build();
    public static Text BLACKLIST_NO_ADD = Text.builder().color(TextColors.RED).append(Text.of("Could not add to blacklist.. maybe try holding something?")).build();
    public static Text BLACKLIST_NO_ADD_2 = Text.builder().color(TextColors.RED).append(Text.of("Could not add to blacklist.")).build();
    public static Text BLACKLIST_REMOVED = Text.builder().color(TextColors.GREEN).append(Text.of("Succesfully removed from the blacklist.")).build();
    public static Text BLACKLIST_REMOVED_FAIL = Text.builder().color(TextColors.RED).append(Text.of("Failed to remove from the blacklist, please make sure the id is correct.")).build();

    public static Text quickItemFormat(ItemStack value) {
        return Text.builder()
                .color(TextColors.AQUA)
                .append(Text.of("["))
                .append(Text.of(value.getTranslation()))
                .append(Text.of("]"))
                .onHover(TextActions.showItem(value.createSnapshot()))
                .build();
    }

    public static Text ADD_TO_BLACKLIST(String id) {
        return Text.builder()
                .append(Text.of(TextColors.GREEN, "Added "))
                .append(Text.of(TextColors.WHITE, id))
                .append(Text.of(TextColors.GREEN, " to the market blacklist."))
                .build();
    }
}
