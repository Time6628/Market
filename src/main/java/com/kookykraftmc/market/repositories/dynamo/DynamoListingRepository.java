package com.kookykraftmc.market.repositories.dynamo;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.repositories.ListingRepository;
import com.kookykraftmc.market.service.ItemSerializer;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Stream;

@Singleton
public class DynamoListingRepository implements ListingRepository<MarketConfig.DynamoDataStoreConfig> {

    private AmazonDynamoDBAsync client = null;
    private DynamoDB dynamoDB;
    private DynamoDBMapper mapper;

    @Inject
    private ItemSerializer itemSerializer;

    @Inject
    private Logger logger;

    @Override
    public void init(MarketConfig.DynamoDataStoreConfig dynamoDataStoreConfig) {
        AmazonDynamoDBAsyncClientBuilder builder = AmazonDynamoDBAsyncClientBuilder.standard();
        builder.setCredentials(new DefaultAWSCredentialsProviderChain());
        builder.setRegion(dynamoDataStoreConfig.region);
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
    public Listing insert(Listing listing) {
        try {
            DynamoDBListing dynamoDBListing = new DynamoDBListing();
            dynamoDBListing.setItemStack(itemSerializer.serializeItem(listing.getItemStack()));
            dynamoDBListing.setSeller(listing.getSeller().toString());
            dynamoDBListing.setPrice(listing.getPrice());
            dynamoDBListing.setQuantity(listing.getQuantityPerSale());
            dynamoDBListing.setStock(listing.getStock());
            mapper.save(listing);

            listing.setId(dynamoDBListing.getID());
            return listing;
        } catch (Exception e) {
            logger.error("Unable to save listing", e);
            return listing;
        }
    }

    @Override
    public Stream<Listing> all() {
        PaginatedScanList<DynamoDBListing> scan = mapper.scan(DynamoDBListing.class, new DynamoDBScanExpression());
        List<Listing> listings = new ArrayList<>();
        scan.forEach(dbList -> listings.add(toListing(dbList)));
        return listings.stream();
    }

    private Listing toListing(DynamoDBListing dbList) {
        return new Listing(
                dbList.getID(),
                itemSerializer.deserializeItemStack(dbList.getItemStack()).get(),
                UUID.fromString(dbList.getSeller()),
                dbList.getStock(),
                dbList.getPrice(),
                dbList.getQuantity()
        );
    }

    @Override
    public Optional<Listing> get(String listingId) {
        return getById(listingId).stream().map(this::toListing).findFirst();
    }

    @Override
    public void deleteById(String listingId) {
        PaginatedScanList<DynamoDBListing> listing = getById(listingId);

        listing.forEach(mapper::delete);
    }

    private PaginatedScanList<DynamoDBListing> getById(String listingId) {
        DynamoDBScanExpression dbse = new DynamoDBScanExpression()
                .withFilterExpression("ID = :id")
                .withExpressionAttributeValues(Collections.singletonMap(":id", new AttributeValue()
                        .withS(listingId)));
        return mapper.scan(DynamoDBListing.class, dbse);
    }
}
