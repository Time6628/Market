package com.kookykraftmc.market.datastores;

import com.codehusky.huskyui.StateContainer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.pagination.PaginationList;

import java.util.List;
import java.util.UUID;

public interface DataStore {

    void updateUUIDCache(String uuid, String name);

    void subscribe();

    int addListing(Player player, ItemStack itemStack, int quantityPerSale, int price);

    boolean checkForOtherListings(ItemStack itemStack, String s);

    PaginationList getListings();

    StateContainer getListingsGUI();

    List<ItemStack> removeListing(String id, String uuid, boolean staff);

    PaginationList getListing(String id);

    boolean addStock(ItemStack itemStack, String id, UUID uuid);

    ItemStack purchase(UniqueAccount uniqueAccount, String id);

    boolean blacklistAddCmd(String id);

    boolean blacklistRemoveCmd(String id);

    void addIDToBlackList(String id);

    List<String> getBlacklistedItems();

    boolean isBlacklisted(ItemStack itemStack);

    PaginationList getBlacklistedItemList();

    void rmIDFromBlackList(String message);

    PaginationList searchForItem(ItemType itemType);

    PaginationList searchForUUID(UUID uniqueId);

    void updateUUIDCache(Player player);

    void updateBlackList();
}
