package com.kookykraftmc.market.datastores;

import com.kookykraftmc.market.Market;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.pagination.PaginationList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class SQLDataStore implements DataStore {

    Market market = Market.instance;
    String jdbc;

    public SQLDataStore(String jdbc) {
        /*this.jdbc = jdbc;
        setupDB();*/
        market.getLogger().info("MySQL is currently not fully implemented.");
    }

    private void setupDB() {
        try (Connection connection = market.getDataSource(jdbc).getConnection()) {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS market_uuidcache (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "username VARCHAR(36) NOT NULL);");
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS market_listings (" +
                    "id INTEGER NOT NULL," +
                    "item VARCHAR(36) NOT NULL," +
                    "seller VARCHAR(36) NOT NULL," +
                    "stock INTEGER NOT NULL," +
                    "price INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL);");
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS market_uuidcache (" +
                    "uuid INT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUUIDCache(String uuid, String name) {
        try (Connection connection = market.getDataSource(jdbc).getConnection()) {
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO market_uuidcache VALUES (?, ?)");
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe() {
        market.getLogger().error("Looks like you are using a SQL DataStore, PubSub functions disabled.");
    }

    @Override
    public int addListing(Player player, ItemStack itemStack, int quantityPerSale, int price) {
        try (Connection connection = market.getDataSource(jdbc).getConnection()) {
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO market_listings VALUES (?, ?, ?, ?, ?, ?)");
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean checkForOtherListings(ItemStack itemStack, String s) {
        return false;
    }

    @Override
    public PaginationList getListings() {
        return null;
    }

    @Override
    public List<ItemStack> removeListing(String id, String uuid, boolean staff) {
        return null;
    }

    @Override
    public PaginationList getListing(String id) {
        return null;
    }

    @Override
    public boolean addStock(ItemStack itemStack, String id, UUID uuid) {
        return false;
    }

    @Override
    public ItemStack purchase(UniqueAccount uniqueAccount, String id) {
        return null;
    }

    @Override
    public boolean blacklistAddCmd(String id) {
        return false;
    }

    @Override
    public boolean blacklistRemoveCmd(String id) {
        return false;
    }

    @Override
    public void addIDToBlackList(String id) {

    }

    @Override
    public List<String> getBlacklistedItems() {
        return null;
    }

    @Override
    public boolean isBlacklisted(ItemStack itemStack) {
        return false;
    }

    @Override
    public PaginationList getBlacklistedItemList() {
        return null;
    }

    @Override
    public void rmIDFromBlackList(String message) {

    }

    @Override
    public PaginationList searchForItem(ItemType itemType) {
        return null;
    }

    @Override
    public PaginationList searchForUUID(UUID uniqueId) {
        return null;
    }

    @Override
    public void updateUUIDCache(Player player) {
        updateUUIDCache(player.getUniqueId().toString(), player.getName());
    }

    @Override
    public void updateBlackList() {

    }
}
