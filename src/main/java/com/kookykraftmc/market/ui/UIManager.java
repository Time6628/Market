package com.kookykraftmc.market.ui;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.kookykraftmc.market.config.Texts;
import com.kookykraftmc.market.model.Listing;
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
        //State initalState = null;
        if (listings.size() > 45) {
            int amount = listings.size() / 45;
            int leftover = listings.size() % 45;
            for (int i = 0, j = 1; i >= amount; i++, j++) {
                Page.PageBuilder p = Page.builder().setAutoPaging(true).setTitle(Texts.MARKET_BASE).setInventoryDimension(InventoryDimension.of(6,6)).setEmptyStack(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.DISPLAY_NAME, Text.of("")).build());
                List<Listing> subList = listings.subList(i * 45, j * 45);
                subList.forEach(listing -> p.addElement(new ListingUI(listing).getActionableElement(sc)));
                System.out.println(i);
                sc.addState(p.build(String.valueOf(i)));
            }

            if (leftover != 0) {
                Page.PageBuilder p = Page.builder().setAutoPaging(true).setTitle(Texts.MARKET_BASE).setInventoryDimension(InventoryDimension.of(6,6)).setEmptyStack(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.DISPLAY_NAME, Text.of("")).build());
                List<Listing> subList = listings.subList(amount + 1, listings.size());
                subList.forEach(listing -> p.addElement(new ListingUI(listing).getActionableElement(sc)));
            }
        } else {
            Page.PageBuilder p = Page.builder().setAutoPaging(true).setTitle(Texts.MARKET_BASE).setInventoryDimension(InventoryDimension.of(6,6)).setEmptyStack(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GREEN).add(Keys.DISPLAY_NAME, Text.of("")).build());
            listings.forEach(listing -> p.addElement(new ListingUI(listing).getActionableElement(sc)));
            sc.addState(p.build("0"));
        }
        return sc;
    }
}
