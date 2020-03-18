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

import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class PartyPacket {
    public static OutPacket inviteParty(int characterID, String name, int level, int job) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.InviteParty);
        packet.encodeInt(characterID);
        packet.encodeString(name);
        packet.encodeInt(level);
        packet.encodeInt(job);
        packet.encodeBool(false);// bBlocked ?
        return packet;
    }

    public static OutPacket invitePartySent(String name) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.InviteParty_Sent);
        packet.encodeString(name);

        return packet;
    }

    public static OutPacket loadParty(int partyID, PartyData pd) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.LoadParty_Done);
        packet.encodeInt(partyID);
        pd.encode(packet);

        return packet;
    }

    public static OutPacket createNewParty(int partyID, PartyData.TownPortal townPortal) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.CreateNewParty_Done);
        packet.encodeInt(partyID);
        packet.encodeInt(townPortal.getTownID());
        packet.encodeInt(townPortal.getFieldID());
        packet.encodeInt(townPortal.getSkillID());
        packet.encodeShort(townPortal.getFieldPortal().x);
        packet.encodeShort(townPortal.getFieldPortal().y);
        return packet;
    }

    public static OutPacket joinParty(int partyID, String name, PartyData pd) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.JoinParty_Done);
        packet.encodeInt(partyID);
        packet.encodeString(name);
        pd.encode(packet);

        return packet;
    }

    public static OutPacket userMigration(int partyID, PartyData pd) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.UserMigration);
        packet.encodeInt(partyID);
        pd.encode(packet);

        return packet;
    }

    public static OutPacket changeLevelOrJob(int characterID, int level, int job) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.ChangeLevelOrJob);
        packet.encodeInt(characterID);
        packet.encodeInt(level);
        packet.encodeInt(job);
        return packet;
    }

    public static OutPacket changePartyBossDone(int characterID, boolean forced) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.ChangePartyBoss_Done);
        packet.encodeInt(characterID);
        packet.encodeBool(forced);
        return packet;
    }

    public static OutPacket townPortalChanged(int idX, PartyData.TownPortal townPortal) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.PartyInfo_TownPortalChanged);
        packet.encodeByte(idX);
        packet.encodeInt(townPortal.getTownID());
        packet.encodeInt(townPortal.getFieldID());
        packet.encodeInt(townPortal.getSkillID());
        packet.encodeShort(townPortal.getFieldPortal().x);
        packet.encodeShort(townPortal.getFieldPortal().y);
        return packet;
    }

    public static OutPacket adminCannotCreate() {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.AdminCannotCreate);
        return packet;
    }

    public static OutPacket adminCannotInvite() {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.AdminCannotInvite);
        return packet;
    }

    public static OutPacket serverMsg(String msg) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.ServerMsg);
        packet.encodeBool(msg != null);
        if (msg != null) {
            packet.encodeString(msg);
        }
        return packet;
    }

    public static OutPacket partyResult(int type) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(type);
        return packet;
    }

    public static OutPacket userHP(int characterID, int hp, int mhp) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserHP);
        packet.encodeInt(characterID);
        packet.encodeInt(hp);
        packet.encodeInt(mhp);
        return packet;
    }
}
