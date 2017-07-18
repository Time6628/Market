package com.kookykraftmc.market.datastores;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.action.Action;
import com.codehusky.huskyui.states.action.ActionType;
import com.kookykraftmc.market.Market;

import javax.annotation.Nonnull;

/**
 * Created by TimeTheCat on 7/18/2017.
 */
public class MarketAction extends Action {

    private final String marketID;

    public MarketAction(@Nonnull StateContainer container, @Nonnull ActionType type, @Nonnull String goalState, String marketID) {
        super(container, type, goalState);
        this.marketID = marketID;
    }

    @Override
    public void runAction(@Nonnull String currentState) {
        Market.instance.getGame().getCommandManager().process(getObserver(), "market check " + marketID);
        super.runAction(currentState);
    }

    public String getMarketID() {
        return marketID;
    }
}
