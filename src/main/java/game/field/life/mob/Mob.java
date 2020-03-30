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

import common.Request;
import common.game.field.FieldEffectFlags;
import common.game.field.MobAppearType;
import game.GameApp;
import game.field.*;
import game.field.MovePath.Elem;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.field.life.Controller;
import game.field.life.MoveAbility;
import game.field.life.mob.MobDamageLog.Info;
import game.user.User;
import game.user.skill.SkillInfo;
import game.user.skill.Skills.*;
import java.awt.Point;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.user.skill.data.MobSkillLevelData;
import game.user.skill.data.SkillLevelData;
import game.user.skill.entries.MobSkillEntry;
import game.user.skill.entries.SkillEntry;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.Flag;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;
import util.Rect;

/**
 *
 * @author Eric
 */
public class Mob extends Creature {
    private final MobTemplate template;
    private MobGen mobGen;
    private MobStat stat;
    private byte mobType;
    private int templateID;
    private short homeFoothold;
    private int summonType;
    private int summonOption;
    private boolean noDropPriority;
    private Controller controller;
    private boolean nextAttackPossible;
    private boolean experiencedMoveStateChange;
    private int hp;
    private int mp;
    private MobDamageLog damageLog;
    private Point curPos;
    private byte moveAction;
    private short footholdSN;
    private final List<Reward> rewardPicked;
    private boolean alreadyStealed;
    private int itemID_Stolen;
    private final List<Rect> moves;
    private final Lock lockMoveRect;
    private short lastX;
    private short lastY;
    private boolean forcedDead;
    private long lastAttack;
    private long lastMove;
    private long create;
    private long lastRecovery;
    private long lastUpdatePoison;
    private long lastSendMobHP;
    private long lastSkillUse;
    private int skillCommand;
    private final Map<Integer, Long> attackers;
    private boolean testDrops;
    private final List<MobSkillContext> skillContexts;

    public Mob(Field field, MobTemplate template, boolean noDropPriority) {
        super();
        setField(field);
        
        this.template = template;
        this.summonType = MobAppearType.NORMAL;
        this.noDropPriority = noDropPriority;
        this.mobType = 0;//MobSpecies.Beast
        this.controller = null;
        this.nextAttackPossible = false;
        this.lastUpdatePoison = 0;
        this.damageLog = new MobDamageLog();
        this.footholdSN = 0;
        this.rewardPicked = new ArrayList<>();
        this.alreadyStealed = false;
        this.itemID_Stolen = 0;
        this.moves = new ArrayList<>();
        this.lockMoveRect = new ReentrantLock();
        this.lastX = 0;
        this.lastY = 0;
        this.experiencedMoveStateChange = false;
        this.testDrops = false;
        this.attackers = new HashMap<>();
        this.hp = getMaxHP();
        this.mp = getMaxMP();
        this.forcedDead = false;
        this.templateID = template.getTemplateID();
        this.stat = new MobStat();
        long time = System.currentTimeMillis();
        this.lastSendMobHP = time;
        this.lastRecovery = time;
        this.lastAttack = time;
        this.lastMove = time;
        this.create = time;
        this.skillContexts = new ArrayList<>();
        template.makeSkillContext(skillContexts);
        this.stat.setFrom(template);
    }
    
    public double alterEXPbyLevel(int level, double incEXP) {
        return incEXP;
    }
    
    public boolean checkIsPossibleMoveStart(User user, MovePath mp, Pointer<Boolean> result) {
        Point moveStart = new Point(0, 0);
        boolean suddenMove = true;
        Elem head = mp.getElem().getFirst();
        Elem tail = mp.getElem().getLast();
        short x = head.getX();
        short y = head.getY();
        
        moveStart.x = x;
        moveStart.y = y;
        if (lastX == 0 || lastY == 0 || Math.abs(lastX - x) <= 500)
            suddenMove = false;
        lastX = tail.getX();
        lastY = tail.getY();
        if (experiencedMoveStateChange || Rand32.genRandom() % 100 >= 20)
            return false;
        lockMoveRect.lock();
        try {
            if (moves.isEmpty() || moves.size() < 2) {
                return false;
            }
            Rect rc = moves.get(moves.size() - 1);//arcMove.a -> pTail?
            Rect move = new Rect();
            move.left = rc.left;
            move.top = rc.top;
            move.right = rc.right;
            move.bottom = rc.bottom;
            for (int i = 1; i < moves.size(); i++) {
                move = move.unionRect(moves.get(i));
            }
            move.inflateRect(300, 300);
            
            boolean bMove = move.ptInRect(moveStart);
            if (bMove || !suddenMove)
                bMove = true;
            result.set(bMove);
            
            return true;
        } finally {
            lockMoveRect.unlock();
        }
    }
    
    public int distributeExp(Pointer<Integer> lastDamageCharacterID) {
        int damageSum = damageLog.vainDamage;
        int maxDamage = 0;
        int characterID = 0;
        for (Info info : damageLog.getLog()) {
            damageSum += info.damage;
            if (maxDamage < info.damage) {
                maxDamage = info.damage;
                characterID = info.characterID;
            }
            lastDamageCharacterID.set(info.characterID);
        }
        if (damageSum >= damageLog.initHP) {
            if (getEXP() != 0) {
                int idx = damageLog.getLog().size();
                for (Info info : damageLog.getLog()) {
                    if (info.damage > 0) {
                        User user = getField().findUser(info.characterID);
                        if (user == null || user.getField() == null || user.getField().getFieldID() != damageLog.fieldID) {
                            continue;
                        }
                        double lastSum = 0.0d;
                        if (idx == 0) {
                            lastSum = (double) getEXP() * 0.2d;
                        }
                        if (user.lock(1600)) {
                            try {
                                double incEXP = (((double) getEXP() * (double) info.damage) * 0.8 / (double) damageSum + lastSum);
                                incEXP = alterEXPbyLevel(user.getCharacter().getCharacterStat().getLevel(), incEXP);
                                incEXP *= user.getExpRate();
                                incEXP *= getField().getIncEXPRate();
                                incEXP = Math.max(1.0, incEXP);

                                int flag = user.incEXP((int) incEXP, false);
                                if (flag != 0) {
                                    user.sendIncExpMessage(true, (int) incEXP, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, 0);
                                    user.sendCharacterStat(Request.None, flag);
                                }
                            } finally {
                                user.unlock();
                            }
                        }
                        idx--;
                    }
                }
            }
            return characterID;
        } else {
            Logger.logError("Mob damaged HP is less than initial HP");
        }
        return 0;
    }
    
    public void encodeInitData(OutPacket packet) {
        packet.encodeInt(getGameObjectID());
        packet.encodeByte(0);// nCalcDamageStatIndex
        packet.encodeInt(getTemplateID());
        stat.encodeTemporary(packet, MobStats.getMask(MobStats.NONE));
        packet.encodeShort(curPos.x);
        packet.encodeShort(curPos.y);
        packet.encodeByte(moveAction);
        packet.encodeShort(footholdSN);
        packet.encodeShort(homeFoothold);
        packet.encodeByte(summonType);
        if (summonType == MobAppearType.REVIVED || summonType >= 0) {
            packet.encodeInt(summonOption);
        }
        packet.encodeByte(-1);// nTeamForMCarnival
        packet.encodeInt(0);
        packet.encodeInt(0);// nEffectItemID
        packet.encodeInt(0);// nPhase
    }
    
    public Controller getController() {
        return controller;
    }
    
    public Point getCurrentPos() {
        return curPos;
    }
    
    public int getEXP() {
        return template.getEXP();
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Mob;
    }
    
    public int getHP() {
        return hp;
    }
    
    public final byte getLevel() {
        return template.getLevel();
    }
    
    public final int getMaxHP() {
        return template.getMaxHP();
    }
    
    public final int getMaxMP() {
        return template.getMaxMP();
    }
    
    public MobGen getMobGen() {
        return mobGen;
    }
    
    public MobStat getMobStat() {
        return stat;
    }
    
    public void getMobStat(MobStat mobStat) {
        
    }
    
    public int getMP() {
        return mp;
    }
    
    public MobTemplate getTemplate() {
        return template;
    }
    
    @Override
    public int getTemplateID() {
        return templateID;
    }
    
    public void giveReward(int ownerID, Point hit, int delay, boolean steal) {
        if (!alreadyStealed || !steal) {
            User user = getField().findUser(ownerID);
            int ownerDropRate = 1;
            int ownerDropRate_Ticket = 1;
            if (user != null) {
                ownerDropRate = (int) user.getMesoRate();
                ownerDropRate_Ticket = (int) user.getTicketDropRate();
            }
            List<Reward> rewards = Reward.create(template.getRewardInfo(), false, ownerDropRate, ownerDropRate_Ticket, testDrops);
            if (rewards == null || rewards.isEmpty()) {
                return;
            }
            if (steal) {
                Reward reward = rewards.get((int) (Rand32.getInstance().random() % rewards.size()));
                if (reward.getItem() != null) {
                    itemID_Stolen = reward.getItem().getItemID();
                }
                if (reward.getType() == RewardType.MONEY) {
                    reward.setMoney(reward.getMoney() / 2);
                }
                rewards.clear();
                rewards.add(reward);
                alreadyStealed = true;
            } else {
                if (alreadyStealed && itemID_Stolen != 0) {
                    for (Iterator<Reward> it = rewards.iterator(); it.hasNext();) {
                        Reward reward = it.next();
                        if (reward.getItem() != null && reward.getItem().getItemID() == itemID_Stolen) {
                            it.remove();
                        }
                    }
                }
            }
            int x2 = hit.x + rewards.size() * -10;
            for (Reward reward : rewards) {
                getField().getDropPool().create(reward, ownerID, getGameObjectID(), hit.x, hit.y, x2, 0, delay, false, 0);
                x2 += 20;
            }
            if (steal) {
                if (rewards.get(0).getItem() != null)
                    itemID_Stolen = rewards.get(0).getItem().getItemID();
            }
        }
    }
    
    public void heal(int min, int max) {
        int decHP;
        if (max == min)
            decHP = Rand32.genRandom().intValue();
        else
            decHP = min + Rand32.genRandom().intValue() % (max - min);
        setMobHP(Math.min(decHP + getHP(), getMaxHP()));
    }
    
    public void init(MobGen mobGen, short fh) {
        this.damageLog.vainDamage = 0;
        this.damageLog.fieldID = getField().getFieldID();
        this.damageLog.initHP = getHP();
        if (mobGen != null && mobGen.regenInterval != 0)
            mobGen.mobCount.incrementAndGet();
        this.mobGen = mobGen;
        this.homeFoothold = template.getMoveAbility() != MoveAbility.Fly ? fh : 0;
    }
    
    public boolean isNextAttackPossible() {
        return nextAttackPossible;
    }
    
    public boolean isTimeToRemove(long time, boolean fixedMob) {
        if (fixedMob) {
            return System.currentTimeMillis() - create >= 30000;
        }
        // TODO: self destruct and remove after handling
        if (template.getRemoveAfter() == 0) {
            return false;
        }
        return 1000 * template.getRemoveAfter() < time - getCreate();
    }
    
    @Override
    public OutPacket makeEnterFieldPacket() {
        return MobPool.onMobEnterField(this);
    }
    
    @Override
    public OutPacket makeLeaveFieldPacket() {
        byte deadType = MobLeaveField.RemainHP;
        if (!isTimeToRemove(System.currentTimeMillis(), false) && getHP() > 0) {
            deadType = MobLeaveField.ETC;
        }
        return MobPool.onMobLeaveField(getGameObjectID(), deadType);
    }
    
    public void onMobDead(Point hit, int delay) {
        // TODO: handle drop owner properties and update mob quest count
        Pointer<Integer> lastDamageCharacterID = new Pointer<>(0);
        int ownerID = distributeExp(lastDamageCharacterID);
        if (ownerID != 0) {
            User user = GameApp.getInstance().getChannel(getField().getChannelID()).findUser(ownerID);
            if (user != null && user.getField().getFieldID() == getField().getFieldID()) {
                setMobCountQuestInfo(user);
            }
            giveReward(ownerID, hit, delay, false);
        }
    }
    
    public boolean onMobHit(User user, int damage, byte attackType) {
        int characterID = user.getCharacterID();
        long time = System.currentTimeMillis();
        if (time - lastAttack > 5000 && (controller == null || user != controller.getUser()) && !nextAttackPossible)
            getField().getLifePool().changeMobController(characterID, this, true);
        attackers.put(characterID, time);
        if (damage > 0) {
            if (damage > Integer.MAX_VALUE) {
                Logger.logError("Invalid Mob Damage. Name : %s, Damage: %d", user.getCharacterName(), damage);
                return false;
            }
            //int flagReset = stat.resetTemporary(time);
            if (hp > 0) {
                damage = Math.min(damage, hp);
                damageLog.addLog(characterID, damage, time);
                setMobHP(hp - damage);
                //if (hp > 0) {
                    //if (attackType == 1 && stat.getStatOption(MobStats.Stun) != 0) {
                        //flagReset |= stat.reset(MobStats.Stun);
                    //}
                //}
                //sendMobTemporaryStatReset(flagReset);
                return hp <= 0;
            }
        }
        return false;
    }
    
    public int onMobMPSteal(int prop, int percent) {
        int decMP = 0;
        if (template.isBoss()) {
            decMP = Math.min(percent * getMaxMP() / 100, getMP());
            if (Rand32.genRandom() % 100 >= prop || decMP < 0)
                decMP = 0;
            this.mp -= decMP;
        }
        return decMP;
    }
    
    public boolean onMobMove(boolean nextAttackPossible, byte action, int data, Pointer<Integer> skillCommand, Pointer<Integer> slv, Pointer<Boolean> shootAttack) {
        long time = System.currentTimeMillis();
        lastMove = time;
        if (action >= MobActType.MOVE) {
            if (action < MobActType.ATTACK1 || action > MobActType.ATTACKF) {
                if (action >= MobActType.SKILL1 && action <= MobActType.SKILLF && !doSkill(data & 0xFF, (data >> 8) & 0xFF, (data >> 16))) {
                    return false;
                }
            } else {
                // deadly attack handling
            }
        }
        if (time - lastAttack > 5000) {
            int characterID = 0;
            if (controller != null && controller.getUser() != null)
                characterID = controller.getUser().getCharacterID();
            Info infoAdd = null;
            for (Info info : damageLog.getLog()) {
                if (info.characterID == characterID) {
                    infoAdd = info;
                    break;
                }
            }
            if (infoAdd == null || time - infoAdd.time > 5000) {
                for (Iterator<Info> it = damageLog.getLog().descendingIterator(); it.hasNext();) {
                    Info info = it.next();
                    if (time - info.time > 5000)
                        break;
                    if (info.characterID != characterID && getField().getLifePool().changeMobController(info.characterID, this, true))
                        return false;
                }
            }
            this.lastAttack = time;
        }
        this.nextAttackPossible = nextAttackPossible;
        prepareNextSkill(skillCommand, slv, time);
        return true;
    }

    public boolean doSkill(int skillID, int slv, int option) {
        int skillIndex = template.getSkillIndex(skillID, slv);
        if (stat.getStatOption(MobStats.SealSkill) != 0 || skillCommand != skillID || skillIndex < 0) {
            skillCommand = 0;
            return false;
        }
        MobSkillInfo skill = template.getSkillInfo().get(skillIndex);
        MobSkillLevelData level = SkillInfo.getInstance().getMobSkill(skill.getSkillID()).getLevelData().get(skill.getSlv() - 1);
        MobSkillContext context = skillContexts.get(skillIndex);
        this.mp = Math.max(this.mp - level.getConMP(), 0);

        long time = System.currentTimeMillis();
        if (time == 0) {
            time = 1;
        }
        lastSkillUse = time;
        context.setLastSkillUse(time);
        if (context.getSkillID() == MobSkills.SUMMON) {
            context.setSummoned(context.getSummoned() + level.getTemplateIDs().size());
        }
        skillContexts.set(skillIndex, context);
        skillCommand = 0;
        if (MobSkills.isStatChange(skillID)) {
            doSkill_StatChange(skillID, slv, level, option);
        } else if (MobSkills.isUserStatChange(skillID)) {
            doSkill_UserStatChange(skillID, slv, level, option);
        } else if (MobSkills.isPartizanStatChange(skillID)) {
            doSkill_PartizanStatChange(skillID, slv, level, option);
        } else if (MobSkills.isPartizanOneTimeStatChange(skillID)) {
            doSkill_PartizanOneTimeStatChange(skillID, slv, level, option);
        } else if (MobSkills.isSummon(skillID)) {
            doSkill_Summon(level, option);
        } else if (MobSkills.isAffectArea(skillID)) {
            doSkill_AffectArea(skillID, slv, level, option);
        } else {
            Logger.logReport("[Mob Skill] Unhandled skill [0x%X, %d, 0x%X]", skillID, slv, option);
        }
        return true;
    }

    public void doSkill_StatChange(int skillID, int slv, MobSkillLevelData level, int delay) {
        int x = level.getX();
        int reason = skillID | (slv << 16);
        long duration = System.currentTimeMillis() + level.getDuration();

        Flag set = new Flag(Flag.INT_128);
        switch (skillID) {
            case MobSkills.POWERUP:
                set.performOR(stat.setStat(MobStats.PowerUp, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.MAGICUP:
                set.performOR(stat.setStat(MobStats.MagicUp, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.PGUARDUP:
                set.performOR(stat.setStat(MobStats.PGuardUp, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.MGUARDUP:
                set.performOR(stat.setStat(MobStats.MGuardUp, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.HASTE:
            case MobSkills.SPEED:
                set.performOR(stat.setStat(MobStats.Speed, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.PHYSICAL_IMMUNE:
                set.performOR(stat.setStat(MobStats.PImmune, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.MAGIC_IMMUNE:
                set.performOR(stat.setStat(MobStats.MImmune, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.HARDSKIN:
                set.performOR(stat.setStat(MobStats.HardSkin, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.PAD:
                set.performOR(stat.setStat(MobStats.PAD, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.MAD:
                set.performOR(stat.setStat(MobStats.MAD, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.PDR:
                set.performOR(stat.setStat(MobStats.PDD, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.MDR:
                set.performOR(stat.setStat(MobStats.MDD, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.ACC:
                set.performOR(stat.setStat(MobStats.ACC, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.EVA:
                set.performOR(stat.setStat(MobStats.EVA, new MobStatOption(x, reason, duration)));
                break;
            case MobSkills.SEALSKILL:
                set.performOR(stat.setStat(MobStats.SealSkill, new MobStatOption(1, reason, duration)));
                break;
        }
        sendMobTemporaryStatSet(set, delay);
    }

    public void doSkill_UserStatChange(int skillID, int slv, MobSkillLevelData level, int delay) {
        Rect affected = new Rect();
        affected.top = level.getAffectedArea().top;
        affected.bottom = level.getAffectedArea().bottom;
        if ((moveAction & 1) != 0) {// is left
            affected.right = -level.getAffectedArea().left;
            affected.left = -level.getAffectedArea().right;
        } else {
            affected.right = level.getAffectedArea().right;
            affected.left = level.getAffectedArea().left;
        }
        affected.offsetRect(curPos.getX(), curPos.getY());

        Collection<User> users = getField().getUsers();
        if (users.size() != 0) {
            int targetUserCount = level.getTargetUserCount();
            boolean allUsers = true;
            if (targetUserCount >= 0) {
                allUsers = false;
            }
            for (User user : users) {
                if (affected.ptInRect(user.getCurrentPosition()) && targetUserCount != 0 && user.getCharacter().getCharacterStat().getHP() != 0) {
                    user.onStatChangeByMobSkill(skillID, slv, level, delay, getTemplateID());
                    if (!allUsers && --targetUserCount <= 0) {
                        return;
                    }
                }
            }
        }
    }

    public void doSkill_PartizanStatChange(int skillID, int slv, MobSkillLevelData level, int delay) {
        Rect rect = new Rect();
        rect.top = level.getAffectedArea().top;
        rect.bottom = level.getAffectedArea().bottom;
        rect.right = level.getAffectedArea().right;
        rect.left = level.getAffectedArea().left;
        if (rect.isRectEmpty()) {
            return;
        }
        if ((moveAction & 1) != 0) {// is left
            rect.right = -level.getAffectedArea().left;
            rect.left = -level.getAffectedArea().right;
        }
        rect.offsetRect(curPos.getX(), curPos.getY());

        List<Mob> mobs = new ArrayList<>();
        int count = getField().getLifePool().findAffectedMobInRect(rect, mobs, null);
        for (int i = 0; i < count; i++) {
            if (mobs.get(i).getTemplateID() != 9999999) {
                doSkill_UserStatChange(skillID, slv, level, delay);
            }
        }
        mobs.clear();
    }

    public void doSkill_PartizanOneTimeStatChange(int skillID, int slv, MobSkillLevelData level, int delay) {
        Rect rect = new Rect();
        rect.top = level.getAffectedArea().top;
        rect.bottom = level.getAffectedArea().bottom;
        rect.right = level.getAffectedArea().right;
        rect.left = level.getAffectedArea().left;
        if (rect.isRectEmpty()) {
            return;
        }
        if ((moveAction & 1) != 0) {// is left
            rect.right = -level.getAffectedArea().left;
            rect.left = -level.getAffectedArea().right;
        }
        rect.offsetRect(curPos.getX(), curPos.getY());

        List<Mob> mobs = new ArrayList<>();
        int count = getField().getLifePool().findAffectedMobInRect(rect, mobs, null);
        for (int i = 0; i < count; i++) {
            if (mobs.get(i).getTemplateID() != 9999999) {
                doSkill_OneTimeStatChange(skillID, slv, level, delay);
            }
        }
        mobs.clear();
    }

    public void doSkill_OneTimeStatChange(int skillID, int slv, MobSkillLevelData level, int delay) {
        if (skillID != MobSkills.HEAL_M) {
            return;
        }
        int heal = level.getX() + Math.abs(Rand32.genRandom().intValue())  % level.getY();
        int newHP = Math.min(heal + hp, template.getMaxHP());
        setMobHP(newHP);
        sendDamagedPacket(1, -heal);
        sendMobAffectedPacket((slv << 16) | MobSkills.HEAL_M, 0);
    }

    public void doSkill_Summon(MobSkillLevelData level, int delay) {
        List<Integer> revives = level.getTemplateIDs();
        if (revives.isEmpty()) {
            return;
        }
        if (getField().getLifePool().getMobCount() >= 50) {
            return;
        }
        Rect rect = new Rect(-150, -100, 100, 150);
        if (!level.getAffectedArea().isRectEmpty()) {
            rect.left = level.getAffectedArea().left;
            rect.top = level.getAffectedArea().top;
            rect.right = level.getAffectedArea().right;
            rect.bottom = level.getAffectedArea().bottom;
        }
        rect.offsetRect(curPos.getX(), curPos.getY());

        List<Point> points = new ArrayList<>();
        getField().getSpace2D().getFootholdRandom(revives.size(), rect, points);
        if (points.size() < revives.size()) {
            Logger.logError("Illegal summon mob count ( %d/%d ) [Field : %d]", points.size(), revives.size(), getField().getFieldID());
        }
        int index = 0;
        for (Point point : points) {
            int x = point.x;
            Pointer<Integer> pcy = new Pointer<>(point.y);
            StaticFoothold foothold = getField().getSpace2D().getFootholdUnderneath(x, pcy.get(), pcy);
            if (foothold != null) {
                getField().getLifePool().createMob(revives.get(index), null, x, pcy.get(), (short) foothold.getSN(), noDropPriority, level.getEffect(), delay, (byte) 0, 0, null, false);
            }
            index++;
        }
        points.clear();
    }

    public void doSkill_AffectArea(int skillID, int slv, MobSkillLevelData level, int delay) {
        long start = delay + System.currentTimeMillis();
        long end = start + level.getDuration();
        Rect area = level.getAffectedArea().copy();
        Point pt = new Point(curPos.x, curPos.y);
        area.offsetRect(pt.getX(), pt.getY());
        getField().getAffectedAreaPool().insertAffectedArea(true, getGameObjectID(), skillID, slv, start, end, pt, area);
    }

    public void prepareNextSkill(Pointer<Integer> skillCommand, Pointer<Integer> slv, long cur) {
        if (stat.getStatOption(MobStats.SealSkill) != 0) {
            return;
        }
        List<MobSkillInfo> skills = template.getSkillInfo();
        if (skills == null || skills.isEmpty()) {
            return;
        }
        if (!nextAttackPossible) {
            return;
        }
        if (this.skillCommand != 0) {
            return;
        }
        if (lastSkillUse != 0 && cur - lastSkillUse < 3000) {
            return;
        }

        List<Integer> chosenSkills = new ArrayList<>();
        for (int i = 0; i < template.getSkillInfo().size(); i++) {
            MobSkillContext context = null;
            MobSkillInfo skill = null;
            if (i >= 0) {
                int skillSize = template.getSkillInfo().size();
                if (skillSize != 0 && i < skillSize) {
                    context = skillContexts.get(i);
                    skill = template.getSkillInfo().get(i);
                }
            }
            if (context == null || skill == null) {
                continue;
            }
            MobSkillLevelData level = SkillInfo.getInstance().getMobSkill(skill.getSkillID()).getLevelData().get(skill.getSlv() - 1);
            if (level.getHpBelow() != 0 && getHP() / template.getMaxHP() * 100 > level.getHpBelow()) {
                continue;
            }
            int interval = level.getInerval();
            if (interval != 0 && interval + context.getLastSkillUse() - cur > 0) {
                continue;
            }
            int skillID = context.getSkillID();
            if (skillID == MobSkills.HASTE) {
                int option = stat.getStatOption(MobStats.Speed);
                if (option == 0) {
                    chosenSkills.add(i);
                }
                if (Math.abs(100 - option) < Math.abs(100 - level.getX())) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.POWERUP) {
                int option = stat.getStatOption(MobStats.PowerUp);
                if (option == 0) {
                    chosenSkills.add(i);
                }
                if (Math.abs(100 - option) < Math.abs(100 - level.getX())) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.MAGICUP) {
                int option = stat.getStatOption(MobStats.MagicUp);
                if (option == 0) {
                    chosenSkills.add(i);
                }
                if (Math.abs(100 - option) < Math.abs(100 - level.getX())) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.MGUARDUP) {
                int option = stat.getStatOption(MobStats.MGuardUp);
                if (option == 0) {
                    chosenSkills.add(i);
                }
                if (Math.abs(100 - option) < Math.abs(100 - level.getX())) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.PGUARDUP) {
                int option = stat.getStatOption(MobStats.PGuardUp);
                if (option == 0) {
                    chosenSkills.add(i);
                }
                if (Math.abs(100 - option) < Math.abs(100 - level.getX())) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.PHYSICAL_IMMUNE || skillID == MobSkills.MAGIC_IMMUNE) {
                if (stat.getStatOption(MobStats.PImmune) == 0 && stat.getStatOption(MobStats.MImmune) == 0) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.HARDSKIN) {
                int option = stat.getStatOption(MobStats.HardSkin);
                if (option == 0) {
                    chosenSkills.add(i);
                }
                if (Math.abs(100 - option) < Math.abs(100 - level.getX())) {
                    chosenSkills.add(i);
                }
            } else if (skillID == MobSkills.SUMMON) {
                int skillSummonCount = level.getTemplateIDs().size();
                if (skillSummonCount + context.getSummoned() <= level.getLimit()) {
                    chosenSkills.add(i);
                }
            } else {
                chosenSkills.add(i);
            }
        }
        if (!chosenSkills.isEmpty()) {
            int length = chosenSkills.size();
            if (length != 0) {
                int rand = Math.abs(Rand32.genRandom().intValue()) % length;
                MobSkillContext newContext = skillContexts.get(chosenSkills.get(rand));
                skillCommand.set(newContext.getSkillID());
                this.skillCommand = newContext.getSkillID();
                slv.set(newContext.getSlv());
            }
        }
        chosenSkills.clear();
    }
    public void onMobStatChangeSkill(User user, SkillEntry skill, byte slv, int damageSum) {
        SkillLevelData level = skill.getLevelData(slv);
        int prop = level.Prop;
        int x = level.X;
        int y = level.Y;
        int skillID = skill.getSkillID();
        if (prop == 0) {
            prop = 100;
        }
        long time = System.currentTimeMillis();
        long duration = time + 1000 * level.Time;
        if (Rand32.genRandom() % 100 >= prop || template.isBoss()) {
            return;
        }
        
        MobStatOption opt = new MobStatOption();
        opt.setOption(x);
        opt.setReason(skillID);
        opt.setDuration(duration);
        Logger.logReport("X [%d] | Reason [%d] | Time [%d]", x, skillID, level.Time);
        Flag flag = new Flag(Flag.INT_128);
        switch (skillID) {
            case Knight.ICE_CHARGE: {
                int attr;
                if ((attr = template.getDamagedElemAttr().get(AttackElem.Ice)) == AttackElemAttr.Damage0 || attr == AttackElemAttr.Damage50) {
                    return;
                }
                opt.setDuration(time + 1000 * level.Y);
                flag.performOR(stat.setStat(MobStats.Freeze, opt));
                this.experiencedMoveStateChange = true;
            }
            case Knight.CHARGE_BLOW: {
                if (user.getSecondaryStat().getStatOption(CharacterTemporaryStat.WeaponCharge) <= 0) {
                    return;
                }
                int reason = user.getSecondaryStat().getStatReason(CharacterTemporaryStat.WeaponCharge);
                if (reason == Knight.ICE_CHARGE) {
                    return;
                }
                opt.setOption(1);
                flag.performOR(stat.setStat(MobStats.Stun, opt));
            }
            case Wizard2.ColdBeam: {
                int attr;
                if ((attr = template.getDamagedElemAttr().get(AttackElem.Ice)) == AttackElemAttr.Damage0 || attr == AttackElemAttr.Damage50) {
                    return;
                }
                opt.setOption(1);
                flag.performOR(stat.setStat(MobStats.Freeze, opt));
                this.experiencedMoveStateChange = true;
                break;
            }
            case Hunter.ArrowBomb:
            case Thief.Steal:
            case InFighter.BACKSPIN_BLOW: {
                if (skillID == Hunter.ArrowBomb && damageSum < template.getPushedDamage() && (Rand32.genRandom() % 3) != 0) {
                    return;
                }
                opt.setOption(1);
                flag.performOR(stat.setStat(MobStats.Stun, opt));
                this.experiencedMoveStateChange = true;
                break;
            }
            case Wizard1.PoisonBreath:
            case ArchMage1.FIRE_DEMON:
            case ArchMage1.METEOR: {
                int attr;
                if ((attr = template.getDamagedElemAttr().get(AttackElem.Poison)) == AttackElemAttr.Damage0 || attr == AttackElemAttr.Damage50) {
                    return;
                }
                opt.setOption(Math.max(Math.min(Short.MAX_VALUE, getMaxHP() / (70 - slv)), level.MAD));
                opt.setModOption(user.getCharacterID());
                opt.setDuration(time + 1000 * level.DotTime);
                this.lastUpdatePoison = time;
                flag.performOR(stat.setStat(MobStats.Poison, opt));
                break;
            }
            case Wizard1.Slow:
            case Wizard2.Slow: {
                flag.performOR(stat.setStat(MobStats.Speed, opt));
                break;
            }
            case Mage1.SEAL: {
                opt.setOption(1);
                flag.performOR(stat.setStat(MobStats.Seal, opt));
                break;
            }
            case Page.Threaten:
            case Rogue.Disorder: {
                flag.performOR(stat.setStat(MobStats.PAD, opt));
                flag.performOR(stat.setStat(MobStats.PDD, new MobStatOption(y, skillID, duration)));
                break;
            }
            default: {
                return;
            }
        }
        if (flag.isSet()) {
            Logger.logReport("Sending");
            sendMobTemporaryStatSet(flag, 0);
        }
    }
    
    public void sendChangeControllerPacket(User user, byte level) {
        if (user != null) {
            if (level != 0) {
                user.sendPacket(MobPool.onMobChangeController(this, 0, level));
            } else {
                sendReleaseControlPacket(user, getGameObjectID());
            }
        }
    }
    
    public void sendMobTemporaryStatReset(Flag reset) {
        if (reset.isSet()) {
            getField().splitSendPacket(getSplit(), MobPool.onStatReset(this, reset), null);
        }
    }
    
    public void sendMobTemporaryStatSet(Flag flag, int delay) {
        if (flag.isSet()) {
            getField().splitSendPacket(getSplit(), MobPool.onStatSet(this, flag, (short) delay), null);
        }
    }
    
    public static void sendReleaseControlPacket(User user, int mobID) {
        if (user != null) {
            user.sendPacket(MobPool.onMobChangeController(null, mobID, (byte) 0));
        }
    }
    
    public void setController(Controller ctrl) {
        this.nextAttackPossible = false;
        this.skillCommand = 0;
        this.controller = ctrl;
        long time = System.currentTimeMillis();
        this.lastAttack = time;
        this.lastMove = time;
    }
    
    public void setForcedDead(boolean forced) {
        this.forcedDead = forced;
    }
    
    public void setMobHP(int hp) {
        if (hp >= 0 && hp <= getMaxHP() && hp != getHP()) {
            this.hp = hp;

            int color = template.getHpTagColor();
            int bg = template.getHpTagBgColor();
            if (color != 0 && bg != 0 && !template.isHpGaugeHide()) {
                sendMobHPChange(hp, color, bg, false);
            }
            broadcastHP();
        }
    }

    public void sendMobHPEnd() {
        int color = template.getHpTagColor();
        int bg = template.getHpTagBgColor();
        if (color != 0 && bg != 0) {
            sendMobHPChange(-(template.isHpGaugeHide() ? 1 : 0), color, bg, true);
        }
    }

    public void sendMobHPChange(int hp, int color, int bgColor, boolean enforce) {
        if (System.currentTimeMillis() - this.lastSendMobHP > 500 || enforce) {
            this.lastSendMobHP = System.currentTimeMillis();
            getField().splitSendPacket(getSplit(), FieldPacket.onFieldEffect(FieldEffectFlags.MobHPTag, null, template.getTemplateID(), hp, template.getMaxHP(), color, bgColor), null);
        }
    }

    public void broadcastHP() {
        if (attackers.size() <= 0) {
            return;
        }
        int percentage = 100 * getHP() / template.getMaxHP();
        if (percentage <= 0 && getHP() > 0) {
            percentage = 1;
        }
        OutPacket packet = MobPool.onHPIndicator(getGameObjectID(), percentage);
        for (Iterator<Integer> it = attackers.keySet().iterator(); it.hasNext();) {
            int characterID = it.next();
            User user = GameApp.getInstance().getChannel(getField().getChannelID()).findUser(characterID);
            if (user != null && user.getField() == getField()) {
                user.sendPacket(packet);
            } else {
                it.remove();
            }
        }
    }

    public void setMobType(int type) {
        this.mobType = (byte) type;
    }
    
    public boolean setMovePosition(int x, int y, byte moveAction, short sn) {
        byte action = (byte) (moveAction >> 1);
        if (action < 1 || action >= 17) {
            return false;
        } else {
            if (this.curPos == null) {
                this.curPos = new Point(0, 0);
            }
            this.curPos.x = x;
            this.curPos.y = y;
            this.moveAction = moveAction;
            this.footholdSN = template.getMoveAbility() != MoveAbility.Fly ? sn : 0;
            return true;
        }
    }
    
    public void setRemoved() {
        GameObjectBase.unregisterGameObject(this);
        if (mobGen != null) {
            if (mobGen.regenInterval != 0) {
                int mobCount = mobGen.mobCount.decrementAndGet();
                if (mobCount == 0) {
                    long regen = 0;
                    int delay = 7 * mobGen.regenInterval / 10;
                    if (delay != 0)
                        regen = 13 * mobGen.regenInterval / 10 + Rand32.getInstance().random() % delay;
                    mobGen.regenAfter = regen + System.currentTimeMillis();
                }
            }
        }
    }
    
    public void update(long time) {
        if (hp <= 0)
            return;
        if (stat.getStatOption(MobStats.Poison) != 0)
            updatePoison(time);
        Flag reset = stat.resetTemporary(time);
        if (hp == 1) {
            if (stat.getStatOption(MobStats.Poison) != 0)
                reset.performOR(stat.reset(MobStats.Poison));
        }
        if (reset.isSet()) {
            sendMobTemporaryStatReset(reset);
        }
        if (time - lastRecovery > 8000) {
            setMobHP(Math.min(hp + template.getHPRecovery(), getMaxHP()));
            mp = Math.min(getMP() + template.getMPRecovery(), getMaxMP());
            lastRecovery = time;
        }
        if (time - lastMove > 5000) {
            getField().getLifePool().changeMobController(0, this, false);
            lastAttack = time;
            lastMove = time;
        }
        for (Iterator<Long> it = attackers.values().iterator(); it.hasNext();) {
            long attackTime = it.next();
            if (time - attackTime > 1000 * 5)
                it.remove();
        }
    }
    
    public void updatePoison(long time) {
        if (stat.getStatOption(MobStats.Poison) > 0) {
            long lastPoison = Math.min(time, stat.getStatDuration(MobStats.Poison));
            long times = (lastPoison - lastUpdatePoison) / 1000;
            int oldHP = hp;
            int poison = stat.getStatOption(MobStats.Poison);
            
            setMobHP(Math.max(1, (int) (hp - times * poison)));
            
            if (hp != oldHP)
                damageLog.addLog(stat.getStat(MobStats.Poison).getModOption(), oldHP - hp, time);
            
            lastUpdatePoison += 1000 * times;
        }
    }

    public int getSummonType() {
        return summonType;
    }

    public void setSummonType(int summonType) {
        this.summonType = summonType;
    }

    public int getSummonOption() {
        return summonOption;
    }

    public void setSummonOption(int summonOption) {
        this.summonOption = summonOption;
    }

    public void setFieldBossMobHP(int hp) {
        if (getField().getFieldID() == Field.HONTALE_MAP_ID && getTemplateID() == BossIDs.HONTALE_SPIRIT_MOB_ID ||
            getField().getFieldID() == Field.BABY_BOSS_MAP_ID && getTemplateID() == BossIDs.BABYBOSS_DUMMY5_MOB_ID) {
            setMobHP(hp);
        }
    }

    public long getCreate() {
        return create;
    }

    public void setMobCountQuestInfo(User user) {
        if (user == null || user.getHP() <= 0 || user.getField() == null || user.getField() != getField()) {
            return;
        }
        // channel checks ?
        MobTemplate template = getTemplate();
        if (template != null) {
            template.setMobCountQuestInfo(user);
        }
    }

    public void setTestDrops(boolean testDrops) {
        this.testDrops = testDrops;
    }

    public byte getMobType() {
        return mobType;
    }

    public void sendSuspendReset(boolean suspendReset) {
        getField().splitSendPacket(getSplit(), MobPool.onSuspendReset(getGameObjectID(), suspendReset), null);
    }

    public boolean isForcedDead() {
        return forcedDead;
    }

    public short getFootholdSN() {
        return footholdSN;
    }

    public boolean isNoDropPriority() {
        return noDropPriority;
    }

    public byte getMoveAction() {
        return moveAction;
    }

    public void sendDamagedPacket(int type, int decHP) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobDamaged);
        packet.encodeInt(getGameObjectID());
        packet.encodeByte(type);
        packet.encodeInt(decHP);
        if (template.isDamagedByMob()) {
            packet.encodeInt(getHP());
            packet.encodeInt(template.getMaxHP());
        }
        getField().splitSendPacket(getSplit(), packet, null);
    }

    public void sendMobAffectedPacket(int skillID, int delay) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobAffected);
        packet.encodeInt(getGameObjectID());
        packet.encodeInt(skillID);
        packet.encodeShort(delay);
        getField().splitSendPacket(getSplit(), packet, null);
    }
}
