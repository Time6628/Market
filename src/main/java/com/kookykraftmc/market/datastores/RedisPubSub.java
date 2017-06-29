package com.kookykraftmc.market.datastores;

import com.kookykraftmc.market.Market;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
public class RedisPubSub extends JedisPubSub {


    private final RedisDataStore dataStore;

    RedisPubSub(RedisDataStore redisDataStore) {
        this.dataStore = redisDataStore;
    }

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals(Channels.marketBlacklistAdd)) dataStore.addIDToBlackList(message);
        else if (channel.equals(Channels.marketBlacklistRemove)) dataStore.rmIDFromBlackList(message);
    }

    static class Channels {
        static String marketBlacklistAdd = "market-blacklist-add"; //itemID
        static String marketBlacklistRemove = "market-blacklist-remove"; //itemID
        static String[] channels = { marketBlacklistAdd, marketBlacklistRemove };
    }
}
