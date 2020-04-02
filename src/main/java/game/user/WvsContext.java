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
package game.user;

import common.BroadcastMsg;
import common.GivePopularityRes;
import common.item.ItemSlotBase;
import common.user.CharacterStat;
import common.user.MessageType;
import common.user.WildHunterInfo;
import game.field.drop.DropPickup;
import game.user.item.ChangeLog;
import game.user.item.InventoryManipulator;
import game.user.skill.SkillRecord;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.Flag;
import game.user.stat.SecondaryStat;
import java.util.List;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.FileTime;

/**
 *
 * @author Eric
 */
public class WvsContext {
    
    public static OutPacket onInventoryOperation(List<ChangeLog> changeLog, byte onExclResult) {
        return InventoryManipulator.makeInventoryOperation(onExclResult, changeLog);
    }
    
    public static OutPacket onInventoryGrow(int ti, int slotMax) {
        OutPacket packet = new OutPacket(LoopbackPacket.InventoryGrow);
        packet.encodeByte(ti);
        packet.encodeByte(slotMax);
        return packet;
    }

    public static OutPacket onStatChanged(byte onExclRequest, CharacterStat cs, int flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.StatChanged);
        packet.encodeByte(onExclRequest);
        cs.encodeChangeStat(packet, flag);
        packet.encodeByte(0);
        packet.encodeByte(0);
        packet.encodeByte(0);// for secure i guess
        return packet;
    }

    public static OutPacket onTemporaryStatSet(SecondaryStat ss, Flag flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.TemporaryStatSet);
        ss.encodeForLocal(packet, flag);
        packet.encodeShort(0);// delay
        if (CharacterTemporaryStat.isMovementAffectingStat(flag)) {
            packet.encodeByte(0);
        }
        return packet;
    }

    public static OutPacket onTemporaryStatReset(Flag flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.TemporaryStatReset);
        packet.encodeBuffer(flag.toByteArray());
        if (CharacterTemporaryStat.isMovementAffectingStat(flag)) {
            packet.encodeByte(0);
        }
        return packet;
    }

    public static OutPacket onBroadcastMsg(byte type, String msg) {
        return onBroadcastMsg(type, msg, 0);
    }

    public static OutPacket onBroadcastMsg(byte type, String msg, int templateID) {
        OutPacket packet = new OutPacket(LoopbackPacket.BroadcastMsg);
        packet.encodeByte(type);
        packet.encodeString(msg);

        ItemSlotBase item = null;
        switch (type) {
            case BroadcastMsg.SPEAKER_WORLD:
            case BroadcastMsg.SKULL_SPEAKER:
                packet.encodeByte(0);
                packet.encodeByte(0);
                break;
            case BroadcastMsg.ITEM_SPEAKER:
                packet.encodeByte(0);
                packet.encodeByte(0);
                packet.encodeBool(item != null);
                if (item != null) {
                    item.encode(packet);
                }
                break;
            case BroadcastMsg.NOTICE_WITHOUT_PREFIX:
                packet.encodeInt(0);
                break;
            case BroadcastMsg.UTIL_DLG_EX:
                packet.encodeInt(templateID);
                break;
            case BroadcastMsg.SPEAKER_BRIDGE:
                packet.encodeByte(0);
                break;
            case BroadcastMsg.ART_SPEAKER_WORLD:
                int lines = 0;
                packet.encodeByte(lines);
                if (lines > 1) {
                    packet.encodeString("");
                }
                if (lines > 2) {
                    packet.encodeString("");
                }
                packet.encodeByte(0);
                packet.encodeByte(0);
                break;
            case BroadcastMsg.BLOW_WEATHER:
                packet.encodeInt(0);
                break;
            case BroadcastMsg.GACHAPON_ANNOUNCE:
                packet.encodeInt(0);
                packet.encodeString("");
                item.encode(packet);
                break;
            case BroadcastMsg.GACHAPON_ANNOUNCE_OPEN:
            case BroadcastMsg.GACHAPON_ANNOUNCE_COPY:
                packet.encodeString("");
                item.encode(packet);
                break;
            case BroadcastMsg.CASH_SHOP_AD:
                packet.encodeInt(0);// item slot
                break;
        }
        return packet;
    }

    public static OutPacket onDropPickUpMessage(int dropType, int incMeso, int itemID, int quantity) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.DropPickUpMessage);

        packet.encodeByte(dropType);
        if (dropType == DropPickup.AddInventoryItem) {
            packet.encodeInt(itemID);
            packet.encodeInt(quantity);
        } else if (dropType == DropPickup.Messo) {
            packet.encodeByte(0);// bPortionNotFound
            packet.encodeInt(incMeso);
            packet.encodeShort(0);// Internet Cafe Meso Bonus
        }
        return packet;
    }

    public static OutPacket onQuestRecordMessage(int questID, int type, Object value) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.QuestRecordMessage);

        packet.encodeShort(questID);
        packet.encodeByte(type);
        switch (type) {
            case 1:
                packet.encodeString(value == null ? "" : (String) value);
                break;
            case 2:
                packet.encodeFileTime((FileTime) value);
                break;
            default:
                packet.encodeBool((Boolean) value);
                break;
        }
        return packet;
    }

    public static OutPacket onQuestRecordExMessage(int questID, String rawStr) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.QuestRecordExMessage);
        packet.encodeShort(questID);
        packet.encodeString(rawStr);
        return packet;
    }

    public static OutPacket onCashItemExpireMessage(int itemID) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.CashItemExpireMessage);
        packet.encodeInt(itemID);
        return packet;
    }

    public static OutPacket onIncExpMessage(boolean isLastHit, int incExp, boolean onQuest, int incEXPbySMQ, int eventPrecentage, int partyBonusPercentage, int playTimeHour, int questBonusRate, int questBonusRemainConut, int partyBonusEventRate, int weddingBonusEXP, int partyBonusEXP, int itemBonusEXP, int premiumIpEXP, int rainbowWeekEventEXP, int partyEXPRingEXP, int cakePieEventBonus) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.IncEXPMessage);

        packet.encodeBool(isLastHit);
        packet.encodeInt(incExp);
        packet.encodeBool(onQuest);
        packet.encodeInt(incEXPbySMQ);
        packet.encodeByte(eventPrecentage);
        packet.encodeByte(partyBonusPercentage);
        packet.encodeInt(weddingBonusEXP);
        if (eventPrecentage > 0) {
            packet.encodeByte(playTimeHour);
        }
        if (onQuest) {
            packet.encodeByte(questBonusRate);
            if (questBonusRate > 0) {
                packet.encodeByte(questBonusRemainConut);
            }
        }
        packet.encodeByte(partyBonusEventRate);
        packet.encodeInt(partyBonusEXP);
        packet.encodeInt(itemBonusEXP);
        packet.encodeInt(premiumIpEXP);
        packet.encodeInt(rainbowWeekEventEXP);
        packet.encodeInt(partyEXPRingEXP);
        packet.encodeInt(cakePieEventBonus);

        return packet;
    }

    public static OutPacket onIncPOPMessage(int incPOP) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.IncPOPMessage);
        packet.encodeInt(incPOP);
        return packet;
    }

    public static OutPacket onIncMoneyMessage(int incMoney) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.IncMoneyMessage);
        packet.encodeInt(incMoney);
        return packet;
    }

    public static OutPacket onIncSPMessage(int job, int inc) {
        OutPacket packet = new OutPacket(LoopbackPacket.Message);
        packet.encodeByte(MessageType.IncSPMessage);
        packet.encodeShort(job);
        packet.encodeByte(inc);
        return packet;
    }

    public static OutPacket onGivePopularityResult(byte type, String characterName, boolean raise) {
        OutPacket packet = new OutPacket(LoopbackPacket.GivePopularityResult);
        packet.encodeByte(type);
        switch (type) {
            case GivePopularityRes.Success:
            case GivePopularityRes.Notify:
                packet.encodeString(characterName);
                packet.encodeBool(raise);
                break;
        }
        return packet;
    }

    public static OutPacket onCharacterInfo(User user) {
        OutPacket packet = new OutPacket(LoopbackPacket.CharacterInfo);
        packet.encodeInt(user.getCharacterID());
        packet.encodeByte(user.getCharacter().getCharacterStat().getLevel());
        packet.encodeShort(user.getCharacter().getCharacterStat().getJob());
        packet.encodeShort(user.getCharacter().getCharacterStat().getPOP());
        packet.encodeString(user.getCommunity());
        return packet;
    }
    
    public static OutPacket onChangeSkillRecordResult(byte onExclRequest, List<SkillRecord> change) {
        OutPacket packet = new OutPacket(LoopbackPacket.ChangeSkillRecordResult);
        packet.encodeByte(onExclRequest);
        packet.encodeShort(change.size());
        for (SkillRecord skill : change) {
            packet.encodeInt(skill.getSkillID());
            packet.encodeInt(skill.getInfo());
            packet.encodeInt(skill.getMasterLevel());
            packet.encodeFileTime(skill.getDateExpire());
        }
        packet.encodeByte(0);
        return packet;
    }
    
    public static OutPacket onSkillUseResult(byte onExclRequest) {
        OutPacket packet = new OutPacket(LoopbackPacket.SkillUseResult);
        packet.encodeByte(onExclRequest);
        return packet;
    }

    public static OutPacket onWildHunterInfo(WildHunterInfo wildHunterInfo) {
        OutPacket packet = new OutPacket(LoopbackPacket.WildHunterInfo);
        wildHunterInfo.encode(packet);
        return packet;
    }
}
