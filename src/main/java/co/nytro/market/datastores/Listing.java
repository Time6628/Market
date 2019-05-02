package co.nytro.market.datastores;

import co.nytro.market.config.Texts;
import co.nytro.market.Market;
import co.nytro.market.datastores.dynamodb.DynamoDBListing;
import org.bson.Document;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Listing {

    private final ItemStack itemStack;
    private final int price;
    private final int quantity;
    private final UUID seller;
    private final String sellerName;
    private final String id;
    private final int stock;
    private final boolean server;
    private final Map<String, ?> source;

    public Listing(Map<String, String> r, String id, String sellerName) {
        Optional<ItemStack> is = Market.instance.deserializeItemStack(r.get("Item"));
        this.itemStack = is.orElse(null);
        this.price = Integer.parseInt(r.get("Price"));
        this.quantity = Integer.parseInt(r.get("Quantity"));
        this.seller = UUID.fromString(r.get("Seller"));
        this.stock = Integer.parseInt(r.get("Stock"));
        this.id = id;
        this.sellerName = sellerName;
        this.server = Boolean.valueOf(r.getOrDefault("Server", String.valueOf(false)));
        this.source = r;
    }


    public Listing(Document doc, String sellerName) {
        Optional<ItemStack> is = Market.instance.deserializeItemStack(doc.getString("Item"));
        this.itemStack = is.orElse(null);
        this.price = doc.getInteger("Price");
        this.quantity = doc.getInteger("Quantity");
        this.seller = UUID.fromString(doc.getString("Seller"));
        this.id = String.valueOf(doc.getInteger("ID"));
        this.stock = doc.getInteger("Stock");
        this.sellerName = sellerName;
        this.server = doc.getBoolean("Server", false);
        this.source = doc;
    }

    public Listing(DynamoDBListing listing, String sellerName) {
        Optional<ItemStack> is = Market.instance.deserializeItemStack(listing.getItemStack());
        this.itemStack = is.orElse(null);
        this.price = listing.getPrice();
        this.quantity = listing.getQuantity();
        this.seller = UUID.fromString(listing.getSeller());
        this.id = listing.getID();
        this.stock = listing.getStock();
        this.sellerName = sellerName;
        this.server = listing.isServer();
        this.source = (Map<String, ?>) listing;
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

    public Map<String, ?> getSource() {
        return source;
    }

    public String getSellerName() {
        return sellerName;
    }

    public int getStock() {
        return stock;
    }
}
