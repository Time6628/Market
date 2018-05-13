package com.kookykraftmc.market.config;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kookykraftmc.market.Market;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;

import java.io.File;

@Singleton
public class ConfigLoader {

    @Inject
    private Market market;

    @Inject
    public GuiceObjectMapperFactory factory;

    @Inject
    private Logger logger;

    private MarketConfig marketConfig;
    private Texts texts;


    @Inject
    public void postConstruct() {
        if (!market.configDir.exists()) {
            market.configDir.mkdirs();
        }
    }

    public boolean loadConfig() {
        if(marketConfig != null) return true;
        try {
            File file = new File(market.configDir, "market.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(factory).setShouldCopyDefaults(true));
            marketConfig = config.getValue(TypeToken.of(MarketConfig.class), new MarketConfig());
            loader.save(config);
            return true;
        } catch (Exception e) {
            logger.error("Could not load config.", e);
            return false;
        }
    }

    public boolean loadTexts() {
        try {
            File file = new File(market.configDir, "messages.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(factory).setShouldCopyDefaults(true));
            texts = config.getValue(TypeToken.of(Texts.class), new Texts());
            loader.save(config);
            return true;
        } catch (Exception e) {
            logger.error("Could not load config.", e);
            return false;
        }
    }

    public MarketConfig getMarketConfig() {
        return marketConfig;
    }

    public Texts getTexts() {
        return texts;
    }
}
