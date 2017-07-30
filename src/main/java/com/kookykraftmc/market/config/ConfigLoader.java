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

    private final Market market;
    private MarketConfig marketConfig;

    public ConfigLoader(Market market) {
        this.market = market;
        if (!market.configDir.exists()) {
            market.configDir.mkdirs();
        }
    }

    public boolean loadConfig() {
        try {
            File file = new File(market.configDir, "market.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(market.factory).setShouldCopyDefaults(true));
            marketConfig = config.getValue(TypeToken.of(MarketConfig.class), new MarketConfig());
            loader.save(config);
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
