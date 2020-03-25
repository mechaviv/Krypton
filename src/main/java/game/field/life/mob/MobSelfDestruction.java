package game.field.life.mob;

/**
 * Created by MechAviv on 3/23/2020.
 */
public class MobSelfDestruction {
    private int actionType;
    private int bearHP;
    private boolean firstAttack;
    private int removeAfter;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getBearHP() {
        return bearHP;
    }

    public void setBearHP(int bearHP) {
        this.bearHP = bearHP;
    }

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }
}
