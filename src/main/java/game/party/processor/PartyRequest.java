package game.party.processor;

/**
 * Created by MechAviv on 2/5/2020.
 */
public class PartyRequest {
    public static final int
            LOAD_PARTY = 0,
            CREATE_PARTY = 1,
            WITHDRAW_PARTY = 2,
            JOIN_PARTY = 3,
            NOTIFY_MIGRATION = 4,
            CHANGE_LEVEL_OR_JOB = 5,
            CHANGE_PARTY_BOSS = 6;

    private final int type;
    private int characterID;
    private int characterID1;
    private int val;
    private int channelID;
    private boolean levelChanged;
    private boolean kicked;
    private boolean send;
    private boolean forced;
    private boolean success;

    public PartyRequest(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public int getCharacterID1() {
        return characterID1;
    }

    public void setCharacterID1(int characterID1) {
        this.characterID1 = characterID1;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public boolean isLevelChanged() {
        return levelChanged;
    }

    public void setLevelChanged(boolean levelChanged) {
        this.levelChanged = levelChanged;
    }

    public boolean isKicked() {
        return kicked;
    }

    public void setKicked(boolean kicked) {
        this.kicked = kicked;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
