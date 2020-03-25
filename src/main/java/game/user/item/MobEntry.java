package game.user.item;

/**
 * Created by MechAviv on 3/23/2020.
 */
public class MobEntry {
    private int mobTemplateID;
    private int prob;

    public int getMobTemplateID() {
        return mobTemplateID;
    }

    public void setMobTemplateID(int mobTemplateID) {
        this.mobTemplateID = mobTemplateID;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }
}
