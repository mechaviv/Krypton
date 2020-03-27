/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package common.item;

import network.packet.OutPacket;
import util.FileTime;

/**
 *
 * @author Eric
 */
public class ItemSlotEquip extends ItemSlotBase {
    private long sn;
    public String title;
    public FileTime equipped;
    public int prevBonusExpRate;
    public ItemSlotEquipBase item;
    public ItemSlotEquipOpt option;

    public ItemSlotEquip(int itemID) {
        super(itemID);
        this.item = new ItemSlotEquipBase();
        this.option = new ItemSlotEquipOpt();
        this.title = "";
        this.equipped = FileTime.DATE_1900;
        this.prevBonusExpRate = -1;
        this.item.durability = -1;
    }

    @Override
    public short getItemNumber() {
        return 1;
    }

    @Override
    public long getSN() {
        return sn;
    }

    @Override
    public int getType() {
        return ItemSlotType.Equip;
    }

    @Override
    public ItemSlotBase makeClone() {
        ItemSlotEquip item = (ItemSlotEquip) createItem(ItemSlotType.Equip);
        item.setItemID(this.getItemID());
        item.setCashItemSN(this.getCashItemSN());
        item.setItemSN(this.getSN());
        item.setDateExpire(this.getDateExpire());

        item.item = this.item.makeClone();
        item.option = this.option.makeClone();
        return item;
    }

    public boolean isSameEquipItem(ItemSlotEquip src) {
        return this.item.isSameEquipItem(src.item) && this.option.isSameEquipItem(src.option);
    }

    @Override
    public void rawEncode(OutPacket packet) {
        packet.encodeByte(ItemSlotType.Equip);
        super.rawEncode(packet);
        packet.encodeByte(item.ruc);
        packet.encodeByte(item.cuc);
        packet.encodeShort(item.iSTR);
        packet.encodeShort(item.iDEX);
        packet.encodeShort(item.iINT);
        packet.encodeShort(item.iLUK);
        packet.encodeShort(item.iMaxHP);
        packet.encodeShort(item.iMaxMP);
        packet.encodeShort(item.iPAD);
        packet.encodeShort(item.iMAD);
        packet.encodeShort(item.iPDD);
        packet.encodeShort(item.iMDD);
        packet.encodeShort(item.iACC);
        packet.encodeShort(item.iEVA);
        packet.encodeShort(item.iCraft);
        packet.encodeShort(item.iSpeed);
        packet.encodeShort(item.iJump);

        packet.encodeString(title);
        packet.encodeShort(item.attribute);
        packet.encodeByte(item.levelUpType);
        packet.encodeByte(item.level);
        packet.encodeInt(item.exp);
        packet.encodeInt(item.durability);
        packet.encodeInt(item.iuc);

        packet.encodeByte(option.grade);
        packet.encodeByte(option.chuc);
        packet.encodeShort(option.option1);
        packet.encodeShort(option.option2);
        packet.encodeShort(option.option3);
        packet.encodeShort(0);// socket 1
        packet.encodeShort(0);// socket 2

        if (getCashItemSN() == 0) {
            packet.encodeLong(getSN());
        }
        packet.encodeFileTime(equipped);
        packet.encodeInt(prevBonusExpRate);
    }

    @Override
    public void setItemNumber(int number) {

    }

    public void setItemSN(long sn) {
        this.sn = sn;
    }

    public void setItemTitle(String title) {
        this.title = title;
    }

    // Attributes
    public short getItemAttribute() {
        return item.attribute;
    }

    public boolean isProtectedItem() {
        return (item.attribute & 0x1) != 0;
    }

    public boolean isPreventSlipItem() {
        return (item.attribute & 0x2) != 0;
    }

    public boolean isSupportWarmItem() {
        return (item.attribute & 0x4) != 0;
    }

    public boolean isBindedItem() {
        return (item.attribute & 0x8) != 0;
    }

    public boolean isPossibleTradingItem() {
        return (item.attribute & 0x10) != 0;
    }

    public void setItemAttribute(short newAttribute) {
        this.item.attribute = newAttribute;
    }

    public void resetPossibleTrading() {
        this.item.attribute &= 0xFFEF;
    }

    public void resetProtected() {
        this.item.attribute &= 0xFFFE;
    }

    public void setProtected() {
        this.item.attribute |= 0x1;
    }

    public void setPreventSlip() {
        this.item.attribute |= 0x2;
    }

    public void setWarmSupport() {
        this.item.attribute |= 0x4;
    }

    public void setBinded() {
        this.item.attribute |= 0x8;
    }

    public void setPossibleTrading() {
        this.item.attribute |= 0x10;
    }
    // IUC Attributes (Hammer)
    public int getIUCAdd() {
        return (this.item.iuc >> 8) & 0xFF;
    }

    public int getIUCValue() {
        return this.item.iuc;
    }

    public void setIUCAdd(int add) {
        this.item.iuc = (add << 8) + (this.item.iuc & 0xFFFF00FF);
    }

    public void setIUCValue(int value) {
        this.item.iuc = value + (this.item.iuc & 0xFFFFFF00);
    }
    // Potential Attributes
    public int getItemGrade() {
        return this.option.grade;
    }

    public boolean isReleased() {
        return (option.grade & 0x4) != 0;
    }

    public void setReleased(boolean released) {
        if (released) {
            this.option.grade |= 0x4;
        } else {
            this.option.grade = 0;
        }
    }
}
