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

import java.util.ArrayList;
import java.util.List;

import network.packet.InPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class PartyMember {
    private final List<Integer> characterID;
    private final List<String> characterName;
    private final List<Integer> characterJob;
    private final List<Integer> characterLevel;
    private final List<Integer> characterChannelID;
    private int partyBossCharacterID;

    public PartyMember() {
        this.characterID = new ArrayList<>(6);
        this.characterName = new ArrayList<>(6);
        this.characterJob = new ArrayList<>(6);
        this.characterLevel = new ArrayList<>(6);
        this.characterChannelID = new ArrayList<>(6);

        for (int i = 0; i < 6; i++) {
            characterID.add(i, 0);
            characterName.add(i, "");
            characterJob.add(i, 0);
            characterLevel.add(i, 0);
            characterChannelID.add(i, -2);// or channel ID
        }
        partyBossCharacterID = 0;
    }

    // 178 BYTES
    public void encode(OutPacket packet) {
        for (int id : this.characterID) {
            packet.encodeInt(id);
        }
        for (String name : this.characterName) {
            packet.encodeString(name, 13);
        }
        for (int job : this.characterJob) {
            packet.encodeInt(job);
        }
        for (int level : this.characterLevel) {
            packet.encodeInt(level);
        }
        for (int channelID : this.characterChannelID) {
            packet.encodeInt(channelID);
        }
        packet.encodeInt(this.partyBossCharacterID);
    }

    public void decode(InPacket packet) {
        for (int i = 0; i < 6; i++) {
            this.characterID.set(i, packet.decodeInt());
        }
        for (int i = 0; i < 6; i++) {
            this.characterName.set(i, packet.decodeString(13));
        }
        for (int i = 0; i < 6; i++) {
            this.characterJob.set(i, packet.decodeInt());
        }
        for (int i = 0; i < 6; i++) {
            this.characterLevel.set(i, packet.decodeInt());
        }
        for (int i = 0; i < 6; i++) {
            this.characterChannelID.set(i, packet.decodeInt());
        }
        this.partyBossCharacterID = packet.decodeInt();
    }

    public int getCharacterID(String name) {
        int idx = findIndex(name);
        if (idx == -1) {
            return 0;
        } else {
            return characterID.get(idx);
        }
    }

    public int getPartyBossCharacterID() {
        return partyBossCharacterID;
    }

    public List<Integer> getCharacterID() {
        return characterID;
    }

    public List<Integer> getJob() {
        return characterJob;
    }

    public List<Integer> getLevel() {
        return characterLevel;
    }

    public List<Integer> getChannelID() {
        return characterChannelID;
    }

    public List<String> getCharacterName() {
        return characterName;
    }

    public int getMemberCount() {
        int count = 0;
        for (int id : characterID) {
            if (id != 0) {
                count++;
            }
        }
        return count;
    }

    public int findIndex(String name) {
        for (int i = 0; i < characterName.size(); i++) {
            if (characterName.get(i).toLowerCase().equals(name.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public void setPartyBossCharacterID(int partyBossCharacterID) {
        this.partyBossCharacterID = partyBossCharacterID;
    }
}
