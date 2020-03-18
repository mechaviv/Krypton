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
package game.party;

import game.field.Field;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class PartyData {
    private final PartyMember party;
    private final List<TownPortal> townPortals;
    private final List<Integer> fieldIDs, pqRewards, pqRewardTypes;
    private int pqRewardMobTemplateID;
    private boolean pqReward;

    public PartyData() {
        this.party = new PartyMember();
        this.townPortals = new ArrayList<>(6);
        this.fieldIDs = new ArrayList<>(6);
        this.pqRewards = new ArrayList<>(6);
        this.pqRewardTypes = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            townPortals.add(i, new TownPortal());
            fieldIDs.add(i, 0);
            pqRewards.add(i, 0);
            pqRewardTypes.add(i, 0);
        }
    }

    // 378 BYTES
    public void encode(OutPacket packet) {
        party.encode(packet);
        for (int fieldID : fieldIDs) {
            packet.encodeInt(fieldID);
        }
        for (TownPortal townPortal : townPortals) {
            packet.encodeInt(townPortal.getTownID());
            packet.encodeInt(townPortal.getFieldID());
            packet.encodeInt(townPortal.getSkillID());
            packet.encodeInt(townPortal.getFieldPortal().x);
            packet.encodeInt(townPortal.getFieldPortal().y);
        }
        for (int reward : pqRewards) {
            packet.encodeInt(reward);
        }
        for (int rewardType : pqRewardTypes) {
            packet.encodeInt(rewardType);
        }
        packet.encodeInt(pqRewardMobTemplateID);
        packet.encodeInt(pqReward ? 1 : 0);
    }

    public void decode(InPacket packet) {
        party.decode(packet);
        for (int i = 0; i < 6; i++) {
            this.fieldIDs.set(i, packet.decodeInt());
        }
        for (int i = 0; i < 6; i++) {
            TownPortal townPortal = new TownPortal();
            townPortal.setTownID(packet.decodeInt());
            townPortal.setFieldID(packet.decodeInt());
            townPortal.setSkillID(packet.decodeInt());
            townPortal.setFieldPortal(new Point(packet.decodeInt(), packet.decodeInt()));
            this.townPortals.set(i, townPortal);
        }

        for (int i = 0; i < 6; i++) {
            this.pqRewards.set(i, packet.decodeInt());
        }

        for (int i = 0; i < 6; i++) {
            this.pqRewardTypes.set(i, packet.decodeInt());
        }
        this.pqRewardMobTemplateID = packet.decodeInt();
        this.pqReward = packet.decodeInt() != 0;
    }

    public PartyMember getParty() {
        return party;
    }

    public List<Integer> getFieldID() {
        return fieldIDs;
    }

    public List<TownPortal> getTownPortal() {
        return townPortals;
    }

    public class TownPortal {
        private int townID;
        private int fieldID;
        private int skillID;
        private Point fieldPortal;

        public TownPortal() {
            this.townID = Field.Invalid;
            this.fieldID = Field.Invalid;
            this.skillID = -1;// or 0 ?
            this.fieldPortal = new Point(-1, -1);// or just 0, 0 ?
        }

        public int getTownID() {
            return townID;
        }

        public void setTownID(int townID) {
            this.townID = townID;
        }

        public int getFieldID() {
            return fieldID;
        }

        public void setFieldID(int fieldID) {
            this.fieldID = fieldID;
        }

        public int getSkillID() {
            return skillID;
        }

        public void setSkillID(int skillID) {
            this.skillID = skillID;
        }

        public Point getFieldPortal() {
            return fieldPortal;
        }

        public void setFieldPortal(Point fieldPortal) {
            this.fieldPortal = fieldPortal;
        }
    }
}
