package co.nytro.market.commands.subcommands;

import co.nytro.market.Market;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 * Created by TimeTheCat on 3/26/2017.
 */
public class BlackListCommand implements CommandExecutor {
    private final Market pl = Market.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        pl.getDataStore().getBlacklistedItemList().sendTo(src);
        return CommandResult.success();
    }
}
