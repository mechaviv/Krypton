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
package game.user.skill;

import common.JobAccessor;
import common.JobCategory;
import common.item.ItemAccessor;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import game.user.skill.Skills.*;
import game.user.skill.entries.SkillEntry;
import game.user.stat.BasicStat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class SkillAccessor {
    public static final int
            // The maximum damage that can be hit upon a monster from our client
            MAX_CLIENT_DAMAGE   = 9999,
            
            // The maximum per stat that a player can obtain and/or use.
            STR_MAX             = 999,
            DEX_MAX             = 999,
            INT_MAX             = 999,
            LUK_MAX             = 999,
            PAD_MAX             = 999,
            PDD_MAX             = 999,
            MAD_MAX             = 999,
            MDD_MAX             = 999,
            ACC_MAX             = 999,
            EVA_MAX             = 999,
            SPEED_MAX           = 130,
            JUMP_MAX            = 123,
            HP_MAX              = 30000,
            MP_MAX              = 10000,
            POP_MAX             = 30000,//Unconfirmed
            AP_MAX              = 255,//Unconfirmed
            SP_MAX              = 255//Unconfirmed
    ;
    /**
     * The formula that controls the randomized ranges between a HP/MP increase.
     * 
     * The formulation of the array is as follows:
     * [Job Category][Level Up][Inc Val]
     * 
     * Where IncVal arrays are as follows:
     * [Min HP Inc, MaxHP Inc, HP Inc Modulo Rand] [Min MP Inc, Max MP Inc, MP Inc Modulo Rand]
     * 
     * The numbers will always follow the same sequence for each job.
     */
    static final int[][][] INC_HP_MP = { // [5][2][24]
        { {15, 20, 0, 10, 12, 20}, {10, 15, 0, 6, 8, 15} },//Beginner
        { {27, 35, 0, 4, 6, 20}, {20, 25, 0, 2, 4, 15} },//Warrior
        { {10, 15, 0, 22, 24, 20}, {5, 10, 0, 18, 20, 15} },//Magician
        { {22, 27, 0, 14, 16, 20}, {15, 20, 0, 10, 12, 15} },//Bowman
        { {22, 27, 0, 14, 16, 20}, {15, 20, 0, 10, 12, 15} },//Thief
    };

    public static List<Integer> getSkillRootFromJob(int job, List<Integer> a) {
        if (JobAccessor.findJob(job) != null) {
            int jobType = job % 1000 / 100;
            if (jobType > 0) {
                int jobCode = 100 * (jobType + 10 * (job / 1000));
                a.add(jobCode);

                int jobCat = job % 100 / 10;
                if (jobCat > 0) {
                    int jobCodeCat = jobCode + 10 * jobCat;
                    a.add(jobCodeCat);

                    for (int i = 1; i <= 8; i++) {
                        if (job % 10 < i) {
                            break;
                        }
                        jobCodeCat++;
                        a.add(jobCodeCat);
                    }
                }
            }
        }
        return a;
    }

    public static int getEndureDuration(CharacterData cd) {
        int jobCategory = JobAccessor.getJobCategory(cd.getCharacterStat().getJob());
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (jobCategory == JobCategory.FIGHTER) {
            int slv = SkillInfo.getInstance().getSkillLevel(cd, Warrior.Endure, skillEntry);
            if (slv > 0) {
                return 1000 * skillEntry.get().getLevelData(slv).Time;
            }
        } else if (jobCategory == JobCategory.THIEF) {
            if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.ASSASSIN.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Assassin.Endure, skillEntry);
                if (slv > 0) {
                    return 1000 * skillEntry.get().getLevelData(slv).Time;
                }
            } else if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.THIEF.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Thief.Endure, skillEntry);
                if (slv > 0) {
                    return 1000 * skillEntry.get().getLevelData(slv).Time;
                }
            }
        }
        return 0;
    }
    
    public static int getHPRecoveryUpgrade(CharacterData cd) {
        int jobCategory = JobAccessor.getJobCategory(cd.getCharacterStat().getJob());
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (jobCategory == JobCategory.FIGHTER) {
            int slv = SkillInfo.getInstance().getSkillLevel(cd, Warrior.ImproveBasic, skillEntry);
            if (slv > 0) {
                return skillEntry.get().getLevelData(slv).HP;
            }
        } else if (jobCategory == JobCategory.THIEF) {
            if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.ASSASSIN.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Assassin.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).HP;
                }
            } else if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.THIEF.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Thief.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).HP;
                }
            }
        }
        return 0;
    }
    
    public static int getMPRecoveryUpgrade(CharacterData cd) {
        int jobCategory = JobAccessor.getJobCategory(cd.getCharacterStat().getJob());
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (jobCategory == JobCategory.WIZARD) {
            return (int) ((double) SkillInfo.getInstance().getSkillLevel(cd, Magician.ImproveBasic, null) * (double) cd.getCharacterStat().getLevel() * 0.1d);
        } else if (jobCategory == JobCategory.THIEF) {
            if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.ASSASSIN.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Assassin.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).MP;
                }
            } else if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.THIEF.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Thief.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).MP;
                }
            }
        }
        return 0;
    }
    
    public static boolean isCorrectItemForBooster(int weaponType, int job) {
        switch (JobAccessor.findJob(job)) {
            case FIGHTER:
                return weaponType == 30 || weaponType == 31 || weaponType == 40 || weaponType == 41;
            case Page:
                return weaponType == 30 || weaponType == 32 || weaponType == 40 || weaponType == 42;
            case Spearman:
                return weaponType == 43 || weaponType == 44;
            case WIZARD_FIRE_POISON:
            case WIZARD_THUNDER_COLD:
            case CLERIC:
                return weaponType == 37 || weaponType == 38;
            case HUNTER:
                return weaponType == 45;
            case CROSSBOWMAN:
                return weaponType == 46;
            case ASSASSIN:
                return weaponType == 47;
            case THIEF:
                return weaponType == 33;
            default: {
                return false;
            }
        }
    }
    
    public static int decHPVal(int job) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.NONE:
                val = 12;
                break;
            case JobCategory.FIGHTER:
                val = 54;
                break;
            case JobCategory.WIZARD:
                val = 10;
                break;
            case JobCategory.ARCHER:
            case JobCategory.THIEF:
                val = 20;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static int decMPVal(int job, int INT) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.NONE:
                val = 8;
                break;
            case JobCategory.FIGHTER:
                val = 4;
                break;
            case JobCategory.WIZARD:
                val = -30 - 3 * INT / 40;
                break;
            case JobCategory.ARCHER:
            case JobCategory.THIEF:
                val = 12;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static int incHPVal(int job) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.NONE:
                val = 8;
                break;
            case JobCategory.FIGHTER:
                val = 20;
                break;
            case JobCategory.WIZARD:
                val = 6;
                break;
            case JobCategory.ARCHER:
            case JobCategory.THIEF:
                val = 16;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static int incMPVal(int job) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.NONE:
                val = 6;
                break;
            case JobCategory.FIGHTER:
                val = 2;
                break;
            case JobCategory.WIZARD:
                val = 18;
                break;
            case JobCategory.ARCHER:
            case JobCategory.THIEF:
                val = 10;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static boolean incMaxHPMP(CharacterData cd, BasicStat bs, int flag, boolean levelUp) {
        int hpInc = 0;
        int mpInc = 0;
        short job = bs.getJob();
        int jobCategory = job / 100;
        boolean inc = false;
        boolean incHP = (flag & CharacterStatType.MHP) != 0;
        boolean incMP = (flag & CharacterStatType.MMP) != 0;
        if (jobCategory < 0 || jobCategory > 4) {
            return inc;
        }
        if (JobAccessor.findJob(job) != null) {
            int minHP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][0];
            int maxHP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][1];
            int randHP = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][2];//Useless, always 0 and Nexon only has a MP rand for INT.
            int minMP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][3];
            int maxMP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][4];
            int randMP = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][5];
            if (incHP) {//Nexon uses the C++ engine RNG, we will use the JVM's RNG.
                hpInc = minHP + ThreadLocalRandom.current().nextInt(Short.MAX_VALUE) % (maxHP - minHP + 1);
            }
            if (incMP) {
                mpInc = ThreadLocalRandom.current().nextInt(Short.MAX_VALUE) % (maxMP - minMP + 1) + minMP + bs.getINT() * randMP / 200;
            }
            if (incHP) {
                int skillID = Warrior.MHPInc;
                SkillEntry hpIncSkill = SkillInfo.getInstance().getSkill(skillID);
                if (cd.getSkillRecord().containsKey(skillID)) {
                    int skillLevel;
                    if ((skillLevel = cd.getSkillRecord().get(skillID)) > 0 && hpIncSkill != null) {
                        if (skillLevel >= hpIncSkill.getMaxLevel())
                            skillLevel = hpIncSkill.getMaxLevel();
                        if (levelUp)
                            hpInc += hpIncSkill.getLevelData(skillLevel).X;
                        else
                            hpInc += hpIncSkill.getLevelData(skillLevel).Y;
                    }
                }
            }
            if (incMP) {
                int skillID = Magician.MMPInc;
                SkillEntry mpIncSkill = SkillInfo.getInstance().getSkill(skillID);
                if (cd.getSkillRecord().containsKey(skillID)) {
                    int skillLevel;
                    if ((skillLevel = cd.getSkillRecord().get(skillID)) > 0 && mpIncSkill != null) {
                        if (skillLevel >= mpIncSkill.getMaxLevel())
                            skillLevel = mpIncSkill.getMaxLevel();
                        if (levelUp)
                            mpInc += mpIncSkill.getLevelData(skillLevel).X;
                        else
                            mpInc += mpIncSkill.getLevelData(skillLevel).Y;
                    }
                }
            }
            if (incHP) {
                if (cd.getCharacterStat().getMHP() < HP_MAX) {
                    cd.getCharacterStat().setMHP((short) Math.min(Math.max(hpInc + cd.getCharacterStat().getMHP(), 50), HP_MAX));
                    inc = true;
                }
            }
            if (incMP) {
                if (cd.getCharacterStat().getMMP() < MP_MAX) {
                    cd.getCharacterStat().setMMP((short) Math.min(Math.max(mpInc + cd.getCharacterStat().getMMP(), 5), MP_MAX));
                    inc = true;
                }
            }
        }
        return inc;
    }
    
    public static boolean isTeleportSkill(int skillID) {
        final int[] skills = { Wizard1.Teleport, Wizard2.Teleport, Cleric.Teleport };
        
        for (int skill : skills) {
            if (skillID == skill)
                return true;
        }
        return false;
    }
    
    public static int getTeleportSkillLevel(CharacterData cd) {
        int skill = 0;
        switch (JobAccessor.findJob(cd.getCharacterStat().getJob())) {
            case WIZARD_FIRE_POISON:
                skill = Wizard1.Teleport;
                break;
            case WIZARD_THUNDER_COLD:
                skill = Wizard2.Teleport;
                break;
            case CLERIC:
                skill = Cleric.Teleport;
                break;
        }
        
        return SkillInfo.getInstance().getSkillLevel(cd, skill, null);
    }
    
    public static int getMPStealSkillData(CharacterData cd, int attackType, Pointer<Integer> prop, Pointer<Integer> percent) {
        prop.set(0);
        percent.set(0);
        if (attackType != 3)
            return 0;
        
        int skill = 0;
        switch (JobAccessor.findJob(cd.getCharacterStat().getJob())) {
            case WIZARD_FIRE_POISON:
                skill = Wizard1.MPEater;
                break;
            case WIZARD_THUNDER_COLD:
                skill = Wizard2.MPEater;
                break;
            case CLERIC:
                skill = Cleric.MPEater;
                break;
        }
        
        if (skill != 0) {
            Pointer<SkillEntry> skillEntry = new Pointer<>(null);
            int slv = SkillInfo.getInstance().getSkillLevel(cd, skill, skillEntry);
            if (slv > 0) {
                if (skillEntry.get() != null) {
                    prop.set(skillEntry.get().getLevelData(slv).Prop);
                    percent.set(skillEntry.get().getLevelData(slv).X);
                }
                return skill;
            }
        }
        
        return 0;
    }
    
    public static int getMasteryFromSkill(CharacterData cd, int skillID, Pointer<Integer> inc) {
        int mastery = 0;
        
        SkillEntry skill = SkillInfo.getInstance().getSkill(skillID);
        if (skill != null) {
            int slv = SkillInfo.getInstance().getSkillLevel(cd, skillID, null);
            if (slv != 0) {
                if (inc != null) {
                    inc.set(skill.getLevelData(slv).X);
                }
                mastery = skill.getLevelData(slv).Mastery;
            }
        }
        
        return mastery;
    }

    
    public static int getWeaponMastery(CharacterData cd, int weaponItemID, int attackType, Pointer<Integer> accInc) {
        final int MELEE = 1, SHOOT = 2;
        
        int wt = ItemAccessor.getWeaponType(weaponItemID);
        int mastery = 0;
        switch (wt) {
            case 30: //OneHand_Sword
            case 40: //TowHand_Sword
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Fighter.WeaponMastery, accInc);
                    if (mastery == 0)
                        mastery = SkillAccessor.getMasteryFromSkill(cd, Page.WeaponMastery, accInc);
                }
                break;
            case 31: //OneHand_Axe
            case 41: //TowHand_Axe
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Fighter.WeaponMasteryEx, accInc);
                    if (mastery == 0)
                        mastery = SkillAccessor.getMasteryFromSkill(cd, Page.WeaponMasteryEx, accInc);
                }
                break;
            case 32: //OneHand_Mace
            case 42: //TowHand_Mace
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Page.WeaponMasteryEx, accInc);
                    if (mastery == 0)
                        mastery = SkillAccessor.getMasteryFromSkill(cd, Fighter.WeaponMasteryEx, accInc);
                }
                break;
            case 45: //Bow
                if (attackType == SHOOT) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Hunter.BowMastery, accInc);
                }
                break;
            case 46: //CrossBow
                if (attackType == SHOOT) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Crossbowman.CrossbowMastery, accInc);
                }
                break;
            case 47: //ThrowingGloves
                if (attackType == SHOOT) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Assassin.JavelinMastery, accInc);
                }
                break;
            case 33: //Dagger
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Thief.DaggerMastery, accInc);
                }
                break;
        }
        return mastery;
    }
    
    public static boolean isSelfStatChange(int skillID) {
        switch (skillID) {
            case Warrior.IronBody:
            case Fighter.PowerGuard:
            case Page.PowerGuard:
            case Magician.MagicGuard:
            case Magician.MagicArmor:
            case Cleric.Invincible:
            case Archer.Focus:
            case Hunter.SoulArrow_Bow:
            case Crossbowman.SoulArrow_Crossbow:
            case Rogue.DarkSight:
                return true;
            default: {
                return false;
            }
        }
    }
    
    public static boolean isMobStatChange(int skillID) {
        switch (skillID) {
            case Page.Threaten:
            case Wizard1.Slow:
            case Wizard2.Slow:
                return true;
            default: {
                return false;
            }
        }
    }
    
    public static boolean isWeaponBooster(int skillID) {
        switch (skillID) {
            case Fighter.WeaponBooster:
            case Fighter.WeaponBoosterEx:
            case Page.WeaponBooster:
            case Page.WeaponBoosterEx:
            case Spearman.WeaponBooster:
            case Spearman.WeaponBoosterEx:
            case Hunter.BowBooster:
            case Crossbowman.CrossbowBooster:
            case Assassin.JavelinBooster:
            case Thief.DaggerBooster:
                return true;
            default: {
                return false;
            }
        }
    }
    
    public static boolean isPartyStatChange(int skillID) {
        switch (skillID) {
            case Fighter.Fury:
            case Spearman.IronWall:
            case Spearman.HyperBody:
            case Wizard1.Meditation:
            case Wizard2.Meditation:
            case Cleric.Heal:
            case Cleric.Bless:
            case Assassin.Haste:
            case Thief.Haste:
                return true;
            default: {
                return false;
            }
        }
    }

    public static boolean isIgnoreMasterLevelForCommon(int skillID) {
        switch (skillID) {
            case Hero.CombatMastery:
            case Paladin.BLESSING_ARMOR:
            case DarkKnight.BEHOLDERS_REVENGE:
            case ArchMage1.MASTER_MAGIC:
            case ArchMage2.MASTER_MAGIC:
            case Bishop.MASTER_MAGIC:
            case Bowmaster.VENGEANCE:
            case Bowmaster.MARKMAN_SHIP:
            case CrossbowMaster.MARKMAN_SHIP:
            case CrossbowMaster.ULTIMATE_STRAFE:
            case NightLord.EXPERT_JAVELIN:
            case Shadower.GRID:
            case Viper.COUNTER_ATTACK:
            case Captain.COUNTER_ATTACK:
            case BMage.ENERGIZE:
            case WildHunter.WILD_INSTINCT:
                return true;
        }
        return false;
    }

    public static boolean isSkillNeedMasterLevel(int skillID) {
        if (isIgnoreMasterLevelForCommon(skillID)) {
            return false;
        }
        int job = skillID / 10000;
        int jc = job / 100;
        int jobLevel = JobAccessor.getJobLevel(job);

        if (jc == JobCategory.EVAN || job == JobAccessor.EVAN_JR.getJob()) {
            return jobLevel == 9 || jobLevel == 10 || skillID == Evan.MAGIC_GUARD || skillID == Evan.MAGIC_BOOSTER || skillID == Evan.MAGIC_CRITICAL;
        }
        if (job / 10 == JobCategory.DUAL) {
            return jobLevel == 4 || skillID == Dual2.SLASH_STORM || skillID == Dual3.HUSTLE_DASH || skillID == Dual4.MIRROR_IMAGING || skillID == Dual4.FLYING_ASSAULTER;
        }
        if (job == 100 * jc) {
            return false;
        }
        return job % 10 == 2;
    }

    public static boolean isKeyDownSkill(int skillID) {
        switch (skillID) {
            case ArchMage1.BIGBANG:
            case ArchMage2.BIGBANG:
            case Bishop.BIGBANG:
            case Bowmaster.STORM_ARROW:
            case CrossbowMaster.PIERCING:
            case Dual5.FINAL_CUT:
            case Dual5.MONSTER_BOMB:
            case InFighter.SCREW_PUNCH:
            case Gunslinger.THROWING_BOMB:
            case Captain.RAPID_FIRE:

            case WindBreaker.STORM_ARROW:
            case NightWalker.POISON_BOMB:
            case Striker.SCREW_PUNCH:

            case Evan.ICE_BREATH:
            case Evan.BREATH:

            case WildHunter.SWALLOW:
            case WildHunter.WILD_SHOOT:
            case Mechanic.FLAMETHROWER:
            case Mechanic.FLAMETHROWER_UP:
                return true;
        }
        return false;
    }

    public static boolean isShootSkillNotUsingShootingWeapon(int skillID) {
        switch (skillID) {
            case NightLord.SHOWDOWN:
            case Shadower.SHOWDOWN:
            case Viper.ENERGY_ORB:

            case SoulMaster.SOUL_BLADE:
            case Striker.SPARK:
            case Striker.SHARK_WAVE:

            case Aran.COMBO_SMASH:
            case Aran.COMBO_FENRIR:
            case Aran.COMBO_TEMPEST:

            case WildHunter.SWALLOW_DUMMY_ATTACK:
                return true;
        }
        return false;
    }

    public static boolean isShootSkillNotConsumingBullet(int skillID) {
        if (isShootSkillNotUsingShootingWeapon(skillID)) {
            return true;
        }
        switch (skillID) {
            case Hunter.PowerKnockback:
            case Crossbowman.PowerKnockback:
            case Hermit.SHADOW_MESO:

            case WindBreaker.STORM_BREAK:
            case NightWalker.VAMPIRE:

            case WildHunter.JAGUAR_NUCKBACK:
            case Mechanic.FLAMETHROWER:
            case Mechanic.FLAMETHROWER_UP:
            case Mechanic.ROCKET_PUNCH:
            case Mechanic.GATLING:
            case Mechanic.GATLING_UP:
            case Mechanic.SIEGE1:
            case Mechanic.SIEGE2:
            case Mechanic.SIEGE2_SPECIAL:
                return true;
        }
        return false;
    }

    public static boolean isFinalAttack(int skillID) {
        SkillEntry skillEntry = SkillInfo.getInstance().getSkill(skillID);
        if (skillEntry == null) {
            return false;
        }
        return false;//skillEntry.isFinalAttack();
    }
}
