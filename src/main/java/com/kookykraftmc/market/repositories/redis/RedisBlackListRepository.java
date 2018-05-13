package com.kookykraftmc.market.repositories.redis;

import com.google.inject.Singleton;
import com.kookykraftmc.market.config.MarketConfig;
import com.kookykraftmc.market.model.BlackListItem;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.repositories.BlackListRepository;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;
import java.util.stream.Stream;

@Singleton
public class RedisBlackListRepository implements BlackListRepository<MarketConfig.RedisDataStoreConfig> {
    private static final String BLACKLIST = "market:blacklist";
    private JedisPool jedisPool;

    public void init(MarketConfig.RedisDataStoreConfig cfg) {
        this.jedisPool = setupRedis(cfg);
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

    @Override
    public Stream<BlackListItem> all() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> itemsUUID = jedis.hgetAll(BLACKLIST).keySet();
            return itemsUUID.stream().map(ItemStackId::new).map(BlackListItem::new);
        }
    }

    @Override
    public boolean deleteById(ItemStackId id) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.hexists(BLACKLIST, id.get())) {
                jedis.hdel(BLACKLIST, id.get());
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean add(BlackListItem blackListItem) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.hexists(BLACKLIST, blackListItem.getId().get())) {
                jedis.hset(BLACKLIST, blackListItem.getId().get(), String.valueOf(true));
                return true;
            } else
                return false;
        }
    }
}
