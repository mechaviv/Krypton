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
package common.user;

import common.JobCategory;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.user.item.EquippedSetItem;
import game.user.quest.info.SimpleStrMap;
import game.user.skill.SkillAccessor;
import network.packet.OutPacket;
import util.FileTime;
import util.Logger;

/**
 *
 * @author Eric
 */
public class CharacterData {

    static final int
            BodyPartCount = BodyPart.BP_Count,
            StickerPartBase = BodyPart.STICKER,
            DragonPartCount = BodyPart.DP_COUNT,
            DragonPartBase = BodyPart.DP_BASE,
            MechanicPartCount = BodyPart.MP_COUNT,
            MechanicPartBase = BodyPart.MP_BASE,
            ItemTypeCount = ItemType.NO
    ;

    private final CharacterStat characterStat;
    private final WildHunterInfo wildHunterInfo;
    private final List<ItemSlotBase> equipped;
    private final List<ItemSlotBase> equipped2;
    private final List<ItemSlotBase> dragonEquipped;
    private final List<ItemSlotBase> mechanicEquipped;
    private final List<List<ItemSlotBase>> itemSlot;
    private final List<List<Integer>> itemTrading;
    private final Map<Integer, Integer> skillRecord;
    private final Map<Integer, Integer> skillRecordEx;// additional slv from items
    private final Map<Integer, Integer> skillMasterLev;
    private final Map<Integer, Long> skillCooltimeOver;
    private final Map<Integer, FileTime> skillExpired;
    private final Map<Integer, String> questRecord;
    private final Map<Integer, SimpleStrMap> questRecordEx;
    private final Map<Integer, FileTime> questComplete;
    private final Map<Integer, EquippedSetItem> equippedSetItem;
    private final FileTime equipExtExpire;
    private int moneyTrading;
    private int combatOrders;
    private boolean onTrading;
    public CharacterData() {
        this.characterStat = new CharacterStat();
        this.wildHunterInfo = new WildHunterInfo();

        this.equipped = new ArrayList<>(BodyPartCount + 1);
        this.equipped2 = new ArrayList<>(BodyPartCount + 1);
        this.dragonEquipped = new ArrayList<>(DragonPartCount);
        this.mechanicEquipped = new ArrayList<>(MechanicPartCount);
        this.itemSlot = new ArrayList<>(ItemType.NO);
        this.itemTrading = new ArrayList<>(ItemType.NO);
        this.equipExtExpire = FileTime.DATE_1900;

        this.skillRecord = new HashMap<>();
        this.skillRecordEx = new HashMap<>();
        this.skillMasterLev = new HashMap<>();
        this.skillCooltimeOver = new HashMap<>();
        this.skillExpired = new HashMap<>();

        this.questRecord = new HashMap<>();
        this.questRecordEx = new HashMap<>();
        this.questComplete = new HashMap<>();

        this.equippedSetItem = new HashMap<>();
        this.onTrading = false;
       
        for (int i = 0; i <= BodyPartCount; i++) {
            equipped.add(i, null);
            equipped2.add(i, null);
        }
        for (int i = 0; i < DragonPartCount; i++) {
            dragonEquipped.add(i, null);
        }
        for (int i = 0; i < MechanicPartCount; i++) {
            mechanicEquipped.add(i, null);
        }
        for (int i = 0; i < ItemTypeCount; i++) {
            itemSlot.add(i, new ArrayList<>());
            itemTrading.add(i, new ArrayList<>());
        }
    }

    public void backupItemSlot(List<List<ItemSlotBase>> backupItem, List<List<Integer>> backupItemTrading) {
        for (int i = 0; i < ItemType.NO; i++) {
            backupItem.add(i, new ArrayList<>());
            if (backupItemTrading != null) {
                backupItemTrading.add(i, new ArrayList<>());
            }
        }
        
        for (int ti = ItemType.Equip; ti <= ItemType.Cash; ti++) {
            for (int i = 0; i < itemSlot.get(ti).size(); i++) {
                backupItem.get(ti).add(i, null);
                
                ItemSlotBase item = itemSlot.get(ti).get(i);
                if (item != null) {
                    backupItem.get(ti).set(i, item.makeClone());
                }
            }
            if (backupItemTrading != null) {
                backupItemTrading.get(ti).addAll(itemTrading.get(ti));
            }
        }
    }

    public void clearTradingInfo() {
        this.moneyTrading = 0;
        for (int i = 0; i < ItemType.NO; i++) {
            for (int j = 0; j < itemTrading.get(i).size(); j++) {
                itemTrading.get(i).set(j, 0);
            }
        }
    }

    public void encodeAvatarData(OutPacket packet) {
        characterStat.encode(packet);
        encodeAvatarLook(packet);
    }

    public void encodeAvatarLook(OutPacket packet) {
        packet.encodeByte(characterStat.getGender());
        packet.encodeByte(characterStat.getSkin());
        packet.encodeInt(characterStat.getFace());
        packet.encodeByte(0);
        packet.encodeInt(characterStat.getHair());

        for (int i = 1; i <= BodyPartCount; i++) {
            ItemSlotBase item = equipped.get(i);
            if (item != null) {
                packet.encodeByte(i);
                packet.encodeInt(item.getItemID());
            }
        }
        packet.encodeByte(0xFF);

        for (int i = 1; i <= BodyPartCount; i++) {
            ItemSlotBase item = equipped2.get(i);
            if (item != null) {
                packet.encodeByte(i);
                packet.encodeInt(item.getItemID());
            }
        }
        packet.encodeByte(0xFF);

        ItemSlotBase item = equipped2.get(BodyPart.Weapon);
        packet.encodeInt(item != null ? item.getItemID() : 0);
        packet.encodeBuffer(new byte[12]);// anPetID
    }

    public void encode(OutPacket packet, int flag) {
        packet.encodeLong(flag);
        packet.encodeByte(combatOrders);
        packet.encodeByte(0);// unk
        if ((flag & DBChar.Character) != 0) {
            characterStat.encode(packet);
            packet.encodeByte(0);// nFriendMax
            packet.encodeByte(0);// linked character
        }
        if ((flag & DBChar.Money) != 0) {
            characterStat.encodeMoney(packet);
        }
        if ((flag & DBChar.InventorySize) != 0) {
            for (int ti = ItemType.Equip; ti <= ItemType.Cash; ti++) {
                packet.encodeByte(getItemSlotCount(ti));
            }
        }
        if ((flag & DBChar.EquipTxt) != 0) {
            packet.encodeFileTime(equipExtExpire);
        }

        if ((flag & DBChar.ItemSlotEquip) != 0) {
            for (int i = 1; i <= BodyPartCount; i++) {
                ItemSlotBase item = equipped.get(i);
                if (item != null) {
                    packet.encodeShort(i);
                    item.encode(packet);
                }
            }
            packet.encodeShort(0);

            for (int i = 1; i <= BodyPartCount; i++) {
                ItemSlotBase item = equipped2.get(i);
                if (item != null) {
                    packet.encodeShort(i);
                    item.encode(packet);
                }
            }
            packet.encodeShort(0);

            int slotCount = getItemSlotCount(ItemType.Equip);
            for (int i = 1; i <= slotCount; i++) {
                ItemSlotBase item = itemSlot.get(ItemType.Equip).get(i);
                if (item != null) {
                    packet.encodeShort(i);
                    item.encode(packet);
                }
            }
            packet.encodeShort(0);

            for (int i = 0; i < DragonPartCount; i++) {
                ItemSlotBase item = dragonEquipped.get(i);
                if (item != null) {
                    packet.encodeShort(i);
                    item.encode(packet);
                }
            }
            packet.encodeShort(0);

            for (int i = 0; i < MechanicPartCount; i++) {
                ItemSlotBase item = mechanicEquipped.get(i);
                if (item != null) {
                    packet.encodeShort(i);
                    item.encode(packet);
                }
            }
            packet.encodeShort(0);

        }

        for (int ti = ItemType.Consume; ti <= ItemType.Cash; ti++) {
            if ((flag & ItemAccessor.getItemTypeFromTypeIndex(ti)) != 0) {
                int slotCount = getItemSlotCount(ti);
                for (int i = 1; i <= slotCount; i++) {
                    ItemSlotBase item = itemSlot.get(ti).get(i);
                    if (item != null) {
                        packet.encodeByte(i);
                        item.encode(packet);
                    }
                }
                packet.encodeByte(0);
            }
        }

        if ((flag & DBChar.SkillRecord) != 0) {
            packet.encodeShort(skillRecord.size());
            for (Map.Entry<Integer, Integer> skillEntry : skillRecord.entrySet()) {
                int skillID = skillEntry.getKey();
                packet.encodeInt(skillID);
                packet.encodeInt(skillEntry.getValue());//nSLV
                packet.encodeFileTime(skillExpired.getOrDefault(skillID, FileTime.END));
                if (SkillAccessor.isSkillNeedMasterLevel(skillID)) {
                    packet.encodeInt(skillMasterLev.getOrDefault(skillID, 0));
                }
            }
        }
        if ((flag & DBChar.SkillCooltime) != 0) {
            packet.encodeShort(skillCooltimeOver.size());
            for (Map.Entry<Integer, Long> cooltime : skillCooltimeOver.entrySet()) {
                int remainSec = (int) (cooltime.getValue() - System.currentTimeMillis()) / 1000;
                packet.encodeInt(cooltime.getKey());
                packet.encodeShort(remainSec);
            }
        }
        if ((flag & DBChar.QuestRecord) != 0) {
            packet.encodeShort(questRecord.size());
            for (Map.Entry<Integer, String> questEntry : questRecord.entrySet()) {
                packet.encodeShort(questEntry.getKey());//qr key
                packet.encodeString(questEntry.getValue());// qr value
            }
        }
        if ((flag & DBChar.QuestComplete) != 0) {
            packet.encodeShort(questComplete.size());
            for (Map.Entry<Integer, FileTime> questEntry : questComplete.entrySet()) {
                packet.encodeShort(questEntry.getKey());//qr key
                packet.encodeFileTime(questEntry.getValue());// complete time
            }
        }
        if ((flag & DBChar.MiniGameRecord) != 0) {
            packet.encodeShort(0);
        }
        if ((flag & DBChar.CoupleRecord) != 0) {
            packet.encodeShort(0);
            packet.encodeShort(0);
            packet.encodeShort(0);
        }
        if ((flag & DBChar.MapTransfer) != 0) {
            // INVALID_FIELD_ID
            for (int i = 0; i < 5; i++) {
                packet.encodeInt(999999999);
            }
            for (int i = 0; i < 10; i++) {
                packet.encodeInt(999999999);
            }
        }
        if ((flag & DBChar.NewYearCard) != 0) {
            packet.encodeShort(0);
        }
        if ((flag & DBChar.QuestRecordEx) != 0) {
            packet.encodeShort(questRecordEx.size());
            for (Map.Entry<Integer, SimpleStrMap> questEntry : questRecordEx.entrySet()) {
                packet.encodeShort(questEntry.getKey());//qr key
                packet.encodeString(questEntry.getValue().getRawString());
            }
        }
        if ((flag & DBChar.WildHunterInfo) != 0) {
            if (characterStat.getJob() / 100 == JobCategory.RES_ARCHER) {
                wildHunterInfo.encode(packet);
            }
        }
        if ((flag & DBChar.QuestComplete_Old) != 0) {
            packet.encodeShort(0);
        }
        if ((flag & DBChar.VisitorLog) != 0) {
            packet.encodeShort(0);
        }
    }

    public List<ItemSlotBase> getEquipped() {
        return equipped;
    }

    public List<ItemSlotBase> getEquipped2() {
        return equipped2;
    }

    public final CharacterStat getCharacterStat() {
        return characterStat;
    }

    public List<List<ItemSlotBase>> getItemSlot() {
        return itemSlot;
    }

    public List<ItemSlotBase> getItemSlot(int ti) {
        return itemSlot.get(ti);
    }

    public List<List<Integer>> getItemTrading() {
        return itemTrading;
    }

    public int findCashItemSlotPosition(int ti, long sn) {
        if (ti >= ItemType.Equip && ti <= ItemType.Cash && sn != 0) {
            int slotCount = getItemSlotCount(ti);
            for (int i = 1; i <= slotCount; i++) {
                ItemSlotBase item = itemSlot.get(ti).get(i);
                if (item != null) {
                    if (item.getCashItemSN() == sn) {
                        return i;
                    }
                }
            }
            if (ti == ItemType.Equip) {
                for (int bodyPart = 1; bodyPart <= BodyPartCount; bodyPart++) {
                    ItemSlotBase item = equipped2.get(bodyPart);
                    if (item != null) {
                        if (item.getCashItemSN() == sn) {
                            return -bodyPart - BodyPartCount;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public int findEmptySlotPosition(int ti) {
        if (ti >= ItemType.Equip && ti <= ItemType.Cash) {
            int slotCount = getItemSlotCount(ti);
            for (int i = 1; i <= slotCount; i++) {
                ItemSlotBase item = itemSlot.get(ti).get(i);
                if (item == null) {
                    return i;
                }
            }
        }
        return 0;
    }

    public int findItemSlotPosition(ItemSlotBase item) {
        if (item != null) {
            int ti = item.getItemID() / 1000000;
            if (ti >= ItemType.Equip && ti <= ItemType.Cash) {
                int slotCount = getItemSlotCount(ti);
                for (int i = 1; i <= slotCount; i++) {
                    ItemSlotBase other = itemSlot.get(ti).get(i);
                    if (other != null && other.getItemID() == item.getItemID()) {
                        if (other.isSameItem(item)) {
                            return i;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public int getEmptySlotCount(int ti) {
        if (ti >= ItemType.Equip && ti <= ItemType.Cash) {
            int emptySlotCount = 0;
            int slotCount = getItemSlotCount(ti);
            for (int i = 1; i <= slotCount; i++) {
                ItemSlotBase item = itemSlot.get(ti).get(i);
                if (item == null) {
                    ++emptySlotCount;
                }
            }
            return emptySlotCount;
        }
        return 0;
    }

    public ItemSlotBase getItem(byte ti, int pos) {
        if (ti >= ItemType.Equip && ti <= ItemType.Cash) {
            if (ti == ItemType.Equip) {
                if (pos != 0 && (pos >= -BodyPartCount || pos < -BodyPartCount) && pos <= getItemSlotCount(ItemType.Equip) && pos >= -BodyPartCount - BodyPartCount) {
                    if (pos >= -BodyPartCount) {
                        if (pos >= 0) {
                            return itemSlot.get(ItemType.Equip).get(pos);
                        } else {
                            return equipped.get(-pos);
                        }
                    } else {
                        return equipped2.get(-BodyPartCount - pos);
                    }
                }
            } else {
                if (pos > 0 && pos <= getItemSlotCount(ti)) {
                    return itemSlot.get(ti).get(pos);
                }
            }
        }
        return null;
    }

    public int getItemCount(byte ti, int itemID) {
        int count = 0;
        int slotCount = getItemSlotCount(ti);
        for (int i = 1; i <= slotCount; i++) {
            ItemSlotBase item = getItem(ti, i);
            if (item != null && item.getItemID() == itemID) {
                ++count;
            }
        }
        return count;
    }

    public int getItemSlotCount(int ti) {
        return itemSlot.get(ti).size() - 1;
    }

    public int getMoneyTrading() {
        return moneyTrading;
    }

    public Map<Integer, Integer> getSkillRecord() {
        return skillRecord;
    }

    public void load(ResultSet rs, int flag) throws SQLException {
        if ((flag & DBChar.Character) != 0) {
            characterStat.load(rs);
        }

        if ((flag & DBChar.SkillRecord) != 0) {
            while (rs.next()) {
                int skillID = rs.getInt("SkillID");
                skillRecord.put(skillID, rs.getInt("Level"));
                if (SkillAccessor.isSkillNeedMasterLevel(skillID)) {
                    skillMasterLev.put(skillID, rs.getInt("MasterLevel"));
                }
                long dateExpire = rs.getLong("DateExpire");
                skillExpired.put(skillID, dateExpire != 0 ? FileTime.longToFileTime(dateExpire) : FileTime.END);
            }
        }

        if ((flag & DBChar.QuestRecord) != 0) {
            while (rs.next()) {
                questRecord.put(rs.getInt("QRKey"), rs.getString("QuestState"));
            }
        }

        if ((flag & DBChar.QuestRecordEx) != 0) {
            while (rs.next()) {
                SimpleStrMap simpleStrMap = new SimpleStrMap();
                simpleStrMap.initFromRawString(rs.getString("RawString"));
                questRecordEx.put(rs.getInt("QRKey"), simpleStrMap);
            }
        }

        if ((flag & DBChar.QuestComplete) != 0) {
            while (rs.next()) {
                long completeTime = rs.getLong("CompleteTime");
                questComplete.put(rs.getInt("QRKey"), completeTime != 0 ? FileTime.longToFileTime(completeTime) : FileTime.START);
            }
        }

        if ((flag & DBChar.WildHunterInfo) != 0) {
            while (rs.next()) {
                wildHunterInfo.setRidingType(rs.getByte("RidingType"));
                wildHunterInfo.setIdX(rs.getByte("IDx"));
                String capturedMobs = rs.getString("CapturedMobs");
                String[] splitted = capturedMobs.split(",");
                for (int i = 0; i < wildHunterInfo.getCapturedMobs().length; i++) {
                    wildHunterInfo.getCapturedMobs()[i] = Integer.parseInt(splitted[i]);
                }
            }
        }
    }

    public boolean setItem(byte ti, int pos, ItemSlotBase item) {
        if (ti < ItemType.Equip || ti > ItemType.Cash) {
            return false;
        }
        if (ti == ItemType.Equip) {
            if (pos == 0 || pos < -BodyPartCount && pos >= -BodyPartCount || pos > getItemSlotCount(ItemType.Equip) || pos < -BodyPartCount - BodyPartCount) {
                return false;
            }
            if (pos >= -BodyPartCount) {
                if (pos >= 0) {
                    itemSlot.get(ti).set(pos, item);
                } else {
                    equipped.set(-pos, item);
                }
            } else {
                equipped2.set(-BodyPartCount - pos, item);
            }
        } else {
            if (pos <= 0 || pos > getItemSlotCount(ti)) {
                return false;
            }
            itemSlot.get(ti).set(pos, item);
        }
        return true;
    }

    public void restoreItemSlot(List<List<ItemSlotBase>> backup, List<List<Integer>> backupItemTrading) {
        for (int ti = ItemType.Equip; ti <= ItemType.Cash; ti++) {
            itemSlot.get(ti).clear();
            itemSlot.get(ti).addAll(backup.get(ti));
            if (backupItemTrading != null) {
                itemTrading.get(ti).clear();
                itemTrading.get(ti).addAll(backupItemTrading.get(ti));
            }
        }
    }
    
    public static boolean CheckAdult(int nSSN1, int nSSN2, int nCriticalYear) {
        int nBirthYear; // [sp+0h] [bp-14h]@5
        
        if (nSSN2 / 1000000 != 1 && nSSN2 / 1000000 != 2 && nSSN2 / 1000000 != 5 && nSSN2 / 1000000 != 6)
            nBirthYear = nSSN1 / 10000 + 2000;
        else
            nBirthYear = nSSN1 / 10000 + 1900;
        return Calendar.getInstance().get(Calendar.YEAR) - nBirthYear >= nCriticalYear;
    }

    public void setMoneyTrading(int amount) {
        this.moneyTrading = amount;
    }

    public boolean setTrading(boolean trade) {
        if (this.onTrading == trade) {
            return false;
        } else {
            this.onTrading = trade;
            clearTradingInfo();
            return true;
        }
    }

    public int getCombatOrders() {
        return combatOrders;
    }

    public void setCombatOrders(int combatOrders) {
        this.combatOrders = combatOrders;
    }

    public String getQuestEx(int questID, String key) {
        SimpleStrMap strMap = questRecordEx.getOrDefault(questID, null);
        if (strMap != null) {
            return strMap.getValue(key);
        }
        return "";
    }

    public void initQuestExFromRawStr(int questID, String rawStr) {
        SimpleStrMap simpleStrMap = questRecordEx.getOrDefault(questID, new SimpleStrMap());
        simpleStrMap.initFromRawString(rawStr);
        questRecordEx.put(questID, simpleStrMap);
    }

    public boolean setQuestEx(int questID, String key, String value) {
        if (key == null || key.length() <= 0) {
            return false;
        }
        if (value == null || value.length() <= 0 || value.equals("DayN")) {
            return false;
        }
        SimpleStrMap simpleStrMap = questRecordEx.getOrDefault(questID, new SimpleStrMap());
        boolean result = simpleStrMap.setValue(key, value);
        questRecordEx.put(questID, simpleStrMap);
        return result;
    }

    public boolean resetQuestEx(int questID, String key) {
        if (key == null || key.length() <= 0) {
            return false;
        }
        SimpleStrMap simpleStrMap = questRecordEx.getOrDefault(questID, new SimpleStrMap());
        boolean result = simpleStrMap.setValue(key, null);
        questRecordEx.put(questID, simpleStrMap);
        return result;
    }

    // returns previous value
    public String setQuest(int qrKey, String qrValue) {
        if (qrValue == null) {
            qrValue = "";
        }
        return questRecord.put(qrKey, qrValue);
    }

    public boolean removeQuest(int questID) {
        //  if ( usQRKey == this->m_usSelectedMobQuestID )
        //  {
        //    v2 = &this->m_smQuestData;
        //    v2->dwMobID = 0;
        //    v2->bAbs = 0;
        //    v2->dwBonusEXP = 0;
        //  }
        return questRecord.remove(questID) != null;
    }

    public boolean isEquippedDualDagger() {
        ItemSlotBase main = equipped.get(11);
        ItemSlotBase sub = equipped.get(10);
        if (main != null && sub != null) {
            return ItemAccessor.getWeaponType(main.getItemID()) == ItemAccessor.WeaponTypeFlag.DAGGER && ItemAccessor.getWeaponType(sub.getItemID()) == ItemAccessor.WeaponTypeFlag.SUB_DAGGER;
        }
        return false;
    }
    public List<ItemSlotBase> getDragonEquipped() {
        return dragonEquipped;
    }

    public List<ItemSlotBase> getMechanicEquipped() {
        return mechanicEquipped;
    }

    public Map<Integer, Integer> getSkillMasterLev() {
        return skillMasterLev;
    }

    public Map<Integer, Long> getSkillCooltimeOver() {
        return skillCooltimeOver;
    }

    public Map<Integer, FileTime> getSkillExpired() {
        return skillExpired;
    }

    public Map<Integer, SimpleStrMap> getQuestRecordEx() {
        return questRecordEx;
    }

    public Map<Integer, FileTime> getQuestComplete() {
        return questComplete;
    }

    public Map<Integer, EquippedSetItem> getEquippedSetItem() {
        return equippedSetItem;
    }

    public FileTime getEquipExtExpire() {
        return equipExtExpire;
    }

    public Map<Integer, String> getQuestRecord() {
        return questRecord;
    }

    public WildHunterInfo getWildHunterInfo() {
        return wildHunterInfo;
    }
}
