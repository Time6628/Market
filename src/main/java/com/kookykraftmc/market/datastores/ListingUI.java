package com.kookykraftmc.market.datastores;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.CommandAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.kookykraftmc.market.Texts;
import org.bson.Document;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListingUI extends Listing {
    public ListingUI(Map<String, String> r, String id, String sellerName) {
        super(r, id, sellerName);
    }

    public ListingUI(Document doc, String sellerName) {
        super(doc, sellerName);
    }

    public ActionableElement getActionableElement(StateContainer sc) {
        ItemStack i = getItemStack().copy();
        i.setQuantity(getQuantity());
        List<Text> lore = new ArrayList<>();
        lore.add(Texts.guiListing.apply(getSource()).build());
        lore.add(Text.builder().color(TextColors.WHITE).append(Text.of("Seller: " + getSellerName())).build());

        i.offer(Keys.ITEM_LORE, lore);
        CommandAction ca = new CommandAction(sc, ActionType.CLOSE, "0", "market check " + getId(), CommandAction.CommandReceiver.PLAYER);
        return new ActionableElement(ca, i);
    }
}
