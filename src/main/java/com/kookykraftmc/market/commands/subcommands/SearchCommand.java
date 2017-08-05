package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.Market;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by TimeTheCat on 3/26/2017.
 */
public class SearchCommand implements CommandExecutor {
    private static final Market pl = Market.instance;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> texts = new ArrayList<>();
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/market search name ")).append(Text.of("name - Search for a seller by their name.")).build());
        texts.add(Text.builder().onClick(TextActions.suggestCommand("/market search item ")).append(Text.of("item - Search for an item id. (Careful with tabbing this one, may freeze your game.)")).build());
        pl.getPaginationService().builder().contents(texts).title(Text.of(TextColors.GREEN, "Market Search Help")).sendTo(src);
        return CommandResult.success();
    }

    public static class ItemSearch implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<ItemType> oit = args.getOne(Text.of("item"));
            if (oit.isPresent()) {
                pl.getDataStore().searchForItem(oit.get()).sendTo(src);
            } else throw new CommandException(Text.of(TextColors.RED, "Invalid item type."));
            return CommandResult.success();
        }
    }

    public static class NameSearch implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Optional<User> ou = args.getOne(Text.of("user"));
            if (ou.isPresent()) {
                pl.getDataStore().searchForUUID(ou.get().getUniqueId()).sendTo(src);
            } else throw new CommandException(Text.of(TextColors.RED, "Invalid player name."));
            return CommandResult.success();
        }
    }
}
