package co.nytro.market.datastores.mongo;

import co.nytro.market.Market;

class MongoCollections {
    public static final String marketListings = "market:" + Market.instance.getServerName() + ":listings";

    public static final String marketInfo = "market:" + Market.instance.getServerName() + ":info";

    public static final String uuidCache = "market:" + Market.instance.getServerName() + ":uuidcache";
}
