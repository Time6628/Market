package com.kookykraftmc.market.config;

import com.google.common.reflect.TypeToken;
import com.kookykraftmc.market.Market;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.nio.file.Files;

public class ConfigLoader {

    private Market market;
    private MarketConfig marketConfig;

    public ConfigLoader(Market market) {
        this.market = market;
    }

    public boolean loadConfig() {
        try {
            File file = null;
            Files.createDirectories(this.market.configDir.getParent());
            if (Files.notExists(market.configDir)) {
                file = Files.createFile(market.configDir).toFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode cfg = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(market.factory).setShouldCopyDefaults(true));
            marketConfig = cfg.getValue(TypeToken.of(MarketConfig.class), new MarketConfig());
            loader.save(cfg);
            return true;
        } catch (Exception e) {
            market.getLogger().error("Could not load config.", e);
            return false;
        }
    }

    public MarketConfig getMarketConfig() {
        return marketConfig;
    }
}
