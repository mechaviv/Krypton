package game.user.skill.entries;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class MCSkillEntry {
    private String name, desc;
    private int spendCP;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSpendCP() {
        return spendCP;
    }

    public void setSpendCP(int spendCP) {
        this.spendCP = spendCP;
    }
}
