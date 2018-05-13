package com.kookykraftmc.market.ui;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.CommandAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.kookykraftmc.market.config.Texts;
import com.kookykraftmc.market.model.Listing;
import com.kookykraftmc.market.service.UuidCacheService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class ListingUI {

    private final Listing listing;

    public ListingUI(Listing listing) {
        this.listing = listing;
    }

    public ActionableElement getActionableElement(StateContainer sc) {
        ItemStack i = listing.getItemStack().copy();
        i.setQuantity(listing.getQuantityPerSale());
        List<Text> lore = new ArrayList<>();
        lore.add(Texts.guiListing.apply(listing.toMap()).build());
        String playerName = Sponge.getServiceManager().provide(UuidCacheService.class).get().getName(listing.getSeller());
        lore.add(Text.builder().color(TextColors.WHITE).append(Text.of("Seller: " + playerName)).build());

        i.offer(Keys.ITEM_LORE, lore);
        CommandAction ca = new CommandAction(sc, ActionType.CLOSE, "0", "market check " + listing.getId(), CommandAction.CommandReceiver.PLAYER);
        return new ActionableElement(ca, i);
    }
}
