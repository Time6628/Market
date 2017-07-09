package com.kookykraftmc.market.datastores;

import com.kookykraftmc.market.Market;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.pagination.PaginationList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoDBDataStore implements DataStore {

    private Market market = Market.instance;
    private MongoClient mongoClient;
    private String databaseName;

    public MongoDBDataStore(String host, int port, String database) {
        mongoClient = new MongoClient(host, port);
        databaseName = database;
        setupDB();
    }

    private void setupDB() {
        try (MongoClient client = getClient()) {
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(MongoCollections.marketInfo);
            Document found = collection.find(Filters.all("tags", MongoTags.marketInfoTags)).first();
            if (found == null) {
                Document marketInfoDoc = new Document().append("lastID", 1);
                collection.insertOne(marketInfoDoc);
            }
        }
    }

    private MongoClient getClient() {
        return mongoClient;
    }

    @Override
    public void updateUUIDCache(String uuid, String name) {
        try (MongoClient client = getClient()) {
            Document uuidDoc = new Document()
                    .append("uuid", uuid)
                    .append("name", name);
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(MongoCollections.marketInfo);
            Document found = collection.find(Filters.eq("uuid", uuid)).first();
            if (found == null) {
                collection.insertOne(uuidDoc);
            } else {
                collection.replaceOne(Filters.eq("uuid", uuid), uuidDoc);
            }
        }
    }

    @Override
    public void subscribe() {
        market.getLogger().info("Pub/Sub sync not yet implemented.");
    }

    @Override
    public int addListing(Player player, ItemStack itemStack, int quantityPerSale, int price) {
        try (MongoClient client = getClient()) {
            if (itemStack.getQuantity() < quantityPerSale || quantityPerSale <= 0 || isBlacklisted(itemStack)) return 0;
            if (checkForOtherListings(itemStack, player.getUniqueId().toString())) return -1;
            int id = getLastID() + 1;
            Document doc = new Document();
            doc.append("Item", market.serializeItem(itemStack));
            doc.append("Seller", player.getUniqueId().toString());
            doc.append("Stock", itemStack.getQuantity());
            doc.append("Price", price);
            doc.append("Quantity", quantityPerSale);
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings);
            collection.insertOne(doc);
            setLastID(id);
            return id;
        }
    }

    @Override
    public boolean checkForOtherListings(ItemStack itemStack, String s) {
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.eq("Seller", s));
            final boolean[] hasOther = {false};
            docs.forEach((Consumer<? super Document>) o -> {
                Optional<ItemStack> ooi = market.deserializeItemStack(o.getString("Item"));
                if (!ooi.isPresent()) return;
                if (market.matchItemStacks(ooi.get(), itemStack)) {
                    hasOther[0] = true;
                }
            });

            return hasOther[0];
        }
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

    }

    @Override
    public void updateBlackList() {

    }

    private Integer getLastID() {
        try (MongoClient client = getClient()) {
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(MongoCollections.marketInfo);
            Document found = collection.find(Filters.all("tags", MongoTags.marketInfoTags)).first();
            return found.getInteger("lastID");
        }
    }

    private void setLastID(int lastID) {
        try (MongoClient client = getClient()) {
            Document found = client.getDatabase(databaseName).getCollection(MongoCollections.marketInfo).find(Filters.all("tags", MongoTags.marketInfoTags)).first();
            found.put("lastID", lastID);
            client.getDatabase(databaseName).getCollection(MongoCollections.marketInfo).replaceOne(Filters.all("tags", MongoTags.marketInfoTags), found);
        }
    }
}
