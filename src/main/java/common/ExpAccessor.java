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

import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import game.user.skill.SkillAccessor;
import game.user.stat.BasicStat;
import util.Logger;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class ExpAccessor {
    public static final int MAX_LEVEL = 200;
    public static final int[] EXP_USER = new int[201];

    static {
        // NEXTLEVEL::NEXTLEVEL
        EXP_USER[1] = 15;
        EXP_USER[2] = 34;
        EXP_USER[3] = 57;
        EXP_USER[4] = 92;
        EXP_USER[5] = 135;
        EXP_USER[6] = 372;
        EXP_USER[7] = 560;
        EXP_USER[8] = 840;
        EXP_USER[9] = 1242;
        for (int i = 10; i <= 14; i++) EXP_USER[i] = EXP_USER[9];
        for (int i = 15; i <= 29; i++) EXP_USER[i] = (int) (EXP_USER[i - 1] * 1.2 + 0.5);
        for (int i = 30; i <= 34; i++) EXP_USER[i] = EXP_USER[29];
        for (int i = 35; i <= 39; i++) EXP_USER[i] = (int) (EXP_USER[i - 1] * 1.2 + 0.5);
        for (int i = 40; i <= 69; i++) EXP_USER[i] = (int) (EXP_USER[i - 1] * 1.08 + 0.5);
        for (int i = 70; i <= 74; i++) EXP_USER[i] = EXP_USER[69];
        for (int i = 75; i <= 119; i++) EXP_USER[i] = (int) (EXP_USER[i - 1] * 1.07 + 0.5);
        for (int i = 120; i <= 124; i++) EXP_USER[i] = EXP_USER[119];
        for (int i = 125; i <= 159; i++) EXP_USER[i] = (int) (EXP_USER[i - 1] * 1.07 + 0.5);
        for (int i = 160; i <= 199; i++) EXP_USER[i] = (int) (EXP_USER[i - 1] * 1.06 + 0.5);
        EXP_USER[200] = 0;
    }
    
    /**
     * Processes EXP gains and if requirements are met, will level up the user.
     * 
     * @param cd The user's active CharacterData
     * @param bs The user's active BasicStat
     * @param inc The amount of EXP gained
     * @param reachMaxLv After processing the level up, if the user has reached max level
     * @return If the user has physically leveled up.
     */
    public static boolean tryProcessLevelUp(CharacterData cd, BasicStat bs, int inc, Pointer<Boolean> reachMaxLv) {
        int level = cd.getCharacterStat().getLevel();
        int exp = inc + cd.getCharacterStat().getEXP();
        if (level >= MAX_LEVEL) {
            cd.getCharacterStat().setEXP(0);
            cd.getCharacterStat().setLevel(MAX_LEVEL);
            return false;
        }
        int reqEXP;
        if (level > MAX_LEVEL) {
            reqEXP = Integer.MAX_VALUE;
        } else {
            if (level < 1)
                level = 1;
            reqEXP = EXP_USER[level];
        }
        if (exp < reqEXP) {
            cd.getCharacterStat().setEXP(exp);
            return false;
        }
        int remainExp = exp - reqEXP;
        level++;
        cd.getCharacterStat().setLevel(level);
        if (level == MAX_LEVEL) {
            reachMaxLv.set(true);
        }
        if (level > MAX_LEVEL) {
            reqEXP = Integer.MAX_VALUE;
        } else {
            if (level < 1)
                level = 1;
            reqEXP = EXP_USER[level];
        }
        SkillAccessor.incMaxHPMP(cd, bs, CharacterStatType.MHP | CharacterStatType.MMP, true);
        if (remainExp < reqEXP) {
            exp = remainExp;
        } else {
            exp = reqEXP - 1;
            if (reqEXP - 1 <= 0)
                exp = 0;
        }
        cd.getCharacterStat().setEXP(exp);
        if (cd.getCharacterStat().getHP() > 0) {
            Logger.logReport("New HP [%d] | New MP [%d]", cd.getCharacterStat().getMHP(), cd.getCharacterStat().getMMP());
            cd.getCharacterStat().setHP(cd.getCharacterStat().getMHP());
            cd.getCharacterStat().setMP(cd.getCharacterStat().getMMP());
        }
        return true;
    }
    
    /**
     * Used to decrease a player's EXP if they die.
     * This function is only ever called if the user does not have a Safety Charm/etc.
     * 
     * @param c The user's active CharacterData
     * @param bs The user's active BasicStat
     * @param town If the user's current field is a Town map
     */
    public static void decreaseExp(CharacterData c, BasicStat bs, boolean town) {
        short job = c.getCharacterStat().getJob();
        int level = Math.max(1, c.getCharacterStat().getLevel());
        if (job != 0) {
            if (level >= MAX_LEVEL) {
                c.getCharacterStat().setEXP(0);
            } else {
                int exp = EXP_USER[level];
                
                double rate;
                if (town) {
                    rate = 0.01;
                } else {
                    if (JobAccessor.getJobCategory(job) == JobCategory.ARCHER)
                        rate = 0.08;
                    else
                        rate = 0.2;
                    rate /= (double) c.getCharacterStat().getLUK() + 0.05;
                }
                
                c.getCharacterStat().setEXP(Math.min(Math.max(0, c.getCharacterStat().getEXP() - (int) (exp * rate)), exp - 1));
            }
        }
    }
    
    /**
     * 
     * @param level The user's level
     * @return The total EXP needed for the user's level
     */
    public static int getEXP(int level) {
        if (level < 0 || level >= EXP_USER.length) {
            return Integer.MAX_VALUE;
        }
        return EXP_USER[level];
    }
    
    /**
     * Does the reverse. Instead of giving the EXP, it will find the player's level.
     * Note that this specifically only checks for Explorers.
     * 
     * @param exp The user's EXP
     * @return The user's current level determined from their EXP
     */
    public static int getLevel(final int exp) {
        int level = 0;
        while (exp >= EXP_USER[level]) {
            level++;
            if (level >= MAX_LEVEL) {
                return MAX_LEVEL;
            }
        }
        return level + 1;
    }
}
