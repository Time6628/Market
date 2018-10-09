package com.kookykraftmc.market.commands.subcommands.blacklist;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.Texts;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

/**
 * Created by TimeTheCat on 3/26/2017.
 */
public class BlacklistAddCommand implements CommandExecutor {
    private final Market pl = Market.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) return CommandResult.empty();
        Optional<ItemStack> is = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);
        if (is.isPresent()) {
            ItemStack si = is.get();
            String id = null;
            if (si.supports(Keys.ITEM_BLOCKSTATE)) {
                Optional<BlockState> bs = si.get(Keys.ITEM_BLOCKSTATE);
                if (bs.isPresent()) {
                    id = bs.get().getId();
                }
            } else id = si.getItem().getId();
            boolean s = pl.getDataStore().blacklistAddCmd(id);
            if (s) {
                src.sendMessage(Texts.ADD_TO_BLACKLIST.apply(Collections.singletonMap("id", id)).build());
                return CommandResult.success();
            }
            throw new CommandException(Texts.BLACKLIST_NO_ADD_2);
        } else {
            throw new CommandException(Texts.BLACKLIST_NO_ADD);
        }
    }
}
