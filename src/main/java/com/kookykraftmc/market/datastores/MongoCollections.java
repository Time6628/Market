package com.kookykraftmc.market.datastores;

import com.kookykraftmc.market.Market;

public class MongoCollections {
    public static String marketListings = "market:" + Market.instance.getServerName() + ":listings";

    public static String marketInfo = "market:" + Market.instance.getServerName() + ":info";

    public static String uuidCache = "market:" + Market.instance.getServerName() + ":uuidcache";
}
