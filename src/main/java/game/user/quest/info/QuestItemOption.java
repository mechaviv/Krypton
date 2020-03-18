package game.user.quest.info;

import game.user.item.ItemVariationOption;
import util.FileTime;

/**
 * Created by MechAviv on 1/21/2020.
 */
public class QuestItemOption {
    private boolean named;
    private int period;
    private int jobFlag;
    private int gender;
    private int probRate;
    private int variation;
    private FileTime dateExpire;

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getJobFlag() {
        return jobFlag;
    }

    public void setJobFlag(int jobFlag) {
        this.jobFlag = jobFlag;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getProbRate() {
        return probRate;
    }

    public void setProbRate(int probRate) {
        this.probRate = probRate;
    }

    public int getVariation() {
        return variation;
    }

    public void setVariation(int variation) {
        this.variation = variation;
    }

    public FileTime getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(FileTime dateExpire) {
        this.dateExpire = dateExpire;
    }

    public boolean isNamed() {
        return named;
    }

    public void setNamed(boolean named) {
        this.named = named;
    }
}
