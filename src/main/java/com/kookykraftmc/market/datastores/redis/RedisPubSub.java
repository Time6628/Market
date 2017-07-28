package com.kookykraftmc.market.datastores.redis;

import redis.clients.jedis.JedisPubSub;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
class RedisPubSub extends JedisPubSub {


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
        static final String marketBlacklistAdd = "market-blacklist-add"; //itemID
        static final String marketBlacklistRemove = "market-blacklist-remove"; //itemID
        static final String[] channels = { marketBlacklistAdd, marketBlacklistRemove };
    }
}
