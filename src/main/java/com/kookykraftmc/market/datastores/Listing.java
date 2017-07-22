package com.kookykraftmc.market.datastores;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.Texts;
import org.bson.Document;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Listing {

    private ItemStack itemStack;
    private int price;
    private int quantity;
    private UUID seller;
    private String sellerName;
    private String id;

    public Listing(Map<String, String> r, String id, String sellerName) {
        Optional<ItemStack> is = Market.instance.deserializeItemStack(r.get("Item"));
        this.itemStack = is.orElse(null);
        this.price = Integer.parseInt(r.get("Price"));
        this.quantity = Integer.parseInt(r.get("Quantity"));
        this.seller = UUID.fromString(r.get("Seller"));
        this.id = id;
        this.sellerName = sellerName;
    }


    public Listing(Document doc, String sellerName) {
        Optional<ItemStack> is = Market.instance.deserializeItemStack(doc.getString("Item"));
        this.itemStack = is.orElse(null);
        this.price = doc.getInteger("Price");
        this.quantity = doc.getInteger("Quantity");
        this.seller = UUID.fromString(doc.getString("Seller"));
        this.id = String.valueOf(doc.getInteger("ID"));
    }

    public Text getListingsText() {
        return Text.builder()
                .append(Texts.quickItemFormat(itemStack))
                .append(Text.of(" "))
                .append(Text.of(TextColors.WHITE, "@"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.GREEN, "$" + price))
                .append(Text.of(" "))
                .append(Text.of(TextColors.WHITE, "for"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.GREEN, quantity + "x"))
                .append(Text.of(" "))
                .append(Text.of(TextColors.WHITE, "Seller:"))
                .append(Text.of(TextColors.LIGHT_PURPLE, " " + sellerName))
                .append(Text.of(" "))
                .append(Text.builder()
                        .color(TextColors.GREEN)
                        .onClick(TextActions.runCommand("/market check " + id))
                        .append(Text.of("[Info]"))
                        .onHover(TextActions.showText(Text.of("View more info about this listing.")))
                        .build())
                .build();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public UUID getSeller() {
        return seller;
    }

    public String getId() {
        return id;
    }
}
