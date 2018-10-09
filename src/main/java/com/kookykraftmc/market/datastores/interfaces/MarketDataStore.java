package com.kookykraftmc.market.datastores.interfaces;

import com.kookykraftmc.market.datastores.Listing;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.pagination.PaginationList;

import java.util.List;
import java.util.UUID;

public interface MarketDataStore {

    /**
     * Update a name attached to a uuid.
     *
     * @param uuid The UUID to update.
     * @param name The name to update with.
     */
    void updateUUIDCache(String uuid, String name);

    /**
     * Used for subscribing or resubscribing to pub/sub if supported by the database.
     */
    void subscribe();

    /**
     * Add a new listing to the database.
     *
     * @param player          The {@link Player} that's selling it.
     * @param itemStack       The {@link ItemStack} to sell.
     * @param quantityPerSale The amount of the {@link ItemStack} to sell per sale.
     * @param price           The price to sell the quantity at.
     * @return If it returns 0, there's not enough of the item to sell or the item is blacklisted.
     * If it returns -1, the player is already selling an item of the same type.
     * Otherwise, it returns the listing id.
     */
    int addListing(Player player, ItemStack itemStack, int quantityPerSale, int price);

    /**
     * Checks to see if a player is already selling an item of similar type.
     *
     * @param itemStack The item to check for.
     * @param s         Player's {@link UUID} to check for.
     * @return true if another listing exists, false otherwise.
     */
    boolean checkForOtherListings(ItemStack itemStack, String s);

    /**
     * Gets all of the current listings.
     *
     * @return A list of {@link Listing}s.
     */
    List<Listing> getListings();

    PaginationList getListingsPagination();

    /**
     * Remove a listing from the listings.
     *
     * @param id    The listing id to remove.
     * @param uuid  The {@link UUID} of the person removing it.
     * @param staff If true, remove the listing regardless of the remover's {@link UUID},
     *              otherwise it will ensure the remover is the seller of the listing.
     * @return The leftover items from the listing.
     */
    List<ItemStack> removeListing(String id, String uuid, boolean staff);

    PaginationList getListing(String id);

    /**
     * Adds stock to a listing.
     *
     * @param itemStack The {@link ItemStack} to add.
     * @param id        The listing id to add to.
     * @param uuid      The uuid of the player adding to the stock.
     * @return false if listing is null, or the listing's {@link ItemStack} doesn't match.
     * true if it added successfully.
     */
    boolean addStock(ItemStack itemStack, String id, UUID uuid);

    /**
     * Buy from a listing.
     *
     * @param uniqueAccount The account of the player buying it.
     * @param id            The listing id
     * @return the {@link ItemStack} created from the purchase,
     * null if it could not purchase it.
     */
    ItemStack purchase(UniqueAccount uniqueAccount, String id);

    boolean blacklistAddCmd(String id);

    boolean blacklistRemoveCmd(String id);

    void addIDToBlackList(String id);

    List<String> getBlacklistedItems();

    boolean isBlacklisted(ItemStack itemStack);

    PaginationList getBlacklistedItemList();

    void rmIDFromBlackList(String message);

    /**
     * Search the listings for a specific item.
     *
     * @param itemType The itemtype to search for.
     * @return A {@link PaginationList} of the results to easily send it to players.
     */
    PaginationList searchForItem(ItemType itemType);

    PaginationList searchForUUID(UUID uniqueId);

    void updateUUIDCache(Player player);

    void updateBlackList();
}
