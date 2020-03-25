package game.field.reactor;

import util.Pointer;

/**
 * Created by MechAviv on 3/19/2020.
 */
public class ReactorGen {
    private int templateID;
    private String name;
    private int x;
    private Pointer<Integer> y;
    private int regenInterval;
    private long regenAfter;
    private int reactorCount;
    private boolean flip;

    public ReactorGen() {
        this.y = new Pointer<>(0);
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Pointer<Integer> getY() {
        return y;
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public int getRegenInterval() {
        return regenInterval;
    }

    public void setRegenInterval(int regenInterval) {
        this.regenInterval = regenInterval;
    }

    public long getRegenAfter() {
        return regenAfter;
    }

    public void setRegenAfter(long regenAfter) {
        this.regenAfter = regenAfter;
    }

    public int getReactorCount() {
        return reactorCount;
    }

    public void setReactorCount(int reactorCount) {
        this.reactorCount = reactorCount;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}
