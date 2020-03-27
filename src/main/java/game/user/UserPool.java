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

import game.user.stat.CharacterTemporaryStat;
import game.user.stat.SecondaryStat;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserPool {
    
    /**
     * Adds a user into the field.
     * 
     * @param user The user to enter into the field
     * 
     * @return The enter field packet
     */
    public static OutPacket onUserEnterField(User user) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEnterField);
        packet.encodeInt(user.getCharacterID());

        packet.encodeByte(user.getCharacter().getCharacterStat().getLevel());
        packet.encodeString(user.getCharacterName());

        packet.encodeString("");
        packet.encodeShort(0);
        packet.encodeByte(0);
        packet.encodeShort(0);
        packet.encodeByte(0);

        user.getSecondaryStat().encodeForRemote(packet, CharacterTemporaryStat.getMask(CharacterTemporaryStat.NONE));
        packet.encodeShort(user.getCharacter().getCharacterStat().getJob());
        user.getCharacter().encodeAvatarLook(packet);

        packet.encodeInt(0);// driver ID
        packet.encodeInt(0);// passenser id
        packet.encodeInt(0);// choco count
        packet.encodeInt(0);// active effect itemid
        packet.encodeInt(0);// completed set itemid
        packet.encodeInt(0);// portable chair id
        packet.encodeShort(user.getCurrentPosition().x);
        packet.encodeShort(user.getCurrentPosition().y);
        packet.encodeByte(user.getMoveAction());
        packet.encodeShort(user.getFootholdSN());
        packet.encodeByte(0);// show admin effect
        packet.encodeByte(0);// pet
        packet.encodeInt(0);// taming mob level
        packet.encodeInt(0);// taming mob exp
        packet.encodeInt(0);// taming mob fatigue
        packet.encodeByte(0);// mini room shit
        packet.encodeByte(0);// ad board
        packet.encodeByte(0);// couple record
        packet.encodeByte(0);// friend record
        packet.encodeByte(0);// marriage record
        packet.encodeByte(0);// delayed effect
        packet.encodeByte(0);// new year shit
        packet.encodeInt(0);// phase
        return packet;
    }
    
    /**
     * Removes a user from the field.
     * 
     * @param characterID The ID of the user to remove from the map
     * 
     * @return The leave field packet
     */
    public static OutPacket onUserLeaveField(int characterID) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserLeaveField);
        packet.encodeInt(characterID);
        return packet;
    }
}
