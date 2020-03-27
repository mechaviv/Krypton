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

import game.user.stat.Flag;

/**
 *
 * @author Eric
 */
public class MobStats {
    public static final int
            PAD                 = 0,
            PDD                 = 1,
            MAD                 = 2,
            MDD                 = 3,
            ACC                 = 4,
            EVA                 = 5,
            Speed               = 6,
            Stun                = 7,
            Freeze              = 8,
            Poison              = 9,
            Seal                = 10,
            Darkness            = 11,
            PowerUp             = 12,
            MagicUp             = 13,
            PGuardUp            = 14,
            MGuardUp            = 15,
            Doom                = 16,
            Web                 = 17,
            PImmune             = 18,
            MImmune             = 19,
            Showdown            = 20,
            HardSkin            = 21,
            Ambush              = 22,
            DamagedElemAttr     = 23,
            Venom               = 24,
            Blind               = 25,
            SealSkill           = 26,
            Burned              = 27,
            Dazzle              = 28,
            PCounter            = 29,
            MCounter            = 30,
            Disable             = 31,
            RiseByToss          = 32,
            BodyPressure        = 33,
            Weakness            = 34,
            TimeBomb            = 35,
            MagicCrash          = 36,
            HealByDamage        = 37,
            NONE = 0xFFFFFFFF;
    ;

    public static Flag getMask(int bits) {
        Flag flag = new Flag(Flag.INT_128);
        if (bits == NONE) {
            flag.setEncodeAll();
        } else {
            flag.setValue(1);
            flag.shiftLeft(bits);
        }
        return new Flag(flag, Flag.INT_128);
    }

    private static Flag MOVEMENT_AFFECTING_STAT = null;
    public static boolean isMovementAffectingStat(Flag flag) {
        if (MOVEMENT_AFFECTING_STAT == null) {
            MOVEMENT_AFFECTING_STAT = new Flag(Flag.INT_128);
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Speed));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Stun));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Freeze));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Doom));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(RiseByToss));
        }
        return !flag.operatorAND(MOVEMENT_AFFECTING_STAT).isZero();
    }
}
