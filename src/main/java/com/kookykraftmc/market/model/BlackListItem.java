package com.kookykraftmc.market.model;

import com.kookykraftmc.market.repositories.sql.Identifiable;

import java.util.Objects;

public class BlackListItem implements Identifiable {

    private ItemStackId id;

    public BlackListItem(ItemStackId id) {
        this.id = id;
    }

    public ItemStackId getId() {
        return id;
    }

    public <ID> void setId(ID itemStackId) {
        if(itemStackId instanceof ItemStackId) {
            this.id = (ItemStackId) itemStackId;
        } else {
            this.id = new ItemStackId(itemStackId.toString());
        }
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
