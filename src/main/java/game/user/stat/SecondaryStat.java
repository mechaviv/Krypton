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
import game.user.stat.ts.*;
import network.packet.OutPacket;
import util.Pointer;

import static game.user.stat.CharacterTemporaryStat.*;

/**
 * @author Eric
 */
public class SecondaryStat {
    /* Constant defined sets for comparing the symmetrical difference of two sets */
    public static final int MovementAffecting;
    /* The constant Stat Option held as an empty holder for non-buffed stats */
    static final SecondaryStatOption EMPTY_OPTION;

    static {
        EMPTY_OPTION = new SecondaryStatOption();

        MovementAffecting = Speed | CharacterTemporaryStat.Jump;
    }

    public final TemporaryStatBase[] temporaryStats;
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

        this.temporaryStats = new TemporaryStatBase[TSIndex.NO];
        for (int index = 0; index < TSIndex.NO; index++) {
            switch (index) {
                case TSIndex.ENERGY_CHARGED:
                    temporaryStats[index] = new EnergyChargeStat();
                    break;
                case TSIndex.DASH_SPEED:
                case TSIndex.DASH_JUMP:
                case TSIndex.UNDEAD:
                    temporaryStats[index] = new DashTemporaryStat();
                    break;
                case TSIndex.RIDE_VEHICLE:
                    temporaryStats[index] = new TwoStateTemporaryStat();
                    break;
                case TSIndex.PARTY_BOOSTER:
                    temporaryStats[index] = new PartyBoosterStat();
                    break;
                case TSIndex.GUIDED_BULLET:
                    temporaryStats[index] = new GuidedBulletStat();
                    break;
            }
        }
        clear();
    }

    public static boolean isMovementAffectingStat(int flag) {
        return (flag & MovementAffecting) != 0;
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

        for (int index = 0; index < TSIndex.NO; index++) {
            temporaryStats[index].reset();
        }
    }

    public void encodeForLocal(OutPacket packet, Flag flag) {
        Flag toSend = new Flag(Flag.INT_128);
        for (int cts : stats.keySet()) {
            if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet() && getStatOption(cts) != 0) {
                toSend.performOR(CharacterTemporaryStat.getMask(cts));
            }
        }
        for (int index = 0; index < TSIndex.NO; index++) {
            if (flag.operatorAND(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index))).isSet()) {
                TemporaryStatBase ts = temporaryStats[index];
                if (ts instanceof TwoStateTemporaryStat && ((TwoStateTemporaryStat) ts).isActivated(System.currentTimeMillis())) {
                    toSend.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                } else if (ts.getValue() != 0) {
                    toSend.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                }
            }
        }
        long cur = System.currentTimeMillis();
        packet.encodeBuffer(toSend.toByteArray());
        SecondaryHelper.encodeForLocal(this, packet, cur, toSend);

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

        for (int index = 0; index < TSIndex.NO; index++) {
            if (toSend.operatorAND(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index))).isSet()) {
                temporaryStats[index].encodeForClient(packet);
            }
        }
    }

    public void encodeForRemote(OutPacket packet, Flag flag) {
        Flag toSend = new Flag(Flag.INT_128);
        SecondaryHelper.addStatToFlag(this, flag, toSend);

        packet.encodeBuffer(toSend.toByteArray());

        SecondaryHelper.encodeForRemote(this, packet, toSend);

        packet.encodeByte(0);// nDefenseAtt (total ?)
        packet.encodeByte(0);// nDefenseState (total ?)

        for (int index = 0; index < TSIndex.NO; index++) {
            if (toSend.operatorAND(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index))).isSet()) {
                temporaryStats[index].encodeForClient(packet);
            }
        }
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

        for (int index = 0; index < TSIndex.NO; index++) {
            temporaryStats[index].reset();
        }
        return reset;
    }

    public Flag resetByCTS(int cts) {
        Flag reset = new Flag(Flag.INT_128);

        if (stats.containsKey(cts)) {
            reset.performOR(CharacterTemporaryStat.getMask(cts));
            stats.remove(cts);
        }

        int index = TSIndex.getIndexByCTS(cts);
        TemporaryStatBase ts;
        if (index != -1 && (ts = temporaryStats[index]) != null) {
            if (ts instanceof TwoStateTemporaryStat && ((TwoStateTemporaryStat) ts).isActivated(System.currentTimeMillis())) {
                ts.reset();
                reset.performOR(CharacterTemporaryStat.getMask(cts));
            } else if (ts.getValue() != 0) {
                ts.reset();
                reset.performOR(CharacterTemporaryStat.getMask(cts));
            }
        }
        return reset;
    }

    public Flag resetByReasonID(int reasonID) {
        Flag reset = new Flag(Flag.INT_128);
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getReason() == reasonID) {
                reset.performOR(CharacterTemporaryStat.getMask(stat.getKey()));
                it.remove();
            }
        }

        for (int index = 0; index < TSIndex.NO; index++) {
            TemporaryStatBase ts = temporaryStats[index];
            if (ts.getResaon() == reasonID) {
                if (ts instanceof TwoStateTemporaryStat && ((TwoStateTemporaryStat) ts).isActivated(System.currentTimeMillis())) {
                    reset.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                    temporaryStats[index].reset();
                } else if (ts.getValue() != 0) {
                    reset.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                    temporaryStats[index].reset();
                }
            }
        }
        return reset;
    }

    public Flag resetByTime(long time) {
        Flag reset = new Flag(Flag.INT_128);
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getOption() != 0 && time - stat.getValue().getDuration() > 0) {
                reset.performOR(CharacterTemporaryStat.getMask(stat.getKey()));
                it.remove();
            }
        }

        for (int index = 0; index < TSIndex.NO; index++) {
            if (temporaryStats[index] instanceof TwoStateTemporaryStat) {
                TwoStateTemporaryStat ts = (TwoStateTemporaryStat) temporaryStats[index];
                if (ts != null && ts.getValue() != 0 && ts.isExpiredAt(time)) {
                    ts.reset();
                    reset.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                }
            } else {
                TemporaryStatBase ts = temporaryStats[index];
                if (ts != null && ts.getValue() != 0 && time - ts.getLastUpdated() > 0) {
                    ts.reset();
                    reset.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                }
            }
        }
        return reset;
    }

    public Flag resetByUserSkill() {
        Flag reset = new Flag(Flag.INT_128);
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getReason() / 1000000 > 0) {
                reset.performOR(CharacterTemporaryStat.getMask(stat.getKey()));
                it.remove();
            }
        }

        for (int index = 0; index < TSIndex.NO; index++) {
            TemporaryStatBase ts = temporaryStats[index];
            if (ts.getResaon() / 1000000 > 0) {
                if (ts instanceof TwoStateTemporaryStat && ((TwoStateTemporaryStat) ts).isActivated(System.currentTimeMillis())) {
                    reset.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                    temporaryStats[index].reset();
                } else if (ts.getValue() != 0) {
                    reset.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
                    temporaryStats[index].reset();
                }
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
                this.pad += item.item.iPAD;
                this.pdd += item.item.iPDD;
                this.mad += item.item.iMAD;
                this.mdd += item.item.iMDD;
                this.acc += item.item.iACC;
                this.eva += item.item.iEVA;
                this.craft += item.item.iCraft;
                this.speed += item.item.iSpeed;
                this.jump += item.item.iJump;
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

    public int[] getDiceInfo() {
        return diceInfo;
    }
}
