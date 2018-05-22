package com.kookykraftmc.market.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.*;
import java.util.Optional;

@Singleton
public class ItemSerializer {

    @Inject
    private Logger logger;

    public String serializeItem(ItemStack itemStack) {
        ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(itemStack.toContainer());
        StringWriter stringWriter = new StringWriter();
        try {
            HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
        } catch (IOException e) {
            logger.warn("Could not serialize an item.", e);
        }
        return stringWriter.toString();
    }

    public Optional<ItemStack> deserializeItemStack(String item) {
        ConfigurationNode node;
        try {
            node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
            DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);
            return Sponge.getDataManager().deserialize(ItemStack.class, dataView);
        } catch (Exception e) {
            logger.warn("Could not deserialize an item.", e);
            return Optional.empty();
        }
    }
}
