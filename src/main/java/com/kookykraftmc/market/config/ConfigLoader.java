package com.kookykraftmc.market.config;

import com.google.common.reflect.TypeToken;
import com.kookykraftmc.market.Market;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;

public class ConfigLoader {

    private Market market;
    private MarketConfig marketConfig;

    public ConfigLoader(Market market) {
        this.market = market;
        if (!this.market.configDir.exists()) this.market.configDir.mkdir();
    }

    public boolean loadConfig() {
        try {
            File file = new File(market.configDir, "market.conf");
            if (!file.exists()) file.createNewFile();
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
