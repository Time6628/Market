package com.kookykraftmc.market.config;


import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MarketConfig {

    @Setting(value = "Server", comment = "Name of the server to be used when storing data.")
    public String server = "TEST";

    @Setting(value = "DataStore", comment = "Set to 'redis' if you are using redis, set to 'mongo' if you are using MongoDB.")
    public String dataStore = "redis";

    @Setting("Redis")
    public RedisDataStore redis = new RedisDataStore();

    @Setting("MongoDB")
    public MongoDataStore mongo = new MongoDataStore();

    @Setting(value = "Chest-Is-Default", comment = "Should the chest GUI be the default gui instead of the chat gui.")
    public boolean chestDefault = false;


    @ConfigSerializable
    public static class RedisDataStore {

        @Setting("Host")
        public String host = "localhost";

        @Setting("Port")
        public int port = 6379;

        @Setting("Password")
        public String password = "";

        @Setting("Keys")
        public Keys keys = new Keys();

        @ConfigSerializable
        public static class Keys {

            @Setting(value = "UUID-Cache")
            public String uuidCache = "market:uuidcache";
        }
    }

    @ConfigSerializable
    public static class MongoDataStore {

        @Setting("Host")
        public String host = "localhost";

        @Setting("Port")
        public int port = 27017;

        @Setting("User")
        public String username = "admin";

        @Setting("Password")
        public String password = "";

        @Setting("DataBase")
        public String database = "database";
    }
}
