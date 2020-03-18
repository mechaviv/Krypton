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

import common.JobAccessor;
import common.JobCategory;
import common.item.BodyPart;
import common.item.ItemSlotBase;
import common.item.ItemSlotEquip;
import common.user.CharacterData;
import game.user.skill.DiceFlags;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillInfo;
import game.user.skill.Skills.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import game.user.skill.entries.SkillEntry;
import network.packet.OutPacket;
import util.Pointer;
import static game.user.stat.CharacterTemporaryStat.*;

/**
 *
 * @author Eric
 */
public class SecondaryStat {
    /* The constant Stat Option held as an empty holder for non-buffed stats */
    static final SecondaryStatOption EMPTY_OPTION;
    /* Constant defined sets for comparing the symmetrical difference of two sets */
    public static final int
            MovementAffecting,
            FilterForRemote
                    ;

    private final Map<Integer, SecondaryStatOption> stats;
    private final int[] diceInfo;
    public int pad;
    public int pdd;
    public int mad;
    public int mdd;
    public int acc;
    public int eva;
    public int craft;
    public int speed;
    public int jump;
    public int blessingArmorIncPAD;

    public SecondaryStat() {
        this.stats = new LinkedHashMap<>();
        this.diceInfo = new int[DiceFlags.NO];
    }

    public void clear() {
        this.pad = 0;
        this.pdd = 0;
        this.mad = 0;
        this.mdd = 0;
        this.acc = 0;
        this.eva = 0;
        this.craft = 0;
        this.speed = 0;
        this.jump = 0;
        this.stats.clear();
    }

    public void encodeForLocal(OutPacket packet, Flag flag) {
        Flag toSend = new Flag(Flag.INT_128);
        for (int cts : stats.keySet()) {
            if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet() && getStatOption(cts) != 0) {
                toSend.performOR(CharacterTemporaryStat.getMask(cts));
            }
        }
        long cur = System.currentTimeMillis();
        packet.encodeBuffer(toSend.toByteArray());
        encodeForLocalStat(packet, cur, toSend, PAD);
        encodeForLocalStat(packet, cur, toSend, PDD);
        encodeForLocalStat(packet, cur, toSend, MAD);
        encodeForLocalStat(packet, cur, toSend, MDD);
        encodeForLocalStat(packet, cur, toSend, ACC);
        encodeForLocalStat(packet, cur, toSend, EVA);
        encodeForLocalStat(packet, cur, toSend, Craft);
        encodeForLocalStat(packet, cur, toSend, Speed);
        encodeForLocalStat(packet, cur, toSend, Jump);
        encodeForLocalStat(packet, cur, toSend, EMHP);
        encodeForLocalStat(packet, cur, toSend, EMMP);
        encodeForLocalStat(packet, cur, toSend, EPAD);
        encodeForLocalStat(packet, cur, toSend, EPDD);
        encodeForLocalStat(packet, cur, toSend, EMDD);
        encodeForLocalStat(packet, cur, toSend, MagicGuard);
        encodeForLocalStat(packet, cur, toSend, DarkSight);
        encodeForLocalStat(packet, cur, toSend, Booster);
        encodeForLocalStat(packet, cur, toSend, PowerGuard);
        encodeForLocalStat(packet, cur, toSend, Guard);
        encodeForLocalStat(packet, cur, toSend, SafetyDamage);
        encodeForLocalStat(packet, cur, toSend, SafetyAbsorb);
        encodeForLocalStat(packet, cur, toSend, MaxHP);
        encodeForLocalStat(packet, cur, toSend, MaxMP);
        encodeForLocalStat(packet, cur, toSend, Invincible);
        encodeForLocalStat(packet, cur, toSend, SoulArrow);
        encodeForLocalStat(packet, cur, toSend, Stun);
        encodeForLocalStat(packet, cur, toSend, Poison);
        encodeForLocalStat(packet, cur, toSend, Seal);
        encodeForLocalStat(packet, cur, toSend, Darkness);
        encodeForLocalStat(packet, cur, toSend, ComboCounter);
        encodeForLocalStat(packet, cur, toSend, WeaponCharge);
        encodeForLocalStat(packet, cur, toSend, DragonBlood);
        encodeForLocalStat(packet, cur, toSend, HolySymbol);
        encodeForLocalStat(packet, cur, toSend, MesoUp);
        encodeForLocalStat(packet, cur, toSend, ShadowPartner);
        encodeForLocalStat(packet, cur, toSend, PickPocket);
        encodeForLocalStat(packet, cur, toSend, MesoGuard);
        encodeForLocalStat(packet, cur, toSend, Thaw);
        encodeForLocalStat(packet, cur, toSend, Weakness);
        encodeForLocalStat(packet, cur, toSend, Curse);
        encodeForLocalStat(packet, cur, toSend, Slow);
        encodeForLocalStat(packet, cur, toSend, Morph);
        encodeForLocalStat(packet, cur, toSend, Ghost);
        encodeForLocalStat(packet, cur, toSend, Regen);
        encodeForLocalStat(packet, cur, toSend, BasicStatUp);
        encodeForLocalStat(packet, cur, toSend, Stance);
        encodeForLocalStat(packet, cur, toSend, SharpEyes);
        encodeForLocalStat(packet, cur, toSend, ManaReflection);
        encodeForLocalStat(packet, cur, toSend, Attract);
        encodeForLocalStat(packet, cur, toSend, SpiritJavelin);
        encodeForLocalStat(packet, cur, toSend, Infinity);
        encodeForLocalStat(packet, cur, toSend, Holyshield);
        encodeForLocalStat(packet, cur, toSend, HamString);
        encodeForLocalStat(packet, cur, toSend, Blind);
        encodeForLocalStat(packet, cur, toSend, Concentration);
        encodeForLocalStat(packet, cur, toSend, BanMap);
        encodeForLocalStat(packet, cur, toSend, MaxLevelBuff);
        encodeForLocalStat(packet, cur, toSend, Barrier);
        encodeForLocalStat(packet, cur, toSend, DojangShield);
        encodeForLocalStat(packet, cur, toSend, ReverseInput);
        encodeForLocalStat(packet, cur, toSend, MesoUpByItem);
        encodeForLocalStat(packet, cur, toSend, ItemUpByItem);
        encodeForLocalStat(packet, cur, toSend, RespectPImmune);
        encodeForLocalStat(packet, cur, toSend, RespectMImmune);
        encodeForLocalStat(packet, cur, toSend, DefenseAtt);
        encodeForLocalStat(packet, cur, toSend, DefenseState);
        encodeForLocalStat(packet, cur, toSend, DojangBerserk);
        encodeForLocalStat(packet, cur, toSend, DojangInvincible);
        encodeForLocalStat(packet, cur, toSend, Spark);
        encodeForLocalStat(packet, cur, toSend, SoulMasterFinal);
        encodeForLocalStat(packet, cur, toSend, WindBreakerFinal);
        encodeForLocalStat(packet, cur, toSend, ElementalReset);
        encodeForLocalStat(packet, cur, toSend, WindWalk);
        encodeForLocalStat(packet, cur, toSend, EventRate);
        encodeForLocalStat(packet, cur, toSend, ComboAbilityBuff);
        encodeForLocalStat(packet, cur, toSend, ComboDrain);
        encodeForLocalStat(packet, cur, toSend, ComboBarrier);
        encodeForLocalStat(packet, cur, toSend, BodyPressure);
        encodeForLocalStat(packet, cur, toSend, SmartKnockback);
        encodeForLocalStat(packet, cur, toSend, RepeatEffect);
        encodeForLocalStat(packet, cur, toSend, ExpBuffRate);
        encodeForLocalStat(packet, cur, toSend, IncEffectHPPotion);
        encodeForLocalStat(packet, cur, toSend, IncEffectMPPotion);
        encodeForLocalStat(packet, cur, toSend, StopPortion);
        encodeForLocalStat(packet, cur, toSend, StopMotion);
        encodeForLocalStat(packet, cur, toSend, Fear);
        encodeForLocalStat(packet, cur, toSend, EvanSlow);
        encodeForLocalStat(packet, cur, toSend, MagicShield);
        encodeForLocalStat(packet, cur, toSend, MagicResistance);
        encodeForLocalStat(packet, cur, toSend, SoulStone);
        encodeForLocalStat(packet, cur, toSend, Flying);
        encodeForLocalStat(packet, cur, toSend, Frozen);
        encodeForLocalStat(packet, cur, toSend, AssistCharge);
        encodeForLocalStat(packet, cur, toSend, Enrage);
        encodeForLocalStat(packet, cur, toSend, SuddenDeath);
        encodeForLocalStat(packet, cur, toSend, NotDamaged);
        encodeForLocalStat(packet, cur, toSend, FinalCut);
        encodeForLocalStat(packet, cur, toSend, ThornsEffect);
        encodeForLocalStat(packet, cur, toSend, SwallowAttackDamage);
        encodeForLocalStat(packet, cur, toSend, MorewildDamageUp);
        encodeForLocalStat(packet, cur, toSend, Mine);
        encodeForLocalStat(packet, cur, toSend, Cyclone);
        encodeForLocalStat(packet, cur, toSend, SwallowCritical);
        encodeForLocalStat(packet, cur, toSend, SwallowMaxMP);
        encodeForLocalStat(packet, cur, toSend, SwallowDefence);
        encodeForLocalStat(packet, cur, toSend, SwallowEvasion);
        encodeForLocalStat(packet, cur, toSend, Conversion);
        encodeForLocalStat(packet, cur, toSend, Revive);
        encodeForLocalStat(packet, cur, toSend, Sneak);
        encodeForLocalStat(packet, cur, toSend, Mechanic);
        encodeForLocalStat(packet, cur, toSend, Aura);
        encodeForLocalStat(packet, cur, toSend, DarkAura);
        encodeForLocalStat(packet, cur, toSend, BlueAura);
        encodeForLocalStat(packet, cur, toSend, YellowAura);
        encodeForLocalStat(packet, cur, toSend, SuperBody);
        encodeForLocalStat(packet, cur, toSend, MorewildMaxHP);
        encodeForLocalStat(packet, cur, toSend, Dice);
        encodeForLocalStat(packet, cur, toSend, BlessingArmor);
        encodeForLocalStat(packet, cur, toSend, DamR);
        encodeForLocalStat(packet, cur, toSend, TeleportMasteryOn);
        encodeForLocalStat(packet, cur, toSend, CombatOrders);
        encodeForLocalStat(packet, cur, toSend, Beholder);
        encodeForLocalStat(packet, cur, toSend, SummonBomb);

        packet.encodeByte(0);// nDefenseAtt (total ?)
        packet.encodeByte(0);// nDefenseState (total ?)

        if (toSend.operatorAND(CharacterTemporaryStat.getSwallowBuff()).isSet()) {
            packet.encodeByte(getStat(SwallowEvasion).getModOption());// tSwallowBuffTime
        }
        if (toSend.operatorAND(CharacterTemporaryStat.getMask(Dice)).isSet()) {
            for (int i = 0; i < DiceFlags.NO; i++) {
                packet.encodeInt(diceInfo[i]);
            }
        }
        if (toSend.operatorAND(CharacterTemporaryStat.getMask(BlessingArmor)).isSet()) {
            packet.encodeInt(blessingArmorIncPAD);
        }
        // ts stats
    }

    private void encodeForLocalStat(OutPacket packet, long cur, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            SecondaryStatOption opt = getStat(cts);
            packet.encodeShort(opt.getOption());
            packet.encodeInt(opt.getReason());
            packet.encodeInt((int) (opt.getDuration() - System.currentTimeMillis()));
        }
    }

    public void encodeForRemote(OutPacket packet, Flag flag) {
        Flag toSend = new Flag(Flag.INT_128);
        addStatToFlag(flag, toSend, Speed);
        addStatToFlag(flag, toSend, ComboCounter);
        addStatToFlag(flag, toSend, WeaponCharge);
        addStatToFlag(flag, toSend, Stun);
        addStatToFlag(flag, toSend, Darkness);
        addStatToFlag(flag, toSend, Seal);
        addStatToFlag(flag, toSend, Weakness);
        addStatToFlag(flag, toSend, Curse);
        addStatToFlag(flag, toSend, Poison);
        addStatToFlag(flag, toSend, ShadowPartner);
        addStatToFlag(flag, toSend, DarkSight);
        addStatToFlag(flag, toSend, SoulArrow);
        addStatToFlag(flag, toSend, Morph);
        addStatToFlag(flag, toSend, Ghost);
        addStatToFlag(flag, toSend, Attract);
        addStatToFlag(flag, toSend, SpiritJavelin);
        addStatToFlag(flag, toSend, BanMap);
        addStatToFlag(flag, toSend, Barrier);
        addStatToFlag(flag, toSend, DojangShield);
        addStatToFlag(flag, toSend, ReverseInput);
        addStatToFlag(flag, toSend, RespectPImmune);
        addStatToFlag(flag, toSend, RespectMImmune);
        addStatToFlag(flag, toSend, DefenseAtt);
        addStatToFlag(flag, toSend, DefenseState);
        addStatToFlag(flag, toSend, DojangBerserk);
        addStatToFlag(flag, toSend, DojangInvincible);
        addStatToFlag(flag, toSend, WindWalk);
        addStatToFlag(flag, toSend, RepeatEffect);
        addStatToFlag(flag, toSend, StopPortion);
        addStatToFlag(flag, toSend, StopMotion);
        addStatToFlag(flag, toSend, Fear);
        addStatToFlag(flag, toSend, MagicShield);
        addStatToFlag(flag, toSend, Flying);
        addStatToFlag(flag, toSend, Frozen);
        addStatToFlag(flag, toSend, SuddenDeath);
        addStatToFlag(flag, toSend, FinalCut);
        addStatToFlag(flag, toSend, Cyclone);
        addStatToFlag(flag, toSend, Sneak);
        addStatToFlag(flag, toSend, MorewildDamageUp);
        addStatToFlag(flag, toSend, Mechanic);
        addStatToFlag(flag, toSend, DarkAura);
        addStatToFlag(flag, toSend, BlueAura);
        addStatToFlag(flag, toSend, YellowAura);
        addStatToFlag(flag, toSend, BlessingArmor);

        packet.encodeBuffer(toSend.toByteArray());

        encodeRemoteByteOption(packet, toSend, Speed);
        encodeRemoteByteOption(packet, toSend, ComboCounter);
        encodeRemoteReason(packet, toSend, WeaponCharge);

        encodeRemoteReason(packet, toSend, Stun);
        encodeRemoteReason(packet, toSend, Darkness);
        encodeRemoteReason(packet, toSend, Seal);
        encodeRemoteReason(packet, toSend, Weakness);
        encodeRemoteReason(packet, toSend, Curse);
        encodeRemoteShortOption(packet, toSend, Poison);
        encodeRemoteReason(packet, toSend, Poison);
        encodeRemoteReason(packet, toSend, ShadowPartner);
        encodeRemoteShortOption(packet, toSend, Morph);
        encodeRemoteShortOption(packet, toSend, Ghost);
        encodeRemoteReason(packet, toSend, Attract);
        encodeRemoteIntOption(packet, toSend, SpiritJavelin);
        encodeRemoteReason(packet, toSend, BanMap);
        encodeRemoteReason(packet, toSend, Barrier);
        encodeRemoteReason(packet, toSend, DojangShield);
        encodeRemoteReason(packet, toSend, ReverseInput);
        encodeRemoteIntOption(packet, toSend, RespectPImmune);
        encodeRemoteIntOption(packet, toSend, RespectMImmune);
        encodeRemoteIntOption(packet, toSend, DefenseAtt);
        encodeRemoteIntOption(packet, toSend, DefenseState);
        encodeRemoteReason(packet, toSend, RepeatEffect);
        encodeRemoteReason(packet, toSend, StopPortion);
        encodeRemoteReason(packet, toSend, StopMotion);
        encodeRemoteReason(packet, toSend, Fear);
        encodeRemoteIntOption(packet, toSend, MagicShield);
        encodeRemoteReason(packet, toSend, Frozen);
        encodeRemoteReason(packet, toSend, SuddenDeath);
        encodeRemoteReason(packet, toSend, FinalCut);
        encodeRemoteByteOption(packet, toSend, Cyclone);
        encodeRemoteReason(packet, toSend, Mechanic);
        encodeRemoteReason(packet, toSend, DarkAura);
        encodeRemoteReason(packet, toSend, BlueAura);
        encodeRemoteReason(packet, toSend, YellowAura);
        packet.encodeByte(0);// nDefenseAtt (total ?)
        packet.encodeByte(0);// nDefenseState (total ?)
        // ts stats
    }

    private void addStatToFlag(Flag flag, Flag toSend, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet() && getStatOption(cts) != 0) {
            toSend.performOR(CharacterTemporaryStat.getMask(cts));
        }
    }

    private void encodeRemoteByteOption(OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeByte(getStatOption(cts));
        }
    }

    private void encodeRemoteShortOption(OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeShort(getStatOption(cts));
        }
    }

    private void encodeRemoteIntOption(OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeInt(getStatOption(cts));
        }
    }

    private void encodeRemoteReason(OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeInt(getStatReason(cts));
        }
    }

    public static boolean filterForRemote(int flag) {
        return (flag & FilterForRemote) != 0;
    }

    public SecondaryStatOption getStat(int cts) {
        if (stats.containsKey(cts)) {
            return stats.get(cts);
        }
        return EMPTY_OPTION;
    }

    public long getStatDuration(int cts) {
        return getStat(cts).getDuration();
    }

    public short getStatOption(int cts) {
        return getStat(cts).getOption();
    }

    public int getStatReason(int cts) {
        return getStat(cts).getReason();
    }

    public static boolean isMovementAffectingStat(int flag) {
        return (flag & MovementAffecting) != 0;
    }

    public boolean isSetted(int reason) {
        for (SecondaryStatOption opt : stats.values()) {
            if (opt.getReason() == reason) {
                return true;
            }
        }
        return false;
    }

    public Flag reset() {
        Flag reset = new Flag(Flag.INT_128);
        for (int cts : stats.keySet()) {
            reset.performOR(CharacterTemporaryStat.getMask(cts));
        }
        stats.clear();
        return reset;
    }

    public Flag resetByCTS(int cts) {
        Flag reset = new Flag(Flag.INT_128);
        if (stats.containsKey(cts)) {
            reset.performOR(CharacterTemporaryStat.getMask(cts));
            stats.remove(cts);
        }
        return reset;
    }

    public Flag resetByReasonID(int reasonID) {
        Flag reset = new Flag(Flag.INT_128);
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getReason() == reasonID) {
                reset.performOR(CharacterTemporaryStat.getMask(stat.getKey()));
                it.remove();
            }
        }
        return reset;
    }

    public Flag resetByTime(long time) {
        Flag reset = new Flag(Flag.INT_128);
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getOption() != 0 && time - stat.getValue().getDuration() > 0) {
                reset.performOR(CharacterTemporaryStat.getMask(stat.getKey()));
                it.remove();
            }
        }
        return reset;
    }

    public void setFrom(BasicStat bs, List<ItemSlotBase> realEquip, CharacterData cd) {
        short job = bs.getJob();
        int jc = JobAccessor.getJobCategory(job);
        int slv;
        this.pad = 0;
        this.pdd = 0;
        this.mad = bs.getINT();
        this.mdd = bs.getINT();
        this.eva = (bs.getDEX() / 4 + bs.getLUK() / 2);
        if (jc == JobCategory.ARCHER || jc == JobCategory.THIEF) {
            this.acc = (short) (bs.getDEX() * 0.6 + bs.getLUK() * 0.3);
        } else {
            this.acc = (short) (bs.getDEX() * 0.8 + bs.getLUK() * 0.5);
        }
        this.craft = bs.getLUK() + bs.getDEX() + bs.getINT();
        this.speed = 100;
        this.jump = 100;

        final int BodyPartCount = BodyPart.BP_Count;
        for (int pos = 1; pos <= BodyPartCount; pos++) {
            ItemSlotEquip item = (ItemSlotEquip) realEquip.get(pos);
            if (item != null) {
                this.pad += item.iPAD;
                this.pdd += item.iPDD;
                this.mad += item.iMAD;
                this.mdd += item.iMDD;
                this.acc += item.iACC;
                this.eva += item.iEVA;
                this.craft += item.iCraft;
                this.speed += item.iSpeed;
                this.jump += item.iJump;
            }
        }

        SkillEntry archerAmazonBlessing = SkillInfo.getInstance().getSkill(Archer.AmazonBlessing);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Archer.AmazonBlessing, new Pointer<>(archerAmazonBlessing));
        if (slv != 0) {
            this.acc += archerAmazonBlessing.getLevelData(slv).X;
        }
        SkillEntry thiefNimbleBody = SkillInfo.getInstance().getSkill(Rogue.NimbleBody);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Rogue.NimbleBody, new Pointer<>(thiefNimbleBody));
        if (slv != 0) {
            this.acc += thiefNimbleBody.getLevelData(slv).X;
            this.eva += thiefNimbleBody.getLevelData(slv).Y;
        }

        int attackType = 1;//MELEE
        if (jc == JobCategory.ARCHER || job / 10 == 41)
            attackType = 2;//SHOOT
        int weaponItemID = 0;
        ItemSlotBase item = realEquip.get(BodyPart.Weapon);
        if (item != null)
            weaponItemID = item.getItemID();

        Pointer<Integer> accInc = new Pointer<>(0);
        if (SkillAccessor.getWeaponMastery(cd, weaponItemID, attackType, accInc) != 0) {
            this.acc += accInc.get();
        }

        this.pad = Math.max(Math.min(this.pad, SkillAccessor.PAD_MAX), 0);
        this.pdd = Math.max(Math.min(this.pdd, SkillAccessor.PDD_MAX), 0);
        this.mad = Math.max(Math.min(this.mad, SkillAccessor.MAD_MAX), 0);
        this.mdd = Math.max(Math.min(this.mdd, SkillAccessor.MDD_MAX), 0);
        this.acc = Math.max(Math.min(this.acc, SkillAccessor.ACC_MAX), 0);
        this.eva = Math.max(Math.min(this.eva, SkillAccessor.EVA_MAX), 0);
        this.speed = Math.max(Math.min(this.speed, SkillAccessor.SPEED_MAX), 100);
        this.jump = Math.max(Math.min(this.jump, SkillAccessor.JUMP_MAX), 100);
    }

    public Flag setStat(int cts, SecondaryStatOption opt) {
        stats.put(cts, opt);
        return CharacterTemporaryStat.getMask(cts);
    }

    public void setStatOption(int cts, int option) {
        if (stats.containsKey(cts)) {
            SecondaryStatOption opt = stats.get(cts);
            opt.setOption(option);
        }
    }

    static {
        EMPTY_OPTION = new SecondaryStatOption();

        MovementAffecting = Speed | CharacterTemporaryStat.Jump;
        FilterForRemote = Speed | CharacterTemporaryStat.DarkSight | CharacterTemporaryStat.SoulArrow;
    }
}
