package com.kookykraftmc.market.datastores;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.action.Action;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.kookykraftmc.market.Market;

import javax.annotation.Nonnull;

/**
 * Created by TimeTheCat on 7/18/2017.
 */
public class MarketAction extends RunnableAction {

    private final Runnable runnable;

    public MarketAction(@Nonnull StateContainer container, @Nonnull ActionType type, @Nonnull String goalState, String marketID) {
        super(container, type, goalState);
        this.runnable = () -> Market.instance.getGame().getCommandManager().process(getObserver(), "market check " + marketID);
    }

    @Override
    public void runAction(@Nonnull String currentState) {
        this.runnable.run();
        super.runAction(currentState);
    }

}
