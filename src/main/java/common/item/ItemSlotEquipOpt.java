package common.item;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class ItemSlotEquipOpt {
    public byte grade;// 0 Normal | 1-3 Unreleased | -1~-3 Potential (1 unique 2 epic 3 rare)
    public byte chuc;
    public short option1;
    public short option2;
    public short option3;
    // socket 1, socket 2 (not really exist in this version)

    public ItemSlotEquipOpt makeClone() {
        ItemSlotEquipOpt option = new ItemSlotEquipOpt();
        option.grade = this.grade;
        option.chuc = this.chuc;
        option.option1 = this.option1;
        option.option2 = this.option2;
        option.option3 = this.option3;
        return option;
    }

    public boolean isSameEquipItem(ItemSlotEquipOpt src) {
        return this.grade == src.grade && this.chuc == src.chuc && this.option1 == src.option1 && this.option2 == src.option2 && this.option3 == src.option3;
    }
}
