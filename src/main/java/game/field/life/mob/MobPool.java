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
package game.field.life.mob;

import game.field.MovePath;
import game.user.stat.Flag;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class MobPool {
    
    public static OutPacket onMobEnterField(Mob mob) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobEnterField);
        mob.encodeInitData(packet);
        return packet;
    }
    
    public static OutPacket onMobLeaveField(int mobID, byte deadType) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobLeaveField);
        packet.encodeInt(mobID);
        packet.encodeByte(deadType);
        //if (deadType == Mob.MOB_LEAVE_FIELD_SWALLOW) {
        //    packet.encodeInt(0);// dwSwallowCharacterID
        //}
        return packet;
    }
    
    public static OutPacket onStatSet(Mob mob, Flag flagSet, short delay) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobStatSet);
        packet.encodeInt(mob.getGameObjectID());
        mob.getMobStat().encodeTemporary(packet, flagSet);
        packet.encodeShort(delay);
        packet.encodeByte(0);
        if (MobStats.isMovementAffectingStat(flagSet)) {
            packet.encodeByte(0);
        }
        return packet;
    }

    public static OutPacket onStatReset(Mob mob, Flag flagReset) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobStatReset);
        packet.encodeInt(mob.getGameObjectID());
        packet.encodeBuffer(flagReset.toByteArray());
        packet.encodeByte(0);
        if (MobStats.isMovementAffectingStat(flagReset)) {
            packet.encodeByte(0);
        }
        return packet;
    }

    public static OutPacket onCtrlAck(int mobID, short mobCtrlSN, boolean nextAttackPossible, int mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobCtrlAck);
        packet.encodeInt(mobID);
        packet.encodeShort(mobCtrlSN);
        packet.encodeBool(nextAttackPossible);
        packet.encodeShort(mp);
        packet.encodeByte(0);// skillCommand
        packet.encodeByte(0);// slv
        return packet;
    }
    
    public static OutPacket onMobChangeController(Mob mob, int mobID, byte level) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobChangeController);
        packet.encodeByte(level);
        if (level != 0) {
            mob.encodeInitData(packet);
        } else {
            packet.encodeInt(mobID);
        }
        return packet;
    }
    
    public static OutPacket onMove(int mobID, boolean nextAttackPossible, byte left, int skillID, MovePath mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobMove);
        packet.encodeInt(mobID);
        packet.encodeBool(false);// bNotForceLanding
        packet.encodeBool(false);// bNotChangeAction
        packet.encodeBool(nextAttackPossible);
        packet.encodeByte(left);

        packet.encodeInt(skillID);
        packet.encodeInt(0);// multi target for ball size
        packet.encodeInt(0);// rand time for area attack size
        mp.encode(packet);
        return packet;
    }

    //nHPIndicator

    public static OutPacket onHPIndicator(int mobID, int percentage) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobHPIndicator);
        packet.encodeInt(mobID);
        packet.encodeByte(percentage);
        return packet;
    }
    public static OutPacket onSuspendReset(int mobID, boolean suspendReset) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobSuspendReset);
        packet.encodeInt(mobID);
        packet.encodeBool(suspendReset);
        return packet;
    }
}
