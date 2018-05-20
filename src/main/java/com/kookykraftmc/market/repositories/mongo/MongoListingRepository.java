package com.kookykraftmc.market.repositories.mongo;

import com.google.common.collect.Streams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.repositories.ListingRepository;
import com.kookykraftmc.market.service.ItemSerializer;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class MongoListingRepository implements ListingRepository<MarketConfig.MongoDataStoreConfig> {

    private static final String[] marketInfoTags = new String[]{"lastID"};
    private static final String[] marketListingTags = new String[]{"Item", "Seller", "Stock", "Price", "Quantity", "ID"};
    private final String marketListings = "market:listings";
    private final String marketInfo = "market:info";
    @Inject
    private ItemSerializer itemSerializer;
    private MongoClient mongoClient;
    private String databaseName;

    @Override
    public void init(MarketConfig.MongoDataStoreConfig config) {
        MongoCredential cred = MongoCredential.createCredential(config.username, config.database, config.password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(config.host, config.port), Collections.singletonList(cred));
        databaseName = config.database;
        setupDB();
    }

    private void setupDB() {
        try (MongoClient client = getClient()) {
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(marketInfo);
            Document found = collection.find(Filters.all("tags", marketInfoTags)).first();
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
    public Optional<Listing> upsert(Listing listing) {
        try (MongoClient client = getClient()) {
            String id = listing.getId() != null ? listing.getId() : Integer.toString(setLastID(getLastID() + 1));
            Document doc = new Document();
            doc.append("Item", itemSerializer.serializeItem(listing.getItemStack()));
            doc.append("Seller", listing.getSeller().toString());
            doc.append("Stock", listing.getStock());
            doc.append("Price", listing.getPrice());
            doc.append("Quantity", listing.getQuantityPerSale());
            doc.append("ID", id);
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(marketListings);
            collection.updateOne(Filters.eq("ID", listing.getId()), doc, new UpdateOptions().upsert(true));
            listing.setId(id);
            return Optional.of(listing);
        }
    }

    @Override
    public Stream<Listing> all() {
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(marketListings)
                    .find(Filters.all("tags", marketListingTags));
            return Streams.stream(docs.map(this::toListing));
        }
    }

    @Override
    public Stream<Listing> findAllBySellerId(UUID sellerId) {
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(marketListings)
                    .find(Filters.eq("Seller", sellerId.toString()));
            return Streams.stream(docs.map(this::toListing));

        }
    }

    private Listing toListing(Document document) {
        return new Listing(
                document.getString("ID"),
                itemSerializer.deserializeItemStack(document.getString("Item")).get(),
                UUID.fromString(document.getString("Seller")),
                document.getInteger("Stock"),
                document.getInteger("Price"),
                document.getInteger("Quantity")
        );
    }

    @Override
    public Optional<Listing> getById(String id) {
        try (MongoClient client = getClient()) {
            FindIterable<Document> docs = client.getDatabase(databaseName).getCollection(marketListings)
                    .find(Filters.eq("ID", id));
            return Streams.stream(docs.map(this::toListing)).findFirst();
        }
    }

    @Override
    public void deleteById(String listingId) {
        try (MongoClient client = getClient()) {
            client.getDatabase(databaseName).getCollection(marketListings).deleteOne(Filters.eq("ID", listingId));
        }
    }

    private Integer getLastID() {
        try (MongoClient client = getClient()) {
            MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection(marketInfo);
            Document found = collection.find(Filters.all("tags", marketInfoTags)).first();
            return found.getInteger("lastID");
        }
    }

    private int setLastID(int lastID) {
        try (MongoClient client = getClient()) {
            Document found = client.getDatabase(databaseName).getCollection(marketInfo).find(Filters.all("tags", marketInfoTags)).first();
            found.put("lastID", lastID);
            client.getDatabase(databaseName).getCollection(marketInfo).replaceOne(Filters.all("tags", marketInfoTags), found);
        }

        return lastID;
    }

}
