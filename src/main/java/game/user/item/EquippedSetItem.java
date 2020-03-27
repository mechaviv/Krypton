package game.user.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 1/19/2020.
 */
public class EquippedSetItem {
    private int setItemID;
    private int partsCount;
    private List<Integer> items;

    public EquippedSetItem(int setItemID) {
        this.items = new ArrayList<>();
        this.setItemID = setItemID;
    }

    public int getSetItemID() {
        return setItemID;
    }

    public void setSetItemID(int setItemID) {
        this.setItemID = setItemID;
    }

    public int getPartsCount() {
        return partsCount;
    }

    public void setPartsCount(int partsCount) {
        this.partsCount = partsCount;
    }

    public List<Integer> getItems() {
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }
}
