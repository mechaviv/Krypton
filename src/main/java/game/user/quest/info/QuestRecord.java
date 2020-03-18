package game.user.quest.info;

/**
 * Created by MechAviv on 1/21/2020.
 */
public class QuestRecord {
    private int questID, state;

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
        this.questID = questID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
