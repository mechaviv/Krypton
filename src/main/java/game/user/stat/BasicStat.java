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

import common.item.ItemSlotBase;
import common.item.ItemSlotEquip;
import common.user.CharacterData;
import game.user.item.EquipItem;
import game.user.item.ItemInfo;
import game.user.item.ItemOptionInfo;
import game.user.item.ItemOptionLevelData;
import game.user.skill.SkillAccessor;
import util.Logger;

import java.util.List;

/**
 *
 * @author Eric
 */
public class BasicStat {
    private byte gender;
    private int level;
    private short job;
    private short STR, DEX, INT, LUK;
    private short pop;
    private int mhp, mmp;
    
    public void clear() {
        this.job = 0;
        this.level = 0;
        this.gender = 0;
        this.LUK = 0;
        this.INT = 0;
        this.DEX = 0;
        this.STR = 0;
        this.mmp = 0;
        this.mhp = 0;
        this.pop = 0;
    }
    
    public byte getGender() {
        return gender;
    }

    public int getLevel() {
        return level;
    }

    public short getJob() {
        return job;
    }

    public short getSTR() {
        return STR;
    }

    public short getDEX() {
        return DEX;
    }

    public short getINT() {
        return INT;
    }

    public short getLUK() {
        return LUK;
    }

    public short getPOP() {
        return pop;
    }

    public int getMHP() {
        return mhp;
    }

    public int getMMP() {
        return mmp;
    }
    
    public void setFrom(CharacterData c, List<ItemSlotBase> realEquip, List<ItemSlotBase> realEquip2, int maxHPIncRate, int maxMPIncRate, int basicStatInc, int maxHPInc, int maxMPInc, int swallowMaxMPIncRate, int conversionMaxHPIncRate, int morewildMaxHPIncRate, int pdsMHPr, int pdsMMPr, int jaguarRidingHPIncRate) {
        this.gender = c.getCharacterStat().getGender();
        this.level = c.getCharacterStat().getLevel();
        this.job = c.getCharacterStat().getJob();
        this.STR = c.getCharacterStat().getSTR();
        this.INT = c.getCharacterStat().getINT();
        this.DEX = c.getCharacterStat().getDEX();
        this.LUK = c.getCharacterStat().getLUK();
        this.pop = c.getCharacterStat().getPOP();
        this.mhp = c.getCharacterStat().getMHP();
        this.mmp = c.getCharacterStat().getMMP();

        int incMaxHPr = 0;
        int incMaxMPr = 0;
        BasicStatRateOption option = new BasicStatRateOption();
        ItemSlotEquip equip;
        for (ItemSlotBase item : realEquip) {
            // TODO: Add position checks
            if ((equip = (ItemSlotEquip) item) != null) {
                this.STR += equip.item.iSTR;
                this.INT += equip.item.iINT;
                this.DEX += equip.item.iDEX;
                this.LUK += equip.item.iLUK;
                this.mhp += equip.item.iMaxHP;
                this.mmp += equip.item.iMaxMP;

                EquipItem equipItem = ItemInfo.getEquipItem(equip.getItemID());
                if (equipItem != null) {
                    incMaxHPr += equipItem.getIncMaxHPr();
                    incMaxMPr += equipItem.getIncMaxMPr();

                    int level = equipItem.getReqLevel() / 10;
                    applyItemOption(equip.option.option1, level);
                    applyItemOption(equip.option.option2, level);
                    applyItemOption(equip.option.option3, level);
                    applyItemOptionR(equip.option.option1, level, option);
                    applyItemOptionR(equip.option.option2, level, option);
                    applyItemOptionR(equip.option.option3, level, option);
                }
            }
        }
        // dragon & mechanic here too
        // seti tems

        this.STR += basicStatInc * c.getCharacterStat().getSTR() / 100;
        this.DEX += basicStatInc * c.getCharacterStat().getDEX() / 100;
        this.INT += basicStatInc * c.getCharacterStat().getINT() / 100;
        this.LUK += basicStatInc * c.getCharacterStat().getLUK() / 100;
        this.mhp += maxHPInc;
        this.mmp += maxMPInc;

        this.STR += option.STRr * this.STR / 100;
        this.DEX += option.DEXr * this.DEX / 100;
        this.INT += option.INTr * this.INT / 100;
        this.LUK += option.LUKr * this.LUK / 100;

        int maxHPIncRateSkill = Math.max(conversionMaxHPIncRate, maxHPIncRate);
        maxHPIncRateSkill = Math.max(maxHPIncRateSkill, morewildMaxHPIncRate);

        this.mhp += (jaguarRidingHPIncRate + pdsMHPr + incMaxHPr + option.MHPr + maxHPIncRateSkill) * this.mhp / 100;
        this.mmp += (pdsMMPr + swallowMaxMPIncRate + maxMPIncRate + incMaxMPr + option.MMPr) * this.mmp / 100;

        this.mhp = Math.min(this.mhp, SkillAccessor.HP_MAX);
        this.mmp = Math.min(this.mmp, SkillAccessor.MP_MAX);
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
        this.STR += optData.incSTR;
        this.DEX += optData.incDEX;
        this.INT += optData.incINT;
        this.LUK += optData.incLUK;
        this.mhp += optData.incMaxHP;
        this.mmp += optData.incMaxMP;
    }

    public void applyItemOptionR(int itemOptionID, int level, BasicStatRateOption bOption) {
        ItemOptionInfo option = ItemOptionInfo.getItemOption(itemOptionID);
        if (option == null) {
            return;
        }
        ItemOptionLevelData optData = option.levelData.get(level);
        if (optData == null) {
            return;
        }
        bOption.STRr += optData.incSTRr;
        bOption.DEXr += optData.incDEXr;
        bOption.INTr += optData.incINTr;
        bOption.LUKr += optData.incLUKr;
        bOption.MHPr += optData.incMaxHPr;
        bOption.MMPr += optData.incMaxMPr;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public void setJob(short job) {
        this.job = job;
    }

    public void setSTR(short STR) {
        this.STR = STR;
    }

    public void setDEX(short DEX) {
        this.DEX = DEX;
    }

    public void setINT(short INT) {
        this.INT = INT;
    }

    public void setLUK(short LUK) {
        this.LUK = LUK;
    }

    public void setPOP(short pop) {
        this.pop = pop;
    }

    public void setMHP(int mhp) {
        this.mhp = mhp;
    }

    public void setMMP(int mmp) {
        this.mmp = mmp;
    }
}
