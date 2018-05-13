package com.kookykraftmc.market.commands.subcommands.blacklist;

import com.kookykraftmc.market.Market;
import com.kookykraftmc.market.config.Texts;
import com.kookykraftmc.market.model.ItemStackId;
import com.kookykraftmc.market.service.MarketService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.Optional;

/**
 * Created by TimeTheCat on 3/26/2017.
 */
public class BlacklistRemoveCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> oid = args.getOne("id");
        oid.ifPresent(s1 -> {
            boolean s = Sponge.getServiceManager().provide(MarketService.class).get().removeFromBlackList(new ItemStackId(s1));
            if (s) src.sendMessage(Texts.BLACKLIST_REMOVED);
            else src.sendMessage(Texts.BLACKLIST_REMOVED_FAIL);
        });
        return CommandResult.success();
    }
}
