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

import common.user.UserEffect;
import game.user.quest.QuestFlag;
import game.user.quest.QuestTimer;
import game.user.skill.Skills;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

import java.util.List;

/**
 *
 * @author Eric
 */
public class UserLocal {
    
    /**
     * The (local) user effect packet. 
     * This sends both skill and level-up effects to the user using them.
     * 
     * @param userEffect The type of user effect (@see game.user.User.UserEffect)
     * @param args The optional arguments (nSkillID and nSLV for skill effects)
     * 
     * @return The local user effect packet
     */
    public static OutPacket onEffect(byte userEffect, String str, int... args) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEffectLocal);
        packet.encodeByte(userEffect);
        switch (userEffect) {
            case UserEffect.LevelUp:
                break;
            case UserEffect.SkillUse:
                int skillID = args[0];
                packet.encodeInt(args[0]);
                packet.encodeByte(args[2]);
                packet.encodeByte(args[1]);
                if (skillID == Skills.Citizen.SUMMON_MONSTER) {
                    packet.encodeByte(args[3]);// bLeft
                    packet.encodeShort(args[4]);// x
                    packet.encodeShort(args[5]);// y
                }
                if (skillID == Skills.Citizen.CAPTURE) {
                    packet.encodeByte(args[3]);
                }
                break;
            case UserEffect.SkillAffected:
                packet.encodeInt(args[0]);
                packet.encodeByte(args[1]);
                break;
            case UserEffect.SkillSpecial:
                packet.encodeInt(args[0]);
                break;
            case UserEffect.AvatarOriented:
            case UserEffect.ReservedEffect:
                packet.encodeString(str);
                packet.encodeInt(0);// no use
                break;
        }
        return packet;
    }

    public static OutPacket balloonMsg(String message, int width, int timeOut, int x, int y) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserBalloonMsg);
        packet.encodeString(message);
        packet.encodeShort(width);
        packet.encodeShort(timeOut);

        boolean automated = x == 0 && y == 0;
        packet.encodeBool(automated);
        if (!automated) {
            packet.encodeInt(x);
            packet.encodeInt(y);
        }
        return packet;
    }

    public static OutPacket setDirectionMode(boolean set, int duration) {
        OutPacket packet = new OutPacket(LoopbackPacket.SetDirectionMode);
        packet.encodeBool(set);
        packet.encodeInt(duration);
        return packet;
    }

    public static OutPacket setStandAloneMode(boolean standAloneMode) {
        OutPacket packet = new OutPacket(LoopbackPacket.SetStandAloneMode);
        packet.encodeBool(standAloneMode);
        return packet;
    }

    public static class QuestResult {
        public static OutPacket onStartQuestTimer(List<QuestTimer> timers) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_Start_QuestTimer);

            packet.encodeShort(timers.size());
            for (QuestTimer timer : timers) {
                packet.encodeShort(timer.getQuestID());
                packet.encodeInt(timer.getRemain());
            }
            return packet;
        }

        public static OutPacket onEndQuestTimer(List<Integer> quests) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_End_QuestTimer);

            packet.encodeShort(quests.size());
            for (Integer quest : quests) {
                packet.encodeShort(quest);
            }
            return packet;
        }

        public static OutPacket onStartTimeKeepQuestTimer(QuestTimer timer) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_Start_TimeKeepQuestTimer);

            packet.encodeShort(timer.getQuestID());
            packet.encodeInt(timer.getRemain());

            return packet;
        }

        public static OutPacket onEndTimeKeepQuestTimer(List<Integer> quests) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_End_TimeKeepQuestTimer);

            packet.encodeShort(quests.size());
            for (Integer quest : quests) {
                packet.encodeShort(quest);
            }
            return packet;
        }

        public static OutPacket onActSuccess(int questID, int npcTemplateID, int nextQuest) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_Act_Success);
            packet.encodeShort(questID);
            packet.encodeInt(npcTemplateID);
            packet.encodeShort(nextQuest);
            return packet;
        }

        public static OutPacket onActFailedInventory(int questID) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_Act_Failed_Inventory);
            packet.encodeShort(questID);
            return packet;
        }

        public static OutPacket onActFailedTimeOver(int questID) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_Act_Failed_TimeOver);
            packet.encodeShort(questID);
            return packet;
        }

        public static OutPacket ActResetQuestTimer(int questID) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(QuestFlag.QuestRes_Act_Reset_QuestTimer);
            packet.encodeShort(questID);
            return packet;
        }

        public static OutPacket onActFailed(int questID, int failCode) {
            OutPacket packet = new OutPacket(LoopbackPacket.UserQuestResult);
            packet.encodeByte(failCode);
            packet.encodeShort(questID);
            return packet;
        }
    }
}
