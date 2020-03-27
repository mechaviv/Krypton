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
package game.user.stat;

import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class CharacterTemporaryStat {
    public static final int
            PAD             = 0,
            PDD             = 1,
            MAD             = 2,
            MDD             = 3,
            ACC             = 4,
            EVA             = 5,
            Craft           = 6,
            Speed           = 7,
            Jump            = 8,
            MagicGuard      = 9,
            DarkSight       = 10,
            Booster         = 11,
            PowerGuard      = 12,
            MaxHP           = 13,
            MaxMP           = 14,
            Invincible      = 15,
            SoulArrow       = 16,
            Stun            = 17,
            Poison          = 18,
            Seal            = 19,
            Darkness        = 20,
            ComboCounter    = 21,
            WeaponCharge    = 22,
            DragonBlood     = 23,
            HolySymbol      = 24,
            MesoUp          = 25,
            ShadowPartner   = 26,
            PickPocket        = 27,
            MesoGuard        = 28,
            Thaw        = 29,
            Weakness        = 30,
            Curse        = 31,
            Slow        = 32,
            Morph        = 33,
            Regen        = 34,
            BasicStatUp        = 35,
            Stance        = 36,
            SharpEyes        = 37,
            ManaReflection        = 38,
            Attract        = 39,
            SpiritJavelin        = 40,
            Infinity        = 41,
            Holyshield        = 42,
            HamString        = 43,
            Blind        = 44,
            Concentration        = 45,
            BanMap        = 46,
            MaxLevelBuff        = 47,
            MesoUpByItem        = 48,
            Ghost        = 49,
            Barrier        = 50,
            ReverseInput        = 51,
            ItemUpByItem        = 52,
            RespectPImmune        = 53,
            RespectMImmune        = 54,
            DefenseAtt        = 55,
            DefenseState        = 56,
            IncEffectHPPotion        = 57,
            IncEffectMPPotion        = 58,
            DojangBerserk        = 59,
            DojangInvincible        = 60,
            Spark        = 61,
            DojangShield        = 62,
            SoulMasterFinal        = 63,
            WindBreakerFinal        = 64,
            ElementalReset        = 65,
            WindWalk        = 66,
            EventRate        = 67,
            ComboAbilityBuff        = 68,
            ComboDrain        = 69,
            ComboBarrier        = 70,
            BodyPressure        = 71,
            SmartKnockback        = 72,
            RepeatEffect        = 73,
            ExpBuffRate        = 74,
            StopPortion        = 75,
            StopMotion        = 76,
            Fear        = 77,
            EvanSlow        = 78,
            MagicShield        = 79,
            MagicResistance        = 80,
            SoulStone        = 81,
            Flying        = 82,
            Frozen        = 83,
            AssistCharge        = 84,
            Enrage        = 85,
            SuddenDeath        = 86,
            NotDamaged        = 87,
            FinalCut        = 88,
            ThornsEffect        = 89,
            SwallowAttackDamage        = 90,
            MorewildDamageUp        = 91,
            Mine        = 92,
            EMHP        = 93,
            EMMP        = 94,
            EPAD        = 95,
            EPDD        = 96,
            EMDD        = 97,
            Guard        = 98,
            SafetyDamage        = 99,
            SafetyAbsorb        = 100,
            Cyclone        = 101,
            SwallowCritical        = 102,
            SwallowMaxMP        = 103,
            SwallowDefence        = 104,
            SwallowEvasion        = 105,
            Conversion        = 106,
            Revive        = 107,
            Sneak        = 108,
            Mechanic        = 109,
            Aura        = 110,
            DarkAura        = 111,
            BlueAura        = 112,
            YellowAura        = 113,
            SuperBody        = 114,
            MorewildMaxHP        = 115,
            Dice        = 116,
            BlessingArmor        = 117,
            DamR        = 118,
            TeleportMasteryOn        = 119,
            CombatOrders        = 120,
            Beholder        = 121,
            EnergyCharged        = 122,
            DashSpeed        = 123,
            DashJump        = 124,
            RideVehicle        = 125,
            PartyBooster        = 126,
            GuidedBullet       = 127,
            Undead       = 128,
            SummonBomb  = 129,
            COUNT_PLUS1 = 130,
            NONE        = 0xFFFFFFFF//0xFFFFFFFF
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

    private static Flag SWALLOW_BUFF = null;
    public static Flag getSwallowBuff() {
        if (SWALLOW_BUFF == null) {
            SWALLOW_BUFF = new Flag(Flag.INT_128);
            SWALLOW_BUFF.performOR(getMask(SwallowAttackDamage));
            SWALLOW_BUFF.performOR(getMask(SwallowDefence));
            SWALLOW_BUFF.performOR(getMask(SwallowCritical));
            SWALLOW_BUFF.performOR(getMask(SwallowMaxMP));
            SWALLOW_BUFF.performOR(getMask(SwallowEvasion));
        }
        return new Flag(SWALLOW_BUFF, Flag.INT_128);
    }

    private static Flag MOVEMENT_AFFECTING_STAT = null;
    public static boolean isMovementAffectingStat(Flag flag) {
        if (MOVEMENT_AFFECTING_STAT == null) {
            MOVEMENT_AFFECTING_STAT = new Flag(Flag.INT_128);
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Speed));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Jump));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Stun));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Weakness));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Slow));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Morph));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Ghost));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(BasicStatUp));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Attract));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(RideVehicle));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(DashSpeed));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(DashJump));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Flying));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(Frozen));
            MOVEMENT_AFFECTING_STAT.performOR(getMask(YellowAura));
        }
        return !flag.operatorAND(MOVEMENT_AFFECTING_STAT).isZero();
    }

    private static Flag CALC_DAMAGE_STAT = null;
    public static boolean isCalcDamageStat(Flag flag) {
        if (CALC_DAMAGE_STAT == null) {
            CALC_DAMAGE_STAT = new Flag(Flag.INT_128);
            CALC_DAMAGE_STAT.performOR(getMask(PAD));
            CALC_DAMAGE_STAT.performOR(getMask(MAD));
            CALC_DAMAGE_STAT.performOR(getMask(ACC));
            CALC_DAMAGE_STAT.performOR(getMask(EVA));
            CALC_DAMAGE_STAT.performOR(getMask(Darkness));
            CALC_DAMAGE_STAT.performOR(getMask(ComboCounter));
            CALC_DAMAGE_STAT.performOR(getMask(WeaponCharge));
            CALC_DAMAGE_STAT.performOR(getMask(BasicStatUp));
            CALC_DAMAGE_STAT.performOR(getMask(SharpEyes));
            CALC_DAMAGE_STAT.performOR(getMask(MaxLevelBuff));
            CALC_DAMAGE_STAT.performOR(getMask(EnergyCharged));
            CALC_DAMAGE_STAT.performOR(getMask(ComboAbilityBuff));
            CALC_DAMAGE_STAT.performOR(getMask(AssistCharge));
            CALC_DAMAGE_STAT.performOR(getMask(SuddenDeath));
            CALC_DAMAGE_STAT.performOR(getMask(FinalCut));
            CALC_DAMAGE_STAT.performOR(getMask(ThornsEffect));
            CALC_DAMAGE_STAT.performOR(getMask(EPAD));
            CALC_DAMAGE_STAT.performOR(getMask(DarkAura));
            CALC_DAMAGE_STAT.performOR(getMask(DamR));
            CALC_DAMAGE_STAT.performOR(getMask(BlessingArmor));
        }
        return !flag.operatorAND(CALC_DAMAGE_STAT).isZero();
    }
}
