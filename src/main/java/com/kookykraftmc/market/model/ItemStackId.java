package com.kookykraftmc.market.model;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;


/**
 * <p>The format of the ItemStackId may vary between implementations
 * but in Minecraft, it follows the format of {@code domain:type}, an
 * example being {@code minecraft:stone}.</p>
 */
public class ItemStackId {
    private String id;

    public ItemStackId(String id) {
        this.id = id;
    }

    public String get() {
        return id;
    }

    public static ItemStackId from(ItemStack itemStack) {
        Optional<BlockType> type = itemStack.getType().getBlock();
        return new ItemStackId(type.map(blockType -> blockType.getDefaultState().getId()).orElseGet(() -> itemStack.getType().getId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemStackId)) return false;
        ItemStackId that = (ItemStackId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
