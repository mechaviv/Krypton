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
package game.field.drop;

import util.FileTime;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class RewardInfo {
    private byte type;
    private int money;
    private int itemId;
    private int prob;
    private int min;
    private int max;
    private int maxCount;
    private int period;
    private FileTime dateExpire;
    private List<Integer> qrKeys;
    private boolean premiumMap;

    public RewardInfo() {
        this.qrKeys = new ArrayList<>();
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public FileTime getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(FileTime dateExpire) {
        this.dateExpire = dateExpire;
    }

    public List<Integer> getQrKey() {
        return qrKeys;
    }

    public void setQrKey(List<Integer> qrKeys) {
        this.qrKeys = qrKeys;
    }

    public boolean isPremiumMap() {
        return premiumMap;
    }

    public void setPremiumMap(boolean premiumMap) {
        this.premiumMap = premiumMap;
    }
}
