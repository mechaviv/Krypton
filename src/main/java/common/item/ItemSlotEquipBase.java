package common.item;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class ItemSlotEquipBase {
    public byte ruc;//Remaining Upgrade Count
    public byte cuc;//Current Upgrade Count
    public short iSTR;
    public short iDEX;
    public short iINT;
    public short iLUK;
    public short iMaxHP;
    public short iMaxMP;
    public short iPAD;//Physical Attack Damage
    public short iMAD;//Magic Attack Damage
    public short iPDD;//Physical Defense
    public short iMDD;//Magic Defense
    public short iACC;//Accuracy Rate
    public short iEVA;//Evasion
    public short iCraft;//Hands
    public short iSpeed;
    public short iJump;
    public short attribute;
    public byte levelUpType;
    public byte level;
    public int exp;
    public int durability;
    public int iuc;

    public ItemSlotEquipBase makeClone() {
        ItemSlotEquipBase item = new ItemSlotEquipBase();
        // TODO: Apply and use setters with this class.
        item.ruc = this.ruc;
        item.cuc = this.cuc;
        item.iSTR = this.iSTR;
        item.iDEX = this.iDEX;
        item.iINT = this.iINT;
        item.iLUK = this.iLUK;
        item.iMaxHP = this.iMaxHP;
        item.iMaxMP = this.iMaxMP;
        item.iPAD = this.iPAD;
        item.iMAD = this.iMAD;
        item.iPDD = this.iPDD;
        item.iMDD = this.iMDD;
        item.iACC = this.iACC;
        item.iEVA = this.iEVA;
        item.iCraft = this.iCraft;
        item.iSpeed = this.iSpeed;
        item.iJump = this.iJump;
        item.attribute = this.attribute;
        item.levelUpType = this.levelUpType;
        item.level = this.level;
        item.exp = this.exp;
        item.durability = this.durability;
        item.iuc = this.iuc;

        return item;
    }

    public boolean isSameEquipItem(ItemSlotEquipBase src) {
        return this.ruc == src.ruc && this.cuc == src.cuc && this.iSTR == src.iSTR && this.iDEX == src.iDEX && this.iINT == src.iINT && this.iLUK == src.iLUK
                && this.iMaxHP == src.iMaxHP && this.iMaxMP == src.iMaxMP && this.iPAD == src.iPAD && this.iMAD == src.iMAD && this.iPDD == src.iPDD
                && this.iMDD == src.iMDD && this.iACC == src.iACC && this.iEVA == src.iEVA && this.iCraft == src.iCraft && this.iSpeed == src.iSpeed
                && this.iJump == src.iJump && this.attribute == src.attribute && this.levelUpType == src.levelUpType && this.level == src.level && this.exp == src.exp
                && this.durability == src.durability && this.iuc == src.iuc;
    }
}
