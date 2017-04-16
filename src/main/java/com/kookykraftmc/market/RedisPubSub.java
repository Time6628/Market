package com.kookykraftmc.market;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
public class RedisPubSub extends JedisPubSub {
    Market market = Market.instance;

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals(Channels.marketBlacklistAdd)) market.addIDToBlackList(message);
        else if (channel.equals(Channels.marketBlacklistRemove)) market.rmIDFromBlackList(message);
    }

    public static class Channels {
        public static String marketBlacklistAdd = "market-blacklist-add"; //itemID
        public static String marketBlacklistRemove = "market-blacklist-remove"; //itemID
        public static String[] channels = { marketBlacklistAdd, marketBlacklistRemove };
    }
}
