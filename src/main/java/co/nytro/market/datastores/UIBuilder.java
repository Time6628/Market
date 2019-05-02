package co.nytro.market.datastores;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import co.nytro.market.config.Texts;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.CommandAction;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class UIBuilder {

    public static StateContainer getStateContainer(List<Listing> listings) {
        StateContainer sc = new StateContainer();
        //State initalState = null;
        for (Listing listing : listings) {
            Page.PageBuilder p = Page.builder()
                    .setAutoPaging(true)
                    .setTitle(Texts.MARKET_BASE)
                    .setInventoryDimension(InventoryDimension.of(9, 6))
                    .setEmptyStack(ItemStack.builder()
                            .itemType(ItemTypes.STAINED_GLASS_PANE)
                            .add(Keys.DYE_COLOR, DyeColors.GREEN)
                            .add(Keys.DISPLAY_NAME, Text.of("")
                            ).build());
            p.addElement(getElementFromListing(listing, sc));
            sc.addState(p.build("Main Market"));
        }
        return sc;
    }

    private static ActionableElement getElementFromListing(Listing listing, StateContainer sc) {
        ItemStack i = listing.getItemStack().copy();
        i.setQuantity(listing.getQuantity());
        List<Text> lore = new ArrayList<>();
        lore.add(Texts.guiListing.apply(listing.getSource()).build());
        lore.add(Text.builder().color(TextColors.WHITE).append(Text.of("Seller: " + listing.getSellerName())).build());
        i.offer(Keys.ITEM_LORE, lore);
        CommandAction ca = new CommandAction(sc, ActionType.CLOSE, "Main Market", "market check " + listing.getId(), CommandAction.CommandReceiver.PLAYER);
        return new ActionableElement(ca, i);
    }
}
