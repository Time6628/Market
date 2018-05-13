package com.kookykraftmc.market.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.ConfigLoader;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.repositories.dynamo.DynamoBlackListRepository;
import com.kookykraftmc.market.repositories.dynamo.DynamoListingRepository;
import com.kookykraftmc.market.repositories.mongo.MongoBlackListRepository;
import com.kookykraftmc.market.repositories.redis.RedisBlackListRepository;
import com.kookykraftmc.market.repositories.redis.RedisListingRepository;
import com.kookykraftmc.market.repositories.mongo.MongoListingRepository;
import com.kookykraftmc.market.repositories.sql.SQLBlackListRepository;
import com.kookykraftmc.market.repositories.sql.SQLListingRepository;
import org.slf4j.Logger;

@Singleton
public class RepositoryProvider {

    @Inject
    private Logger logger;

    private BlackListRepository blackListRepository;
    private ListingRepository listingRepository;

    @Inject private RedisBlackListRepository redisBlackListRepository;
    @Inject private RedisListingRepository redisListingRepository;
    @Inject private MongoBlackListRepository mongoBlackListRepository;
    @Inject private MongoListingRepository mongoListingRepository;
    @Inject private DynamoBlackListRepository dynamoBlackListRepository;
    @Inject private DynamoListingRepository dynamoListingRepository;
    @Inject private SQLBlackListRepository sqlBlackListRepository;
    @Inject private SQLListingRepository sqlListingRepository;

    @Inject
    private ConfigLoader configLoader;

    @Inject
    public void init() {
        if (configLoader.loadConfig()){
            MarketConfig cfg = configLoader.getMarketConfig();

            switch (cfg.dataStore) {
                case "redis":
                    logger.info("Redis enabled.");
                    this.blackListRepository = redisBlackListRepository;
                    redisBlackListRepository.init(cfg.redis);
                    this.listingRepository = redisListingRepository;
                    redisListingRepository.init(cfg.redis);
                    break;
                case "mongo":
                    logger.info("MongoDB enabled.");
                    this.blackListRepository = this.mongoBlackListRepository;
                    mongoBlackListRepository.init(cfg.mongo);
                    this.listingRepository = this.mongoListingRepository;
                    mongoListingRepository.init(cfg.mongo);
                    break;
                case "dynamo":
                    logger.info("DynamoDB enabled.");
                    this.blackListRepository = this.dynamoBlackListRepository;
                    dynamoBlackListRepository.init(cfg.dynamodb);
                    this.listingRepository = this.dynamoListingRepository;
                    dynamoListingRepository.init(cfg.dynamodb);
                    break;
                case "sql":
                    logger.info("SQL enabled.");
                    this.blackListRepository = this.sqlBlackListRepository;
                    sqlBlackListRepository.init(cfg.sqldb);
                    this.listingRepository = this.sqlListingRepository;
                    sqlListingRepository.init(cfg.sqldb);
                    break;
            }
        }

    }

    public BlackListRepository<?> getBlackListRepository() {
        return blackListRepository;
    }

    public ListingRepository<?> getListingRepository() {
        return listingRepository;
    }
}
