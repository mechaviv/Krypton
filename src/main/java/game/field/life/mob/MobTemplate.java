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
package game.field.life.mob;

import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import game.field.life.MoveAbility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.user.User;
import game.user.quest.QuestDemand;
import game.user.quest.QuestMan;
import game.user.quest.UserQuestRecord;
import game.user.quest.info.QuestMobInfo;
import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzNodeType;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzSAXProperty;
import util.wz.WzUtil;
import util.wz.WzXML;

/**
 *
 * @author Eric
 */
public class MobTemplate implements WzXML {
    private static final Map<Integer, MobTemplate> templates = new HashMap<>();
    private static final Lock lockMob = new ReentrantLock();
    private int templateID;
    private String name;
    private boolean bodyAttack;
    private boolean notAttack;
    private byte moveAbility;
    private boolean boss;
    private byte level;
    private int maxHP;
    private int maxMP;
    private byte speed;
    private byte flySpeed;
    private byte chaseSpeed;
    private short pad;
    private short pdd;
    private short mad;
    private short mdd;
    private byte acc;
    private byte eva;
    private int exp;
    private int pushedDamage;
    private final List<Integer> damagedElemAttr;
    private short hpRecovery;
    private short mpRecovery;
    private boolean undead;
    private double fs;
    private int hpTagColor;
    private int hpTagBgColor;
    private boolean invincible;
    private boolean hasPublicDrop;
    private boolean hasExplosiveDrop;
    private int deadBuff;
    private int removeAfter;
    private boolean removeQuest;
    private boolean pickUpDrop;
    private boolean firstAttack;
    private int selfDestructionHP;
    private boolean damagedByMob;
    private boolean doNotRemove;
    private int dropItemPeriod;
    private int banType;
    private String banMsg;
    private MobSelfDestruction selfDestructionInfo;
    private int getCP;
    private boolean hpGaugeHide;
    private int fixedDamage;
    private boolean onlyNormalAttack;

    private final List<MobAttackInfo> attackInfo;
    private final List<MobSkillInfo> skillInfo;
    private final List<RewardInfo> rewardInfo;
    private final List<Integer> reviveTemplateIDs;
    private final List<MobBanMap> banMap;

    public MobTemplate() {
        this.damagedElemAttr = new ArrayList<>(AttackElem.Count);
        this.attackInfo = new ArrayList<>();
        this.skillInfo = new ArrayList<>();
        this.rewardInfo = new ArrayList<>();
        this.reviveTemplateIDs = new ArrayList<>();
        this.banMap = new ArrayList<>();
        for (int i = 0; i < AttackElem.Count; i++) {
            this.damagedElemAttr.add(i, AttackElemAttr.None);
        }
    }
    
    public static MobTemplate getMobTemplate(int templateID) {
        lockMob.lock();
        try {
            return templates.get(templateID);
        } finally {
            lockMob.unlock();
        }
    }
    
    public static void load(boolean useSAX) {
        Logger.logReport("Loading Mob Attributes");
        WzPackage mobDir = new WzFileSystem().init("Mob").getPackage();
        WzProperty reward = new WzFileSystem().init("Data").getPackage().getItem("Reward.img");
        if (mobDir != null) {
            if (useSAX) {
                for (WzSAXProperty mobData : mobDir.getSAXEntries().values()) {
                    int templateID = Integer.parseInt(mobData.getFileName().replaceAll(".img.xml", ""));
                    registerMob(templateID, mobData, reward.getNode(String.format("m%07d", templateID)));
                }
            } else {
                for (WzProperty mobData : mobDir.getEntries().values()) {
                    int templateID = Integer.parseInt(mobData.getNodeName().replaceAll(".img", ""));
                    registerMob(templateID, mobDir, mobData, reward.getNode(String.format("m%07d", templateID)));
                }
            }
            mobDir.release();
            reward.release();
        }
        mobDir = null;
        reward = null;
    }

    private static void registerMob(int templateID, WzPackage mobDir, WzProperty prop, WzProperty reward) {
        WzProperty info = prop.getNode("info");
        if (info == null) {
            return;
        }
        MobTemplate template = new MobTemplate();
        template.templateID = templateID;

        String link = WzUtil.getString(info.getNode("link"), null);
        if (link != null) {
            info = mobDir.getItem(link + ".img").getNode("info");
        }
        if (info == null) {
            Logger.logError("[Mob Template] No Info for [%d]", templateID);
        }
        if (templates.containsKey(templateID)) {
            Logger.logError("[Mob Template] Duplicated mob template [%d]", templateID);
            return;
        }
        boolean jump = prop.getNode("jump") != null;
        boolean move = prop.getNode("move") != null;
        boolean fly = prop.getNode("fly") != null;
        if (fly) {
            template.moveAbility = MoveAbility.Fly;
        } else {
            if (jump) {
                if (!move) {
                    template.moveAbility = MoveAbility.Jump;
                }
            } else {
                template.moveAbility = move ? MoveAbility.Walk : MoveAbility.Stop;
            }
        }
        template.name = WzUtil.getString(info.getNode("name"), "NULL");
        template.bodyAttack = WzUtil.getBoolean(info.getNode("bodyAttack"), false);
        template.notAttack = WzUtil.getBoolean(info.getNode("notAttack"), false);
        template.boss = WzUtil.getBoolean(info.getNode("boss"), false);
        template.level = WzUtil.getByte(info.getNode("level"), 1);
        template.maxHP = WzUtil.getInt32(info.getNode("maxHP"), 0);
        template.maxMP = WzUtil.getInt32(info.getNode("maxMP"), 0);
        template.speed = (byte) Math.min(140, Math.max(0, WzUtil.getByte(info.getNode("speed"), 0) + 100));
        template.flySpeed = (byte) Math.min(140, Math.max(0, WzUtil.getByte(info.getNode("flySpeed"), 0) + 100));
        template.chaseSpeed = (byte) Math.min(140, Math.max(0, WzUtil.getByte(info.getNode("chaseSpeed"), 0) + 100));

        template.pad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("PADamage"), 0)));
        template.pdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("PDDamage"), 0)));
        template.mad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("MADamage"), 0)));
        template.mdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("MDDamage"), 0)));
        template.acc = WzUtil.getByte(info.getNode("acc"), 0);
        template.eva = WzUtil.getByte(info.getNode("eva"), 0);
        template.exp = WzUtil.getInt32(info.getNode("exp"), 0);
        template.pushedDamage = WzUtil.getInt32(info.getNode("pushed"), 1);
        
        String elemAttr = WzUtil.getString(info.getNode("elemAttr"), null);
        if (elemAttr != null && !elemAttr.isEmpty()) {
            for (int i = 0; i < elemAttr.length(); i += 2) {
                int elem = AttackElem.getElementAttribute(elemAttr.charAt(i));
                int attr = elemAttr.charAt(i + 1) - '0';
                
                template.damagedElemAttr.set(elem, attr);
            }
        }
        
        template.hpRecovery = WzUtil.getShort(info.getNode("hpRecovery"), 0);
        template.mpRecovery = WzUtil.getShort(info.getNode("mpRecovery"), 0);
        template.undead = WzUtil.getBoolean(info.getNode("undead"), false);
        template.pickUpDrop = WzUtil.getBoolean(info.getNode("pickUp"), false);
        template.firstAttack = WzUtil.getBoolean(info.getNode("firstAttack"), false);
        template.selfDestructionHP = WzUtil.getInt32(info.getNode("selfDestructionHP"), -1);
        template.fs = WzUtil.getDouble(info.getNode("fs"), 1.0);
        template.hpTagColor = WzUtil.getInt32(info.getNode("hpTagColor"), 0);
        template.hpTagBgColor = WzUtil.getInt32(info.getNode("hpTagBgcolor"), 0);
        template.invincible = WzUtil.getBoolean(info.getNode("invincible"), false);
        template.hasPublicDrop = WzUtil.getBoolean(info.getNode("publicReward"), false);
        template.hasExplosiveDrop = WzUtil.getBoolean(info.getNode("explosiveReward"), false);
        template.deadBuff = WzUtil.getInt32(info.getNode("buff"), 0);
        template.removeAfter = WzUtil.getInt32(info.getNode("removeAfter"), 0);
        template.removeQuest = WzUtil.getBoolean(info.getNode("removeQuest"), false);
        template.damagedByMob = WzUtil.getBoolean(info.getNode("damagedByMob"), false);
        template.doNotRemove = WzUtil.getBoolean(info.getNode("doNotRemove"), false);
        template.dropItemPeriod = WzUtil.getInt32(info.getNode("dropItemPeriod"), 0);
        template.getCP = WzUtil.getInt32(info.getNode("getCP"), 0);
        template.fixedDamage = WzUtil.getInt32(info.getNode("fixedDamage"), 0);
        template.onlyNormalAttack = WzUtil.getBoolean(info.getNode("onlyNormalAttack"), false);
        template.doNotRemove = WzUtil.getBoolean(info.getNode("HPgaugeHide"), false);

        WzProperty selfDestruction = info.getNode("selfDestruction");
        if (selfDestruction != null) {
            template.selfDestructionInfo = new MobSelfDestruction();
            template.selfDestructionInfo.setActionType(WzUtil.getInt32(selfDestruction.getNode("action"), 0));
            template.selfDestructionInfo.setBearHP(WzUtil.getInt32(selfDestruction.getNode("hp"), -1));
            template.selfDestructionInfo.setFirstAttack( WzUtil.getBoolean(info.getNode("firstAttack"), false));
            template.selfDestructionInfo.setRemoveAfter(WzUtil.getInt32(selfDestruction.getNode("removeAfter"), 0));
        }

        WzProperty revives = info.getNode("revive");
        if (revives != null) {
            for (WzProperty revive : revives.getChildNodes()) {
                template.reviveTemplateIDs.add(WzUtil.getInt32(revive, 0));
            }
        }

        for (int i = 1; ; i++) {
            WzProperty attack = prop.getNode(String.format("attack%d", i));
            if (attack == null) {
                break;
            }
            WzProperty attackInfo = attack.getNode("info");
            if (attackInfo != null) {
                byte type = WzUtil.getByte(attackInfo.getNode("type"), 0);
                short conMP = WzUtil.getShort(attackInfo.getNode("conMP"), 0);
                boolean magic = WzUtil.getBoolean(attackInfo.getNode("magic"), false);
                
                template.attackInfo.add(new MobAttackInfo(type, conMP, magic));
            }
        }

        WzProperty skills = info.getNode("skill");
        if (skills != null) {
            for (WzProperty skill : skills.getChildNodes()) {
                MobSkillInfo mobSkillInfo = new MobSkillInfo();
                mobSkillInfo.setSkillID(WzUtil.getInt32(skill.getNode("skill"), 0));
                mobSkillInfo.setSlv(WzUtil.getInt32(skill.getNode("level"), 0));
                template.skillInfo.add(mobSkillInfo);
            }
        }

        WzProperty banPath = info.getNode("ban");
        if (banPath != null) {
            template.banType = WzUtil.getInt32(banPath.getNode("banType"), 0);
            template.banMsg = WzUtil.getString(banPath.getNode("banMsg"), null);

            WzProperty banMapPath = banPath.getNode("banMap");
            if (banMapPath != null) {
                for (int i = 0; ; i++) {
                    WzProperty banMap = banMapPath.getNode("" + i);
                    if (banMap == null) {
                        break;
                    }
                    MobBanMap mobBanMap = new MobBanMap();
                    mobBanMap.setFieldID(WzUtil.getInt32(banMap.getNode("field"), 0));
                    mobBanMap.setPortalName(WzUtil.getString(banMap.getNode("portal"), null));
                    template.banMap.add(mobBanMap);
                }
            }
        }
        Reward.loadReward(reward, template.rewardInfo);
        
        templates.put(templateID, template);
    }
    
    private static void registerMob(int templateID, WzSAXProperty prop, WzProperty reward) {
        MobTemplate template = new MobTemplate();
        template.templateID = templateID;
        
        prop.addEntity(template);
        prop.parse();

        Reward.loadReward(reward, template.rewardInfo);
        templates.put(templateID, template);
    }
    
    public static void unload() {
        templates.clear();
    }
    
    @Override
    public void parse(String root, String name, String value, WzNodeType type) {
        if (type.equals(WzNodeType.IMGDIR)) {
            switch (name) {
                case "fly":
                    this.moveAbility = MoveAbility.Fly;
                    break;
                case "jump":
                    this.moveAbility = MoveAbility.Jump;
                    break;
                case "move":
                    this.moveAbility = MoveAbility.Walk;
                    break;
                default: {
                    if (name.startsWith("attack") && !name.equals("attackF")) {
                        this.attackInfo.add(new MobAttackInfo());
                    }
                }
            }
        } else if (type.equals(WzNodeType.INT)) {
            if (!this.attackInfo.isEmpty()) {
                MobAttackInfo attack = this.attackInfo.get(this.attackInfo.size() - 1);
                switch (name) {
                    case "type":
                        attack.type = WzUtil.getByte(value, 0);
                        break;
                    case "conMP":
                        attack.conMP = WzUtil.getShort(value, 0);
                        break;
                    case "magic":
                        attack.magicAttack = WzUtil.getBoolean(value, false);
                        break;
                }
            }
        }
        switch (name) {
            case "name":
                this.name = value;
                if (this.name == null) {
                    this.name = "NULL";
                }
                break;
            case "bodyAttack":
                this.bodyAttack = WzUtil.getBoolean(value, false);
                break;
            case "boss":
                this.boss = WzUtil.getBoolean(value, false);
                break;
            case "level":
                this.level = WzUtil.getByte(value, 1);
                break;
            case "maxHP":
                this.maxHP = WzUtil.getInt32(value, 0);
                break;
            case "maxMP":
                this.maxMP = WzUtil.getInt32(value, 0);
                break;
            case "speed":
                this.speed = (byte) Math.min(140, Math.max(0, WzUtil.getByte(value, 0)));
                break;
            case "PADamage":
                this.pad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "PDDamage":
                this.pdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "MADamage":
                this.mad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "MDDamage":
                this.mdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "acc":
                this.acc = WzUtil.getByte(value, 0);
                break;
            case "eva":
                this.eva = WzUtil.getByte(value, 0);
                break;
            case "exp":
                this.exp = WzUtil.getInt32(value, 0);
                break;
            case "pushed":
                this.pushedDamage = WzUtil.getInt32(value, 1);
                break;
            case "elemAttr": {
                String elemAttr = value;
                if (elemAttr.length() == 1) {
                    elemAttr += "0";
                }
                for (int i = 0; i < elemAttr.length(); i += 2) {
                    int elem = AttackElem.getElementAttribute(elemAttr.charAt(i));
                    int attr = elemAttr.charAt(i + 1) - '0';

                    this.damagedElemAttr.set(elem, attr);
                }
                break;
            }
            case "hpRecovery":
                this.hpRecovery = WzUtil.getShort(value, 0);
                break;
            case "mpRecovery":
                this.mpRecovery = WzUtil.getShort(value, 0);
                break;
            case "undead":
                this.undead = WzUtil.getBoolean(value, false);
                break;
        }
    }

    public void setMobCountQuestInfo(User user) {
        if (user == null || user.getHP() <= 0 || user.getField() == null) {
            return;
        }
        List<Integer> mobQuests = QuestMan.getInstance().getQuestByMob(getTemplateID());
        if (mobQuests == null) {
            return;
        }
        for (Integer key : mobQuests) {
            if (user.lock()) {
                try {
                    String value = UserQuestRecord.get(user, key);
                    if (value == null) {
                        continue;
                    }
                    if (value.length() % 3 != 0) {
                        Logger.logError("Invalid QuestRecord : CharacterID : %d, QuestID : %d", user.getCharacterID(), key);
                        break;
                    }
                    for (char c : value.toCharArray()) {
                        byte cVal = (byte) c;
                        // only 0-9 nums
                        if (cVal < 0x30 || cVal > 0x39) {
                            Logger.logError("Invalid QuestRecord : CharacterID : %d, QuestID : %d", user.getCharacterID(), key);
                            return;
                        }
                    }
                    QuestDemand complete = QuestMan.getInstance().getCompleteDemand(key);
                    List<QuestMobInfo> mobInfos = complete.getDemandMob();
                    int[] mobKills = new int[mobInfos.size()];
                    int index = 0, mobKillsIndex = 0;
                    boolean changed = false;
                    for (QuestMobInfo mobInfo : mobInfos) {
                        int kills = 0;
                        if (value.length() > index) {
                            kills = Integer.parseInt(value.substring(index, index + 3));
                        }
                        mobKills[mobKillsIndex] = kills;
                        if (mobInfo.getMobID() == getTemplateID()) {
                            int mobKillCount = mobKills[mobKillsIndex];
                            if (mobKillCount < mobInfo.getCount()) {
                                mobKills[mobKillsIndex] = mobKillCount + 1;
                                changed = true;
                            }
                        }
                        mobKillsIndex++;
                        index += 3;
                    }
                    String newValue = "";
                    for (Integer kills : mobKills) {
                        newValue += String.format("%03d", kills);
                    }
                    if (changed) {
                        UserQuestRecord.set(user, key, newValue);
                        // some quest check ? idk for what
                    }
                } finally {
                    user.unlock();
                }
            }
        }
    }

    public int getTemplateID() {
        return templateID;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isBodyAttack() {
        return bodyAttack;
    }
    
    public byte getMoveAbility() {
        return moveAbility;
    }
    
    public boolean isBoss() {
        return boss;
    }
    
    public byte getLevel() {
        return level;
    }
    
    public int getMaxHP() {
        return maxHP;
    }
    
    public int getMaxMP() {
        return maxMP;
    }
    
    public byte getSpeed() {
        return speed;
    }
    
    public short getPAD() {
        return pad;
    }
    
    public short getPDD() {
        return pdd;
    }
    
    public short getMAD() {
        return mad;
    }
    
    public short getMDD() {
        return mdd;
    }
    
    public byte getACC() {
        return acc;
    }
    
    public byte getEVA() {
        return eva;
    }
    
    public int getEXP() {
        return exp;
    }
    
    public int getPushedDamage() {
        return pushedDamage;
    }
    
    public short getHPRecovery() {
        return hpRecovery;
    }
    
    public short getMPRecovery() {
        return mpRecovery;
    }
    
    public boolean isUndead() {
        return undead;
    }
    
    public List<Integer> getDamagedElemAttr() {
        return damagedElemAttr;
    }
    
    public List<MobAttackInfo> getAttackInfo() {
        return attackInfo;
    }
    
    public List<RewardInfo> getRewardInfo() {
        return rewardInfo;
    }

    public byte getFlySpeed() {
        return flySpeed;
    }

    public byte getChaseSpeed() {
        return chaseSpeed;
    }

    public boolean isNotAttack() {
        return notAttack;
    }

    public int getHpTagColor() {
        return hpTagColor;
    }

    public int getHpTagBgColor() {
        return hpTagBgColor;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public boolean isHasPublicDrop() {
        return hasPublicDrop;
    }

    public boolean isHasExplosiveDrop() {
        return hasExplosiveDrop;
    }

    public int getDeadBuff() {
        return deadBuff;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public boolean isRemoveQuest() {
        return removeQuest;
    }

    public boolean isPickUpDrop() {
        return pickUpDrop;
    }

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public int getSelfDestructionHP() {
        return selfDestructionHP;
    }

    public boolean isDamagedByMob() {
        return damagedByMob;
    }

    public boolean isDoNotRemove() {
        return doNotRemove;
    }

    public int getDropItemPeriod() {
        return dropItemPeriod;
    }

    public int getBanType() {
        return banType;
    }

    public String getBanMsg() {
        return banMsg;
    }

    public MobSelfDestruction getSelfDestructionInfo() {
        return selfDestructionInfo;
    }

    public int getGetCP() {
        return getCP;
    }

    public boolean isHpGaugeHide() {
        return hpGaugeHide;
    }

    public int getFixedDamage() {
        return fixedDamage;
    }

    public boolean isOnlyNormalAttack() {
        return onlyNormalAttack;
    }

    public List<MobSkillInfo> getSkillInfo() {
        return skillInfo;
    }

    public List<Integer> getReviveTemplateIDs() {
        return reviveTemplateIDs;
    }

    public List<MobBanMap> getBanMap() {
        return banMap;
    }
}
