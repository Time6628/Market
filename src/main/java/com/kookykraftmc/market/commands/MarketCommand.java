package com.kookykraftmc.market.commands;

import com.kookykraftmc.market.config.Texts;
import com.kookykraftmc.market.service.MarketService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TimeTheCat on 3/10/2017.
 */
public class MarketCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        MarketService.getPaginationService().builder().title(Texts.MARKET_BASE)
                .contents(getCommands()).sendTo(src);

        return CommandResult.success();
    }

    public List<Text> getCommands() {
        List<Text> commands = new ArrayList<>();
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Show the items in the market.")))
                .onClick(TextActions.suggestCommand("/market listings"))
                .append(Text.of("/market listings"))
                .build());
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Add the item in your hand to the market.")))
                .onClick(TextActions.suggestCommand("/market create <quantity> <price>"))
                .append(Text.of("/market create <quantity> <price>"))
                .build());
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Get info about a listing.")))
                .onClick(TextActions.suggestCommand("/market check <id>"))
                .append(Text.of("/market check <id>"))
                .build());
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Buy an item from the market.")))
                .onClick(TextActions.suggestCommand("/market buy <id>"))
                .append(Text.of("/market buy <id>"))
                .build());
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Add more stock to your listing.")))
                .onClick(TextActions.suggestCommand("/market addstock <id>"))
                .append(Text.of("/market addstock <id>"))
                .build());
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Search the market for a playername or item id.")))
                .onClick(TextActions.suggestCommand("/market search <name|item>"))
                .append(Text.of("/market search <name|item>"))
                .build());
        commands.add(Text.builder()
                .onHover(TextActions.showText(Text.of("Remove a listing from the market.")))
                .onClick(TextActions.suggestCommand("/market removelisting <id>"))
                .append(Text.of("/market removelisting <id>"))
                .build());
        return commands;


    }
}
