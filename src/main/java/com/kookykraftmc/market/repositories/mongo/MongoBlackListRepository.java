package com.kookykraftmc.market.repositories.mongo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;
import org.slf4j.Logger;

import java.util.stream.Stream;

@Singleton
public class MongoBlackListRepository implements BlackListRepository<MarketConfig.MongoDataStoreConfig> {
    @Inject
    private Logger logger;

    @Override
    public void init(MarketConfig.MongoDataStoreConfig dynamoDataStoreConfig) {
        logger.error("Blacklist not implemented yet on MongoDB");
    }

    @Override
    public Stream<BlackListItem> all() {
        return Stream.empty();
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
