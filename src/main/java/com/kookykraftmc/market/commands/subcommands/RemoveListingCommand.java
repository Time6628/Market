package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.Texts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

/**
 * Created by TimeTheCat on 3/19/2017.
 */
public class RemoveListingCommand implements CommandExecutor {
    private final Market pl = Market.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        Player player = (Player) src;
        Optional<String> oid = args.getOne(Text.of("id"));
        oid.ifPresent(s -> {
            List<ItemStack> is = pl.getDataStore().removeListing(s, player.getUniqueId().toString(), player.hasPermission("market.command.staff.removelisting"));
            if (is != null) {
                for (ItemStack i : is) {
                    player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class), QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class)).offer(i);
                }
                player.sendMessage(Text.of(TextColors.GREEN, "Removed listing " + s + "."));
            } else player.sendMessage(Texts.INVALID_LISTING);
        });
        return CommandResult.success();
    }
}
