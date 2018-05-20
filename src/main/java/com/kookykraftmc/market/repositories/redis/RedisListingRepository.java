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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
    public Optional<Listing> upsert(Listing listing) {
        try (Jedis jedis = jedisPool.getResource()) {
            listing.setId(getOrCreateId(listing, jedis));

            String key = marketItemKey(listing.getId());
            Transaction m = jedis.multi();
            m.hset(key, "Item", itemSerializer.serializeItem(listing.getItemStack()));
            m.hset(key, "Seller", listing.getSeller().toString());
            m.hset(key, "Stock", String.valueOf(listing.getStock()));
            m.hset(key, "Price", String.valueOf(listing.getPrice()));
            m.hset(key, "Quantity", String.valueOf(listing.getQuantityPerSale()));
            m.exec();

            jedis.hset(forSale(), listing.getId(), listing.getSeller().toString());

            return Optional.ofNullable(listing);
        }
    }

    private String getOrCreateId(Listing listing, Jedis jedis) {
        if (isNotBlank(listing.getId())) return listing.getId();
        if (!jedis.exists(lastMarketId())) {
            jedis.set(lastMarketId(), String.valueOf(1));
        }
        String id = jedis.get(lastMarketId());
        jedis.incr(lastMarketId());
        return id;
    }

    @Override
    public Stream<Listing> all() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> openListings = jedis.hgetAll(forSale()).keySet();
            List<Listing> listings = new ArrayList<>();
            openListings.forEach(listingId -> {
                Map<String, String> listing = jedis.hgetAll(marketItemKey(listingId));
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
            if (!jedis.hexists(forSale(), id)) return Optional.empty();
            return Optional.of(toListing(jedis.hgetAll(marketItemKey(id)), id));
        }
    }

    public void deleteById(String listingId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(forSale(), listingId);
            jedis.del(marketItemKey(listingId));
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

    private String lastMarketId() {
        return "market:" + this.serverName + ":lastID";
    }

    private String marketItemKey(String id) {
        return "market:" + this.serverName + ":" + id;
    }

    private String forSale() {
        return "market:" + this.serverName + ":open";
    }
}
