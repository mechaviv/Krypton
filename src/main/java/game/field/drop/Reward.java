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

import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import game.user.item.BundleItem;
import game.user.item.EquipItem;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.user.quest.QuestDemand;
import game.user.quest.QuestMan;
import game.user.quest.info.QuestItemInfo;
import util.FileTime;
import util.Rand32;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class Reward {
    private byte type;
    private ItemSlotBase item;
    private int money;
    private int period;
    private RewardInfo info;
    
    public Reward() {
        this(RewardType.MONEY, null, 0, 0);
    }
    
    public Reward(byte type, ItemSlotBase item, int money, int period) {
        this.type = type;
        this.item = item;
        this.money = money;
        this.period = period;
    }
    
    public byte getType() {
        return type;
    }
    
    public ItemSlotBase getItem() {
        return item;
    }
    
    public int getMoney() {
        return money;
    }
    
    public int getPeriod() {
        return period;
    }
    
    public RewardInfo getInfo() {
        return info;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setItem(ItemSlotBase item) {
        this.item = item;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setInfo(RewardInfo info) {
        this.info = info;
    }
    
    public static List<Reward> create(List<RewardInfo> rewardInfo, boolean premiumMap, int ownerDropRate, int ownerDropRate_Ticket, boolean test) {
        final float INC_DROP_RATE = 3.0f; //fIncDropRate, default 1
        final float INC_DROP_RATE_WSE = 1.0f;
        final float REWARD_RATE = 1.0f; //dRewardRate, always 1 (used for monster carnival which doesn't exist)

        List<Reward> rewards = new ArrayList<>();
        for (RewardInfo info : rewardInfo) {
            int itemRate = 1;
            int moneyRate = 1;
            if (info.getType() == RewardType.MONEY) {
                moneyRate = ownerDropRate;
            } else {
                if (info.getType() == RewardType.ITEM)
                    itemRate = ownerDropRate_Ticket;
            }
            int minProb = (int) (long) (1000000000.0d / INC_DROP_RATE * INC_DROP_RATE_WSE / (double) ownerDropRate / (double) itemRate / REWARD_RATE);
            int maxProb = 1000000000;
            if (minProb > 0) {
                maxProb = Math.abs(Rand32.getInstance().random()) % minProb;
            }
            if (test) maxProb = 1;
            if (maxProb < info.getProb()) {
                Reward reward = new Reward();
                if (!info.isPremiumMap() || premiumMap) {
                    reward.setType(info.getType());
                    reward.setInfo(info);
                    if (info.getType() == RewardType.MONEY) {
                        int min = 2 * info.getMoney() / 5 + 1;
                        int max = 4 * info.getMoney() / 5;
                        int rand = 1;
                        if (min > 0) {
                            rand = Math.max(1, max + Math.abs(Rand32.getInstance().random()) % min);
                        }
                        reward.setMoney(moneyRate * rand);
                        rewards.add(reward);
                    } else {
                        if (reward.getType() == RewardType.ITEM) {
                            ItemSlotBase item = ItemInfo.getItemSlot(info.getItemId(), ItemVariationOption.Normal);
                            if (item != null) {
                                if (info.getPeriod() > 0) {
                                    //item.setDateExpire(ItemAccessor.getDateExpireFromPeriod(info.getPeriod()));
                                } else {
                                    //item.setDateExpire(info.getDateExpire());
                                }
                                if (ItemAccessor.isBundleTypeIndex(ItemAccessor.getItemTypeIndexFromID(info.getItemId()))) {
                                    int rand = Math.min(info.getMax() - info.getMin() + 1, 1);
                                    if (rand > 1)
                                        rand = info.getMin() + Math.abs(Rand32.getInstance().random()) % rand;
                                    item.setItemNumber(rand);
                                }
                                reward.setItem(item);
                                rewards.add(reward);
                            }
                        }
                    }
                }
            }
        }
        Collections.shuffle(rewards);
        return rewards;
    }

    public static int loadReward(WzProperty reward, List<RewardInfo> rewardInfos) {
        if (reward == null) {
            return -1;
        }
        int count = reward.getChildNodes().size();
        if (count <= 0) {
            return -1;
        }
        for (int i = 0; i < count; i++) {
            RewardInfo rewardInfo = new RewardInfo();
            WzProperty rewardData = reward.getNode("" + i);

            int money = WzUtil.getInt32(rewardData.getNode("money"), 0);
            if (money == 0) {
                rewardInfo.setType(RewardType.ITEM);
                int itemID = WzUtil.getInt32(rewardData.getNode("item"), 0);
                rewardInfo.setItemId(itemID);
                rewardInfo.setMin(WzUtil.getInt32(rewardData.getNode("min"), 1));
                rewardInfo.setMax(WzUtil.getInt32(rewardData.getNode("max"), 1));
                rewardInfo.setPeriod(WzUtil.getInt32(rewardData.getNode("period"), 0));

                int dateExpire = WzUtil.getInt32(rewardData.getNode("dateExpire"), 0);
                rewardInfo.setDateExpire(ItemInfo.getItemDateExpire(dateExpire != 0 ? "" + dateExpire : null));

                List<Integer> quests = QuestMan.getInstance().getQuestsByItem(itemID);
                if (quests != null) {
                    for (Integer quest : quests) {
                        rewardInfo.getQrKey().add(quest);
                        QuestDemand demand = QuestMan.getInstance().getCompleteDemand(quest);

                        for (QuestItemInfo questItemInfo : demand.getDemandItem()) {
                            if (questItemInfo.getItemID() == itemID) {
                                rewardInfo.setMaxCount(questItemInfo.getCount());
                            }
                        }
                    }
                }

                int maxPerSlot = 1;
                boolean timeLimited = false;
                BundleItem bundleItem = ItemInfo.getBundleItem(itemID);
                if (bundleItem != null) {
                    maxPerSlot = bundleItem.getSlotMax();
                    timeLimited = bundleItem.isTimeLimited();
                } else {
                    EquipItem equipItem = ItemInfo.getEquipItem(itemID);
                    if (equipItem != null) {
                        timeLimited = equipItem.isTimeLimited();
                    }
                }
                if (FileTime.compareFileTime(rewardInfo.getDateExpire(), FileTime.DATE_2079) < 0 && rewardInfo.getPeriod() != 0) {
                    return 1;
                }

                if (timeLimited != (FileTime.compareFileTime(rewardInfo.getDateExpire(), FileTime.DATE_2079) < 0 || rewardInfo.getPeriod() != 0)) {
                    //  <int name="timeLimited" value="1" />
                    //            <int name="slotMax" value="1" />
                    return 2;
                }
                if (rewardInfo.getMin() <= 0 || rewardInfo.getMax() > maxPerSlot) {
                    return 3;
                }
                if (ItemInfo.isCashItem(itemID)) {
                    return 4;
                }
                if (ItemInfo.getItemSlot(itemID, ItemVariationOption.None) == null) {
                    return 0;// not being added to list
                }
            } else {
                rewardInfo.setType(RewardType.MONEY);
                rewardInfo.setMoney(money);
            }

            String strProb = WzUtil.getString(rewardData.getNode("prob"), "");
            double tempProb = 0.0;
            if (strProb.substring(0, 4).equals("[R8]")) {
                tempProb = Double.parseDouble(strProb.substring(4));
                if (tempProb > 1.0 || tempProb < 0.0) {
                    return 6;
                }
            }
            int prob = (int) (tempProb * 1000000000.0);
            prob = Math.max(prob, 0);
            prob = Math.min(prob, 1000000000);
            rewardInfo.setProb(prob);

            rewardInfo.setPremiumMap(WzUtil.getInt32(rewardData.getNode("premium"), 0) != 0);

            rewardInfos.add(rewardInfo);
        }
        return 0;// success
    }
}
