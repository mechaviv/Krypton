package game.user.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 3/23/2020.
 */
public class MobSummonItem {
    private int itemID;
    private int type;
    private final List<MobEntry> mobs;

    public MobSummonItem() {
        this.mobs = new ArrayList<>();
    }

    public int getItemID() {
        return itemID;
    }

    public int getType() {
        return type;
    }

    public List<MobEntry> getMobs() {
        return mobs;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setType(int type) {
        this.type = type;
    }
}
