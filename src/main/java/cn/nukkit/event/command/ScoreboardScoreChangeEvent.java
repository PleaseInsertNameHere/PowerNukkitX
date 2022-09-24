package cn.nukkit.event.command;

import cn.nukkit.event.HandlerList;
import cn.nukkit.scoreboard.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.scorer.IScorer;

public class ScoreboardScoreChangeEvent extends ScoreboardEvent {

    private static final HandlerList handlers = new HandlerList();

    private final IScorer scorer;
    private int newValue;
    private int oldValue;
    private final ActionType actionType;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ScoreboardScoreChangeEvent(Scoreboard scoreboard, IScorer scorer, int newValue, int oldValue){
        this(scoreboard,scorer,newValue,oldValue,ActionType.CHANGE);
    }

    public ScoreboardScoreChangeEvent(Scoreboard scoreboard, IScorer scorer, int newValue, int oldValue, ActionType actionType) {
        super(scoreboard);
        this.scorer = scorer;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.actionType = actionType;
    }

    public IScorer getScorer() {
        return scorer;
    }

    public int getNewValue() {
        return newValue;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    public void setOldValue(int oldValue) {
        this.oldValue = oldValue;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public enum ActionType{
        CHANGE,
        REMOVE,
        ADD
    }
}
