package co.nytro.market.datastores.redis;

import co.nytro.market.Market;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
public class RedisKeys {
    static final String LAST_MARKET_ID = "market:" + Market.instance.getServerName() + ":lastID";
    static final String FOR_SALE = "market:" + Market.instance.getServerName() + ":open";
    static final String BLACKLIST = "market:blacklist";
    public static String UUID_CACHE = "market:uuidcache";

    static String MARKET_ITEM_KEY(String id) {
        return "market:" + Market.instance.getServerName() + ":" + id;
    }
}
