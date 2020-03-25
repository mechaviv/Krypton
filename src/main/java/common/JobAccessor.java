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
package common;

/**
 *
 * @author Eric
 */
public enum JobAccessor {
    // Beginner
    NOVICE(0),

    // Warrior
    SWORDMAN(100),
    FIGHTER(110), CRUSADER(111), HERO(112),
    Page(120), KNIGHT(121), PALADIN(122),
    Spearman(130), DRAGON_KNIGHT(131), DARK_KNIGHT(132),

    // Magician
    MAGICIAN(200),
    WIZARD_FIRE_POISON(210), MAGE_FIRE_POISON(211), ARCHMAGE_FIRE_POISON(212),
    WIZARD_THUNDER_COLD(220), MAGE_THUNDER_COLD(221), ARCHMAGE_THUNDER_COLD(222),
    CLERIC(230), PRIEST(231), BISHOP(232),

    // Bowman
    ARCHER(300),
    HUNTER(310), RANGER(311), BOWMASTER(312),
    CROSSBOWMAN(320), SNIPER(321), CROSSBOWMASTER(322),

    // Thief
    ROGUE(400),
    ASSASSIN(410), HERMIT(411), NIGHT_LORD(412),
    THIEF(420), THIEF_MASTER(421), SHADOWER(422),
    DUAL_1(430), DUAL_2(431), DUAL_3(432), DUAL_4(433), DUAL_5(434),

    // Pirate
    PIRATE(500),
    INFIGHTER(510), BUCCANEER(511), VIPER(512),
    GUNSLINGER(520), VALKYRIE(521), CAPTAIN(522),

    // Manager
    MANAGER(800), ADMIN(900),
    ADMIN_SUPER_GM(910), ADMIN_USER_GM(920),

    // Cygnus
    NOBLESSE(1000),

    // Cygnus Warrior
    SOUL_FIGHTER_1(1100), SOUL_FIGHTER_2(1110), SOUL_FIGHTER_3(1111), SOUL_FIGHTER_4(1112),
    
    // Cygnus Mage
    FLAME_WIZARD_1(1200), FLAME_WIZARD_2(1210), FLAME_WIZARD_3(1211), FLAME_WIZARD_4(1212),
    
    // Cygnus Archer
    WIND_BREAKER_1(1300), WIND_BREAKER_2(1310), WIND_BREAKER_3(1311), WIND_BREAKER_4(1312),
    
    // Cygnus Thief
    NIGHT_WALKER_1(1400), NIGHT_WALKER_2(1410), NIGHT_WALKER_3(1411), NIGHT_WALKER_4(1412),
    
    // Cygnus Pirate
    STRIKER_1(1500), STRIKER_2(1510), STRIKER_3(1511), STRIKER_4(1512),


    // Heros
    LEGEND(2000), EVAN_JR(2001),
    ARAN_1(2100), ARAN_2(2110), ARAN_3(2111), ARAN_4(2112),
    EVAN_1(2200), EVAN_2(2210), EVAN_3(2211), EVAN_4(2212), EVAN_5(2213), EVAN_6(2214), EVAN_7(2215), EVAN_8(2216), EVAN_9(2217), EVAN_10(2218),

    // Resistances
    CITIZEN(3000),
    BMAGE_1(3200), BMAGE_2(3210), BMAGE_3(3211), BMAGE_4(3212),
    WILD_HUNTER_1(3300), WILD_HUNTER_2(3310), WILD_HUNTER_3(3311), WILD_HUNTER_4(3312),
    MECHANIC_1(3500), MECHANIC_2(3510), MECHANIC_3(3511), MECHANIC_4(3512)
    ;

    private final int job;
    
    private JobAccessor(int job) {
        this.job = job;
    }
    
    public short getJob() {
        return (short) job;
    }
    
    public boolean validate(JobAccessor job) {
        return getJob() >= job.getJob() && getJob() / 100 == job.getJob() / 100;
    }
    
    public static JobAccessor findJob(int jobID) {
        for (JobAccessor job : JobAccessor.values()) {
            if (job.getJob() == jobID) {
                return job;
            }
        }
        return null;
    }
    
    public static int getJobCategory(int jobCode) {
        return jobCode / 100;
    }
    
    public static int getJobChangeLevel(int job, int subJob, int step) {
        int prefix = job / 1000;
        if (prefix == 3 || job / 100 == 22 || job == 2001) {
            return prefix != 1 ? 200 : 120;
        }
        boolean dual_job_born = prefix == 0 && subJob == 1;
        switch (step) {
            case 1:
                return prefix != 0 || getJobCategory(job) != JobCategory.WIZARD ? 10 : 8;
            case 2:
                return dual_job_born ? 20 : 30;
            case 3:
                return dual_job_born ? 55 : 70;
            case 4:
                return 120;
        }
        return prefix != 1 ? 200 : 120;
    }
    
    public static boolean isCorrectJobForSkillRoot(int job, int skillRoot) {
        if ((skillRoot % 100) != 0) {
            return skillRoot / 10 == job / 10 && job % 10 >= skillRoot % 10;
        }
        return skillRoot / 100 == job / 100;
    }
    
    public static long getJobBitflag(int jobCode) {
        return 1L << (jobCode / 100);
    }

    public static int getJobLevel(int job) {
        if (job % 100 == 0 || job == 2001) {
            return 1;
        }
        int level = (job / 10 == 43 ? (job - 430) / 2 : job % 10) + 2;
        if (level >= 2 && (level <= 4 || level <= 10 && (job / 100 == 22 || job == 2001))) {
            return level;
        }
        return 0;
    }

    public static boolean isDualJobBorn(int job, int subJob) {
        return job / 1000 == 0 && subJob == 1;
    }

    public static boolean isBeginnerJob(int job) {
        return job % 1000 == 0 || job == 2001;
    }

    public static String getJobName(int job) {
        switch(job) {
            case 0:
                return "Beginner";
            case 100: // Warrior
                return "Warrior";
            case 110:
                return "Fighter";
            case 111:
                return "Crusader";
            case 112:
                return "Hero";
            case 120:
                return "Page";
            case 121:
                return "White Knight";
            case 122:
                return "Paladin";
            case 130:
                return "Spearman";
            case 131:
                return "Dragon Knight";
            case 132:
                return "Dark Knight";
            case 200: // Magician
                return "Magician";
            case 210:
                return "Wizard (Fire, Poison)";
            case 211:
                return "Mage (Fire, Poison)";
            case 212:
                return "Arch Mage (Fire, Poison)";
            case 220:
                return "Wizard (Ice, Lightninig)";
            case 221:
                return "Mage (Ice, Lightninig)";
            case 222:
                return "Arch Mage (Ice, Lightninig)";
            case 230:
                return "Cleric";
            case 231:
                return "Priest";
            case 232:
                return "Bishop";
            case 300: // Bowman
                return "Archer";
            case 310:
                return "Hunter";
            case 311:
                return "Ranger";
            case 312:
                return "Bowmaster";
            case 320:
                return "Crossbowman";
            case 321:
                return "Sniper";
            case 322:
                return "Marksman";
            case 400: // Thief
                return "Rogue";
            case 410:
                return "Assassin";
            case 411:
                return "Hermit";
            case 412:
                return "Night Lord";
            case 420:
                return "Bandit";
            case 421:
                return "Chief Bandit";
            case 422:
                return "Shadower";
            case 430:
                return "Blade Recruit";
            case 431:
                return "Blade Acolyte";
            case 432:
                return "Blade Specialist";
            case 433:
                return "Blade Lord";
            case 434:
                return "Blade Master";
            case 500:
                return "Pirate";
            case 510:
                return "Brawler";
            case 511:
                return "Marauder";
            case 512:
                return "Buccaneer";
            case 520:
                return "Gunslinger";
            case 521:
                return "Outlaw";
            case 522:
                return "Corsair";
            case 800:
                return "Manager";
            case 900:
                return "Admin (Normal)";
            case 910:
                return "Admin (Super GM)";
            case 920:
                return "Admin (User GM)";
            case 1000:
                return "Noblesse";
            case 1100:
                return "Dawn Warrior 1";
            case 1110:
                return "Dawn Warrior 2";
            case 1111:
                return "Dawn Warrior 3";
            case 1112:
                return "Dawn Warrior 4";
            case 1200:
                return "Blaze Wizard 1";
            case 1210:
                return "Blaze Wizard 2";
            case 1211:
                return "Blaze Wizard 3";
            case 1212:
                return "Blaze Wizard 4";
            case 1300:
                return "Wind Archer 1";
            case 1310:
                return "Wind Archer 2";
            case 1311:
                return "Wind Archer 3";
            case 1312:
                return "Wind Archer 4";
            case 1400:
                return "Night Walker 1";
            case 1410:
                return "Night Walker 2";
            case 1411:
                return "Night Walker 3";
            case 1412:
                return "Night Walker 4";
            case 1500:
                return "Thunder Breaker 1";
            case 1510:
                return "Thunder Breaker 2";
            case 1511:
                return "Thunder Breaker 3";
            case 1512:
                return "Thunder Breaker 4";
            case 2000:
                return "Legend";
            case 2100:
                return "Aran 1";
            case 2110:
                return "Aran 2";
            case 2111:
                return "Aran 3";
            case 2112:
                return "Aran 4";
            case 2001:
                return "Evan Jr";
            case 2200:
                return "Evan 1";
            case 2210:
                return "Evan 2";
            case 2211:
                return "Evan 3";
            case 2212:
                return "Evan 4";
            case 2213:
                return "Evan 5";
            case 2214:
                return "Evan 6";
            case 2215:
                return "Evan 7";
            case 2216:
                return "Evan 8";
            case 2217:
                return "Evan 9";
            case 2218:
                return "Evan 10";
            case 3000:
                return "Citizen";
            case 3200:
                return "Battle Mage 1";
            case 3210:
                return "Battle Mage 2";
            case 3211:
                return "Battle Mage 3";
            case 3212:
                return "Battle Mage 4";
            case 3300:
                return "Wild Hunter 1";
            case 3310:
                return "Wild Hunter 2";
            case 3311:
                return "Wild Hunter 3";
            case 3312:
                return "Wild Hunter 4";
            case 3500:
                return "Mechanic 1";
            case 3510:
                return "Mechanic 2";
            case 3511:
                return "Mechanic 3";
            case 3512:
                return "Mechanic 4";
            default: {
                return "Undefined";
            }
        }
    }
}
