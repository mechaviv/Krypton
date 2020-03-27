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

/**
 *
 * @author Eric
 */
public class ItemSlotBundle extends ItemSlotBase {
    private short number;
    private short attribute;
    private long sn;
    private String title;

    public ItemSlotBundle(int itemID) {
        super(itemID);
        this.number = 1;
        this.attribute = 0;
        this.sn = 0;
        this.title = "";
    }

    @Override
    public short getItemNumber() {
        return number;
    }

    @Override
    public long getSN() {
        return sn;
    }

    @Override
    public int getType() {
        return ItemSlotType.Bundle;
    }

    @Override
    public ItemSlotBase makeClone() {
        ItemSlotBundle item = (ItemSlotBundle) createItem(ItemSlotType.Bundle);
        item.setItemID(this.getItemID());
        item.setItemSN(this.getSN());
        item.setItemNumber(this.getItemNumber());
        item.setDateExpire(this.getDateExpire());
        return item;
    }

    @Override
    public void rawEncode(OutPacket packet) {
        packet.encodeByte(ItemSlotType.Bundle);
        super.rawEncode(packet);
        packet.encodeShort(number);
        packet.encodeString("");
        packet.encodeShort(0);
        if (ItemAccessor.isRechargeableItem(getItemID())) {
            packet.encodeLong(getSN());
        }
    }

    @Override
    public void setItemNumber(int number) {
        this.number = (short) number;
    }

    public void setItemSN(long sn) {
        this.sn = sn;
    }

    public String getItemTitle() {
        return title;
    }

    public void setItemTitle(String title) {
        this.title = title;
    }

    public short getItemAttribute() {
        return this.attribute;
    }

    public boolean isProtectedItem() {
        return (this.attribute & 0x1) != 0;
    }

    public boolean isPreventSlipItem() {
        return (this.attribute & 0x2) != 0;
    }

    public boolean isSupportWarmItem() {
        return (this.attribute & 0x4) != 0;
    }

    public boolean isBindedItem() {
        return (this.attribute & 0x8) != 0;
    }

    public boolean isPossibleTradingItem() {
        return (this.attribute & 0x10) != 0;
    }

    public void setItemAttribute(short newAttribute) {
        this.attribute = newAttribute;
    }

    public void resetPossibleTrading() {
        this.attribute &= 0xFFEF;
    }

    public void resetProtected() {
        this.attribute &= 0xFFFE;
    }

    public void setProtected() {
        this.attribute |= 0x1;
    }

    public void setPreventSlip() {
        this.attribute |= 0x2;
    }

    public void setWarmSupport() {
        this.attribute |= 0x4;
    }

    public void setBinded() {
        this.attribute |= 0x8;
    }

    public void setPossibleTrading() {
        this.attribute |= 0x10;
    }
}
