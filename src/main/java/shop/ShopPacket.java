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
package shop;

import common.item.ItemSlotBase;
import java.util.List;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import shop.user.CashItemInfo;
import shop.user.User;
import shop.user.User.CashItemResult;

/**
 *
 * @author sunnyboy
 */
public class ShopPacket {

    public static OutPacket onBuyDone(CashItemInfo cashItemInfo) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.Buy_Done);
        packet.encodeLong(cashItemInfo.getCashItemSN());
        packet.encodeInt(cashItemInfo.getAccountID());
        packet.encodeInt(cashItemInfo.getCharacterID());
        packet.encodeInt(cashItemInfo.getItemID());
        packet.encodeShort(cashItemInfo.getNumber());
        packet.encodeString(cashItemInfo.getBuyCharacterName(), 13);
        packet.encodeFileTime(cashItemInfo.getDateExpire());
        return packet;
    }

    public static OutPacket onBuyFailed(byte type) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.Buy_Failed);
        packet.encodeByte(type);
        return packet;
    }

    public static OutPacket onGiftDone(String rcvCharacterName, int itemID, short count) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.Gift_Done);
        packet.encodeString(rcvCharacterName);
        packet.encodeInt(itemID);
        packet.encodeShort(count);
        return packet;
    }

    public static OutPacket onGiftFailed(byte type) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.Gift_Failed);
        packet.encodeByte(type);
        return packet;
    }

    public static OutPacket onIncSlotCountDone(byte ti, short newSlotCount) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.IncSlotCount_Done);
        packet.encodeByte(ti);
        packet.encodeShort(newSlotCount);
        return packet;
    }

    public static OutPacket onIncSlotCountFailed(byte type) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.IncSlotCount_Failed);
        packet.encodeByte(type);
        return packet;
    }

    public static OutPacket onLoadLockerDone(List<CashItemInfo> cashItemInfo) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.LoadLocker_Done);
        packet.encodeByte(cashItemInfo.size());
        for (CashItemInfo cashItem : cashItemInfo) {
            packet.encodeLong(cashItem.getCashItemSN());
            packet.encodeInt(cashItem.getAccountID());
            packet.encodeInt(cashItem.getCharacterID());
            packet.encodeInt(cashItem.getItemID());
            packet.encodeShort(cashItem.getNumber());
            packet.encodeString(cashItem.getBuyCharacterName(), 13);
            packet.encodeFileTime(cashItem.getDateExpire());
        }
        return packet;
    }

    public static OutPacket onMoveLToS(short pos, ItemSlotBase item, byte ti) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.MoveLtoS_Done);
        packet.encodeShort(pos);
        packet.encodeByte(ti);
        item.encode(packet);
        return packet;
    }

    public static OutPacket onMoveLToSFailed(byte type) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.MoveLtoS_Failed);
        packet.encodeByte(type);
        return packet;
    }

    public static OutPacket onMoveSToL(CashItemInfo cashItem) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.MoveStoL_Done);
        packet.encodeLong(cashItem.getCashItemSN());
        packet.encodeInt(cashItem.getAccountID());
        packet.encodeInt(cashItem.getCharacterID());
        packet.encodeInt(cashItem.getItemID());
        packet.encodeShort(cashItem.getNumber());
        packet.encodeString(cashItem.getBuyCharacterName(), 13);
        packet.encodeFileTime(cashItem.getDateExpire());
        return packet;
    }

    public static OutPacket onMoveSToLFailed(byte type) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopCashItemResult);
        packet.encodeByte(CashItemResult.MoveStoL_Failed);
        packet.encodeByte(type);
        return packet;
    }

    public static OutPacket onQueryCash(User user) {
        OutPacket packet = new OutPacket(LoopbackPacket.CashShopQueryCashResult);
        packet.encodeInt(user.getNexonCash());
        return packet;
    }
}
