package com.kookykraftmc.market.config;


import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MarketConfig {

    @Setting(value = "Version", comment = "Do not change this.")
    public double version = 0.2;

    @Setting(value = "Server", comment = "Name of the server to be used when storing data.")
    public final String server = "TEST";

    @Setting("Redis")
    public final RedisDataStore redis = new RedisDataStore();

    @Setting("MongoDB")
    public final MongoDataStore mongo = new MongoDataStore();

    @Setting(value = "Chest-Is-Default", comment = "Should the chest GUI be the default gui instead of the chat gui.")
    public final boolean chestDefault = false;


    @ConfigSerializable
    public static class RedisDataStore {

        @Setting("Enabled")
        public final boolean enabled = true;

        @Setting("Host")
        public final String host = "localhost";

        @Setting("Port")
        public final int port = 6379;

        @Setting("Password")
        public final String password = "";

        @Setting("Keys")
        public final Keys keys = new Keys();

        @ConfigSerializable
        public static class Keys {
            @Setting(value = "UUID-Cache")
            public final String uuidCache = "market:uuidcache";
        }
    }

    @ConfigSerializable
    public static class MongoDataStore {
        @Setting(value = "Enabled", comment = "You can have Redis or MongoDB enabled, but not both at the moment.")
        public final boolean enabled = false;

        @Setting("Host")
        public final String host = "localhost";

        @Setting("Port")
        public final int port = 27017;

        @Setting("User")
        public final String username = "admin";

        @Setting("Password")
        public final String password = "";

        @Setting("DataBase")
        public final String database = "database";
    }
}
