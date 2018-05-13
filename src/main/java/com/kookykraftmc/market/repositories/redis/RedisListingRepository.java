package com.kookykraftmc.market.repositories.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.repositories.ListingRepository;
import com.kookykraftmc.market.service.ItemSerializer;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.*;
import java.util.stream.Stream;

@Singleton
public class RedisListingRepository implements ListingRepository<MarketConfig.RedisDataStoreConfig> {
    private JedisPool jedisPool;

    @Inject
    private ItemSerializer itemSerializer;
    private String serverName;

    public void init(MarketConfig.RedisDataStoreConfig cfg) {
        this.jedisPool = setupRedis(cfg);
        this.serverName = cfg.server;
    }

    @Override
    public Optional<Listing> addListing(Listing listing) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.exists(LAST_MARKET_ID())) {
                jedis.set(LAST_MARKET_ID(), String.valueOf(1));
            }
            int id = Integer.parseInt(jedis.get(LAST_MARKET_ID()));
            String key = MARKET_ITEM_KEY(String.valueOf(id));
            Transaction m = jedis.multi();
            m.hset(key, "Item", itemSerializer.serializeItem(listing.getItemStack()));
            m.hset(key, "Seller", listing.getSeller().toString());
            m.hset(key, "Stock", String.valueOf(listing.getStock()));
            m.hset(key, "Price", String.valueOf(listing.getPrice()));
            m.hset(key, "Quantity", String.valueOf(listing.getQuantityPerSale()));
            m.exec();

            jedis.hset(FOR_SALE(), String.valueOf(id), listing.getSeller().toString());

            jedis.incr(LAST_MARKET_ID());

            listing.setId(Integer.toString(id));
            return Optional.ofNullable(listing);
        }
    }

    @Override
    public Stream<Listing> all() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> openListings = jedis.hgetAll(FOR_SALE()).keySet();
            List<Listing> listings = new ArrayList<>();
            openListings.forEach(listingId -> {
                Map<String, String> listing = jedis.hgetAll(MARKET_ITEM_KEY(listingId));
                Listing l = toListing(listing, listingId);
                if (l.getItemStack() != null) {
                    listings.add(l);
                }
            });
            return listings.stream();
        }
    }

    private Listing toListing(Map<String, String> listing, String id) {
        return new Listing(
                id,
                itemSerializer.deserializeItemStack(listing.get("Item")).orElse(null),
                UUID.fromString(listing.get("Seller")),
                Integer.parseInt(listing.get("Stock")),
                Integer.parseInt(listing.get("Price")),
                Integer.parseInt(listing.get("Quantity"))
        );
    }


    @Override
    public Optional<Listing> getById(String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            //if the item is not for sale, do not getById the listing
            if (!jedis.hexists(FOR_SALE(), id)) return Optional.empty();
            return Optional.of(toListing(jedis.hgetAll(MARKET_ITEM_KEY(id)), id));
        }
    }

    public void deleteById(String listingId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(FOR_SALE(), listingId);
            jedis.del(MARKET_ITEM_KEY(listingId));
        }
    }

    private JedisPool setupRedis(MarketConfig.RedisDataStoreConfig cfg) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        if (StringUtils.isNoneEmpty(cfg.password)) {
            return new JedisPool(config, cfg.host, cfg.port, 0, cfg.password);
        } else {
            return new JedisPool(config, cfg.host, cfg.port, 0);
        }
    }

    private String LAST_MARKET_ID() {
        return "market:" + this.serverName + ":lastID";
    }

    private String MARKET_ITEM_KEY(String id) {
        return "market:" + this.serverName + ":" + id;
    }

    private String FOR_SALE() {
        return "market:" + this.serverName + ":open";
    }
}
