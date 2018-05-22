package com.kookykraftmc.market.service;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Comparator;

public class ItemStackComparator implements Comparator<ItemStack> {

    public static int compareItemStack(ItemStack o1, ItemStack o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        ItemStack c1 = o1.copy(), c2 = o2.copy();
        c1.setQuantity(1);
        c2.setQuantity(1);
        return c1.equalTo(c2) ? 0 : 1;
    }

    public static boolean eq(ItemStack o1, ItemStack o2) {
        return compareItemStack(o1, o2) == 0;
    }

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        return ItemStackComparator.compareItemStack(o1, o2);
    }
}
