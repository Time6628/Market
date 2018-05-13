package com.kookykraftmc.market.repositories.dynamo;

import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;

import java.util.stream.Stream;

public class DynamoBlackListRepository implements BlackListRepository<MarketConfig.DynamoDataStoreConfig> {

    @Override
    public void init(MarketConfig.DynamoDataStoreConfig dynamoDataStoreConfig) {

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
