package co.nytro.market.commands;

import co.nytro.market.config.Texts;
import co.nytro.market.Market;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 * Created by TimeTheCat on 3/10/2017.
 */
public class MarketCommand implements CommandExecutor {
    private final Market pl = Market.instance;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        pl.getPaginationService().builder().title(Texts.MARKET_BASE).contents(pl.getCommands()).sendTo(src);

        return CommandResult.success();
    }
}
