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
import common.item.ItemAccessor;
import common.user.CharacterData;
import game.field.life.mob.MobStat;
import game.field.life.mob.MobStats;
import game.field.life.mob.MobTemplate;
import game.user.CharacterActions;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillInfo;
import game.user.skill.Skills.*;

import java.util.ArrayList;
import java.util.List;

import game.user.skill.entries.SkillEntry;
import game.user.stat.psd.AdditionPsd;
import game.user.stat.psd.PassiveSkillData;
import util.Logger;
import util.Pointer;
import util.Rand32;

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
                int rand = random[idx++ % RND_SIZE] % 100;
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
            double rand = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], 100.0, 0.0);
            if (mobACCr < rand) {
                continue;
            }
            if (skill != null) {
                // fix damage stuff
            }
            double dmg = calcDamageByWT(wt, bs, 0, mad);
            double rndDmg = CalcDamageHelper.adjustRandomDamage(dmg, random[idx++ % RND_SIZE], getMasteryConstByWT(wt), mastery);
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
            if (nextAttackCritical || criticalAttackProp > 0 && (rand = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], 0.0, 100.0)) <= criticalAttackProp) {
                int totalCDMin = Math.min(criticalAttackParam + psdCDMin + 20, 50);
                double critDamage = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], totalCDMin, 50.0);
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
            criticalAttackProp = tempProp.get() + 5;
            criticalAttackParam[0]  = tempParam.get();
        }
        int sharpEyesProp = Math.max(Math.min((ss.getStatOption(CharacterTemporaryStat.SharpEyes) >> 8), 100), 0);
        int sharpEyesParam = Math.max((byte) ss.getStatOption(CharacterTemporaryStat.SharpEyes), 0);

        int thornsProp = Math.max(Math.min((ss.getStatOption(CharacterTemporaryStat.ThornsEffect) >> 8), 100), 0);
        int thornsParam = Math.max((byte) ss.getStatOption(CharacterTemporaryStat.ThornsEffect), 0);

        criticalAttackProp += Math.max(sharpEyesProp, thornsProp) + ss.getStatOption(CharacterTemporaryStat.SwallowCritical);
        criticalAttackParam[0] += Math.max(sharpEyesParam, thornsParam);


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

        List<Integer> shadowPartnerDamage = new ArrayList<>();
        if (shadowPartner) {
            for (int i = 0; i < damagePerMob * 2; i++) shadowPartnerDamage.add(i, 0);
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
                if (calcPImmune(ms, ss, random[idx++ % RND_SIZE] % 100) || (skillID == Aran.COMBO_TEMPEST && !template.isBoss())) {
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
            if (skill != null) {
                if (skillID == Paladin.SANCTUARY) {
                    damage.set(i, 1);// modified by server
                    continue;
                }
                if (skill.getLevelData(SLV).FixDamage != 0 || skillID == JobAccessor.getNoviceSkillAsRace(Beginner.VISITOR_MORPH_SKILL_NORMAL, job) || skillID == JobAccessor.getNoviceSkillAsRace(Beginner.VISITOR_MORPH_SKILL_SKILL, job)) {
                    damage.set(i, skill.getLevelData(SLV).FixDamage);
                    continue;
                }
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

            Logger.logReport("CalcDamage__CalcDamageByWT: Damage [%s] | WT [%d] | PAD [%d] | MAD [%d]", damageByWT, wt, pad, mad);
            if (luckySeven) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],(double)bs.getLUK() * 1.4, bs.getLUK()) * 5.5;
                damageByWT = rndProb * (double) pad / 100.0;
                Logger.logReport("Adjust 1");
            }
            if (tripleThrow) {
                double rndProb = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE],(double)bs.getLUK() * 1.4, bs.getLUK()) * 6.0;
                damageByWT = rndProb * (double) pad / 100.0;
                Logger.logReport("Adjust 2");
            }
            if (wt == ItemAccessor.WeaponTypeFlag.WAND || wt == ItemAccessor.WeaponTypeFlag.STAFF) {
                damageByWT *= 0.2;
                Logger.logReport("Adjust 3");
            }
            if (!CharacterActions.isShootAction(action)) {
                // TODO: add is mechanic vehicle when I finish mechanic
                if (!SkillAccessor.isJaguarMeleeAttackSkill(skillID) && skillID != WildHunter.FLASH_RAIN) {
                    if (wt == ItemAccessor.WeaponTypeFlag.BOW || wt == ItemAccessor.WeaponTypeFlag.CROSSBOW) {
                        damageByWT *= 0.6;
                        Logger.logReport("Adjust 4");
                    }
                    if (wt == ItemAccessor.WeaponTypeFlag.THROWINGGLOVE && skillID != Shadower.ASSASSINATION && skillID != NightLord.NINJA_STORM && skillID != NightWalker.VAMPIRE && skillID != NightWalker.POISON_BOMB) {
                        damageByWT *= 0.4;
                        Logger.logReport("Adjust 5");
                    } else if (wt == ItemAccessor.WeaponTypeFlag.GUN && action != CharacterActions.FIREBURNER && action != CharacterActions.COOLINGEFFECT && skillID != Gunslinger.THROWING_BOMB && skillID != Captain.AIR_STRIKE && skillID != Captain.BATTLESHIP_CANNON && skillID != Captain.BATTLESHIP_TORPEDO && skillID != Dual5.MONSTER_BOMB) {
                        damageByWT *= 0.4;
                        Logger.logReport("Adjust 6");
                        if (action == CharacterActions.STRAIGHT || action == CharacterActions.SOMERSAULT) {
                            damageByWT *= 1.8;
                            Logger.logReport("Adjust 7");
                        }
                    }
                }
            }
            if (CharacterActions.isProneStabAction(action)) {
                Logger.logReport("Adjust 8");
                damageByWT *= 0.1;
            }
            if (!luckySeven && !tripleThrow) {
                double old = damageByWT;
                int rand = random[idx++ % RND_SIZE];
                damageByWT = CalcDamageHelper.adjustRandomDamage(damageByWT, rand, getMasteryConstByWT(wt), mastery);
                Logger.logReport("CalcDamage__AdjustRandomDamage: Result DMG [%s] | Damage [%s] | Rand [0x%X] | k [%s] | Mastery [%d]", damageByWT, old, rand, getMasteryConstByWT(wt), mastery);
            }
            damageByWT += damageByWT * psdPDamR / 100.0;

            if (!invincible) {
                if (bs.getLevel() < ms.getLevel()) {
                    int range = ms.getLevel() - bs.getLevel();
                    damageByWT *= (100.0 - range) / 100.0;
                }
            }

            int elemBoost = 0;// cd elem boost
            double oldDamage = damageByWT;
            damageByWT = CalcDamageHelper.getDamageAdjustedByElemAttr(damageByWT, skill, ms.getDamagedElemAttr(), SLV, 1.0, elemBoost/ 100.0);
            Logger.logReport("get_damage_adjusted_by_elemAttr: Result DMG [%s] | Damage [%s] | SLV [%d] | Adjust By Buff [%s] | Boost [%s]", damageByWT, oldDamage, SLV, 1.0, elemBoost / 100.0);
            // battle record tinhg
            oldDamage = damageByWT;
            damageByWT = CalcDamageHelper.getDamageAdjustedByChargedElemAttr(damageByWT, ms.getDamagedElemAttr(), ss, cd);
            Logger.logReport("get_damage_adjusted_by_charged_elemAttr: Result DMG [%s] | Damage [%s]", damageByWT, oldDamage);

            double assisted = CalcDamageHelper.getDamageAdjustedByAssistChargedElemAttr(damageByWT, ms.getDamagedElemAttr(), ss, cd);
            Logger.logReport("get_damage_adjusted_by_assist_charged_elemAttr: Result DMG [%s] | Damage [%s]", assisted, damageByWT);

            damageByWT += assisted;
            if (skillID != DragonKnight.SACRIFICE && skillID != ThiefMaster.ASSAULTER && skillID != Viper.DEMOLITION) {
                int pdR = ms.getPDD() + ms.getStatOption(MobStats.PDD);
                if (ms.getStatOption(MobStats.PDD) != 0 && ms.getStatReason(MobStats.PDD) == Page.Threaten && template.getTemplateID() / 10000 != 882) {
                    pdR = (int) (((double) ms.getPDD() / 100.0 + 1.0) * (double) ms.getStatOption(MobStats.PDD));
                }
                pdR = Math.max(Math.min(pdR, 100), 0);
                int impR = Math.min(psdIMPr + ignoreTargetDEF, 100);
                Logger.logReport("PDr [%d] IMPr [%d]", pdR, impR);
                damageByWT *= ((100.0 - (pdR * impR / -100 + pdR)) / 100.0);
                /*int mdR = Math.max(Math.min(ms.getPDD() + ms.getStatOption(MobStats.PDD), 100), 0);
                int impR = Math.min(psdIMPr + ignoreTargetDEF, 100);
                int mobTotalMDD = mdR * impR / -100 + mdR;
                damageByWT *= ((100.0 - mobTotalMDD) / 100.0);*/
            }

            criticalAttackParam[1] = 0;
            if (skill != null) {
                criticalAttackParam[1] = skill.getLevelData(SLV).Damage;
                if (skill.getSkillID() == Mechanic.SG88) {
                    criticalAttackParam[1] = skill.getLevelData(SLV).Y;
                }
            }
            if (skillID == Sniper.STRAFE) {
                tempSkill = new Pointer<>();
                tempSLV = SkillInfo.getInstance().getSkillLevel(cd, CrossbowMaster.ULTIMATE_STRAFE, tempSkill);
                if (tempSLV != 0 && tempSkill.get() != null) {
                    criticalAttackParam[1] = tempSkill.get().getLevelData(tempSLV).Damage;
                }
            }
            if (skillID == WildHunter.FLASH_RAIN && i == damagePerMob - 1) {
                criticalAttackParam[1] = skill.getLevelData(SLV).X;
            }
            tempSkill = new Pointer<>();
            tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Buccaneer.INFIGHTING_MASTERY, tempSkill);
            if (tempSLV != 0 && tempSkill.get() != null) {
                switch (skillID) {
                    case InFighter.BACKSPIN_BLOW:
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).X;
                        break;
                    case InFighter.DOUBLE_UPPER:
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).Y;
                        break;
                    case InFighter.SCREW_PUNCH:
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).Z;
                        break;
                }
            }
            int comboDamageParam = SkillAccessor.getComboDamageParam(cd, skillID, ss.getStatOption(CharacterTemporaryStat.ComboCounter) - 1);
            if (skillID == Valkyrie.FIRE_BURNER || skillID == Valkyrie.COOLING_EFFECT) {
                tempSkill = new Pointer<>();
                tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Captain.PROPERTY_UPGRADE, tempSkill);
                if (tempSLV != 0 && tempSkill.get() != null) {
                    criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).Damage;
                }
            }
            if (criticalAttackParam[1] > 0) {
                criticalAttackParam[1] += ms.getStatOption(MobStats.RiseByToss);
            }
            if (skillID == Warrior.PowerStrike || skillID == Warrior.SlashBlast) {
                tempSkill = new Pointer<>();
                tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Fighter.ImproveBasic, tempSkill);
                if (tempSLV <= 0) {
                    tempSkill = new Pointer<>();
                    tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Page.ImproveBasic, tempSkill);
                    if (tempSLV <= 0) {
                        tempSkill = new Pointer<>();
                        tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Spearman.ImproveBasic, tempSkill);
                    }
                }
                Logger.logReport("Temp SLV [%d]| Skill [%s]", tempSLV, tempSkill.get() != null);
                if (tempSLV != 0 && tempSkill.get() != null) {
                    if (skillID == Warrior.PowerStrike && criticalAttackParam[1] > 0) {
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).X;
                    }
                    if (skillID == Warrior.SlashBlast && criticalAttackParam[1] > 0) {
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).Y;
                    }
                }
            }
            if (skillID == Archer.ArrowBlow || skillID == Archer.DoubleShot) {
                tempSkill = new Pointer<>();
                tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Hunter.ImproveBasic, tempSkill);
                if (tempSLV <= 0) {
                    tempSkill = new Pointer<>();
                    tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Crossbowman.ImproveBasic, tempSkill);
                }
                if (tempSLV != 0 && tempSkill.get() != null) {
                    if (skillID == Archer.ArrowBlow && criticalAttackParam[1] > 0) {
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).X;
                    }
                    if (skillID ==  Archer.DoubleShot && criticalAttackParam[1] > 0) {
                        criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).Y;
                    }
                }
            }
            if (skillID == ThiefMaster.MESO_EXPLOSION && JobAccessor.isCorrectJobForSkillRoot(job, 422)) {
                tempSkill = new Pointer<>();
                tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Shadower.GRID, tempSkill);
                if (tempSLV != 0 && tempSkill.get() != null) {
                    criticalAttackParam[1] += tempSkill.get().getLevelData(tempSLV).X;
                }
            }
            if (advancedChargeDamage != 0 && skillID == Knight.CHARGE_BLOW) {
                damageByWT *= advancedChargeDamage / 100.0;
            } else if (criticalAttackParam[1] > 0) {
                Logger.logReport("Damage Before[%s]", damageByWT);
                damageByWT = criticalAttackParam[1] / 100.0 * damageByWT;
                Logger.logReport("Damage After [%s]", damageByWT);
            }
            if (comboDamageParam > 0) {
                Logger.logReport("Combo param = [%d]", comboDamageParam);
                damageByWT *= comboDamageParam / 100.0;
            }
            if (shadowPartner) {
                shadowPartnerDamage.set(i, (int) damageByWT);
            }
            int enrage = ss.getStatOption(CharacterTemporaryStat.Enrage);
            if (enrage != 0) {
                damageByWT *= (enrage / 100 + 100) / 100.0;
            }

            double rand;
            Logger.logReport("Prop = [%d]", criticalAttackProp);
            if ((skillID != Shadower.ASSASSINATION || action == CharacterActions.ASSASSINATIONS) && nextAttackCritical || criticalAttackProp > 0 && (rand = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], 0.0, 100.0)) <= criticalAttackProp) {
                int param = criticalAttackParam[0] + psdCDMin + 20;
                param = Math.min(param, 50 + sharpEyesParam);

                rand = CalcDamageHelper.get_rand(random[idx++ % RND_SIZE], param,50 + sharpEyesParam);
                critical.set(i, true);
                damageByWT += (int) rand / 100.0 * (int) damageByWT;
            }
            Logger.logReport("Damage after critical adjust [%s]", damageByWT);
            if (skillID != DragonKnight.SACRIFICE && ms.getStatOption(MobStats.PGuardUp) != 0) {
                damageByWT *= ms.getStatOption(MobStats.PGuardUp) / 100.0;
            }
            if (ss.getStatOption(CharacterTemporaryStat.ShadowPartner) != 0 && shadowPartner) {
                int reason = ss.getStatReason(CharacterTemporaryStat.ShadowPartner);
                if (reason == Dual4.MIRROR_IMAGING) {
                    if (i >= attackCount && !SkillAccessor.isMirrorExceptedSkill(skillID)) {
                        int lvl = SkillInfo.getInstance().getSkillLevel(cd, Dual4.MIRROR_IMAGING);
                        int shadowDmg = Math.max(shadowPartnerDamage.get(i - attackCount), 1);
                        damage.set(i ,shadowDmg * shadowPartnerSkill.get().getLevelData(lvl).X / 100);
                        critical.set(i, false);
                        continue;
                    }
                } else {
                    if (skillID != NightLord.SHOWDOWN && skillID != Shadower.SHOWDOWN && i >= damagePerMob) {
                        int index = i - damagePerMob / 2;
                        if (skill != null) {
                            damageByWT = (int) (damage.get(index) * shadowPartnerSkill.get().getLevelData(spSLV).Y);
                        } else {
                            damageByWT = (int) (damage.get(index) * shadowPartnerSkill.get().getLevelData(spSLV).X);
                        }
                        critical.set(i, critical.get(index));
                    }
                }
            }
            if (skillID == Shadower.ASSASSINATION) {
                //TODO
            }
            if (ss.getStatOption(CharacterTemporaryStat.WindWalk) > 0) {
                // TODO
            }
            if (ss.getStatOption(CharacterTemporaryStat.DarkSight) > 0) {
                // TODO
            }
            if (ms.getStatOption(MobStats.Stun) > 0 || ms.getStatOption(MobStats.Blind) > 0) {
                tempSkill = new Pointer<>();
                tempSLV = SkillInfo.getInstance().getSkillLevel(cd, Crusader.ChanceAttack, tempSkill);
                if (tempSLV > 0) {
                    damageByWT *= tempSkill.get().getLevelData(tempSLV).Damage / 100.0;
                }
            }
            // TODO: monster handicap 9000-9002
            // TODO: mob category stuff
            // TODO: additional boss damage
            if (keyDown != 0) {
                damageByWT *= (90 * keyDown / SkillAccessor.getMaxGaugeTime(skillID) + 10) / 100.0;
            }
            if (ms.getStatOption(MobStats.HardSkin) == 0 || critical.get(i)) {
                if (darkForce != 0) {
                    damageByWT *= (double) (darkForce + 100) / 100.0;
                }
                //damageByWT = GuidedBulletStat.applyGuidedBulletDamage(cd, ss, mobID, damageByWT);

                if (ss.getStatOption(CharacterTemporaryStat.DojangBerserk) != 0) {
                    damageByWT *= (double)ss.getStatOption(CharacterTemporaryStat.DojangBerserk) / 100.0;
                }
                boolean dualAddDamageExcept = SkillAccessor.isDualAddDamageExceptSkill(skillID);
                if (ss.getStatOption(CharacterTemporaryStat.SuddenDeath) != 0 && !dualAddDamageExcept) {
                    damageByWT *= (double)ss.getStatOption(CharacterTemporaryStat.SuddenDeath) / 100.0;
                }
                if (ss.getStatOption(CharacterTemporaryStat.FinalCut) != 0 && !dualAddDamageExcept) {
                    damageByWT *= (double)ss.getStatOption(CharacterTemporaryStat.FinalCut) / 100.0;
                }
                if (AR01Pad != 0) {
                    damageByWT *= (double)(AR01Pad + 100) / 100.0;
                }
                if (SkillAccessor.isJaguarMeleeAttackSkill(skillID)) {
                    tempSkill = new Pointer<>();
                    tempSLV = SkillInfo.getInstance().getSkillLevel(cd, WildHunter.RIDING_MASTERY, tempSkill);
                    if (tempSLV > 0) {
                        damageByWT *= (double) (tempSkill.get().getLevelData(tempSLV).Damage + 100) / 100.0;
                    }
                }
                if (skillID == Hermit.SHADOW_MESO) {
                    // TODO
                }
                // deadly attack ?
                if (!template.isBoss() && bossDAMr > 0) {
                    bossDAMr = 0;
                }
                int damageRate = totalDAMr + psdDIPr + bossDAMr;
                if (ss.getStatOption(CharacterTemporaryStat.DamR) > 0) {
                    damageRate += ss.getStatOption(CharacterTemporaryStat.DamR);
                }
                Logger.logReport("Total Damage Rate [%d]", damageRate);
                damageByWT += (double)damageRate * damageByWT / 100.0;
                damageByWT = Math.min(Math.max(damageByWT, 1), Integer.MAX_VALUE);
                damage.set(i, (int) damageByWT);
                if (ms.getStatOption(MobStats.HealByDamage) != 0) {
                    damage.set(i, (int) damageByWT * ms.getStatOption(MobStats.HealByDamage) / - 100);
                }
            } else {
                damage.set(i, 0);
            }
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
}
