package com.kookykraftmc.market.datastores.mongo;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.codehusky.huskyui.states.State;
import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.Texts;
import com.kookykraftmc.market.datastores.DataStore;
import com.kookykraftmc.market.datastores.Listing;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

public class MongoDBDataStore implements DataStore {

    private final Market market = Market.instance;
    private final MongoClient mongoClient;
    private final String databaseName;

    public MongoDBDataStore(String host, int port, String database, String user, String password) {
        MongoCredential cred = MongoCredential.createCredential(user, database, password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(host, port), Collections.singletonList(cred));
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
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(MongoCollections.uuidCache);
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
            doc.append("ID", id);
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
    public List<Listing> getListings() {
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.all("tags", MongoTags.marketListingTags));
            List<Listing> listings = new ArrayList<>();
            docs.forEach((Consumer<? super Document>) document -> {
                Listing listing = new Listing(document, getNameFromUUIDCache(document.getString("Seller")));
                if (listing.getItemStack() == null) return;
                listings.add(listing);
            });
            return listings;
        }
    }

    @Override
    public PaginationList getListingsPagination() {
        List<Text> texts = new ArrayList<>();
        getListings().forEach(listing -> texts.add(listing.getListingsText()));
        return market.getPaginationService().builder().contents(texts).title(Texts.MARKET_LISTINGS).build();
    }

    @Override
    public StateContainer getListingsGUI() {
        StateContainer sc = new StateContainer();
        Page.PageBuilder p = Page.builder().setAutoPaging(true).setTitle(Texts.MARKET_BASE).setInventoryDimension(InventoryDimension.of(6,6)).setEmptyStack(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.DISPLAY_NAME, Text.of("")).build());
        getListings().forEach(listing -> p.addElement(listing.getActionableElement(sc)));
        sc.setInitialState(p.build("0"));
        sc.addState(new State("1"));
        return sc;
    }

    @Override
    public List<ItemStack> removeListing(String id, String uuid, boolean staff) {
        try (MongoClient client = getClient()) {
            Document doc = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.eq("ID", id)).first();
            if (doc == null) return null;
            else {
                //check to see if the uuid matches the seller, or the user is a staff member
                if (!doc.getString("Seller").equals(uuid) && !staff) return null;
                //get how much stock it has
                int inStock = doc.getInteger("Stock");
                //deserialize the item
                ItemStack listingIS = market.deserializeItemStack(doc.getString("Item")).get();
                //calculate the amount of stacks to make
                int stacksInStock = inStock / listingIS.getMaxStackQuantity();
                //new list for stacks
                List<ItemStack> stacks = new ArrayList<>();
                //until all stacks are pulled out, keep adding more stacks to stacks
                for (int i = 0; i < stacksInStock; i++) {
                    stacks.add(listingIS.copy());
                }
                if (inStock % listingIS.getMaxStackQuantity() != 0) {
                    ItemStack extra = listingIS.copy();
                    extra.setQuantity(inStock % listingIS.getMaxStackQuantity());
                    stacks.add(extra);
                }
                //remove from the listings
                client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).deleteOne(Filters.eq("ID", id));
                return stacks;
            }
        }
    }

    @Override
    public PaginationList getListing(String id) {
        try (MongoClient client = getClient()) {
            Document listing = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.eq("ID", id)).first();
            //if the item is not for sale, do not get the listing
            if (listing == null) return null;
            //create list of Texts for pages
            List<Text> texts = new ArrayList<>();
            //replace with item if key is "Item", replace uuid with name from Vote4Dis cache.
            listing.forEach((key, value) -> {
                switch (key) {
                    case "Item":
                        texts.add(Texts.quickItemFormat(market.deserializeItemStack((String) value).get()));
                        break;
                    case "Seller":
                        texts.add(Text.of("Seller: " + getNameFromUUIDCache((String) value)));
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

            return market.getPaginationService().builder().title(Texts.MARKET_LISTING(id)).contents(texts).build();
        }
    }

    @Override
    public boolean addStock(ItemStack itemStack, String id, UUID uuid) {
        try (MongoClient client = getClient()) {
            Document listing = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.eq("ID", id)).first();
            if (listing == null) return false;
            else if (!listing.getString("Seller").equals(uuid.toString())) return false;
            else {
                ItemStack listingStack = market.deserializeItemStack(listing.getString("Item")).get();
                //if the stack in the listing matches the stack it's trying to add, add it to the stack
                if (market.matchItemStacks(listingStack, itemStack)) {
                    int stock = listing.getInteger("Stock");
                    int quan = itemStack.getQuantity() + stock;
                    listing.put("Stock", stock);
                    client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).replaceOne(Filters.eq("ID", id), listing);
                    return true;
                } else return false;
            }
        }
    }

    @Override
    public ItemStack purchase(UniqueAccount uniqueAccount, String id) {
        try (MongoClient client = getClient()) {
            Document listing = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.eq("ID", id)).first();
            if (listing == null) return null;
            else {
                TransactionResult tr = uniqueAccount.transfer(market.getEconomyService().getOrCreateAccount(listing.getString(listing.getString("Seller"))).get(), market.getEconomyService().getDefaultCurrency(), BigDecimal.valueOf(listing.getLong("Price")), Cause.of(market.marketCause));
                if (tr.getResult().equals(ResultType.SUCCESS)) {
                    //get the itemstack
                    ItemStack is = market.deserializeItemStack(listing.getString("Item")).get();
                    //get the quantity per sale
                    int quant = listing.getInteger("Quantity");
                    //get the amount in stock
                    int inStock = listing.getInteger("Stock");
                    //get the new quantity
                    int newQuant = inStock - quant;
                    //if the new quantity is less than the quantity to be sold, expire the listing
                    if (newQuant < quant) {
                        client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).deleteOne(Filters.eq("ID", id));
                    } else {
                        listing.put("Stock", newQuant);
                        client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).replaceOne(Filters.eq("ID", id), listing);
                    }
                    ItemStack nis = is.copy();
                    nis.setQuantity(quant);
                    return nis;
                } else {
                    return null;
                }
            }
        }
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
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.all("tags", "Item"));
            List<Text> texts = new ArrayList<>();
            docs.forEach((Consumer<? super Document>) document -> {
                Text.Builder l = Text.builder();
                Optional<ItemStack> is = market.deserializeItemStack(document.getString("Item"));
                if (!is.isPresent()) return;
                if (is.get().getItem().equals(itemType)) {
                    l.append(Texts.quickItemFormat(is.get()));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.WHITE, "@"));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.GREEN, "$" + document.getString("Price")));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.WHITE, "for"));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.GREEN, document.getString("Quantity") + "x"));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.WHITE, "Seller:"));
                    l.append(Text.of(TextColors.LIGHT_PURPLE, " " + getNameFromUUIDCache(document.getString("Seller"))));
                    l.append(Text.of(" "));
                    l.append(Text.builder()
                            .color(TextColors.GREEN)
                            .onClick(TextActions.runCommand("/market check " + document.getString("ID")))
                            .append(Text.of("[Info]"))
                            .onHover(TextActions.showText(Text.of("View more info about this listing.")))
                            .build());
                    texts.add(l.build());
                }
            });
            if (texts.size() == 0) texts.add(Text.of(TextColors.RED, "No listings found."));
            return market.getPaginationService().builder().contents(texts).title(Texts.MARKET_LISTINGS).build();
        }
    }

    @Override
    public PaginationList searchForUUID(UUID uniqueId) {
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(MongoCollections.marketListings).find(Filters.eq("Seller", uniqueId));
            List<Text> texts = new ArrayList<>();
            docs.forEach((Consumer<? super Document>) document -> {
                Text.Builder l = Text.builder();
                Optional<ItemStack> is = market.deserializeItemStack(document.getString("Item"));
                is.ifPresent(itemStack -> {
                    l.append(Texts.quickItemFormat(itemStack));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.WHITE, "@"));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.GREEN, "$" + document.getString("Price")));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.WHITE, "for"));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.GREEN, document.get("Quantity") + "x"));
                    l.append(Text.of(" "));
                    l.append(Text.of(TextColors.WHITE, "Seller:"));
                    l.append(Text.of(TextColors.LIGHT_PURPLE, " " + getNameFromUUIDCache(getNameFromUUIDCache(document.getString("Seller")))));
                    l.append(Text.of(" "));
                    l.append(Text.builder()
                            .color(TextColors.GREEN)
                            .onClick(TextActions.runCommand("/market check " + document.getInteger("ID")))
                            .append(Text.of("[Info]"))
                            .onHover(TextActions.showText(Text.of("View more info about this listing.")))
                            .build());
                    texts.add(l.build());
                });
            });
            if (texts.size() == 0) texts.add(Text.of(TextColors.RED, "No listings found."));
            return market.getPaginationService().builder().contents(texts).title(Texts.MARKET_LISTINGS).build();
        }
    }

    @Override
    public void updateUUIDCache(Player player) {
        updateUUIDCache(player.getUniqueId().toString(), player.getName());
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

    private String getNameFromUUIDCache(String uuid) {
        try (MongoClient client = getClient()) {
            Document found = client.getDatabase(databaseName).getCollection(MongoCollections.uuidCache).find(Filters.eq("uuid", uuid)).first();
            return found.getString("name");
        }
    }
}
