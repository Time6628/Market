package com.kookykraftmc.market.repositories.dynamo;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.inject.Inject;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;
import com.kookykraftmc.market.service.ItemSerializer;
import org.slf4j.Logger;

import java.util.stream.Stream;

public class DynamoBlackListRepository implements BlackListRepository<MarketConfig.DynamoDataStoreConfig> {

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
        AmazonDynamoDBAsync client = builder.build();
        dynamoDB = new DynamoDB(client);
        mapper = new DynamoDBMapper(client);
    }

    @Override
    public Stream<BlackListItem> all() {
        return null;
    }

    @Override
    public boolean deleteById(ItemStackId id) {
        return false;
    }

    @Override
    public boolean add(BlackListItem blackListItem) {
        return false;
    }
}
