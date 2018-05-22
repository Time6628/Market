package com.kookykraftmc.market.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MarketConfig {



    @Setting(value = "DataStore", comment = "Set to 'redis' if you are using redis, set to 'mongo' if you are using MongoDB.")
    public String dataStore = "sql";

    @Setting("Redis")
    public RedisDataStoreConfig redis = new RedisDataStoreConfig();

    @Setting("MongoDB")
    public MongoDataStoreConfig mongo = new MongoDataStoreConfig();

    @Setting("DynamoDB")
    public DynamoDataStoreConfig dynamodb = new DynamoDataStoreConfig();

    @Setting("Sql")
    public SQLDataStoreConfig sqldb = new SQLDataStoreConfig();

    @Setting(value = "Chest-Is-Default", comment = "Should the chest GUI be the default gui instead of the chat gui.")
    public boolean chestDefault = false;


    @ConfigSerializable
    public static class RedisDataStoreConfig {

        @Setting("Host")
        public String host = "localhost";

        @Setting("Port")
        public int port = 6379;

        @Setting("Password")
        public String password = "";

        @Setting(value = "Server", comment = "Name of the server to be used when storing data.")
        public String server = "TEST";

    }

    @ConfigSerializable
    public static class MongoDataStoreConfig {

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

    @ConfigSerializable
    public static class DynamoDataStoreConfig {

        @Setting("Region")
        public String region = "us-east-1";
    }

    @ConfigSerializable
    public static class SQLDataStoreConfig {

        @Setting("DataBase")
        public String dbUri = "jdbc:h2:./market.db";
    }
}
