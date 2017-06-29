package com.kookykraftmc.market.commands.subcommands;

import com.kookykraftmc.market.Market;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 * Created by TimeTheCat on 3/26/2017.
 */
public class BlackListCommand implements CommandExecutor {
    Market pl = Market.instance;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        pl.getDataStore().getBlacklistedItemList().sendTo(src);
        return CommandResult.success();
    }
}
