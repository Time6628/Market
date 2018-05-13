package com.kookykraftmc.market.repositories.mongo;

import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;

import java.util.stream.Stream;

@Singleton
public class MongoBlackListRepository implements BlackListRepository<MarketConfig.MongoDataStoreConfig> {
    @Override
    public void init(MarketConfig.MongoDataStoreConfig mongoDataStoreConfig) {

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
