package com.kookykraftmc.market.datastores.redis;

import com.kookykraftmc.market.Market;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
public class RedisKeys {
    public static final String LAST_MARKET_ID = "market:" + Market.instance.getServerName() + ":lastID";
    public static final String FOR_SALE = "market:" + Market.instance.getServerName() + ":open";
    public static final String BLACKLIST = "market:blacklist";
    public static String UUID_CACHE = "market:uuidcache";

    public static String MARKET_ITEM_KEY(String id) {
        return "market:" + Market.instance.getServerName() + ":" + id;
    }
}
