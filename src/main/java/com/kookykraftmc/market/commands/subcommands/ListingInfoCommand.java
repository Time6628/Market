package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.Texts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by TimeTheCat on 3/18/2017.
 */
public class ListingInfoCommand implements CommandExecutor {
    private final Market pl = Market.instance;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> oid = args.getOne(Text.of("id"));
        if (oid.isPresent()) {
            PaginationList p = pl.getDataStore().getListing(oid.get());
            if (p != null) p.sendTo(src);
        } else throw new CommandException(Texts.INVALID_LISTING);
        return CommandResult.success();
    }
}
