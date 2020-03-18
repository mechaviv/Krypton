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
package game.field;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Rect;

/**
 *
 * @author Eric
 */
public class MovePath {
    private short x;
    private short y;
    private short vx;
    private short vy;
    private short fhLast;
    private final Rect moveRect;
    private final LinkedList<Elem> elements;
    private final List<Integer> keyPadState;

    public MovePath() {
        this.elements = new LinkedList<>();
        this.keyPadState = new ArrayList<>();
        this.moveRect = new Rect();
    }

    public void decode(InPacket packet) {
        boolean passive = true;
        short oldX = packet.decodeShort();
        short oldY = packet.decodeShort();
        short oldVX = packet.decodeShort();
        short oldVY = packet.decodeShort();

        this.x = oldX;
        this.y = oldY;
        this.vx = oldVX;
        this.vy = oldVY;

        int count = packet.decodeByte();
        for (int i = 0; i < count; i++) {
            Elem elem = new Elem();
            elem.setAttr(packet.decodeByte());
            switch (elem.getAttr()) {
                case MovePathAttribute.NORMAL:
                case MovePathAttribute.HANG_ON_BACK:
                case MovePathAttribute.FALLDOWN:
                case MovePathAttribute.WINGS:
                case MovePathAttribute.MOB_ATTACK_RUSH:
                case MovePathAttribute.MOB_ATTACK_RUSH_STOP:
                    elem.setX(packet.decodeShort());
                    elem.setY(packet.decodeShort());
                    elem.setVx(packet.decodeShort());
                    elem.setVy(packet.decodeShort());
                    short fh = packet.decodeShort();
                    elem.setFh(fh);
                    this.fhLast = fh;
                    if (elem.getAttr() == MovePathAttribute.FALLDOWN) {
                        elem.setFhFallStart(packet.decodeShort());
                    }
                    elem.setOffsetX(packet.decodeShort());
                    elem.setOffsetY(packet.decodeShort());
                    break;
                case MovePathAttribute.JUMP:
                case MovePathAttribute.IMPACT:
                case MovePathAttribute.START_WINGS:
                case MovePathAttribute.MOB_TOSS:
                case MovePathAttribute.DASH_SLIDE:
                case MovePathAttribute.MOB_LADDER:
                case MovePathAttribute.MOB_RIGHTANGLE:
                case MovePathAttribute.MOB_STOPNODE_START:
                case MovePathAttribute.MOB_BEFORE_NODE:
                    elem.setX(oldX);
                    elem.setY(oldY);
                    elem.setFh((short) 0);
                    elem.setVx(packet.decodeShort());
                    elem.setVy(packet.decodeShort());
                    break;
                case MovePathAttribute.IMMEDIATE:
                case MovePathAttribute.TELEPORT:
                case MovePathAttribute.ASSAULTER:
                case MovePathAttribute.ASSASSINATION:
                case MovePathAttribute.RUSH:
                case MovePathAttribute.SIT_DOWN:
                    elem.setX(packet.decodeShort());
                    elem.setY(packet.decodeShort());
                    fh = packet.decodeShort();
                    elem.setFh(fh);
                    this.fhLast = fh;
                    elem.setVx((short) 0);
                    elem.setVy((short) 0);
                    break;
                case MovePathAttribute.STAT_CHANGE:
                    elem.setStat(packet.decodeByte());
                    elem.setVx((short) 0);
                    elem.setVy((short) 0);
                    elem.setX(oldX);
                    elem.setY(oldY);
                    elem.setElapse((short) 0);
                    elem.setMoveAction((byte) 0);
                    elem.setFh((short) 0);
                    this.fhLast = 0;
                    break;
                case MovePathAttribute.START_FALL_DOWN:
                    elem.setX(oldX);
                    elem.setY(oldY);
                    elem.setFh((short) 0);
                    elem.setVx(packet.decodeShort());
                    elem.setVy(packet.decodeShort());
                    elem.setFhFallStart(packet.decodeShort());
                    break;
                case MovePathAttribute.FLYING_BLOCK:
                    elem.setX(packet.decodeShort());
                    elem.setY(packet.decodeShort());
                    elem.setVx(packet.decodeShort());
                    elem.setVy(packet.decodeShort());
                    break;
                case MovePathAttribute.FLASH_JUMP:
                case MovePathAttribute.ROCKET_BOOSTER:
                case MovePathAttribute.BACKSTEP_SHOT:
                case MovePathAttribute.MOB_POWER_KNOCKBACK:
                case MovePathAttribute.VERTICAL_JUMP:
                case MovePathAttribute.CUSTOM_IMPACT:
                case MovePathAttribute.COMBAT_STEP:
                case MovePathAttribute.HIT:
                case MovePathAttribute.TIME_BOMB_ATTACK:
                case MovePathAttribute.SNOWBALL_TOUCH:
                case MovePathAttribute.BUFFZONE_EFFECT:
                    elem.setX(oldX);
                    elem.setY(oldY);
                    elem.setVx(oldVX);
                    elem.setVy(oldVY);
                    break;
            }
            if (elem.getAttr() != MovePathAttribute.STAT_CHANGE) {
                elem.setMoveAction(packet.decodeByte());
                elem.setElapse(packet.decodeShort());
                // my client opt = 0 (first short in setfield)
                //if ( CClientOptMan::GetOpt(TSingleton<CClientOptMan>::ms_pInstance, 2u) )
                //      {
                //        pElem->usRandCnt = CInPacket::Decode2(v3);
                //        pElem->usActualRandCnt = CInPacket::Decode2(v3);
                //      }
                oldX = elem.getX();
                oldY = elem.getY();
                oldVX = elem.getVx();
                oldVY = elem.getVy();
            }
            elements.add(elem);
        }
        if (passive) {
            int keys = packet.decodeByte();

            int value = 0;
            for (int i = 0; i < keys; i++) {
                if (i % 2 != 0) {
                    value >>= 4;
                } else {
                    value = packet.decodeByte();
                }
                keyPadState.add(value);
            }
            moveRect.left = packet.decodeShort();
            moveRect.top = packet.decodeShort();
            moveRect.right = packet.decodeShort();
            moveRect.bottom = packet.decodeShort();
        }
    }

    public void encode(OutPacket packet) {
        short x = getX();
        short y = getY();
        packet.encodeShort(x);
        packet.encodeShort(y);
        packet.encodeShort(getVx());
        packet.encodeShort(getVy());
        this.moveRect.right = x;
        this.moveRect.left = x;
        this.moveRect.bottom = y;
        this.moveRect.top = y;
        packet.encodeByte(elements.size());
        for (Elem elem : elements) {
            packet.encodeByte(elem.getAttr());
            switch (elem.getAttr()) {
                case MovePathAttribute.NORMAL:
                case MovePathAttribute.HANG_ON_BACK:
                case MovePathAttribute.FALLDOWN:
                case MovePathAttribute.WINGS:
                case MovePathAttribute.MOB_ATTACK_RUSH:
                case MovePathAttribute.MOB_ATTACK_RUSH_STOP:
                    packet.encodeShort(elem.getX());
                    packet.encodeShort(elem.getY());
                    packet.encodeShort(elem.getVx());
                    packet.encodeShort(elem.getVy());
                    packet.encodeShort(elem.getFh());
                    if (elem.getAttr() == MovePathAttribute.FALLDOWN) {
                        packet.encodeShort(elem.getFhFallStart());
                    }
                    packet.encodeShort(elem.getOffsetX());
                    packet.encodeShort(elem.getOffsetY());
                    break;
                case MovePathAttribute.JUMP:
                case MovePathAttribute.IMPACT:
                case MovePathAttribute.START_WINGS:
                case MovePathAttribute.MOB_TOSS:
                case MovePathAttribute.DASH_SLIDE:
                case MovePathAttribute.MOB_LADDER:
                case MovePathAttribute.MOB_RIGHTANGLE:
                case MovePathAttribute.MOB_STOPNODE_START:
                case MovePathAttribute.MOB_BEFORE_NODE:
                    packet.encodeShort(elem.getVx());
                    packet.encodeShort(elem.getVy());
                    break;
                case MovePathAttribute.IMMEDIATE:
                case MovePathAttribute.TELEPORT:
                case MovePathAttribute.ASSAULTER:
                case MovePathAttribute.ASSASSINATION:
                case MovePathAttribute.RUSH:
                case MovePathAttribute.SIT_DOWN:
                    packet.encodeShort(elem.getX());
                    packet.encodeShort(elem.getY());
                    packet.encodeShort(elem.getFh());
                    break;
                case MovePathAttribute.STAT_CHANGE:
                    packet.encodeByte(elem.getStat());
                    continue;
                case MovePathAttribute.START_FALL_DOWN:
                    packet.encodeShort(elem.getVx());
                    packet.encodeShort(elem.getVy());
                    packet.encodeShort(elem.getFhFallStart());
                    break;
                case MovePathAttribute.FLYING_BLOCK:
                    packet.encodeShort(elem.getX());
                    packet.encodeShort(elem.getY());
                    packet.encodeShort(elem.getVx());
                    packet.encodeShort(elem.getVy());
                    break;
            }
            int newX = elem.getX();
            if (newX < this.moveRect.left) {
                this.moveRect.left = newX;
            }
            if (newX < this.moveRect.right) {
                this.moveRect.right = newX;
            }
            int newY = elem.getY();
            if (newY < this.moveRect.top) {
                this.moveRect.top = newY;
            }
            if (newY < this.moveRect.bottom) {
                this.moveRect.bottom = newY;
            }
            packet.encodeByte(elem.getMoveAction());
            packet.encodeShort(elem.getElapse());
        }
    }

    public LinkedList<Elem> getElem() {
        return elements;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public short getVx() {
        return vx;
    }

    public void setVx(short vx) {
        this.vx = vx;
    }

    public short getVy() {
        return vy;
    }

    public void setVy(short vy) {
        this.vy = vy;
    }

    public Rect getMoveRect() {
        return moveRect;
    }

    public class Elem {
        private byte attr;
        private short x;
        private short y;
        private short vx;
        private short vy;
        private byte moveAction;
        private short fh;
        private short fhFallStart;
        private short elapse;
        private byte stat;
        private short offsetX;
        private short offsetY;
        public Elem() {
            // dummy
        }

        public byte getAttr() {
            return attr;
        }

        public short getX() {
            return x;
        }

        public short getY() {
            return y;
        }

        public short getVx() {
            return vx;
        }

        public short getVy() {
            return vy;
        }

        public byte getMoveAction() {
            return moveAction;
        }

        public short getFh() {
            return fh;
        }

        public short getElapse() {
            return elapse;
        }

        public void setAttr(byte attr) {
            this.attr = attr;
        }

        public void setX(short x) {
            this.x = x;
        }

        public void setY(short y) {
            this.y = y;
        }

        public void setVx(short vx) {
            this.vx = vx;
        }

        public void setVy(short vy) {
            this.vy = vy;
        }

        public void setMoveAction(byte moveAction) {
            this.moveAction = moveAction;
        }

        public void setFh(short fh) {
            this.fh = fh;
        }

        public void setElapse(short elapse) {
            this.elapse = elapse;
        }

        public short getFhFallStart() {
            return fhFallStart;
        }

        public void setFhFallStart(short fhFallStart) {
            this.fhFallStart = fhFallStart;
        }

        public byte getStat() {
            return stat;
        }

        public void setStat(byte stat) {
            this.stat = stat;
        }

        public short getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(short offsetX) {
            this.offsetX = offsetX;
        }

        public short getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(short offsetY) {
            this.offsetY = offsetY;
        }
    }

    public class MovePathAttribute {
        public static final int
                NORMAL = 0x0,
                JUMP = 0x1,
                IMPACT = 0x2,
                IMMEDIATE = 0x3,
                TELEPORT = 0x4,
                HANG_ON_BACK = 0x5,
                ASSAULTER = 0x6,
                ASSASSINATION = 0x7,
                RUSH = 0x8,
                STAT_CHANGE = 0x9,
                SIT_DOWN = 0xA,
                START_FALL_DOWN = 0xB,
                FALLDOWN = 0xC,
                START_WINGS = 0xD,
                WINGS = 0xE,
                ARAN_ADJUST = 0xF,
                MOB_TOSS = 0x10,
                FLYING_BLOCK = 0x11,
                DASH_SLIDE = 0x12,
                BMAGE_ADJUST = 0x13,
                FLASH_JUMP = 0x14,
                ROCKET_BOOSTER = 0x15,
                BACKSTEP_SHOT = 0x16,
                MOB_POWER_KNOCKBACK = 0x17,
                VERTICAL_JUMP = 0x18,
                CUSTOM_IMPACT = 0x19,
                COMBAT_STEP = 0x1A,
                HIT = 0x1B,
                TIME_BOMB_ATTACK = 0x1C,
                SNOWBALL_TOUCH = 0x1D,
                BUFFZONE_EFFECT = 0x1E,
                MOB_LADDER = 0x1F,
                MOB_RIGHTANGLE = 0x20,
                MOB_STOPNODE_START = 0x21,
                MOB_BEFORE_NODE = 0x22,
                MOB_ATTACK_RUSH = 0x23,
                MOB_ATTACK_RUSH_STOP = 0x24;
    }
}
