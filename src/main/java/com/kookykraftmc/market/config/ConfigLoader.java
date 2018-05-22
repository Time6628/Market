package com.kookykraftmc.market.config;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;

import java.io.File;

@Singleton
public class ConfigLoader {

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    private Logger logger;

    private MarketConfig marketConfig;
    private Texts texts;


    @Inject
    public void postConstruct() {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public boolean loadConfig() {
        if (marketConfig != null) return true;
        try {
            File file = new File(configDir, "market.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
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
            File file = new File(configDir, "messages.conf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
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
