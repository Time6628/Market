package co.nytro.market.commands.subcommands;

import co.nytro.market.Market;
import co.nytro.market.config.Texts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CreateServerCommand implements CommandExecutor {
    private final Market pl = Market.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof ConsoleSource) return CommandResult.success();
        Player player = (Player) src;
        if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
            ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
            Optional<Integer> oquan = args.getOne(Text.of("quantity"));
            if (oquan.isPresent()) {
                int quan = oquan.get();
                if (quan > itemStack.getQuantity()) throw new CommandException(Texts.NOT_ENOUGH_ITEMS);
                if (quan > itemStack.getMaxStackQuantity()) throw new CommandException(Texts.TOO_ENOUGH_ITEMS);
                Optional<Integer> oprice = args.getOne(Text.of("price"));
                oprice.ifPresent(integer -> {
                    int price = integer;
                    int v = pl.getDataStore().addListing(itemStack, quan, price);
                    if (v == 0) player.sendMessage(Texts.COULD_NOT_MAKE_LISTING);
                    else if (v == -1) player.sendMessage(Texts.USE_ADD_STOCK);
                    else {
                        pl.getDataStore().getListing(String.valueOf(v)).sendTo(src);
                        player.setItemInHand(HandTypes.MAIN_HAND, null);
                    }
                });
            }
        } else {
            throw new CommandException(Texts.AIR_ITEM);
        }
        return CommandResult.success();
    }
}
