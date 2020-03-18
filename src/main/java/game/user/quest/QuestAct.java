package game.user.quest;

import game.user.quest.info.act.ActItem;
import game.user.quest.info.act.ActSkill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 1/21/2020.
 */
public class QuestAct {
    private int incExp;
    private int incMoney;
    private int incPop;
    private int incPetTameness;
    private String info;
    private int nextQuest;
    private boolean petSpeed;
    private int buffItemID;
    private final List<ActItem> actItem;
    private final List<ActSkill> actSkill;
    private String msg;
    private final List<Integer> maps;
    private String npcAction;

    public QuestAct() {
        this.actItem = new ArrayList<>();
        this.actSkill = new ArrayList<>();
        this.maps = new ArrayList<>();
    }
    public int getIncExp() {
        return incExp;
    }

    public void setIncExp(int incExp) {
        this.incExp = incExp;
    }

    public int getIncMoney() {
        return incMoney;
    }

    public void setIncMoney(int incMoney) {
        this.incMoney = incMoney;
    }

    public int getIncPop() {
        return incPop;
    }

    public void setIncPop(int incPop) {
        this.incPop = incPop;
    }

    public int getIncPetTameness() {
        return incPetTameness;
    }

    public void setIncPetTameness(int incPetTameness) {
        this.incPetTameness = incPetTameness;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNextQuest() {
        return nextQuest;
    }

    public void setNextQuest(int nextQuest) {
        this.nextQuest = nextQuest;
    }


    public int getBuffItemID() {
        return buffItemID;
    }

    public void setBuffItemID(int buffItemID) {
        this.buffItemID = buffItemID;
    }

    public List<ActItem> getActItem() {
        return actItem;
    }

    public List<ActSkill> getActSkill() {
        return actSkill;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Integer> getMaps() {
        return maps;
    }

    public String getNpcAction() {
        return npcAction;
    }

    public void setNpcAction(String npcAction) {
        this.npcAction = npcAction;
    }

    public boolean isPetSpeed() {
        return petSpeed;
    }

    public void setPetSpeed(boolean petSpeed) {
        this.petSpeed = petSpeed;
    }
}
