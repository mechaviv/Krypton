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
import common.item.ItemAccessor;
import common.user.CharacterData;
import game.field.life.mob.MobStat;
import game.field.life.mob.MobStats;
import game.field.life.mob.MobTemplate;
import game.user.CharacterActions;
import game.user.item.ItemInfo;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillInfo;
import game.user.skill.Skills.*;
import java.util.List;

import game.user.skill.entries.SkillEntry;
import game.user.stat.psd.AdditionPsd;
import game.user.stat.psd.PassiveSkillData;
import org.python.jline.internal.Log;
import util.Logger;
import util.Pointer;
import util.Rand32;

/**
 *
 * @author Eric
 */
public class CalcDamage {
    private static final int RND_SIZE = 7;
    
    private final Rand32 rndGenForCharacter;
    private final Rand32 rndForCheckDamageMiss;
    private final Rand32 rndGenForMob;
    private int invalidCount;
    
    public CalcDamage() {
        this.rndGenForCharacter = new Rand32();
        this.rndForCheckDamageMiss = new Rand32();
        this.rndGenForMob = new Rand32();
        this.invalidCount = 0;
    }
    
    public boolean checkMDamageMiss(MobStat ms, CharacterData cd, BasicStat bs, SecondaryStat ss, long randForMissCheck) {
        int eva = Math.max(0, Math.min(ss.eva + ss.getStatOption(CharacterTemporaryStat.EVA), SkillAccessor.EVA_MAX));
        int level = bs.getLevel();
        eva -= (ms.getLevel() - level) / 2;
        int mobACC = 0;
        if (level >= ms.getLevel() || eva > 0) 
            mobACC = eva;
        double rand = (double) mobACC;
        double b = rand * 0.1d;
        rand = getRand(randForMissCheck, b, rand);
        int acc = Math.max(0, Math.min(ms.getACC() + ms.getStatOption(MobStats.ACC), SkillAccessor.ACC_MAX));
        return rand >= (double) acc;
    }
    
    public boolean checkPDamageMiss(MobStat ms, CharacterData cd, BasicStat bs, SecondaryStat ss, long randForMissCheck) {
        int eva = Math.max(0, Math.min(ss.eva + ss.getStatOption(CharacterTemporaryStat.EVA), SkillAccessor.EVA_MAX));
        int level = bs.getLevel();
        eva -= (ms.getLevel() - level) / 2;
        int mobACC = 0;
        if (level >= ms.getLevel() || eva > 0) 
            mobACC = eva;
        double rand = (double) mobACC;
        int acc = Math.max(0, Math.min(ms.getACC() + ms.getStatOption(MobStats.ACC), SkillAccessor.ACC_MAX));
        double b = rand / ((double) acc * 4.5) * 100.0;
        if (JobAccessor.getJobCategory(bs.getJob()) == JobCategory.THIEF) {
            b = Math.max(5.0, Math.min(b, 95.0));
        } else {
            b = Math.max(2.0, Math.min(b, 80.0));
        }
        return b > (double) (randForMissCheck % 10000000) * 0.0000100000010000001;
    }
    
    public void decInvalidCount() {
        if (this.invalidCount > 0) {
            --this.invalidCount;
        }
    }
    
    public int getInvalidCount() {
        return this.invalidCount;
    }
    
    public boolean isExceedInvalidCount() {
        return this.invalidCount <= 20 && this.invalidCount > 10;
    }
    
    public void incInvalidCount() {
        ++this.invalidCount;
        if (this.invalidCount > 50) {
            this.invalidCount = 0;
        }
    }

    public void MDamage(CharacterData cd, BasicStat bs, SecondaryStat ss, int mobID, MobStat ms, MobTemplate template, PassiveSkillData psd, boolean nextAttackCritical, int damagePerMob, int weaponItemID, int action, SkillEntry skill, byte slv, List<Integer> damage, List<Boolean> critical, int criticalProb, int criticalDamage, int totalDamR, int bossDamR, int ignoreTargetDEF, int mobCount, int keyDown, int dragonFury, int AR01Mad) {
        int psdCr = 0;
        int psdCDMin = 0;

        int psdPADr = 0;
        int psdMADr = 0;
        int psdPADx = 0;
        int psdMADx = 0;
        int psdACCr = 0;
        int psdAR = 0;
        int psdMDamR = 0;
        int psdDIPr = 0;
        int psdIMPr = 0;

        if (psd != null) {
            psdCr = psd.getCr();
            psdCDMin = psd.getCDMin();
            psdPADr = psd.getPADr();
            psdMADr = psd.getMADr();
            psdPADx = psd.getPADx();
            psdMADx = psd.getMADx();
            psdACCr = psd.getACCr();
            psdMDamR = psd.getMDamR();
            psdDIPr = psd.getDIPr();
            psdIMPr = psd.getIMPr();
            psdAR = psd.getAr();

            if (psd.getAdditionPsd().size() != 0 && skill != null) {
                AdditionPsd apsd = psd.getAdditionPsd().getOrDefault(skill.getSkillID(), null);
                if (apsd != null) {
                    psdCr += apsd.getCr();
                    psdCDMin += apsd.getCDMin();
                    psdMDamR += apsd.getMDamr();
                    psdDIPr += apsd.getDIPr();
                    psdIMPr += apsd.getIMPr();
                    psdAR += apsd.getAr();
                }
            }
        }
        int idx = 0;
        int[] random = new int[RND_SIZE];
        for (int i = 0; i < random.length; i++) {
            random[i] = rndGenForCharacter.random();
        }
        String randstar = "";
        for (int randd : random) {
            randstar += String.format("0x%X,", randd);
        }
        //Logger.logReport("Randoms = [%s]", randstar);
        int wt = ItemAccessor.getWeaponType(weaponItemID);

        int acc = ss.getACC(cd, psdACCr, bs.calcBasePACC());
        int mad = ss.getMAD(psdMADx, psdMADr, dragonFury);
        int mastery = SkillAccessor.getMagicMastery(cd, null);
        if (skill != null && mastery == 0) {
            mastery = skill.getLevelData(slv).Mastery;
        }
        int amp = SkillAccessor.getAmplification(cd, 0, null);
        int criticalAttackProp = 5;
        int criticalAttackParam = 0;

        Pointer<SkillEntry> tempSkill;
        int tempSLV;

        int job = cd.getCharacterStat().getJob();
        if (job / 100 == 22 || job == 2001) {
            tempSkill = new Pointer<>();
            tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Evan.MAGIC_CRITICAL, tempSkill);
            if (tempSLV > 0 && tempSkill.get() != null) {
                criticalAttackProp += tempSkill.get().getLevelData(slv).Prop;
                criticalAttackParam += tempSkill.get().getLevelData(slv).Damage;
            }
        }
        int sharpEyesProp = Math.max(Math.min((ss.getStatOption(CharacterTemporaryStat.SharpEyes) >> 8), 100), 0);
        int sharpEyesParam = Math.max((byte) ss.getStatOption(CharacterTemporaryStat.SharpEyes), 0);

        int thornsProp = Math.max(Math.min((ss.getStatOption(CharacterTemporaryStat.ThornsEffect) >> 8), 100), 0);
        int thornsParam = Math.max((byte) ss.getStatOption(CharacterTemporaryStat.ThornsEffect), 0);

        criticalAttackProp += Math.max(sharpEyesProp, thornsProp);
        criticalAttackParam += Math.max(sharpEyesParam, thornsParam);

        int additionCDProp = 0;// cd->critical.nProb
        int additionCDParam = 0;// cd->critical.nDamage

        criticalAttackProp += additionCDProp;
        criticalAttackParam += additionCDParam;

        criticalAttackProp += Math.max(Math.min(criticalProb, 100), 0);
        criticalAttackParam += criticalDamage * criticalAttackParam / 100;

        criticalAttackProp += Math.max(Math.min(psdCr, 100), 0);

        for (int i = 0; i < damagePerMob; i++) {
            damage.set(i, 0);
            if (ms.isInvincible()) {
                continue;
            }
            if (ms.getStatOption(MobStats.MImmune) != 0) {
                int rand = random[idx++ % 7] % 100;
                if (rand > ss.getStatOption(CharacterTemporaryStat.RespectMImmune)) {
                    damage.set(i, 1);
                    continue;
                }
            }
            idx++;
            if (ss.getStatOption(CharacterTemporaryStat.Seal) != 0) {
                continue;
            }
            int mobEVA = Math.max(Math.min((ms.getEVA() + ms.getStatOption(MobStats.EVA)), 9999), 0);
            int mobACCr = CalcDamageHelper.calcACCr(acc, mobEVA, bs.getLevel(), ms.getLevel(), psdAR);
            double rand = CalcDamageHelper.get_rand(random[idx++ % 7], 100.0, 0.0);
            if (mobACCr < rand) {
                continue;
            }
            if (skill != null) {
                // fix damage stuff
            }
            double dmg = calcDamageByWT(wt, bs, 0, mad);
            double rndDmg = CalcDamageHelper.adjustRandomDamage(dmg, random[idx++ % 7], getMasteryConstByWT(wt), mastery);
            rndDmg += (psdMDamR * rndDmg / 100.0) * amp / 100;

            int elemBoost = 0;
            int adjustByByBuff = ss.getStatOption(CharacterTemporaryStat.ElementalReset);
            rndDmg = CalcDamageHelper.getDamageAdjustedByElemAttr(rndDmg, skill, ms.getDamagedElemAttr(), slv, 1.0 - adjustByByBuff / 100.0, elemBoost / 100.0);
            int mdR = Math.max(Math.min(ms.getMDD() + ms.getStatOption(MobStats.MDD), 100), 0);
            int impR = Math.min(psdIMPr + ignoreTargetDEF, 100);
            int mobTotalMDD = mdR * impR / -100 + mdR;
            rndDmg *= ((100.0 - mobTotalMDD) / 100.0);
            if (ms.getStatOption(MobStats.MGuardUp) != 0) {
                rndDmg *= ms.getStatOption(MobStats.MGuardUp) / 100.0;
            }
            if (skill != null) {
                int skillDamage = skill.getLevelData(slv).Damage;
                if (skillDamage > 0) {
                    rndDmg = skillDamage / 100.0 * rndDmg;
                }
            }
            if (nextAttackCritical || criticalAttackProp > 0 && (rand = CalcDamageHelper.get_rand(random[idx++ % 7], 0.0, 100.0)) <= criticalAttackProp) {
                int totalCDMin = Math.min(criticalAttackParam + psdCDMin + 20, 50);
                double critDamage = CalcDamageHelper.get_rand(random[idx++ % 7], totalCDMin, 50.0);
                critical.set(i, true);
                rndDmg += (int) critDamage / 100.0 * rndDmg;
            }
            // hardskin handle
            int bossDam = 0;
            if (template.isBoss()) {
                bossDam = bossDamR;
            }
            int total = totalDamR + psdDIPr + bossDam;
            if (ss.getStatOption(CharacterTemporaryStat.DamR) != 0) {
                total += ss.getStatOption(CharacterTemporaryStat.DamR);
            }
            rndDmg += rndDmg * total / 100.0;
            damage.set(i, (int) rndDmg);
        }
    }

    public void PDamage(CharacterData cd, BasicStat bs, SecondaryStat ss, int mobID, MobStat ms, MobTemplate template, PassiveSkillData psd, boolean nextAttackCritical, int attackCount, int damagePerMob, int weaponItemID, int bulletItemID, int attackType, int action, boolean shadowPartner, SkillEntry skill, int SLV, List<Integer> damage, List<Boolean> critical, int criticalProb, int criticalDamage, int totalDAMr, int bossDAMr, int ignoreTargetDEF, int dragonFury, int AR01Pad, int keyDown, int darkForce, int advancedChargeDamage, boolean invincible) {
        int job = cd.getCharacterStat().getJob();

        Pointer<SkillEntry> shadowPartnerSkill = new Pointer<>();
        int spSLV = SkillInfo.getInstance().getSkillLevel(cd, SkillAccessor.getShadowPartnerSkillID(job), shadowPartnerSkill);

        int psdCr = 0;
        int psdCDMin = 0;

        int psdPADr = 0;
        int psdMADr = 0;
        int psdPADx = 0;
        int psdMADx = 0;
        int psdACCr = 0;
        int psdAR = 0;
        int psdPDamR = 0;
        int psdDIPr = 0;
        int psdIMPr = 0;

        if (psd != null) {
            psdCr = psd.getCr();
            psdCDMin = psd.getCDMin();
            psdPADr = psd.getPADr();
            psdMADr = psd.getMADr();
            psdPADx = psd.getPADx();
            psdMADx = psd.getMADx();
            psdACCr = psd.getACCr();
            psdPDamR = psd.getPDamR();
            psdDIPr = psd.getDIPr();
            psdIMPr = psd.getIMPr();
            psdAR = psd.getAr();

            if (psd.getAdditionPsd().size() != 0 && skill != null) {
                AdditionPsd apsd = psd.getAdditionPsd().getOrDefault(skill.getSkillID(), null);
                if (apsd != null) {
                    psdCr += apsd.getCr();
                    psdCDMin += apsd.getCDMin();
                    psdPDamR += apsd.getPDamr();
                    psdDIPr += apsd.getDIPr();
                    psdIMPr += apsd.getIMPr();
                    psdAR += apsd.getAr();
                }
            }
        }
        int idx = 0;
        int[] random = new int[RND_SIZE];
        for (int i = 0; i < random.length; i++) {
            random[i] = rndGenForCharacter.random();
        }
        int skillID = 0;
        if (skill != null) skillID = skill.getSkillID();

        int wt = ItemAccessor.getWeaponType(weaponItemID);

        int acc = ss.getACC(cd, psdACCr, bs.calcBasePACC());

        int blessingArmorIncPAD = 0;
        if (ss.getStatOption(CharacterTemporaryStat.BlessingArmor) > 0) {
            blessingArmorIncPAD = ss.getStat(CharacterTemporaryStat.BlessingArmor).getModOption();
        }

        int pad = ss.getPAD(cd, bulletItemID, psdPADx, psdPADr, blessingArmorIncPAD);
        int mad = ss.getMAD(psdMADx, psdMADr, dragonFury);
        int mastery = SkillAccessor.getWeaponMastery(cd, ss, weaponItemID, attackType, skillID, null, null);

        int criticalAttackProp;
        int[] criticalAttackParam = new int[2];
        if (action == CharacterActions.ASSASSINATIONS) {
            criticalAttackProp = skill.getLevelData(SLV).Prop + 5;
            criticalAttackParam[0] = skill.getLevelData(SLV).CDMin;
        } else {
            Pointer<Integer> tempProp = new Pointer<>(0);
            Pointer<Integer> tempParam = new Pointer<>(0);
            SkillAccessor.getCriticalSkillLevel(cd, weaponItemID, attackType, tempProp, tempParam);
            criticalAttackProp = tempProp.get();
            criticalAttackParam[0]  = tempParam.get();
        }
        int sharpEyesProp = Math.max(Math.min((ss.getStatOption(CharacterTemporaryStat.SharpEyes) >> 8), 100), 0);
        int sharpEyesParam = Math.max((byte) ss.getStatOption(CharacterTemporaryStat.SharpEyes), 0);

        int thornsProp = Math.max(Math.min((ss.getStatOption(CharacterTemporaryStat.ThornsEffect) >> 8), 100), 0);
        int thornsParam = Math.max((byte) ss.getStatOption(CharacterTemporaryStat.ThornsEffect), 0);

        criticalAttackProp += Math.max(sharpEyesProp, thornsProp) + ss.getStatOption(CharacterTemporaryStat.SwallowCritical);
        criticalAttackParam[0]  += thornsParam;// + cd->critical.nProb (also take a look in kmst leak)

        Pointer<SkillEntry> tempSkill;
        int tempSLV;

        boolean stuned = ms.getStatOption(MobStats.Stun) != 0;
        if (stuned) {
            tempSkill = new Pointer<>();
            tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Buccaneer.STUN_MASTERY, tempSkill);
            if (tempSLV > 0) {
                criticalAttackProp += Math.max(Math.min(tempSkill.get().getLevelData(tempSLV).Prop, 100), 0);
                criticalAttackParam[0] += tempSkill.get().getLevelData(tempSLV).Damage;
            }
        }

        int comboAbility = ss.getStatOption(CharacterTemporaryStat.ComboAbilityBuff);
        if (comboAbility > 0) {
            tempSkill = new Pointer<>();
            tempSLV = SkillInfo.getInstance().getSkillLevel(cd, job != 2000 ? Aran.COMBO_ABILITY : Legend.COMBO_ABILITY, tempSkill);
            if (tempSLV > 0) {
                comboAbility = Math.max(comboAbility, tempSkill.get().getLevelData(tempSLV).X);
                criticalAttackProp += Math.max(Math.min(comboAbility * tempSkill.get().getLevelData(tempSLV).Y, 100), 0);
                criticalAttackParam[0] += comboAbility * tempSkill.get().getLevelData(tempSLV).Damage;
            }
        }
        criticalAttackProp += Math.max(Math.min(criticalProb, 100), 0);
        criticalAttackParam[0] += criticalDamage * criticalAttackParam[0] / 100;

        criticalAttackProp += Math.max(Math.min(psdCr, 100), 0);

        if (ss.isWildhunterJaguarVehicle()) {
            tempSkill = new Pointer<>();
            tempSLV = SkillInfo.getInstance().getSkillLevel(cd, WildHunter.JAGUAR_RIDING, tempSkill);
            if (tempSLV > 0) {
                criticalAttackProp += Math.max(tempSkill.get().getLevelData(tempSLV).W, 0);
            }
        }
        int ninjaStromProp = 0;
        if (skillID == NightLord.NINJA_STORM) {
            ninjaStromProp = skill.getLevelData(SLV).Prop;
        }

        int whirlWindProp = 0;
        if (skillID == Aran.ROLLING_SPIN) {
            whirlWindProp = skill.getLevelData(SLV).Prop;
        }

        for (int i = 0; i < damagePerMob; i++) {
            damage.set(i, 0);
            if (skillID == Beginner.BAMBOO || skillID == Noblesse.BAMBOO || skillID == Legend.BAMBOO || skillID == EvanJr.BAMBOO || skillID == Citizen.BAMBOO) {
                if (!template.isBoss()) {
                    damage.set(i, template.getMaxHP());
                    continue;
                }
                damage.set(i, (int) (template.getMaxHP() * 0.3));
                continue;
            }
            if (skillID == Beginner.MASSACRE || skillID == Noblesse.MASSACRE || skillID == Legend.MASSACRE || skillID == EvanJr.MASSACRE || skillID == Citizen.MASSACRE) {
                damage.set(i, template.getMaxHP());
                continue;
            }
            if (!invincible && ms.isInvincible()) {
                continue;
            }
            if (ms.getStatOption(MobStats.Freeze) == 0 || ms.getStatReason(MobStats.Freeze) != Aran.COMBO_TEMPEST) {
                if (calcPImmune(ms, ss, random[idx++ % RND_SIZE] % 100)) {
                    damage.set(i, 1);
                    continue;
                }
                if (skillID == Aran.COMBO_TEMPEST && !template.isBoss()) {
                    damage.set(i, 1);
                    continue;
                }
            }
            if (skillID == CrossbowMaster.SNIPING || skillID == WildHunter.SNIPING && attackType == 1) {
                if (!template.isBoss()) {
                    damage.set(i, (int) (999999.0 - CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], 10000.0, 0.0)));
                    continue;
                }
                damage.set(i, 500000);
                continue;
            }
            if (skillID == NightLord.NINJA_STORM || skillID == Aran.ROLLING_SPIN) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], 100.0, 0.0);
                double prob = skillID == NightLord.NINJA_STORM ? ninjaStromProp : whirlWindProp;
                if (prob <= rndProb) {
                    damage.set(i, 0);
                    continue;
                }
            }
            if ((ms.getStatOption(MobStats.Freeze) != 0 && skillID == Sniper.STRAFE && attackType == 1) || skillID == Dual4.OWL_DEATH) {
                if (i == 0 && !template.isBoss()) {
                    double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],0.0, 100.0);
                    if (rndProb < skill.getLevelData(SLV).Prop) {
                        damage.set(0, template.getMaxHP());
                        continue;
                    }
                }
            }
            if (attackType != 3 && skillID != 0 && ss.getStatOption(CharacterTemporaryStat.Seal) != 0) {
                continue;
            }
            int mobEVA = Math.max(Math.min((ms.getEVA() + ms.getStatOption(MobStats.EVA)), 9999), 0);
            double accR = CalcDamageHelper.calcACCr(acc, mobEVA, bs.getLevel(), ms.getLevel(), psdAR);

            if (!invincible) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],100.0, 0.0);
                if (accR < rndProb) {
                    continue;
                }
            }
            if (skillID == Paladin.SANCTUARY) {
                damage.set(i, 1);// modified by server
                continue;
            }
            if (skill != null && skill.getLevelData(SLV).FixDamage != 0 || skillID == JobAccessor.getNoviceSkillAsRace(Beginner.VISITOR_MORPH_SKILL_NORMAL, job) || skillID == JobAccessor.getNoviceSkillAsRace(Beginner.VISITOR_MORPH_SKILL_SKILL, job)) {
                damage.set(i, skill.getLevelData(SLV).FixDamage);
                continue;
            }
            if (ss.getStatOption(CharacterTemporaryStat.Darkness) != 0) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],100.0, 0.0);
                if (rndProb > 20.0) {
                    idx += CalcDamageHelper.get_rand(random[idx % RND_SIZE],0.0, 5.0) + 1;
                    continue;
                }
            }
            if (ms.getStatOption(MobStats.Freeze) != 0 && ms.getStatReason(MobStats.Freeze) == Aran.COMBO_TEMPEST) {
                damage.set(i, template.getMaxHP());
                continue;
            }
            if (skillID == Hero.Rush || skillID == Paladin.RUSH || skillID == DarkKnight.RUSH || skillID == Paladin.BLAST || skillID == Dual2.SLASH_STORM || skillID == Dual3.HUSTLE_RUSH || skillID == Dual4.BLOODY_STORM || skillID == Dual4.FLYING_ASSAULTER) {
                idx++;
            }
            boolean luckySeven = skillID == Rogue.LuckySeven || skillID == NightWalker.LUCKY_SEVEN;
            boolean tripleThrow = skillID == NightLord.TRIPLE_THROW || skillID == NightWalker.TRIPLE_THROW;
            double damageByWT = calcDamageByWT(wt, bs, pad, mad);

            if (luckySeven) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],(double)bs.getLUK() * 1.4, bs.getLUK()) * 5.5;
                damageByWT = rndProb * (double) pad / 100.0;
            }
            if (tripleThrow) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],(double)bs.getLUK() * 1.4, bs.getLUK()) * 6.0;
                damageByWT = rndProb * (double) pad / 100.0;
            }
            if (wt == ItemAccessor.WeaponTypeFlag.WAND || wt == ItemAccessor.WeaponTypeFlag.STAFF) {
                damageByWT *= 0.2;
            }
            if (!CharacterActions.isShootAction(action)) {
                // TODO: add is mechanic vehicle when I finish mechanic
                if (!SkillAccessor.isJaguarMeleeAttackSkill(skillID) && skillID != WildHunter.FLASH_RAIN) {
                    if (wt == ItemAccessor.WeaponTypeFlag.BOW || wt == ItemAccessor.WeaponTypeFlag.CROSSBOW) {
                        damageByWT *= 0.6;
                    }
                    if (skillID != Shadower.ASSASSINATION && skillID != NightLord.NINJA_STORM && skillID != NightWalker.VAMPIRE && skillID != NightWalker.POISON_BOMB) {
                        damageByWT *= 0.4;
                    } else if (wt == ItemAccessor.WeaponTypeFlag.GUN && action != CharacterActions.FIREBURNER && action != CharacterActions.COOLINGEFFECT && skillID != Gunslinger.THROWING_BOMB && skillID != Captain.AIR_STRIKE && skillID != Captain.BATTLESHIP_CANNON && skillID != Captain.BATTLESHIP_TORPEDO && skillID != Dual5.MONSTER_BOMB) {
                        damageByWT *= 0.4;
                        if (action == CharacterActions.STRAIGHT || action == CharacterActions.SOMERSAULT) {
                            damageByWT *= 1.8;
                        }
                    }
                }
            }
            if (CharacterActions.isProneStabAction(action)) {
                damageByWT *= 0.1;
            }
            if (!luckySeven && !tripleThrow) {
                damageByWT = CalcDamageHelper.adjustRandomDamage(damageByWT, random[idx++ % RND_SIZE], (long) getMasteryConstByWT(wt), mastery);
            }
            damageByWT += damageByWT * psdPDamR / 100.0;

            double damageAdjustedByMobLevel = damageByWT;
            if (!invincible) {
                if (bs.getLevel() < ms.getLevel()) {
                    int range = ms.getLevel() - bs.getLevel();
                    damageAdjustedByMobLevel = (double) (100.0 - range) / 100.0 * damageByWT;
                }
            }
            // cd elem boost ?
            List<Integer> mobDamagedElemAttr = ms.getDamagedElemAttr();
            // mob attr here
            // weapon charge elem attr

            if (skillID != DragonKnight.SACRIFICE && skillID != ThiefMaster.ASSAULTER && skillID != Viper.DEMOLITION) {
            }
            double rnd = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], 0.0, 100.0);
            if (nextAttackCritical || criticalAttackProp > 0 && criticalAttackProp > rnd) {
                int param = criticalAttackParam[0] + psdCDMin + 20;
                param = Math.min(param, sharpEyesParam + 50);

                rnd = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], param, sharpEyesParam + 50);
                critical.set(i, true);
                damageByWT += rnd / 100.0 * damageByWT;
            }
            damage.set(i, (int) damageByWT);
        }
    }

    public boolean calcPImmune(MobStat ms, SecondaryStat ss, long rand) {
        return ms.getStatOption(MobStats.PImmune) != 0 && rand > ss.getStatOption(CharacterTemporaryStat.RespectPImmune);
    }

    public double calcDamageByWT(int wt, BasicStat bs, int pad, int mad) {
        int job = bs.getJob();
        // beginner calculation
        if (job == 1000 * (job / 1000) || job == 2001) {
            return CalcDamageHelper.calcBaseDamage(bs.getSTR(), bs.getDEX(), 0, pad, 1.2);
        }
        if (JobAccessor.isMageJob(job / 100)) {// nexon do it jobid % 1000 / 100 but it'll make it 2 for all mage job so I changed it a little
            return CalcDamageHelper.calcBaseDamage(bs.getINT(), bs.getLUK(), 0, mad, 1.0);
        }
        switch (wt) {
            case ItemAccessor.WeaponTypeFlag.OH_SWORD:
            case ItemAccessor.WeaponTypeFlag.OH_AXE:
            case ItemAccessor.WeaponTypeFlag.OH_MACE:
                return CalcDamageHelper.calcBaseDamage(bs.getSTR(), bs.getDEX(), 0, pad, 1.2);
            case ItemAccessor.WeaponTypeFlag.TH_SWORD:
            case ItemAccessor.WeaponTypeFlag.TH_AXE:
            case ItemAccessor.WeaponTypeFlag.TH_MACE:
                return CalcDamageHelper.calcBaseDamage(bs.getSTR(), bs.getDEX(), 0, pad, 1.32);
            case ItemAccessor.WeaponTypeFlag.DAGGER:
                return CalcDamageHelper.calcBaseDamage(bs.getLUK(), bs.getDEX(), bs.getSTR(), pad, 1.3);
            case ItemAccessor.WeaponTypeFlag.BAREHAND:
                return CalcDamageHelper.calcBaseDamage(bs.getSTR(), bs.getDEX(), 0, 1, 1.43);
            case ItemAccessor.WeaponTypeFlag.SPEAR:
            case ItemAccessor.WeaponTypeFlag.POLEARM:
                return CalcDamageHelper.calcBaseDamage(bs.getSTR(), bs.getDEX(), 0, pad, 1.49);
            case ItemAccessor.WeaponTypeFlag.BOW:
                return CalcDamageHelper.calcBaseDamage(bs.getDEX(), bs.getSTR(), 0, pad, 1.2);
            case ItemAccessor.WeaponTypeFlag.CROSSBOW:
                return CalcDamageHelper.calcBaseDamage(bs.getDEX(), bs.getSTR(), 0, pad, 1.35);
            case ItemAccessor.WeaponTypeFlag.THROWINGGLOVE:
                return CalcDamageHelper.calcBaseDamage(bs.getLUK(), bs.getDEX(), 0, pad, 1.75);
            case ItemAccessor.WeaponTypeFlag.KNUCKLE:
                return CalcDamageHelper.calcBaseDamage(bs.getSTR(), bs.getDEX(), 0, pad, 1.7);
            case ItemAccessor.WeaponTypeFlag.GUN:
                return CalcDamageHelper.calcBaseDamage(bs.getDEX(), bs.getSTR(), 0, pad, 1.5);
        }
        return 0.0;
    }

    public double getMasteryConstByWT(int wt) {
        switch (wt) {
            case ItemAccessor.WeaponTypeFlag.WAND:
            case ItemAccessor.WeaponTypeFlag.STAFF:
                return 0.25;
            case ItemAccessor.WeaponTypeFlag.BOW:
            case ItemAccessor.WeaponTypeFlag.CROSSBOW:
            case ItemAccessor.WeaponTypeFlag.THROWINGGLOVE:
            case ItemAccessor.WeaponTypeFlag.GUN:
                return 0.15;
        }
        return 0.2;
    }

    public void PDamage(CharacterData cd, BasicStat bs, SecondaryStat ss, MobStat ms, int damagePerMob, int weaponItemID, int bulletItemID, int attackType, int action, SkillEntry skill, byte slv, List<Integer> damage) {
        int idx = 0;
        long[] random = new long[RND_SIZE];
        for (int i = 0; i < random.length; i++) {
            random[i] = rndGenForCharacter.random();
        }
        
        int skillID = 0;
        if (skill != null)
            skillID = skill.getSkillID();
        int wt = ItemAccessor.getWeaponType(weaponItemID);
        int acc = Math.max(0, Math.min(ss.acc + ss.getStatOption(CharacterTemporaryStat.ACC), SkillAccessor.ACC_MAX));
        int mobPDD = acc;
        int eva = Math.max(0, ms.getLevel() - bs.getLevel());
        double a = (double) mobPDD * 100.0 / ((double) eva * 10.0 + 255.0);
        int pad = ss.pad + ss.getStatOption(CharacterTemporaryStat.PAD);
        if (bulletItemID != 0)
            pad += ItemInfo.getBulletPAD(bulletItemID);
        pad = Math.max(0, Math.min(pad, SkillAccessor.PAD_MAX));
        if (damagePerMob <= 0) {
            return;
        }

        for (int dmg : damage) {
            mobPDD = Math.max(0, Math.min(ms.getEVA() + ms.getStatOption(MobStats.EVA), SkillAccessor.EVA_MAX));
            
            double b = a * 1.3;
            double dmgDone = a * 0.7;
            long rand = random[idx++ % RND_SIZE];
            
            if (b != dmgDone) {
                b = getRand(rand, dmgDone, b);
            }
            if (b >= (double) mobPDD) {
                if (((wt != 45 && wt != 46 && wt != 47) || (action >= 22 && action <= 27)) && wt != 32) {
                    switch (wt) {
                        case 45: // Bow
                            break;
                        case 46: // CrossBow
                            break;
                        case 41: // TowHand_Axe
                        case 42: // TowHand_Mace
                            if (action >= 5 && action <= 15) {
                                
                            } else {
                                
                            }
                            break;
                        case 43: // Spear
                        case 44: // PoleArm
                            if ((action >= 5 && action <= 15) == (wt == 43)) {
                                
                            } else {
                                
                            }
                            break;
                        case 40: // TowHand_Sword
                            break;
                        case 31: // OneHand_Axe
                        case 32: // OneHand_Mace
                        case 37: // Wand
                        case 38: // Staff
                            if (action >= 5 && action <= 15) {
                                
                            } else {
                                
                            }
                            break;
                        case 30: // OneHand_Sword
                        case 33: // Dagger
                            if (JobAccessor.getJobCategory(bs.getJob()) == JobCategory.THIEF && wt == 33) {
                                
                            } else {
                                
                            }
                            break;
                        case 47: // ThrowingGloves
                            if (skillID != 0 && skillID == Rogue.DoubleStab_Dagger) {
                                
                            } else {
                                
                            }
                            break;
                    }
                } else {
                    
                }
                // Begin Critical Calculations
            }
        }
    }

    public void setSeed(int s1, int s2, int s3) {
        rndGenForCharacter.seed(s1, s2, s3);
        rndForCheckDamageMiss.seed(s1, s2, s3);
        rndGenForMob.seed(s1, s2, s3);
    }
    
    public void skip() {
        for (int i = 0; i < RND_SIZE; i++) {
            rndGenForCharacter.random();
        }
    }
    
    public static final double getRand(long rand, double f0, double f1) {
        double random;
        if (f1 != f0) {
            // swap f1 with f0
            if (f1 > f0) {
                double tmp = f1;
                f0 = f1;
                f1 = tmp;
            }
            long val = rand % 10000000;
            random = f1 + (f0 - f1) * val * 0.000000100000010000001;
        } else {
            random = f0;
        }
        return random;
    }
}
