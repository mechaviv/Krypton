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
import common.user.WildHunterInfo;
import game.messenger.Character;
import game.user.item.EquipItem;
import game.user.item.ItemInfo;
import game.user.item.ItemOptionInfo;
import game.user.item.ItemOptionLevelData;
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
import util.Logger;
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
    public final int[] diceInfo;
    public int pad;
    public int pdd;
    public int mad;
    public int mdd;
    public int acc;
    public int eva;
    public int itemPADr;
    public int itemPDDr;
    public int itemMADr;
    public int itemMDDr;
    public int itemACCr;
    public int itemEVAr;
    public int craft;
    public int speed;
    public int jump;

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
        this.itemPADr = 0;
        this.itemPDDr = 0;
        this.itemMADr = 0;
        this.itemMDDr = 0;
        this.itemACCr = 0;
        this.itemEVAr = 0;
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
            packet.encodeInt(getStat(BlessingArmor).getModOption());
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
        int level = bs.getLevel();
        this.pad = 0;
        this.pdd = 0;
        this.mad = 0;
        this.mdd = 0;
        this.acc = 0;
        this.eva = 0;
        this.itemPADr = 0;
        this.itemPDDr = 0;
        this.itemMADr = 0;
        this.itemMDDr = 0;
        this.itemACCr = 0;
        this.itemEVAr = 0;
        this.craft = bs.getLUK() + bs.getDEX() + bs.getINT();
        this.speed = 100;
        this.jump = 100;

        // set items

        SecondaryStatRateOption optionRate = new SecondaryStatRateOption();

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

                EquipItem equip = ItemInfo.getEquipItem(item.getItemID());
                if (equip != null) {
                    int optLevel = (equip.getReqLevel() - 1) / 10;
                    if (item.option != null) {
                        applyItemOption(item.option.option1, optLevel);
                        applyItemOption(item.option.option2, optLevel);
                        applyItemOption(item.option.option3, optLevel);
                        applyItemOptionR(item.option.option1, optLevel, optionRate);
                        applyItemOptionR(item.option.option2, optLevel, optionRate);
                        applyItemOptionR(item.option.option3, optLevel, optionRate);
                    }
                }
            } else if (pos == BodyPart.Weapon && job % 1000 / 100 == 5) {
                this.pad += level > 30 ? 31.0 : level * 0.7 + 10.0;
            }
        }

        SkillEntry thiefNimbleBody = SkillInfo.getInstance().getSkill(Rogue.NimbleBody);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Rogue.NimbleBody, new Pointer<>(thiefNimbleBody));
        if (slv != 0) {
            this.acc += thiefNimbleBody.getLevelData(slv).X;
            this.eva += thiefNimbleBody.getLevelData(slv).Y;
        }

        SkillEntry nightWalkerNimbleBody = SkillInfo.getInstance().getSkill(NightWalker.NIMBLE_BODY);
        slv = SkillInfo.getInstance().getSkillLevel(cd, NightWalker.NIMBLE_BODY, new Pointer<>(nightWalkerNimbleBody));
        if (slv != 0) {
            this.acc += nightWalkerNimbleBody.getLevelData(slv).X;
            this.eva += nightWalkerNimbleBody.getLevelData(slv).Y;
        }
        SkillEntry pirateQuickMotion = SkillInfo.getInstance().getSkill(Pirate.QUICKMOTION);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Pirate.QUICKMOTION, new Pointer<>(pirateQuickMotion));
        if (slv != 0) {
            this.acc += pirateQuickMotion.getLevelData(slv).X;
            this.eva += pirateQuickMotion.getLevelData(slv).Y;
        }

        SkillEntry strikerQuickMotion = SkillInfo.getInstance().getSkill(Striker.QUICKMOTION);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Striker.QUICKMOTION, new Pointer<>(strikerQuickMotion));
        if (slv != 0) {
            this.acc += strikerQuickMotion.getLevelData(slv).X;
            this.eva += strikerQuickMotion.getLevelData(slv).Y;
        }
        SkillEntry evanDragonSoul = SkillInfo.getInstance().getSkill(Evan.DRAGON_SOUL);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Evan.DRAGON_SOUL, new Pointer<>(evanDragonSoul));
        if (slv != 0) {
            this.mad += evanDragonSoul.getLevelData(slv).MAD;
        }
        int blessOfNymphSkillID = JobAccessor.getNoviceSkillAsRace(Beginner.BLESS_OF_NYMPH, cd.getCharacterStat().getJob());
        SkillEntry blessOfNymph = SkillInfo.getInstance().getSkill(blessOfNymphSkillID);
        slv = SkillInfo.getInstance().getSkillLevel(cd, blessOfNymphSkillID, new Pointer<>(blessOfNymph));
        if (slv != 0) {
            this.pad += blessOfNymph.getLevelData(slv).X;
            this.mad += blessOfNymph.getLevelData(slv).Y;
            this.acc += blessOfNymph.getLevelData(slv).Z;
            this.eva += blessOfNymph.getLevelData(slv).Z;
        }
        if (isWildhunterJaguarVehicle()) {
            SkillEntry wildHunterJaguarRiding = SkillInfo.getInstance().getSkill(WildHunter.JAGUAR_RIDING);
            slv = SkillInfo.getInstance().getSkillLevel(cd, WildHunter.JAGUAR_RIDING, new Pointer<>(wildHunterJaguarRiding));
            if (slv != 0) {
                this.eva += wildHunterJaguarRiding.getLevelData(slv).Y;
            }
        }
        int attackType = 1;
        if (job % 1000 / 100 == 3 || job / 10 == 41 || job / 10 == 52 || job / 100 == 13 || job / 100 == 14) {
            attackType = 2;
        }

        int weaponItemID = 0;
        ItemSlotBase item = realEquip.get(BodyPart.Weapon);
        if (item != null)
            weaponItemID = item.getItemID();

        Pointer<Integer> accInc = new Pointer<>(0);
        Pointer<Integer> padInc = new Pointer<>(0);
        Pointer<Integer> madInc = new Pointer<>(0);
        if (SkillAccessor.getWeaponMastery(cd, this, weaponItemID, attackType, 0, accInc, padInc) != 0) {
            this.acc += accInc.get();
            this.pad += padInc.get();
            Logger.logReport("Adding [%d] PAD", padInc.get());
        }
        if (SkillAccessor.getMagicMastery(cd, madInc) != 0) {
            this.mad += madInc.get();
        }
        this.speed += SkillAccessor.getIncreaseSpeed(cd);
        int yellowAura = getStatOption(YellowAura);
        if (yellowAura != 0) {
            int reasonID = getStatReason(YellowAura);
            SkillEntry yellowAuraSkill = SkillInfo.getInstance().getSkill(reasonID);
            if (yellowAuraSkill != null) {
                this.speed += yellowAuraSkill.getLevelData(yellowAura).X;
                int superBody = getStatOption(SuperBody);
                if (superBody != 0) {
                    SkillEntry superBodySkill = SkillInfo.getInstance().getSkill(BMage.SUPER_BODY_YELLOW);
                    if (superBodySkill != null) {
                        this.speed += superBodySkill.getLevelData(superBody).X;
                    }
                }
            }
        }
        itemPADr += optionRate.PADr;
        itemPDDr += optionRate.PDDr;
        itemMADr += optionRate.MADr;
        itemMDDr += optionRate.MDDr;
        itemACCr += optionRate.ACCr;
        itemEVAr += optionRate.EVAr;

        this.pad = Math.max(Math.min(this.pad, SkillAccessor.PAD_MAX), 0);
        Logger.logReport("PAD = [%d]", pad);
        this.pdd = Math.max(Math.min(this.pdd, SkillAccessor.PDD_MAX), 0);
        this.mad = Math.max(Math.min(this.mad, SkillAccessor.MAD_MAX), 0);
        this.mdd = Math.max(Math.min(this.mdd, SkillAccessor.MDD_MAX), 0);
        this.acc = Math.max(Math.min(this.acc, SkillAccessor.ACC_MAX), 0);
        this.eva = Math.max(Math.min(this.eva, SkillAccessor.EVA_MAX), 0);
        this.speed = Math.max(Math.min(this.speed, SkillAccessor.SPEED_MAX), 100);
        this.jump = Math.max(Math.min(this.jump, SkillAccessor.JUMP_MAX), 100);
    }

    public void applyItemOption(int itemOptionID, int level) {
        ItemOptionInfo option = ItemOptionInfo.getItemOption(itemOptionID);
        if (option == null) {
            return;
        }
        ItemOptionLevelData optData = option.levelData.get(level);
        if (optData == null) {
            return;
        }
        this.acc += optData.incACC;
        this.eva += optData.incEVA;
        this.pad += optData.incPAD;
        this.mad += optData.incMAD;
        this.pdd += optData.incPDD;
        this.mdd += optData.incMDD;
        this.speed += optData.incSpeed;
        this.jump += optData.incJump;
    }

    public void applyItemOptionR(int itemOptionID, int level, SecondaryStatRateOption sOption) {
        ItemOptionInfo option = ItemOptionInfo.getItemOption(itemOptionID);
        if (option == null) {
            return;
        }
        ItemOptionLevelData optData = option.levelData.get(level);
        if (optData == null) {
            return;
        }
        sOption.ACCr += optData.incACCr;
        sOption.EVAr += optData.incEVAr;
        sOption.PADr += optData.incPADr;
        sOption.MADr += optData.incMADr;
        sOption.PDDr += optData.incPDDr;
        sOption.MDDr += optData.incMDDr;
    }

    public int getIncPAD(CharacterData cd) {
        int pad = getStatOption(PAD);
        int energyChargePAD = 0;
        EnergyChargeStat energyChargeStat = (EnergyChargeStat) temporaryStats[TSIndex.ENERGY_CHARGED];
        if (energyChargeStat.isActivated(System.currentTimeMillis())) {
            int job = cd.getCharacterStat().getJob();
            Pointer<SkillEntry> skill = new Pointer<>();
            int slv = SkillInfo.getInstance().getSkillLevel(cd, JobAccessor.isCygnusJob(job) ? Striker.ENERGY_CHARGE : Buccaneer.ENERGY_CHARGE, skill);
            if (skill.get() != null && slv > 0) {
                energyChargePAD = skill.get().getLevelData(slv).PAD;
            }
        }
        return Math.max(pad, energyChargePAD);
    }

    public int getIncEPAD(CharacterData cd) {
        int epad = getStatOption(EPAD);
        int energyChargeEPAD = 0;
        EnergyChargeStat energyChargeStat = (EnergyChargeStat) temporaryStats[TSIndex.ENERGY_CHARGED];
        if (energyChargeStat.isActivated(System.currentTimeMillis())) {
            int job = cd.getCharacterStat().getJob();
            Pointer<SkillEntry> skill = new Pointer<>();
            int slv = SkillInfo.getInstance().getSkillLevel(cd, JobAccessor.isCygnusJob(job) ? Striker.ENERGY_CHARGE : Buccaneer.ENERGY_CHARGE, skill);
            if (skill.get() != null && slv > 0) {
                energyChargeEPAD = skill.get().getLevelData(slv).EPAD;
            }
        }
        return Math.max(epad, energyChargeEPAD);
    }

    public int getPAD(CharacterData cd, int bulletItemID, int psdPADx, int psdPADr, int incPAD) {
        int job = cd.getCharacterStat().getJob();
        int energyChargePAD = getIncPAD(cd);
        int incEPAD = getIncEPAD(cd);

        int totalPAD = this.pad + energyChargePAD + incEPAD + psdPADx + incPAD;
        if (!JobAccessor.isMechanicJob(job) && bulletItemID != 0) {
            totalPAD += ItemInfo.getBulletPAD(bulletItemID);
        }
        int comboAbilityBuff = getStatOption(ComboAbilityBuff);
        if (comboAbilityBuff != 0) {
            Pointer<SkillEntry> skill = new Pointer<>();
            int slv = SkillInfo.getInstance().getSkillLevel(cd, job != 2000 ? Aran.COMBO_ABILITY : Legend.COMBO_ABILITY, skill);
            if (skill.get() != null && slv > 0) {
                int abilityPAD = skill.get().getLevelData(slv).X;
                if (comboAbilityBuff / 10 < abilityPAD) {
                    abilityPAD = comboAbilityBuff;
                }
                totalPAD += abilityPAD * skill.get().getLevelData(slv).Y;
            }
        }

        int padRate = 0;

        int maxLevelBuff = getStatOption(MaxLevelBuff);
        int darkAura = getStatOption(DarkAura);
        int morewildDamageUp = getStatOption(MorewildDamageUp);
        int swallowAttackDamage = getStatOption(SwallowAttackDamage);
        if (maxLevelBuff > 0 || darkAura > 0 || morewildDamageUp > 0 || swallowAttackDamage > 0) {
            padRate = morewildDamageUp + swallowAttackDamage + darkAura + maxLevelBuff;
        }
        padRate += psdPADr + this.itemPADr;
        if (padRate > 0) {
            totalPAD += totalPAD * padRate / 100;
        }
        return Math.max(Math.min(totalPAD, SkillAccessor.PAD_MAX), 0);
    }

    public int getMAD(int psdMADx, int psdMADr, int dragonFury) {
        int totalMAD = this.mad + psdMADx + getStatOption(MAD);

        int madRate = 0;

        int maxLevelBuff = getStatOption(MaxLevelBuff);
        int darkAura = getStatOption(DarkAura);
        int swallowAttackDamage = getStatOption(SwallowAttackDamage);
        if (maxLevelBuff > 0 || darkAura > 0 || dragonFury > 0 || swallowAttackDamage > 0) {
            madRate = dragonFury + swallowAttackDamage + darkAura + maxLevelBuff;
        }
        madRate += psdMADr + itemMADr;
        if (madRate > 0) {
            totalMAD += totalMAD * madRate / 100;
        }
        return Math.max(Math.min(totalMAD, SkillAccessor.MAD_MAX), 0);
    }

    public int getIncACC(CharacterData cd) {
        int acc = getStatOption(ACC);
        int energyChargeACC = 0;
        EnergyChargeStat energyChargeStat = (EnergyChargeStat) temporaryStats[TSIndex.ENERGY_CHARGED];
        if (energyChargeStat.isActivated(System.currentTimeMillis())) {
            int job = cd.getCharacterStat().getJob();
            Pointer<SkillEntry> skill = new Pointer<>();
            int slv = SkillInfo.getInstance().getSkillLevel(cd, JobAccessor.isCygnusJob(job) ? Striker.ENERGY_CHARGE : Buccaneer.ENERGY_CHARGE, skill);
            if (skill.get() != null && slv > 0) {
                energyChargeACC = skill.get().getLevelData(slv).ACC;
            }
        }
        return Math.max(acc, energyChargeACC);
    }


    public int getACC(CharacterData cd, int psdACCr, int baseACC) {
        int totalACC = this.acc + baseACC + getIncACC(cd);
        int accRate = psdACCr + itemACCr;
        if (accRate > 0) {
            totalACC += totalACC * accRate / 100;
        }
        return Math.max(Math.min(totalACC, SkillAccessor.ACC_MAX), 0);
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

    public boolean isRidingSkillVehicle() {
        TwoStateTemporaryStat ts = (TwoStateTemporaryStat) temporaryStats[TSIndex.RIDE_VEHICLE];
        if (ts.isActivated(System.currentTimeMillis()) && ts.getValue() / 10000 == 193) {
            return true;
        }
        return false;
    }

    public boolean isWildhunterJaguarVehicle() {
        if (!isRidingSkillVehicle()) {
            return false;
        }
        int val = temporaryStats[TSIndex.RIDE_VEHICLE].getValue();
        for (int i = 1; i < WildHunterInfo.RIDING_WILD_HUNTER_JAGUAR.length; i++) {
            if (val == WildHunterInfo.RIDING_WILD_HUNTER_JAGUAR[i]) return true;
        }
        return false;
    }

    public int getJaguarRidingMaxHPUp(CharacterData cd) {
        if (!isWildhunterJaguarVehicle()) {
            return 0;
        }
        Pointer<SkillEntry> skill = new Pointer<>();
        int slv = SkillInfo.getInstance().getSkillLevel(cd, WildHunter.JAGUAR_RIDING, skill);
        if (slv <= 0) {
            return 0;
        }
        return skill.get().getLevelData(slv).Z;
    }
}
