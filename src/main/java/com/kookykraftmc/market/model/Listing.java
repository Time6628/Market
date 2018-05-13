package com.kookykraftmc.market.model;

import com.kookykraftmc.market.repositories.sql.Identifiable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Listing implements Identifiable {

    private String id;
    private ItemStack itemStack;
    private UUID seller;
    private int stock;
    private int price;
    private int quantityPerSale;

    public Listing(ItemStack itemStack, UUID seller, int stock, int price, int quantityPerSale) {
        this.itemStack = itemStack;
        this.seller = seller;
        this.stock = stock;
        this.price = price;
        this.quantityPerSale = quantityPerSale;
    }

    public Listing(String id, ItemStack itemStack, UUID seller, int stock, int price, int quantityPerSale) {
        this(itemStack, seller, stock, price, quantityPerSale);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public <ID> void setId(ID id) {
        this.id = id.toString();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }


    public UUID getSeller() {
        return seller;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantityPerSale() {
        return quantityPerSale;
    }

    public Map<String, ?> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("ID", this.id);
        map.put("Price", Integer.toString(this.price));
        map.put("Stock", Integer.toString(this.stock));
        map.put("Quantity", Integer.toString(this.quantityPerSale));
        return null;
    }
}
