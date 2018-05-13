package com.kookykraftmc.market.model;

import java.util.Objects;

public class BlackListItem {

    private ItemStackId id;

    public BlackListItem(ItemStackId id) {
        this.id = id;
    }

    public ItemStackId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlackListItem)) return false;
        BlackListItem that = (BlackListItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
