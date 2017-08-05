package com.kookykraftmc.market.datastores;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.kookykraftmc.market.config.Texts;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;

import java.util.List;

public class UIManager {

    public static StateContainer getStateContainer(List<Listing> listings) {
        StateContainer sc = new StateContainer();
        Page.PageBuilder p = Page.builder().setAutoPaging(true).setTitle(Texts.MARKET_BASE).setInventoryDimension(InventoryDimension.of(6,6)).setEmptyStack(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.DISPLAY_NAME, Text.of("")).build());
        listings.forEach(listing -> p.addElement(new ListingUI(listing).getActionableElement(sc)));
        sc.setInitialState(p.build("0"));
        return sc;
    }
}
