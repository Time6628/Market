package com.kookykraftmc.market.datastores.dynamodb;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.*;
import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.datastores.Listing;
import com.kookykraftmc.market.datastores.MarketDataStore;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.pagination.PaginationList;

import java.util.List;
import java.util.UUID;

public class DynamoDBDataStore implements MarketDataStore {

    private final Market market = Market.instance;
    private AmazonDynamoDBAsync client = null;
    private DynamoDB dynamoDB;

    public DynamoDBDataStore(MarketConfig.DynamoDataStoreConfig config) {
        AmazonDynamoDBAsyncClientBuilder builder = AmazonDynamoDBAsyncClientBuilder.standard();
        builder.setCredentials(new DefaultAWSCredentialsProviderChain());
        builder.setRegion(config.region);
        client = builder.build();
        dynamoDB = new DynamoDB(client);
        setupTables();
    }

    private void setupTables() {
        CreateTableRequest uuidCTR = new CreateTableRequest().withTableName("uuidcache").withKeySchema(new KeySchemaElement("UUID", KeyType.HASH));
        dynamoDB.createTable(uuidCTR);
        CreateTableRequest listingsCTR = new CreateTableRequest().withTableName("marketlistings").withKeySchema(new KeySchemaElement("ID", KeyType.HASH));
        dynamoDB.createTable(listingsCTR);
    }

    @Override
    public void updateUUIDCache(String uuid, String name) {
        try {
            Item item = new Item().withPrimaryKey("UUID", uuid).withString("Name", name);
            dynamoDB.getTable("uuidcache").putItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe() {
        market.getLogger().info("Pub/Sub sync not yet implemented.");
    }

    @Override
    public int addListing(Player player, ItemStack itemStack, int quantityPerSale, int price) {
        try {
            if (itemStack.getQuantity() < quantityPerSale || quantityPerSale <= 0 || isBlacklisted(itemStack)) return 0;
            if (checkForOtherListings(itemStack, player.getUniqueId().toString())) return -1;

            DynamoDBListing listing = new DynamoDBListing();
            listing.setItemStack(market.serializeItem(itemStack));
            listing.setSeller(player.getUniqueId().toString());
            listing.setPrice(price);
            listing.setQuantity(quantityPerSale);
            DynamoDBMapper mapper = new DynamoDBMapper(client);
            mapper.save(listing);

            market.getLogger().info("listing id: " + listing.getID());

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean checkForOtherListings(ItemStack itemStack, String s) {
        return false;
    }

    @Override
    public List<Listing> getListings() {
        return null;
    }

    @Override
    public PaginationList getListingsPagination() {
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
}
