package com.kookykraftmc.market.datastores.dynamodb;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.*;
import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.config.Texts;
import com.kookykraftmc.market.datastores.Listing;
import com.kookykraftmc.market.datastores.MarketDataStore;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DynamoDBDataStore implements MarketDataStore {

    private final Market market = Market.instance;
    private AmazonDynamoDBAsync client = null;
    private DynamoDB dynamoDB;
    private DynamoDBMapper mapper;

    public DynamoDBDataStore(MarketConfig.DynamoDataStoreConfig config) {
        AmazonDynamoDBAsyncClientBuilder builder = AmazonDynamoDBAsyncClientBuilder.standard();
        builder.setCredentials(new DefaultAWSCredentialsProviderChain());
        builder.setRegion(config.region);
        client = builder.build();
        dynamoDB = new DynamoDB(client);
        setupTables();
        mapper = new DynamoDBMapper(client);
    }

    private void setupTables() {
        CreateTableRequest uuidCTR = new CreateTableRequest().withTableName("uuidcache").withKeySchema(new KeySchemaElement("UUID", KeyType.HASH));
        dynamoDB.createTable(uuidCTR);
        CreateTableRequest listingsCTR = new CreateTableRequest().withTableName("marketlistings");
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
            listing.setStock(itemStack.getQuantity());
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
        List<Listing> filtered = getListings().stream()
                .filter(listing -> listing.getSeller().equals(UUID.fromString(s)))
                .filter(listing -> market.matchItemStacks(listing.getItemStack(), itemStack)).collect(Collectors.toList());
        return filtered.size() != 0;
    }

    @Override
    public List<Listing> getListings() {
        PaginatedScanList<DynamoDBListing> scan = mapper.scan(DynamoDBListing.class, new DynamoDBScanExpression());
        List<Listing> listings = new ArrayList<>();
        scan.forEach(dbList -> listings.add(new Listing(dbList, getNameFromUUIDCache(dbList.getSeller()))));
        return listings;
    }

    @Override
    public PaginationList getListingsPagination() {
        List<Text> texts = new ArrayList<>();
        getListings().forEach(listing -> texts.add(listing.getListingsText()));
        return market.getPaginationService().builder().contents(texts).title(Texts.MARKET_LISTINGS).build();
    }

    @Override
    public List<ItemStack> removeListing(String id, String uuid, boolean staff) {
        DynamoDBScanExpression dbse = new DynamoDBScanExpression().withFilterExpression("ID = :id").withExpressionAttributeValues(Collections.singletonMap(":id", new AttributeValue().withS(id)));
        PaginatedScanList<DynamoDBListing> listing = mapper.scan(DynamoDBListing.class, dbse);
        DynamoDBListing l = listing.get(0);

        if (!l.getSeller().equals(uuid) && !staff) return null;

        Listing ll = new Listing(l, getNameFromUUIDCache(l.getSeller()));
        int inStock = ll.getStock();
        ItemStack listingIS = ll.getItemStack();
        int stacksInStock = inStock / listingIS.getMaxStackQuantity();
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < stacksInStock; i++) {
            stacks.add(listingIS.copy());
        }

        if (inStock % listingIS.getMaxStackQuantity() != 0) {
            ItemStack extra = listingIS.copy();
            extra.setQuantity(inStock % listingIS.getMaxStackQuantity());
            stacks.add(extra);
        }

        mapper.delete(l);
        return stacks;
    }

    @Override
    public PaginationList getListing(String id) {
        DynamoDBScanExpression dbse = new DynamoDBScanExpression().withFilterExpression("ID = :id").withExpressionAttributeValues(Collections.singletonMap(":id", new AttributeValue().withS(id)));
        PaginatedScanList<DynamoDBListing> listing = mapper.scan(DynamoDBListing.class, dbse);
        Listing l = new Listing(listing.get(0), getNameFromUUIDCache(listing.get(0).getSeller()));
        List<Text> texts = new ArrayList<>();
        listing.get(0).getValues().forEach((key, value) -> {
            switch (key) {
                case "Item":
                    texts.add(Texts.quickItemFormat(market.deserializeItemStack(value).get()));
                    break;
                case "Seller":
                    texts.add(Text.of("Seller: " + getNameFromUUIDCache(value)));
                    break;
                default:
                    texts.add(Text.of(key + ": " + value));
                    break;
            }
        });

        texts.add(Text.builder()
                .append(Text.builder()
                        .color(TextColors.GREEN)
                        .append(Text.of("[Buy]"))
                        .onClick(TextActions.suggestCommand("/market buy " + id))
                        .build())
                .append(Text.of(" "))
                .append(Text.builder()
                        .color(TextColors.GREEN)
                        .append(Text.of("[QuickBuy]"))
                        .onClick(TextActions.runCommand("/market buy " + id))
                        .onHover(TextActions.showText(Text.of("Click here to run the command to buy the item.")))
                        .build())
                .build());

        return market.getPaginationService().builder().title(Texts.MARKET_LISTING.apply(Collections.singletonMap("id", id)).build()).contents(texts).build();
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

    private String getNameFromUUIDCache(String uuid) {
        Map<String, AttributeValue> val = new HashMap<>();
        val.put("UUID", new AttributeValue(uuid));
        GetItemResult gir = null;
        try {
            gir = client.getItemAsync(new GetItemRequest("uuidcache", val)).get();
            return gir.getItem().get("UUID").getS();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
