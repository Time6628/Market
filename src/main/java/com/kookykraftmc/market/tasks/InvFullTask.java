package com.kookykraftmc.market.tasks;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.Texts;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;

import java.util.concurrent.TimeUnit;

/**
 * Created by TimeTheCat on 4/3/2017.
 */
public class InvFullTask implements Runnable {
    private final Market pl = Market.instance;
    private final Player player;
    private final ItemStack item;

    public InvFullTask(ItemStack item, Player player) {
        this.item = item;
        this.player = player;
    }

    @Override
    public void run() {
        InventoryTransactionResult offer = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class), QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class)).offer(item);
        if (!offer.getType().equals(InventoryTransactionResult.Type.SUCCESS)) {
            player.sendMessage(Texts.INV_FULL);
            pl.getScheduler().createTaskBuilder()
                    .name("Market Delivery")
                    .execute(new InvFullTask(item, player))
                    .delay(30, TimeUnit.SECONDS)
                    .submit(pl);
        } else {
            player.sendMessage(Texts.PURCHASE_SUCCESSFUL);
        }
    }
}
