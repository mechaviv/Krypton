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
package game.user;

import common.*;
import common.item.*;
import common.user.CharacterData;
import common.user.CharacterStat;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import common.user.UserEffect;
import game.Channel;
import game.GameApp;
import game.field.*;
import game.field.drop.Drop;
import game.field.drop.DropPickup;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.field.life.AttackIndex;
import game.field.life.AttackInfo;
import game.field.life.MoveAbility;
import game.field.life.mob.*;
import game.field.life.npc.*;
import game.field.portal.Portal;
import game.field.portal.PortalMap;
import game.field.summoned.Summoned;
import game.messenger.Messenger;
import game.miniroom.MiniRoom;
import game.miniroom.MiniRoomBase;
import game.party.PartyMan;
import game.party.PartyPacket;
import game.party.PartyResCode;
import game.script.ScriptVM;
import game.user.command.CommandHandler;
import game.user.command.UserGradeCode;
import game.user.func.FunckeyMapped;
import game.user.item.*;
import game.user.quest.QuestAct;
import game.user.quest.QuestFlag;
import game.user.quest.QuestMan;
import game.user.quest.UserQuestRecord;
import game.user.quest.info.QuestItemOption;
import game.user.quest.info.act.ActItem;
import game.user.skill.*;
import game.user.skill.Skills.*;
import game.user.skill.data.MobSkillLevelData;
import game.user.skill.data.SkillLevelData;
import game.user.skill.entries.SkillEntry;
import game.user.stat.*;
import game.user.stat.psd.AdditionPsd;
import game.user.stat.psd.PassiveSkillData;
import network.database.CommonDB;
import network.database.GameDB;
import network.packet.*;
import util.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eric
 */
public class User extends Creature {
    // Misc. Variables
    private final Lock lock;
    private final Lock lockSocket;
    // Skills
    private final UserSkill userSkill;
    // Cheat Inspector
    // private CheatInspector cheatInspector;
    private final CalcDamage calcDamage;
    // Character Data
    private final CharacterData character;
    private final List<ItemSlotBase> realEquip;
    // Avatar Look
    private final AvatarLook avatarLook;
    // Basic Stat
    private final BasicStat basicStat;
    // Secondary Stat
    private final SecondaryStat secondaryStat;
    // Passive Stat
    private final PassiveSkillData passiveSkillData;
    // User RNG's
    private final Rand32 rndActionMan;
    // Party stuff
    private final LinkedList<Integer> partyInvitedCharacterID;
    private final ReentrantLock partyInviteLock;
    // Summoned
    private final List<Summoned> summoneds;
    private int lastPassiveSkillDataUpdate;
    private int accountID;
    private int characterID;
    private int gradeCode;
    private int localSocketSN;
    // Account/Character Names
    private String nexonClubID;
    private String characterName;
    private Npc tradingNpc;
    private long lastSelectNPCTime;
    private boolean onTransferField;
    private int incorrectFieldPositionCount;
    private int curFieldKey;
    private long lastCharacterDataFlush;
    private long nextGeneralItemCheck;
    private long nextCheckCashItemExpire;
    private long lastCharacterHPInc;
    private long lastCharacterMPInc;
    private int illegalHPIncTime;
    private int illegalHPIncSize;
    private int illegalMPIncTime;
    private int illegalMPIncSize;
    private String community;
    private long lastAttack;
    private long lastAttackTime;
    private long lastAttackDelay;
    private long finalAttackDelay;
    private long lastDragonBloodUpdate;
    private int attackCheckIgnoreCnt;
    private int attackSpeedErr;
    // Hide
    private boolean hide;
    // User Emotions
    private int emotion;
    // User-specific rates
    private double incExpRate = 1.0d;
    private double incMesoRate = 1.0d;
    private double incDropRate = 1.0d;
    private double incDropRate_Ticket = 1.0d;
    private int incEXPRate = 100;
    // Trade Limits
    private int tradeMoneyLimit;
    private int tempTradeMoney;
    private int invalidTryRepeatCount;
    private int invalidUserActionCount;
    private int invalidMobMoveCount;
    private int invalidHitPointCount;
    private int skipWarpCount;
    private int warpCheckedCount;
    private int invalidDamageCount;
    private int invalidDamageMissCount;
    private int characterDataModFlag;
    private int avatarModFlag;
    // Skills
    private int preparedSkill;
    private long lastKeyDown;
    private boolean keyDown;

    // Mini Rooms
    // private UserMiniRoom userMR;
    private MiniRoomBase miniRoom;
    private boolean miniRoomBalloon;
    // MSMessenger
    private Messenger userMSM;
    private boolean msMessenger;
    // ScriptVM
    private ScriptVM runningVM;
    // Client
    private ClientSocket socket;
    private long loginTime;
    private long logoutTime;
    private boolean closeSocketNextTime;
    private boolean temporaryLogging;
    // Movement
    private Point curPos;
    private byte moveAction;
    private short footholdSN;
    // Monster Carnival
    private int teamForMCarnival;

    // Function key mapped
    private FunckeyMapped[] funcKeyMapped;
    private boolean funcKeyMappedInitEmpty;
    private int petConsumeItemID_HP;
    private int petConsumeItemID_MP;

    protected User(int characterID) {
        super();

        this.hide = false;
        this.onTransferField = false;
        this.closeSocketNextTime = false;

        this.emotion = 0;
        this.invalidHitPointCount = 0;
        this.invalidMobMoveCount = 0;
        this.skipWarpCount = 0;
        this.warpCheckedCount = 0;
        this.invalidDamageCount = 0;
        this.invalidDamageMissCount = 0;
        this.tradeMoneyLimit = 0;
        this.tempTradeMoney = 0;
        this.accountID = -1;
        this.incorrectFieldPositionCount = 0;
        this.avatarModFlag = 0;
        this.characterDataModFlag = 0;
        this.lastSelectNPCTime = 0;

        long time = System.currentTimeMillis();
        this.lastCharacterHPInc = time;
        this.lastCharacterMPInc = time;
        this.lastCharacterDataFlush = time;
        this.nextCheckCashItemExpire = time;
        this.lastAttack = time;
        this.lastPassiveSkillDataUpdate = Utilities.timeGetTime();
        this.lastDragonBloodUpdate = time;
        this.nexonClubID = "";
        this.characterName = "";
        this.community = "#TeamEric";

        this.curPos = new Point(0, 0);
        this.lock = new ReentrantLock(true);
        this.lockSocket = new ReentrantLock(true);
        this.partyInviteLock = new ReentrantLock(true);
        this.basicStat = new BasicStat();
        this.secondaryStat = new SecondaryStat();
        this.passiveSkillData = new PassiveSkillData();
        this.avatarLook = new AvatarLook();
        this.rndActionMan = new Rand32();
        this.calcDamage = new CalcDamage();
        this.userSkill = new UserSkill(this);
        this.userMSM = new Messenger(this);
        // TODO: Nexon-like user caching to avoid DB load upon each login/migrate.
        this.partyInvitedCharacterID = new LinkedList<>();
        this.character = GameDB.rawLoadCharacter(characterID);
        this.summoneds = new ArrayList<>();
        this.realEquip = new ArrayList<>(BodyPart.BP_Count + 1);

        for (int i = 0; i <= BodyPart.BP_Count; i++) {
            this.realEquip.add(i, null);
        }

        this.basicStat.clear();
        this.secondaryStat.clear();
        this.passiveSkillData.clear();
    }

    public User(ClientSocket socket) {
        this(socket.getCharacterID());

        this.socket = socket;
        this.localSocketSN = socket.getLocalSocketSN();

        Pointer<Integer> grade = new Pointer<>(0);
        Pointer<Integer> id = new Pointer<>(0);
        Pointer<String> nexonID = new Pointer<>("");
        GameDB.rawLoadAccount(socket.getCharacterID(), id, nexonID, grade);

        this.accountID = id.get();
        this.nexonClubID = nexonID.get();
        this.gradeCode = grade.get();

        this.characterID = character.getCharacterStat().getCharacterID();
        this.characterName = character.getCharacterStat().getName();

        this.funcKeyMapped = FunckeyMapped.getDefault();
        this.petConsumeItemID_HP = 0;
        this.petConsumeItemID_MP = 0;

        List<Integer> data = new ArrayList<>();
        int funcCount = GameDB.rawGetFuncKeyMapped(characterID, data);
        this.funcKeyMappedInitEmpty = funcCount == 0;
        if (funcCount != 0) {
            this.funcKeyMappedInitEmpty = true;
            for (int i = 0; i < funcCount; i += 3) {
                int keyID = data.get(i);
                int type = data.get(i + 1);
                int funcID = data.get(i + 2);
                if (keyID == 200) {
                    this.petConsumeItemID_HP = funcID;
                } else if (keyID == 201) {
                    this.petConsumeItemID_MP = funcID;
                } else {
                    this.funcKeyMapped[keyID] = new FunckeyMapped(type, funcID);
                    this.funcKeyMappedInitEmpty = false;
                }
            }
        }
        this.validateStat(true);

        // Apply default configured rates
        this.incExpRate *= GameApp.getInstance().getExpRate();
        this.incMesoRate = GameApp.getInstance().getMesoRate();
        this.incDropRate *= GameApp.getInstance().getDropRate();
        this.incDropRate_Ticket *= GameApp.getInstance().getDropRate();
    }

    /**
     * This is a User::~User deleting destructor.
     * <p>
     * WARNING: This method should ONLY be used when you NULL the User object.
     */
    public final void destructUser() {
        /* Begin CUser::~CUser destructor */
        flushCharacterData(0, true);
        Logger.logReport("User logout");
        realEquip.clear();
        /* Begin CharacterData::~CharacterData destructor */
        for (List<Integer> itemTrading : character.getItemTrading())
            itemTrading.clear();
        character.getItemTrading().clear();
        //character.getQuestRecord().clear();
        character.getSkillRecord().clear();
        for (List<ItemSlotBase> itemSlot : character.getItemSlot())
            itemSlot.clear();
        character.getItemSlot().clear();
        character.getEquipped2().clear();
        character.getEquipped().clear();
        /* End CharacterData::~CharacterData destructor */
        if (miniRoom != null) {
            miniRoom = null;
        }
        if (runningVM != null) {
            runningVM = null;
        }
        /* End CUser::~CUser destructor */
    }

    ///////////////////////////// CQWUser START /////////////////////////////
    public boolean canStatChange(int inc, int dec) {
        // TODO

        return false;
    }

    public int getLevel() {
        return character.getCharacterStat().getLevel();
    }

    public MiniRoomBase getMiniRoom() {
        return miniRoom;
    }

    public void setMiniRoom(MiniRoomBase miniRoom) {
        this.miniRoom = miniRoom;
    }

    public boolean incAP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int ap = character.getCharacterStat().getAP();
            if (onlyFull && (ap + inc < 0 || ap + inc > SkillAccessor.AP_MAX)) {
                return false;
            }
            int newSP = Math.max(Math.min(ap + inc, SkillAccessor.AP_MAX), 0);
            character.getCharacterStat().setAP((short) newSP);
            if (newSP == ap) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incDEX(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int DEX = character.getCharacterStat().getDEX();
            if (onlyFull && (DEX + inc < 0 || DEX + inc > SkillAccessor.DEX_MAX)) {
                return false;
            }
            int newDEX = Math.max(Math.min(DEX + inc, SkillAccessor.DEX_MAX), 0);
            character.getCharacterStat().setDEX((short) newDEX);
            if (newDEX == DEX) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public int incEXP(int inc, boolean onlyFull) {
        if (lock(1500)) {
            try {
                int flag = CharacterStatType.EXP;
                if (getHP() > 0 && inc > 0 && (!onlyFull || inc + character.getCharacterStat().getEXP() >= 0)) {
                    if (inc < 0) {
                        if (character.getCharacterStat().getEXP() + inc < 0) {
                            character.getCharacterStat().setEXP(0);
                            addCharacterDataMod(DBChar.Character);
                            return flag;
                        }
                    }
                    if (inc <= 0) {
                        addCharacterDataMod(DBChar.Character);
                        return flag;
                    }
                    Pointer<Boolean> reachMaxLev = new Pointer<>(false);
                    if (ExpAccessor.tryProcessLevelUp(character, basicStat, inc, reachMaxLev)) {
                        if (!JobAccessor.isBeginnerJob(character.getCharacterStat().getJob())) {
                            incSP(3, false);
                            incAP(5, false);
                            flag |= CharacterStatType.AP | CharacterStatType.SP;
                        } else if (character.getCharacterStat().getLevel() < 11) {
                            int dexValue = Math.max(getLevel() - getCharacter().getCharacterStat().getDEX(), 0);
                            if (dexValue > 0) {
                                incDEX(dexValue, false);
                                flag |= CharacterStatType.DEX;
                            }
                            int val = 4 * character.getCharacterStat().getLevel() + 12;
                            int strVal = (val - getCharacter().getCharacterStat().getSTR()) & ((val - getCharacter().getCharacterStat().getSTR() <= 0 ? 1 : 0) - 1);
                            if (strVal > 0) {
                                incSTR(strVal, false);
                                flag |= CharacterStatType.STR;
                            }
                        }
                        flag |= CharacterStatType.LEV | CharacterStatType.HP | CharacterStatType.MP | CharacterStatType.MHP | CharacterStatType.MMP;
                        validateStat(false);
                        onLevelUp();
                        if (PartyMan.getInstance().charIdToPartyID(getCharacterID()) > 0) {
                            PartyMan.getInstance().postChangeLevelOrJob(getCharacterID(), getLevel(), true);
                        }
                        if (reachMaxLev.get()) {
                            setMaxLevelReach();
                        }
                    }
                    addCharacterDataMod(DBChar.Character);
                    return flag;
                }
            } finally {
                unlock();
            }
        }
        return 0;
    }

    public boolean incHP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int hp = character.getCharacterStat().getHP();
            int mhp = getBasicStat().getMHP();
            if (hp == 0) {
                return false;
            }
            if (onlyFull && (inc < 0 && hp + inc < 0 || inc > 0 && hp + inc > mhp)) {
                return false;
            }
            int newHP = Math.max(Math.min(hp + inc, mhp), 0);
            character.getCharacterStat().setHP(newHP);
            if (newHP == 0) {
                //cheatInspector.initUserDamagedTime(0, false);
                onUserDead();
            }
            if (newHP == hp) {
                return false;
            }
            characterDataModFlag |= DBChar.Character;
            return true;
        } finally {
            unlock();
        }
    }

    public boolean incINT(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int INT = character.getCharacterStat().getINT();
            if (onlyFull && (INT + inc < 0 || INT + inc > SkillAccessor.INT_MAX)) {
                return false;
            }
            int newINT = Math.max(Math.min(INT + inc, SkillAccessor.INT_MAX), 0);
            character.getCharacterStat().setINT((short) newINT);
            if (newINT == INT) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incLUK(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int LUK = character.getCharacterStat().getLUK();
            if (onlyFull && (LUK + inc < 0 || LUK + inc > SkillAccessor.LUK_MAX)) {
                return false;
            }
            int newLUK = Math.max(Math.min(LUK + inc, SkillAccessor.LUK_MAX), 0);
            character.getCharacterStat().setLUK((short) newLUK);
            if (newLUK == LUK) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incMHP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int mhp = character.getCharacterStat().getMHP();
            if (onlyFull && (mhp + inc < 50 || mhp + inc > SkillAccessor.HP_MAX)) {
                return false;
            }
            int newMHP = Math.max(Math.min(mhp + inc, SkillAccessor.HP_MAX), 50);
            character.getCharacterStat().setMHP(newMHP);
            if (newMHP == mhp) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incMMP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int mmp = character.getCharacterStat().getMMP();
            if (onlyFull && (mmp + inc < 5 || mmp + inc > SkillAccessor.MP_MAX)) {
                return false;
            }
            int newMMP = Math.max(Math.min(mmp + inc, SkillAccessor.MP_MAX), 5);
            character.getCharacterStat().setMMP(newMMP);
            if (newMMP == mmp) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incMP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            if (inc < 0 && getHP() <= 0) {
                return false;
            }
            int mp = character.getCharacterStat().getMP();
            int mmp = getBasicStat().getMMP();
            if (onlyFull && (inc < 0 && mp + inc < 0 || inc > 0 && mp + inc > mmp)) {
                return false;
            }
            int newMP = Math.max(Math.min(mp + inc, mmp), 0);
            character.getCharacterStat().setMP(newMP);
            if (newMP == mp) {
                return false;
            }
            characterDataModFlag |= DBChar.Character;
            return true;
        } finally {
            unlock();
        }
    }

    public boolean incMoney(int inc, boolean onlyFull) {
        return incMoney(inc, onlyFull, false);
    }

    public boolean incMoney(int inc, boolean onlyFull, boolean totalMoneyChange) {
        lock.lock();
        try {
            if (InventoryManipulator.rawIncMoney(character, inc, onlyFull)) {
                characterDataModFlag |= DBChar.Character;
                return true;
            }
            return false;
        } finally {
            unlock();
        }
    }

    public boolean incPOP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int pop = character.getCharacterStat().getPOP();
            if (onlyFull && (pop + inc < -SkillAccessor.POP_MAX || pop + inc > SkillAccessor.POP_MAX)) {
                return false;
            }
            int newPOP = Math.min(Math.max(pop + inc, -SkillAccessor.POP_MAX), SkillAccessor.POP_MAX);
            character.getCharacterStat().setPOP((short) newPOP);
            if (newPOP == pop) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incSP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int sp = character.getCharacterStat().getSP();
            if (onlyFull && (sp + inc < 0 || sp + inc > SkillAccessor.SP_MAX)) {
                return false;
            }
            int newSP = Math.max(Math.min(sp + inc, SkillAccessor.SP_MAX), 0);
            character.getCharacterStat().setSP((short) newSP);
            if (newSP == sp) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public boolean incSTR(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int STR = character.getCharacterStat().getSTR();
            if (onlyFull && (STR + inc < 0 || STR + inc > SkillAccessor.STR_MAX)) {
                return false;
            }
            int newSTR = Math.max(Math.min(STR + inc, SkillAccessor.STR_MAX), 0);

            character.getCharacterStat().setSTR((short) newSTR);
            if (newSTR == STR) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }

    public int initEXP() {
        if (lock()) {
            try {
                if (character.getCharacterStat().getLevel() >= ExpAccessor.MAX_LEVEL && character.getCharacterStat().getEXP() > 0) {
                    character.getCharacterStat().setEXP(0);
                    characterDataModFlag |= DBChar.Character;
                    return CharacterStatType.EXP;
                }
            } finally {
                unlock();
            }
        }
        return 0;
    }

    public boolean isValidStat(int STR, int DEX, int INT, int LUK, int remainAP) {
        // TODO
        return false;
    }

    public void setFace(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setFace(val);
            validateStat(false);
        } finally {
            unlock();
        }
    }

    public void setGender(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setGender((byte) val);
            validateStat(false);
        } finally {
            unlock();
        }
    }

    public void setHair(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setHair(val);
            validateStat(false);
        } finally {
            unlock();
        }
    }

    public void setJob(int val) {
        if (JobAccessor.findJob(val) != null) {
            lock.lock();
            try {
                List<Integer> curSkillRoot = new ArrayList<>();
                List<Integer> newSkillRoot = new ArrayList<>();
                SkillAccessor.getSkillRootFromJob(character.getCharacterStat().getJob(), curSkillRoot);
                SkillAccessor.getSkillRootFromJob(val, newSkillRoot);
                for (Iterator<Integer> it = curSkillRoot.iterator(); it.hasNext(); ) {
                    int skillRoot = it.next();
                    if (newSkillRoot.contains(skillRoot)) {
                        it.remove();
                    }
                }
                character.getCharacterStat().setJob((short) val);
                if (val != 0 && character.getCharacterStat().getLevel() < 11) {
                    int ap = character.getCharacterStat().getAP();

                    int STR = character.getCharacterStat().getSTR() - 4;
                    if (STR > 0) {
                        ap += STR;
                    }
                    int DEX = character.getCharacterStat().getDEX() - 4;
                    if (DEX > 0) {
                        ap += DEX;
                    }
                    int INT = character.getCharacterStat().getINT() - 4;
                    if (INT > 0) {
                        ap += INT;
                    }
                    int LUK = character.getCharacterStat().getLUK() - 4;
                    if (LUK > 0) {
                        ap += LUK;
                    }
                    character.getCharacterStat().setSTR((short) 4);
                    character.getCharacterStat().setDEX((short) 4);
                    character.getCharacterStat().setINT((short) 4);
                    character.getCharacterStat().setLUK((short) 4);
                    character.getCharacterStat().setAP(ap);
                }
                if (!curSkillRoot.isEmpty()) {
                    List<SkillRecord> changes = new ArrayList<>();
                    for (int skillRoot : curSkillRoot) {
                        SkillRoot root = SkillInfo.getInstance().getSkillRoot(skillRoot);
                        if (root != null) {
                            for (SkillEntry skill : root.getSkills()) {
                                int skillID = skill.getSkillID();
                                character.getSkillRecord().remove(skillID);
                                character.getSkillMasterLev().remove(skillID);
                                SkillRecord change = new SkillRecord();
                                change.setInfo(-1);
                                change.setSkillID(skillID);
                                change.setMasterLevel(0);
                                changes.add(change);
                            }
                        }
                    }
                    validateStat(true);
                    UserSkillRecord.sendCharacterSkillRecord(this, (byte) 0, changes);
                    changes.clear();
                }
                if (!newSkillRoot.isEmpty()) {
                    List<SkillRecord> changes = new ArrayList<>();
                    for (int skillRoot : newSkillRoot) {
                        SkillRoot root = SkillInfo.getInstance().getSkillRoot(skillRoot);
                        if (root != null) {
                            for (SkillEntry skill : root.getSkills()) {
                                int skillID = skill.getSkillID();
                                if (SkillAccessor.isSkillNeedMasterLevel(skillID)) {
                                    int defaultMasterLev = skill.getDefaultMasterLev();
                                    character.getSkillMasterLev().put(skillID, defaultMasterLev);

                                    int slv = character.getSkillRecord().getOrDefault(skillID, 0);
                                    if (defaultMasterLev > 0) {
                                        character.getSkillRecord().put(skillID, slv);
                                        SkillRecord change = new SkillRecord();
                                        change.setSkillID(skillID);
                                        change.setInfo(slv);
                                        change.setMasterLevel(defaultMasterLev);
                                        changes.add(change);
                                    }
                                }
                            }
                        }
                    }
                    validateStat(true);
                    UserSkillRecord.sendCharacterSkillRecord(this, (byte) 0, changes);
                    changes.clear();
                }
                validateStat(true);
                addCharacterDataMod(DBChar.Character);
                if (PartyMan.getInstance().charIdToPartyID(getCharacterID()) > 0) {
                    PartyMan.getInstance().postChangeLevelOrJob(getCharacterID(), val, false);
                }
                //sendCharacterStat(Request.None, CharacterStatType.Job);
                onUserEffect(false, true, UserEffect.JobChanged);
            } finally {
                unlock();
            }
        }
    }

    public void setSkin(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setSkin((byte) val);
            validateStat(false);
        } finally {
            unlock();
        }
    }
    ///////////////////////////// CQWUser END ///////////////////////

    public void statChange(int inc, int dec, short incHP, short incMP) {
        lock.lock();
        try {
            switch (inc) {
                case CharacterStatType.STR:
                    incSTR(1, true);
                    break;
                case CharacterStatType.DEX:
                    incDEX(1, true);
                    break;
                case CharacterStatType.INT:
                    incINT(1, true);
                    break;
                case CharacterStatType.LUK:
                    incLUK(1, true);
                    break;
            }
            switch (inc) {
                case CharacterStatType.STR:
                    incSTR(-1, true);
                    break;
                case CharacterStatType.DEX:
                    incDEX(-1, true);
                    break;
                case CharacterStatType.INT:
                    incINT(-1, true);
                    break;
                case CharacterStatType.LUK:
                    incLUK(-1, true);
                    break;
            }
            if (inc == CharacterStatType.MHP || dec == CharacterStatType.MHP)
                character.getCharacterStat().setMHP(character.getCharacterStat().getMHP() + incHP);
            if (inc == CharacterStatType.MMP || dec == CharacterStatType.MMP)
                character.getCharacterStat().setMMP(character.getCharacterStat().getMMP() + incMP);
        } finally {
            unlock();
        }
    }

    public void addCharacterDataMod(int flag) {
        this.characterDataModFlag |= flag;
    }

    public boolean canAttachAdditionalProcess() {
        if (socket != null && !onTransferField && getHP() > 0 && miniRoom == null && tradingNpc == null) {
            return runningVM == null;
        }
        return false;
    }

    public void destroyAdditionalProcess() {
        lock.lock();
        try {
            this.tradingNpc = null;
            if (miniRoom != null) {
                //miniRoom.onUserLeave(this);
                miniRoom = null;
            }
            if (runningVM != null) {
                runningVM.destruct();
                runningVM.destroy(this);
            }
        } finally {
            unlock();
        }
    }

    public void closeSocket() {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.postClose();
            }
        } finally {
            lockSocket.unlock();
        }
    }

    public final boolean lock() {
        return lock(700);
    }

    public final boolean lock(long timeout) {
        try {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    public final void unlock() {
        lock.unlock();
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
    }

    public BasicStat getBasicStat() {
        return basicStat;
    }

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    public Channel getChannel() {
        if (socket != null) {
            return socket.getChannel();
        }
        return null;
    }

    public byte getChannelID() {
        if (socket != null) {
            return socket.getChannelID();
        }
        return 0;
    }

    public CharacterData getCharacter() {
        return character;
    }

    public int getCharacterID() {
        return characterID;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(int grade) {
        this.gradeCode = grade;
    }

    public int getHP() {
        return character.getCharacterStat().getHP();
    }

    public int getLocalSocketSN() {
        return localSocketSN;
    }

    public Messenger getMessenger() {
        return userMSM;
    }

    public int getPosMap() {
        return character.getCharacterStat().getPosMap();
    }

    public void setPosMap(int map) {
        character.getCharacterStat().setPosMap(map);
    }

    public byte getPortal() {
        return character.getCharacterStat().getPortal();
    }

    public void setPortal(byte portal) {
        character.getCharacterStat().setPortal(portal);
    }

    public ScriptVM getScriptVM() {
        return runningVM;
    }

    public void setScriptVM(ScriptVM vm) {
        this.runningVM = vm;
    }

    public SecondaryStat getSecondaryStat() {
        return secondaryStat;
    }

    public ClientSocket getSocket() {
        return socket;
    }

    public byte getWorldID() {
        return GameApp.getInstance().getWorldID();
    }

    public double getExpRate() {
        return incExpRate;
    }

    public double getMesoRate() {
        return incMesoRate;
    }

    public double getDropRate() {
        return incDropRate;
    }

    public double getTicketDropRate() {
        return incDropRate_Ticket;
    }

    public boolean isGM() {
        return gradeCode >= UserGradeCode.GM.getGrade();
    }

    public boolean isHide() {
        return hide;
    }

    public void leaveField() {
        if (getField() != null) {
            getField().onLeave(this);
            FieldSet parentSet = getField().getParentFieldSet();
            if (!isGM() && parentSet != null && parentSet.isMCarnivalWaitingFieldSet()) {
                parentSet.banishUser(true);
            }
            if (getField().getForcedReturnFieldID() == Field.Invalid) {
                if (getHP() == 0) {
                    if (getField().getReturnFieldID() == Field.Invalid) {
                        if (lock()) {
                            try {
                                setPosMap(getField().getReturnFieldID());
                                if (OrionConfig.LOG_PACKETS) {
                                    Logger.logReport("To find crash : just before GetRandStartPoint2");
                                }
                                setPortal(getField().getPortal().getRandStartPoint().getPortalIdx());
                                if (getHP() == 0) {
                                    character.getCharacterStat().setHP(50);
                                }
                                addCharacterDataMod(DBChar.Character);
                            } finally {
                                unlock();
                            }
                        }
                    }
                    //setField(null);
                    return;
                }
                if (lock()) {
                    try {
                        Portal portal = getField().getPortal().findCloseStartPoint(curPos.x, curPos.y);
                        if (getPosMap() != getField().getFieldID() || getPortal() != portal.getPortalIdx()) {
                            setPosMap(getField().getFieldID());
                            setPortal(portal.getPortalIdx());
                            addCharacterDataMod(DBChar.Character);
                        }
                    } finally {
                        unlock();
                    }
                }
            } else {
                if (lock()) {
                    try {
                        setPosMap(getField().getForcedReturnFieldID());
                        Field field = FieldMan.getInstance(getChannelID()).getField(getField().getForcedReturnFieldID());
                        byte portal = 0;
                        if (field.getPortal() != null) {
                            portal = field.getPortal().getRandStartPoint().getPortalIdx();
                        }
                        setPortal(portal);
                        addCharacterDataMod(DBChar.Character);
                    } finally {
                        unlock();
                    }
                }
            }
            //setField(null);
        }
    }

    @Override
    public boolean isShowTo(User user) {
        if (hide && user.gradeCode >= gradeCode) {
            return true;
        }
        return !hide || hide && user.isGM();
    }

    @Override
    public OutPacket makeLeaveFieldPacket() {
        return UserPool.onUserLeaveField(characterID);
    }

    @Override
    public OutPacket makeEnterFieldPacket() {
        return UserPool.onUserEnterField(this);
    }

    public boolean setMovePosition(int x, int y, byte moveAction, short sn) {
        if (lock()) {
            try {
                this.curPos.x = x;
                this.curPos.y = y;
                this.moveAction = moveAction;
                this.footholdSN = sn;
                return true;
            } finally {
                unlock();
            }
        }
        return false;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String name) {
        this.community = name;
    }

    public Point getCurrentPosition() {
        return curPos;
    }

    public byte getMoveAction() {
        return moveAction;
    }

    public short getFootholdSN() {
        return footholdSN;
    }

    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.User;
    }

    @Override
    public String toString() {
        return character.getCharacterStat().getName();
    }

    public void onMigrateInSuccess() {
        Logger.logReport("User login from (%s)", socket.getAddr());
        if (getPosMap() / 10000000 == 99) {
            setPosMap(990001100);
        }
        Field field = FieldMan.getInstance(getChannelID()).getField(getPosMap());
        setField(field);
        if (getHP() == 0) {
            character.getCharacterStat().setHP((short) 50);
        }
        if (getField() == null) {
            field = FieldMan.getInstance(getChannelID()).getField(Field.Basic);
            setField(field);
            setPosMap(field.getFieldID());
            setPortal(field.getPortal().getRandStartPoint().getPortalIdx());
        }
        characterDataModFlag |= DBChar.Character;
        if (character.getCharacterStat().getLevel() >= ExpAccessor.MAX_LEVEL && character.getCharacterStat().getEXP() > 0) {
            initEXP();
        }
        PortalMap portal = getField().getPortal();
        int count = 0;
        if (!portal.getPortal().isEmpty()) {
            count = portal.getPortal().size();
        }
        byte portalIdx = getPortal();
        if (portalIdx <= 0)
            portalIdx = 0;
        int idx = count - 1;
        if (portalIdx < idx)
            idx = portalIdx;
        if (portal.getPortal().get(idx).getPortalType() > 0) {
            idx = portal.getRandStartPoint().getPortalIdx();
        }
        setPortal((byte) idx);
        curPos.x = portal.getPortal().get(idx).getPortalPos().x;
        curPos.y = portal.getPortal().get(idx).getPortalPos().y;
        moveAction = 0;
        footholdSN = 0;

        sendSetFieldPacket(true);

        // FuncKeyMapped
        sendPacket(FuncKeyMappedMan.onInit(funcKeyMappedInitEmpty, funcKeyMapped));
        sendPacket(FuncKeyMappedMan.onPetConsumeItemInit(petConsumeItemID_HP));
        sendPacket(FuncKeyMappedMan.onPetConsumeMPItemInit(petConsumeItemID_MP));

        resetTemporaryStat(System.currentTimeMillis(), 0);
        SecondaryStatOption option = secondaryStat.getStat(CharacterTemporaryStat.ComboCounter);
        if (option.getOption() != 0) {
            Pointer<SkillEntry> comboSkill = new Pointer<>();
            int slv = SkillInfo.getInstance().getSkillLevel(character, Crusader.ComboAttack, comboSkill);
            if (slv >= 20) {
                option.setModOption(comboSkill.get().getLevelData(slv).X);

                Pointer<SkillEntry> advSkill = new Pointer<>();
                int advSLV = SkillInfo.getInstance().getSkillLevel(character, Hero.AdvancedCombo, advSkill);
                if (advSLV != 0) {
                    SkillLevelData sd = advSkill.get().getLevelData(advSLV);
                    option.setModOption(sd.X | (sd.Prop << 16));
                }
            }
        }
        if (getField().onEnter(this)) {
            // I'm genuinely curious why Nexon has migration packets for messenger,
            // when you can't "migrate" channels without logging out first?
            // The only "migration" done is to the Shop, which can't access messenger.
            String firstUserEnter = getField().getFirstUserEnter();
            if (!getField().isUserEntered() && firstUserEnter != null && !firstUserEnter.isEmpty()) {
                ScriptVM script = new ScriptVM();
                if (script.setScript(this, "field/first/" + firstUserEnter, this)) {
                    script.run(this);
                }
                getField().setUserEntered(true);
            }
            String userEnter = getField().getUserEnter();
            if (userEnter != null && !userEnter.isEmpty()) {
                ScriptVM script = new ScriptVM();
                if (script.setScript(this, "field/" + userEnter, this)) {
                    script.run(this);
                }
            }
        } else {
            Logger.logError("Failed in entering field");
            closeSocket();
        }
    }

    public void onSocketDestroyed(boolean migrate) {
        if (lock()) {
            try {
                destroyAdditionalProcess();
                leaveField();
                GameObjectBase.unregisterGameObject(this);
                getChannel().unregisterUser(this);
            } finally {
                unlock();
            }
        }
        lockSocket.lock();
        try {
            socket = null;
        } finally {
            lockSocket.unlock();
        }
    }

    public void onSummonedPacket(short type, InPacket packet) {
        int summonedID = packet.decodeInt();
        if (getField() == null) {
            // skip calc damage stuff
            return;
        }
        Summoned summoned = getSummonedBySummonedID(summonedID);
        if (summoned == null) {
            // skip calc damage stuff
            return;
        }
        switch (type) {
            case ClientPacket.SummonedHit:
                summoned.onHit(this, packet);
                break;
            case ClientPacket.SummonedSkill:
                summoned.onSkill(this, packet);
                break;
            case ClientPacket.SummonedAttack:
                summoned.onAttack(this, packet);
                break;
            case ClientPacket.SummonedMove:
                summoned.onMove(this, packet);
                break;
        }
    }

    public void onFieldPacket(short type, InPacket packet) {
        if (getField() != null) {
            getField().onPacket(this, type, packet);
        }
    }

    public void onPacket(short type, InPacket packet) {
        switch (type) {
            case ClientPacket.UserTransferFieldRequest:
                onTransferFieldRequest(packet);
                break;
            case ClientPacket.UserMigrateToCashShopRequest:
                onMigrateToCashShopRequest();
                break;
            case ClientPacket.UserMove:
                onMove(packet);
                break;
            case ClientPacket.UserChat:
                onChat(packet);
                break;
            case ClientPacket.UserEmotion:
                onEmotion(packet);
                break;
            case ClientPacket.UserDropMoneyRequest:
                onDropMoneyRequest(packet);
                break;
            case ClientPacket.PartyRequest:
                onPartyRequest(packet);
                break;
            case ClientPacket.PartyResult:
                onPartyResult(packet);
                break;
            case ClientPacket.UserSkillUpRequest:
                userSkill.onSkillUpRequest(packet);
                break;
            case ClientPacket.UserHit:
                onHit(packet);
                break;
            case ClientPacket.UserMeleeAttack:
            case ClientPacket.UserShootAttack:
            case ClientPacket.UserMagicAttack:
                onAttack(type, packet);
                break;
            case ClientPacket.UserSkillUseRequest:
                userSkill.onSkillUseRequest(packet);
                break;
            case ClientPacket.UserSkillCancelRequest:
                userSkill.onSkillCancelRequest(packet);
                break;
            case ClientPacket.UserSkillPrepareRequest:
                userSkill.onSkillPrepareRequest(packet);
                break;
            case ClientPacket.UserCharacterInfoRequest:
                onCharacterInfoRequest(packet);
                break;
            case ClientPacket.UserPortalScriptRequest:
                onPortalScriptRequest(packet);
                break;
            case ClientPacket.UserQuestRequest:
                onQuestRequest(packet);
                break;
            case ClientPacket.UserChangeSlotPositionRequest:
                onChangeSlotPositionRequest(packet);
                break;
            case ClientPacket.UserUpgradeItemUseRequest:
                onUpgradeItemRequest(packet);
                break;
            case ClientPacket.MiniRoom:
                MiniRoom.onMiniRoom(this, packet);
                break;
            case ClientPacket.UserAbilityUpRequest:
                onAbilityUpRequest(packet);
                break;
            case ClientPacket.UserAbilityMassUpRequest:
                onAbilityMassUpRequest(packet);
                break;
            case ClientPacket.UserStatChangeItemUseRequest:
                onStatChangeItemUseRequest(packet);
                break;
            case ClientPacket.UserShopRequest:
                onShopRequest(packet);
                break;
            case ClientPacket.UserSelectNpc:
                onSelectNpc(packet);
                break;
            case ClientPacket.Messenger:
                getMessenger().onMessenger(packet);
                break;
            case ClientPacket.UserConsumeCashItemUseRequest:
                onConsumeCashItemUseRequest(packet);
                break;
            case ClientPacket.UserScriptMessageAnswer:
                onScriptMessageAnswer(packet);
                break;
            case ClientPacket.UserChangeStatRequest:
                onChangeStatRequest(packet);
                break;
            case ClientPacket.Whisper:
                onWhisper(packet);
                break;
            case ClientPacket.UserPortalScrollUseRequest:
                onPortalScrollUseRequest(packet);
                break;
            case ClientPacket.BroadcastMsg:
                onBroadcastMsg(packet);
                break;
            case ClientPacket.Admin:
                onAdmin(packet);
                break;
            case ClientPacket.FuncKeyMappedModified:
                onFuncKeyMappedModified(packet);
                break;
            case ClientPacket.PassiveskillInfoUpdate:
                onPassiveskillInfoUpdate(packet);
                break;
            default: {
                if (type >= ClientPacket.BEGIN_FIELD && type <= ClientPacket.END_FIELD) {
                    onFieldPacket(type, packet);
                } else if (type >= ClientPacket.BEGIN_SUMMONED && type <= ClientPacket.END_SUMMONED) {
                    onSummonedPacket(type, packet);
                } else {
                    Logger.logReport("[Unidentified Packet] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
                }
            }
        }
    }

    public void onAbilityUpRequest(InPacket packet) {
        if (lock()) {
            try {
                if (character.getCharacterStat().getAP() <= 0) {
                    Logger.logError("No ability point left");
                    closeSocket();
                    return;
                }
                packet.decodeInt();
                int flag = packet.decodeInt();
                boolean up;
                switch (flag) {
                    case CharacterStatType.STR:
                        up = incSTR(1, true);
                        break;
                    case CharacterStatType.DEX:
                        up = incDEX(1, true);
                        break;
                    case CharacterStatType.INT:
                        up = incINT(1, true);
                        break;
                    case CharacterStatType.LUK:
                        up = incLUK(1, true);
                        break;
                    case CharacterStatType.MHP:
                    case CharacterStatType.MMP:
                        up = SkillAccessor.incMaxHPMP(character, basicStat, flag, false);
                        break;
                    default: {
                        Logger.logError("Incorrect AP-Up stat");
                        return;
                    }
                }
                if (up) {
                    flag |= CharacterStatType.AP;
                    character.getCharacterStat().setAP(character.getCharacterStat().getAP() - 1);
                    validateStat(false);
                }
                addCharacterDataMod(DBChar.Character);
                sendCharacterStat(Request.Excl, flag);
            } finally {
                unlock();
            }
        }
    }

    public void onAbilityMassUpRequest(InPacket packet) {
        if (character.getCharacterStat().getAP() <= 0) {
            Logger.logError("No ability point left");
            closeSocket();
            return;
        }
        packet.decodeInt();// time

        int totalFlag = 0;

        int changes = packet.decodeInt();
        for (int i = 0; i < changes; i++) {
            int flag = packet.decodeInt();
            int value = packet.decodeInt();
            if (character.getCharacterStat().getAP() < value) {
                Logger.logError("No enough ability point left");
                closeSocket();
                return;
            }
            totalFlag |= flag;
            boolean up;
            switch (flag) {
                case CharacterStatType.STR:
                    up = incSTR(value, true);
                    break;
                case CharacterStatType.DEX:
                    up = incDEX(value, true);
                    break;
                case CharacterStatType.INT:
                    up = incINT(value, true);
                    break;
                case CharacterStatType.LUK:
                    up = incLUK(value, true);
                    break;
                default: {
                    Logger.logError("Incorrect AP-Up stat");
                    return;
                }
            }
            if (up) {
                totalFlag |= CharacterStatType.AP;
                character.getCharacterStat().setAP(character.getCharacterStat().getAP() - value);
                validateStat(false);
            }
        }
        addCharacterDataMod(DBChar.Character);
        sendCharacterStat(Request.Excl, totalFlag);
    }

    public void onAdmin(InPacket packet) {
        if (!isGM()) {
            return;
        }
        byte type = packet.decodeByte();

        if (lock()) {
            try {
                switch (type) {
                    case 0: {// Create Item
                        int itemID = packet.decodeInt();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        if (ti < ItemType.Equip || ti > ItemType.Cash) {
                            return;
                        }

                        ItemSlotBase item = ItemInfo.getItemSlot(itemID, ItemVariationOption.Normal);
                        if (item != null) {
                            if (item.getType() == ItemSlotType.Bundle) {
                                item.setItemNumber(SkillInfo.getInstance().getBundleItemMaxPerSlot(itemID, character));
                            }
                            List<ChangeLog> changeLog = new ArrayList<>();
                            if (Inventory.rawAddItem(this, ti, item, changeLog, null)) {
                                Inventory.sendInventoryOperation(this, Request.None, changeLog);
                                addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                            }
                            changeLog.clear();
                        }
                        break;
                    }
                    case 1: {// Delete Inventory
                        byte ti = packet.decodeByte();
                        if (ti < ItemType.Equip || ti > ItemType.Cash) {
                            return;
                        }
                        for (int pos = 1; pos <= character.getItemSlotCount(ti); pos++) {
                            ItemSlotBase item = character.getItem(ti, pos);
                            if (item != null) {
                                List<ChangeLog> changeLog = new ArrayList<>();
                                if (Inventory.rawRemoveItem(this, ti, (short) pos, item.getItemNumber(), changeLog, new Pointer<>(0), null)) {
                                    Inventory.sendInventoryOperation(this, Request.None, changeLog);
                                    addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                                }
                                changeLog.clear();
                            }
                        }
                        break;
                    }
                    case 2: {// Inc Exp
                        int flag = incEXP(packet.decodeInt(), false);
                        if (flag != 0) {
                            sendCharacterStat(Request.None, flag);
                        }
                        break;
                    }
                    case 3: {// Block
                        String characterName = packet.decodeString();
                        break;
                    }
                    case 4: {// Send User? Temp Block?
                        String characterName = packet.decodeString();
                        int duration = packet.decodeInt();//or fieldID
                        break;
                    }
                    default: {
                        Logger.logReport("New admin command found (%d)", type);
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void onFuncKeyMappedModified(InPacket packet) {
        int funcType = packet.decodeInt();

        if (funcType == 0) {// KeyModified
            int size = packet.decodeInt();
            if (size <= 0) {
                return;
            }
            for (int i = 0; i < size; i++) {
                int index = packet.decodeInt();
                if (index >= 0 && index < 89) {
                    int type = packet.decodeByte();
                    int ID = packet.decodeInt();
                    funcKeyMapped[index] = new FunckeyMapped(type, ID);
                }
            }

        } else if (funcType == 1) {// PetConsumeItemModified
            this.petConsumeItemID_HP = packet.decodeInt();
        } else if (funcType == 2) {// PetConsumeMPItemModified
            this.petConsumeItemID_MP = packet.decodeInt();
        } else {
            return;
        }
        List<Integer> changed = new ArrayList<>();
        for (int i = 0; i < 89; i++) {
            FunckeyMapped defaultFunc = FunckeyMapped.getDefault()[i];
            FunckeyMapped modifiedFunc = this.funcKeyMapped[i];
            if (modifiedFunc.getType() != defaultFunc.getType() || modifiedFunc.getID() != defaultFunc.getID()) {
                changed.add(i);
            }
        }
        if (this.petConsumeItemID_HP != 0) changed.add(200);
        if (this.petConsumeItemID_MP != 0) changed.add(201);

        List<Integer> data = new ArrayList<>();// data to be saved
        for (Integer keyID : changed) {
            data.add(keyID);
            if (keyID == 200) {
                data.add(2);
                data.add(this.petConsumeItemID_HP);
            } else if (keyID == 201) {
                data.add(2);
                data.add(this.petConsumeItemID_MP);
            } else {
                data.add(funcKeyMapped[keyID].getType());
                data.add(funcKeyMapped[keyID].getID());
            }
        }
        GameDB.rawUpdateFuncKeyMapped(getCharacterID(), data);
    }

    public void onPassiveskillInfoUpdate(InPacket packet) {
        if (packet.decodeInt() - lastPassiveSkillDataUpdate >= 10000) {
            validateStat(false);
            lastPassiveSkillDataUpdate = Utilities.timeGetTime();
        }
    }

    public void onAttack(short type, InPacket packet) {
        if (secondaryStat.getStatOption(CharacterTemporaryStat.DarkSight) != 0) {
            Flag reset = secondaryStat.resetByCTS(CharacterTemporaryStat.DarkSight);
            sendTemporaryStatReset(reset);
            return;
        }
        if (getCurFieldKey() != packet.decodeByte()) {
            return;
        }
        boolean extraByte = false;
        if (packet.decodeByte() == 1) {
            extraByte = true;
        }
        packet.decodeBuffer(3);
        //[01] 00 78 00 00 00 07 D1 02 CB FD 4C 02 D3 06 65 03
        //packet.decodeInt();// ~pDrInfo.dr0
        packet.decodeInt();// ~pDrInfo.dr1
        if (extraByte) packet.decodeByte();
        byte attackInfo = packet.decodeByte();//nDamagePerMob | 16 * nMobCount
        byte damagePerMob = (byte) (attackInfo & 0xF);
        byte mobCount = (byte) ((attackInfo >>> 4) & 0xF);
        int weaponItemID = avatarLook.getEquipped().get(11);
        packet.decodeInt();// ~pDrInfo.dr2
        packet.decodeInt();// ~pDrInfo.dr3
        int skillID = packet.decodeInt();
        int combatOrders = packet.decodeByte();

        packet.decodeInt();// get_rand(pDrInfo.dr0, 0)
        packet.decodeInt();// CCrc32::GetCrc32(pData, 4u, n, 0, 0)

        if (type == ClientPacket.UserMagicAttack) {
            packet.decodeInt();// ~pDrInfo.dr0
            packet.decodeInt();// ~pDrInfo.dr1
            packet.decodeInt();// ~pDrInfo.dr2
            packet.decodeInt();// ~pDrInfo.dr3
            packet.decodeInt();// get_rand(pDrInfo.dr0, 0)
            packet.decodeInt();// CCrc32::GetCrc32(pData, 4u, n, 0, 0)
        }
        packet.decodeInt();// SKILLLEVELDATA::GetCrc(v206)
        packet.decodeInt();// SKILLLEVELDATA::GetCrc(v206)

        int keyDown = 0;
        if (SkillAccessor.isKeyDownSkill(skillID)) {
            keyDown = packet.decodeInt();
        }
        long time = System.currentTimeMillis();
        this.lastAttack = time;
        int bulletItemID = 0;
        int mobTemplateID = 0;
        int banMapMobTemplateID = 0;

        int option = packet.decodeByte();
        if (type == ClientPacket.UserShootAttack) {
            packet.decodeByte();// pUser->m_bNextShootExJablin && CUserLocal::CheckApplyExJablin(pUser, pSkill, nAttackAction);
        }
        if (SkillAccessor.isSkillPrepare(skillID) && skillID != Bowmaster.STORM_ARROW) {
            if (getPreparedSkill() != skillID) {
                Logger.logError("Attack packet without prepare [%d,%d]", getPreparedSkill(), skillID);
                return;
            }
        }
        if (skillID != Bowmaster.STORM_ARROW) setPreparedSkill(0);

        short actionMask = packet.decodeShort();//((_BYTE)bLeft << 7) | nAction & 0x7F
        byte left = (byte) ((actionMask >>> 15) & 1);
        int action = (short) (actionMask & 0x7FFF);
        packet.decodeInt();// GETCRC32Svr<long>(&::pData[24 * nAttackAction], 0x5Fu);
        byte actionType = packet.decodeByte();
        byte speedDegree = packet.decodeByte();
        int cliAttackTime = packet.decodeInt();
        packet.decodeInt();// some affected area info

        if (getHP() > 0 && getField() != null) {
            if (skillID == Fighter.FinalAttack || skillID == Fighter.FinalAttackEx || skillID == Page.FinalAttack || skillID == Page.FinalAttackEx
                    || skillID == Spearman.FinalAttack || skillID == Spearman.FinalAttackEx || skillID == Hunter.FinalAttack_Bow || skillID == Crossbowman.FinalAttack_Crossbow)
                this.lastAttackDelay = this.finalAttackDelay;
            long attackTime = System.currentTimeMillis();
            if (attackCheckIgnoreCnt <= 0) {
                if (attackTime - lastAttackTime >= lastAttackDelay) {
                    attackSpeedErr = 0;
                } else {
                    if (attackSpeedErr == 2) {
                        Logger.logError("[ User ] user's attack speed is abnormally fast [ name=%s, actionNo=%d, skillID=%d, userDelay=%d < minDelay=%d, finalAttack=%d, boosterLevel=%d, fieldid=%d ]");
                        return;
                    }
                    attackSpeedErr++;
                }
            } else {
                attackCheckIgnoreCnt--;
            }
            lastAttackTime = attackTime;
            lastAttackDelay = 0;
            finalAttackDelay = 0;
            short bulletItemPos = 0;
            short cashBulletItemPos = 0;
            byte shootRange = 0;
            byte slv = 0;

            if (type == ClientPacket.UserShootAttack) {
                bulletItemPos = packet.decodeShort();
                cashBulletItemPos = packet.decodeShort();
                shootRange = packet.decodeByte();
                if ((option & 0x40) != 0 && !SkillAccessor.isShootSkillNotConsumingBullet(skillID)) {
                    packet.decodeInt();
                }
            }

            List<AttackInfo> attack = new ArrayList<>(mobCount);
            for (int i = 0; i < mobCount; i++) {
                AttackInfo info = new AttackInfo();
                info.mobID = packet.decodeInt();
                Mob mob = getField().getLifePool().getMob(info.mobID);
                if (mob != null) {
                    mobTemplateID = mob.getTemplateID();
                    if (mob.getTemplate().getBanType() == 2) {
                        banMapMobTemplateID = mobTemplateID;
                    }
                }
                info.hitAction = packet.decodeByte();

                int foreActMask = packet.decodeByte();
                info.foreAction = (byte) (foreActMask & 0x7F);
                info.left = (byte) ((foreActMask >> 7) & 1);

                info.frameIDx = packet.decodeByte();

                int statMask = packet.decodeByte();
                info.calcDamageStatIndex = (byte) (statMask & 0x7F);
                info.doomed = (byte) ((statMask >> 7) & 1);

                info.hit.x = packet.decodeShort();
                info.hit.y = packet.decodeShort();
                info.posPrev.x = packet.decodeShort();
                info.posPrev.y = packet.decodeShort();
                info.delay = packet.decodeShort();
                info.attackCount = damagePerMob;
                for (int j = 0; j < damagePerMob; j++) {
                    int damage = packet.decodeInt();
                    info.damageCli.set(j, damage);
                }
                packet.decodeInt();// mobCrc
                attack.add(info);
            }
            Point userPos = new Point();
            userPos.x = packet.decodeShort();
            userPos.y = packet.decodeShort();

            Point ballStart = new Point();
            if (type == ClientPacket.UserShootAttack) {
                ballStart = new Point(packet.decodeShort(), packet.decodeShort());
            }

            byte attackType;
            if (type == ClientPacket.UserMagicAttack) {
                attackType = 3;
            } else if (type == ClientPacket.UserShootAttack) {
                attackType = 2;
            } else if (type == ClientPacket.UserMeleeAttack) {
                attackType = 1;
            } else {
                Logger.logError("Attack Type Error %d", type);
                return;
            }

            SkillEntry skill = null;
            int maxCount = 1;
            int bulletCount = 1;
            if (skillID > 0) {
                if (lock()) {
                    try {
                        slv = (byte) SkillInfo.getInstance().getSkillLevel(character, skillID, null);
                        if (slv > 0) {
                            skill = SkillInfo.getInstance().getSkill(skillID);
                            if (skillID != Cleric.Heal && !SkillInfo.getInstance().adjustConsumeForActiveSkill(this, skillID, slv, false, 0)) {
                                Logger.logReport("Failed to adjust consume for active skill!!! (SkillID:%d,SLV:%d)", skillID, slv);
                                getCalcDamage().skip();
                                return;
                            }
                            if (skill != null) {
                                SkillLevelData levelData = skill.getLevelData(slv);
                                if (levelData != null) {
                                    bulletCount = Math.max(1, levelData.BulletCount);
                                    if (skillID == Hunter.ArrowBomb) {
                                        maxCount = 16;
                                    } else {
                                        // TODO: Check for all skills that can hit multiple mobs.
                                        maxCount = 16;
                                    }
                                }
                            }
                        }
                    } finally {
                        unlock();
                    }
                }
            }
            if (maxCount <= 1)
                maxCount = 1;
            if (maxCount < mobCount) {
                Logger.logError("Invalid mob count (Skill:%d,Lv:%d,Mob:%d)", skillID, slv, mobTemplateID);
            } else {
                if (bulletItemPos > 0) {
                    List<ChangeLog> changeLog = new ArrayList<>();
                    ItemSlotBase item = character.getItem(ItemType.Consume, bulletItemPos);
                    if (item != null) {
                        if (character.getItemTrading().get(ItemType.Consume).get(bulletItemPos) != 0 || item.getItemNumber() < bulletCount) {
                            getCalcDamage().skip();
                            return;
                        }
                        if (ItemAccessor.isJavelinItem(item.getItemID())) {
                            if (Inventory.rawWasteItem(this, bulletItemPos, (short) bulletCount, changeLog)) {
                                addCharacterDataMod(DBChar.ItemSlotConsume);
                                bulletItemID = item.getItemID();
                                Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            }
                        } else if (secondaryStat.getStatOption(CharacterTemporaryStat.SoulArrow) == 0) {
                            Pointer<Integer> decRet = new Pointer<>(0);
                            if (Inventory.rawRemoveItem(this, ItemType.Consume, bulletItemPos, (short) bulletCount, changeLog, decRet, null) && decRet.get() == bulletCount) {
                                addCharacterDataMod(DBChar.ItemSlotConsume);
                                bulletItemID = item.getItemID();
                                Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            } else {
                                Logger.logError("Invalid skill info in attack packet (nItemPos: %d, nCount: %d, nDecRet: %d)", bulletItemPos, bulletCount, decRet.get());
                            }
                        }
                        changeLog.clear();
                    }
                }
                int advancedChargeProp = 0;
                int advancedChargeDamage = 0;
                if (getCharacter().getCharacterStat().getJob() == JobAccessor.PALADIN.getJob()) {
                    Pointer<SkillEntry> advCharge = new Pointer<>();
                    int advChargeSLV = SkillInfo.getInstance().getSkillLevel(getCharacter(), Paladin.ADVANCED_CHARGE, advCharge);
                    if (advChargeSLV > 0) {
                        advancedChargeDamage = advCharge.get().getLevelData(advChargeSLV).Damage;
                    }
                }
                boolean successAttack = getField().getLifePool().onUserAttack(this, type, attackType, mobCount, damagePerMob, skill, slv, action, left, speedDegree, bulletItemID, attack, ballStart);

                SecondaryStatOption comboOpt = secondaryStat.getStat(CharacterTemporaryStat.ComboCounter);
                int oldOption = comboOpt.getOption();
                if (oldOption != 0 && type == ClientPacket.UserMeleeAttack && skillID != Crusader.Shout) {
                    if (successAttack) {
                        int prop = comboOpt.getModOption() >> 16;
                        int maxOrbCount = comboOpt.getModOption() & 0xFFFF;
                        Logger.logReport("Prop = [%d] | Mod Option = [%d]", prop, maxOrbCount);

                        int incComboCounter = 1;
                        if (oldOption != maxOrbCount) {
                            incComboCounter = (Rand32.genRandom() % 100 < prop ? 1 : 0) + 1;
                        }
                        int newComboCounter = incComboCounter + oldOption;
                        newComboCounter = Math.min(newComboCounter, maxOrbCount + 1);
                        comboOpt.setOption(newComboCounter);
                    }
                    if (skillID == Crusader.Coma || skillID == Crusader.Panic) {
                        comboOpt.setOption(1);
                    }
                    secondaryStat.setStat(CharacterTemporaryStat.ComboCounter, comboOpt);
                    if (comboOpt.getOption() != oldOption) {
                        sendTemporaryStatSet(CharacterTemporaryStat.getMask(CharacterTemporaryStat.ComboCounter));
                    }
                }
                int job = getCharacter().getCharacterStat().getJob();
                int wt = ItemAccessor.getWeaponType(weaponItemID);

                int mobStatSkillID = 0;
                int mobStatSkillDelay = 0;

                if (secondaryStat.getStatOption(CharacterTemporaryStat.HamString) != 0
                &&  wt == ItemAccessor.WeaponTypeFlag.BOW
                &&  skillID != Hunter.PowerKnockback
                &&  skillID != Hunter.ArrowBomb) {
                    mobStatSkillID = Bowmaster.HAMSTRING;
                    mobStatSkillDelay = 1000;
                }
                if (secondaryStat.getStatOption(CharacterTemporaryStat.Blind) != 0
                && wt == ItemAccessor.WeaponTypeFlag.CROSSBOW
                && skillID != Crossbowman.PowerKnockback) {
                    mobStatSkillID = CrossbowMaster.BLIND;
                    mobStatSkillDelay = 1000;
                }
                if (job == 412 && wt == ItemAccessor.WeaponTypeFlag.THROWINGGLOVE
                &&  skillID != 0 && type == ClientPacket.UserShootAttack
                &&  skillID != Assassin.Drain
                &&  skillID != Hermit.SHADOW_MESO
                &&  skillID != NightLord.SHOWDOWN
                &&  skillID != NightLord.NINJA_STORM) {
                    mobStatSkillID = NightLord.VENOM;
                    mobStatSkillDelay = 1000;
                }
                if (job == 422 && wt == ItemAccessor.WeaponTypeFlag.DAGGER
                &&  skillID != Thief.Steal
                &&  skillID != ThiefMaster.THIEVES
                &&  skillID != ThiefMaster.MESO_EXPLOSION
                &&  skillID != Shadower.SHOWDOWN) {
                    mobStatSkillID = Shadower.VENOM;
                    mobStatSkillDelay = 1000;
                }
                if (mobStatSkillID != 0) {
                    Pointer<SkillEntry> mobStatSkill = new Pointer<>();
                    int mobStatSLV = SkillInfo.getInstance().getSkillLevel(getCharacter(), mobStatSkillID, mobStatSkill);
                    if (mobStatSLV > 0 && mobStatSkill.get() != null && mobCount > 0) {
                        for (int i = 0; i < mobCount; i++) {
                            if (damagePerMob <= 0) {
                                break;
                            }
                            AttackInfo att = attack.get(i);
                            for (int j = 0; j < damagePerMob; j++) {
                                if (att.damageCli.get(j) <= 0) {
                                    break;
                                }
                                getField().getLifePool().onMobStatChangeSkill(this, att.mobID, mobStatSkill.get(), (byte) mobStatSLV);
                            }
                        }
                    }
                }
                if (skillID != 0) {
                    SkillLevelData levelData = skill.getLevelData(slv);
                    if (skillID == Mage1.POISON_MIST) {
                        int delay = 700;
                        long start = time + delay;
                        long end = start + 1000 * levelData.Time;
                        Rect rect = levelData.affectedArea.copy();
                        rect.offsetRect(userPos.getX(), userPos.getY());
                        getField().getAffectedAreaPool().insertAffectedArea(false, getCharacterID(), skillID, slv, start, end, userPos, rect);
                    }
                    if (skillID == NightLord.NINJA_STORM && mobCount > 0) {
                        for (int i = 0; i < mobCount; i++) {
                            AttackInfo atk = attack.get(i);
                            if (atk.damageCli.get(i) <= 0) {
                                continue;
                            }
                            getField().getLifePool().onMobStatChangeSkill(this, atk.mobID, skill, slv);
                        }
                    }
                    // meso explosion drop remove here
                    if (skillID == DragonKnight.DRAGON_ROAR) {
                        sendTemporaryStatSet(secondaryStat.setStat(CharacterTemporaryStat.Stun, new SecondaryStatOption(1, DragonKnight.DRAGON_ROAR, time + 1000 * 2)));
                    }
                    if (banMapMobTemplateID != 0) {
                        banMapByMob(banMapMobTemplateID);
                    }
                }
            }
        }
    }

    public void onBroadcastMsg(InPacket packet) {
        if (isGM() && getField() != null) {
            byte bmType = packet.decodeByte();
            String msg = packet.decodeString();

            getChannel().broadcast(WvsContext.onBroadcastMsg(bmType, msg));
        }
    }

    public void onChat(InPacket packet) {
        if (getField() == null) {
            return;
        }
        packet.decodeInt();
        String text = packet.decodeString();

        if (text.charAt(0) == '!' || text.charAt(0) == '@') {
            CommandHandler.handle(this, text);
            return;
        }

        getField().splitSendPacket(getSplit(), UserCommon.onChat(characterID, text), null);
    }

    public void onCharacterInfoRequest(InPacket packet) {
        User target = getField().findUser(packet.decodeInt());
        if (target == null || target.isGM()) {
            sendCharacterStat(Request.Excl, 0);
        } else {
            if (target.lock()) {
                try {
                    sendPacket(WvsContext.onCharacterInfo(target));
                } finally {
                    target.unlock();
                }
            }
        }
    }

    public void onPortalScriptRequest(InPacket packet) {
        if (getField() == null || getCurFieldKey() != packet.decodeByte()) {
            return;
        }
        String portalName = packet.decodeString();
        Portal portal = getField().getPortal().findPortal(portalName);
        if (portal != null && portal.isScriptPortal()) {
            int userPosX = packet.decodeShort();
            int userPosY = packet.decodeShort();
            int rangeX = Math.abs(portal.getPortalPos().x - userPosX);
            int rangeY = Math.abs(portal.getPortalPos().y - userPosY);
            if (rangeX > 100 || rangeY > 100) {
                Logger.logReport("Invalid Script Portal Access Position ( CID:%d, FieldID:%d, PortalPos(%d, %d), UserPos(%d, %d) )", getCharacterID(), getField().getFieldID(), portal.getPortalPos().x, portal.getPortalPos().y, userPosX, userPosY);
                // CUser::SendMacroLog("Invalid Script Portal Access Position, Level : %d", getLevel());
            }
            // CCheatInspector::InsertUserAction(&v2->m_cheatInspector, __PAIR__(v9, ptPos), 3);
            // CCheatInspector::CheckUserActionPosition(&v23.p[4]._m_pPrev, v23.p, 0, 1);
            String scriptName = portal.getPortalScriptName();
            if (scriptName != null && !scriptName.isEmpty()) {
                ScriptVM script = new ScriptVM();
                if (script.setScript(this, "portals/" + scriptName, portal)) {
                    script.run(this);
                }
            }
            sendCharacterStat(Request.Excl, 0);
        }
    }

    public void onQuestRequest(InPacket packet) {
        if (getField() == null) {
            //sendCharacterStat(Request.Excl, 0);
            return;
        }
        if (lock()) {
            try {
                // CQuest in v95 idb can help for rewards :)
                int type = packet.decodeByte();
                int questID = packet.decodeShort();

                NpcTemplate template = null;
                Npc npc = null;
                if (type != QuestFlag.QuestReq_LostItem && type != QuestFlag.QuestReq_ResignQuest) {
                    template = NpcTemplate.getNpcTemplate(packet.decodeInt());
                    if (template == null) {
                        return;
                    }
                    if (!QuestMan.getInstance().isAutoStartQuest(questID)) {
                        npc = getField().getLifePool().getNpc(template.getName());
                        if (npc == null) {
                            return;
                        }
                        short userPosX = packet.decodeShort();
                        short userPosY = packet.decodeShort();

                        int rangeX = Math.abs(npc.getCurrentPos().x - userPosX);
                        int rangeY = Math.abs(npc.getCurrentPos().y - userPosY);
                        if (rangeX > 1200 || rangeY > 800) {
                            //Logger.logReport("Invalid NPC(Quest) Access Position ( CID:%d, FieldID:%d, NPCPos(%d, %d), UserPos(%d, %d) )", getCharacterID(), getField().getFieldID(), npc.getCurrentPos().x, npc.getCurrentPos().y, userPosX, userPosY);
                            //CUser::SendMacroLog("Invalid NPC(Quest) Access Position, Level : %d", getLevel());
                            //return;
                        }
                        // CCheatInspector::InsertUserAction(&v2->m_cheatInspector, (tagPOINT)result, 2);
                    }
                }
                switch (type) {
                    case QuestFlag.QuestReq_LostItem:
                        lostQuestItem(packet, questID);
                        break;
                    case QuestFlag.QuestReq_AcceptQuest:
                        acceptQuest(packet, questID, template.getTemplateID(), npc);
                        break;
                    case QuestFlag.QuestReq_CompleteQuest:
                        completeQuest(packet, questID, template.getTemplateID(), npc, false);
                        break;
                    case QuestFlag.QuestReq_ResignQuest:
                        resignQuest(packet, questID);
                        break;
                    case QuestFlag.QuestReq_OpeningScript:
                        scriptLinkedQuest(packet, questID, template.getTemplateID(), npc, 0);
                        break;
                    case QuestFlag.QuestReq_CompleteScript:
                        scriptLinkedQuest(packet, questID, template.getTemplateID(), npc, 1);
                        break;
                }
            } finally {
                unlock();
            }
        }
    }

    public void onChangeSlotPositionRequest(InPacket packet) {
        packet.decodeInt();
        byte type = packet.decodeByte();
        short oldPos = packet.decodeShort();
        short newPos = packet.decodeShort();
        short count = packet.decodeShort();
        Inventory.changeSlotPosition(this, Request.Excl, type, oldPos, newPos, count);
    }

    public void onChangeStatRequest(InPacket packet) {
        if (getField() == null) {
            return;
        }
        short hp = 0;
        short mp = 0;
        packet.decodeInt();
        int flag = packet.decodeInt();
        if ((flag & CharacterStatType.HP) != 0) {
            hp = packet.decodeShort();
        }
        if ((flag & CharacterStatType.MP) != 0) {
            mp = packet.decodeShort();
        }
        byte option = packet.decodeByte();
        double recoveryRate = getField().getRecoveryRate();
        if (lock()) {
            try {
                if (getHP() == 0) {
                    return;
                }
                long time = System.currentTimeMillis();
                if (hp > 0) {
                    int restForHPDuration = 10000;
                    if (JobAccessor.getJobCategory(character.getCharacterStat().getJob()) == JobCategory.FIGHTER
                            && SkillInfo.getInstance().getSkillLevel(character, Warrior.ImproveBasic) > 0) {
                        restForHPDuration = 5000;
                    }
                    if ((option & 1) != 0) {
                        restForHPDuration = SkillAccessor.getEndureDuration(character);
                    }
                    if ((time - this.lastCharacterHPInc) < restForHPDuration - 2000) {
                        ++illegalHPIncTime;
                    }
                    if (illegalHPIncTime > 9) {
                        Logger.logError("Illegal HP recovery time : %d", characterID);
                        return;
                    }
                    int recoveryHP = (int) ((SkillAccessor.getHPRecoveryUpgrade(character) + 10.0d) * recoveryRate);
                    if (recoveryHP < hp) {
                        ++illegalHPIncSize;
                    }
                    if (illegalHPIncSize > 9) {
                        Logger.logError("Illegal HP recovery size : %d", characterID);
                        return;
                    }
                    incHP(recoveryHP, false);
                    lastCharacterHPInc = time;
                }
                if (mp > 0) {
                    if ((time - lastCharacterMPInc) < 8000) {
                        ++illegalMPIncTime;
                    }
                    if (illegalMPIncTime > 7) {
                        Logger.logError("Illegal MP recovery time : %d", characterID);
                        return;
                    }
                    int recoveryMP = (int) ((SkillAccessor.getMPRecoveryUpgrade(character) + 3.0d) * recoveryRate);
                    if (recoveryMP < mp) {
                        ++illegalMPIncSize;
                    }
                    if (illegalMPIncSize > 7) {
                        Logger.logError("Illegal MP recovery size : %d (nMP : %d)", recoveryMP, mp);
                        return;
                    }
                    incMP(recoveryMP, false);
                    lastCharacterMPInc = time;
                }
                sendCharacterStat(Request.None, flag);
            } finally {
                unlock();
            }
        }
    }

    public void onConsumeCashItemUseRequest(InPacket packet) {
        short pos = packet.decodeShort();
        int itemID = packet.decodeInt();
        String message = packet.decodeString();

        List<ChangeLog> changeLog = new ArrayList<>();
        Pointer<Integer> decRet = new Pointer<>(0);
        if (Inventory.rawRemoveItem(this, ItemType.Consume, pos, (short) 1, changeLog, decRet, null) && decRet.get() == 1) {
            if (ItemAccessor.isWeatherItem(itemID)) {
                getField().onWeather(itemID, message, 8000);
            } else {
                // idk if this is evan a megaphone packet or not, yolo it works
                getField().broadcastPacket(FieldPacket.onGroupMessage(characterName, message), false);
            }
            Inventory.sendInventoryOperation(this, Request.Excl, changeLog);
        } else {
            sendCharacterStat(Request.Excl, 0);
        }
        changeLog.clear();
    }

    public void onUpgradeItemRequest(InPacket packet) {
        Inventory.upgradeEquip(this, packet.decodeShort(), packet.decodeShort());
    }

    public void onEmotion(InPacket packet) {
        if (getField() != null) {
            emotion = packet.decodeInt();
            if (emotion < 8) {
                getField().splitSendPacket(getSplit(), UserRemote.onEmotion(this.characterID, emotion), this);
            }
        }
    }

    public void onDropMoneyRequest(InPacket packet) {
        if (getHP() == 0) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        int amount = packet.decodeInt();
        if (amount >= 10 && amount <= 50000) {
            if (character.getCharacterStat().getLevel() <= 15) {
                // not sure if this even exists actually
            }
            if (getField() != null) {
                Pointer<Integer> y2 = new Pointer<>(0);
                if (getField().getSpace2D().getFootholdUnderneath(getCurrentPosition().x, getCurrentPosition().y, y2) != null) {
                    if (!incMoney(-amount, true, true)) {
                        return;
                    }
                    sendCharacterStat(Request.Excl, CharacterStatType.Money);
                    Reward reward = new Reward();
                    reward.setMoney(amount);
                    reward.setType(RewardType.MONEY);
                    reward.setPeriod(0);
                    int x = getCurrentPosition().x;
                    int y1 = getCurrentPosition().y;
                    getField().getDropPool().create(reward, this.characterID, 0, x, y1, x, y2.get(), 0, false, 0);
                }
            }
        }
    }

    public void onGivePopularityRequest(InPacket packet) {
        int targetID = packet.decodeInt();
        User target = getField().findUser(targetID);
        if (target == null || target == this) {
            sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.InvalidCharacterID, null, false));
        } else {
            if (character.getCharacterStat().getLevel() < 15) {
                sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.LevelLow, null, false));
            } else {
                boolean incFame = packet.decodeBool();

                byte ret = GameDB.rawCheckGivePopularity(characterID, targetID);
                if (ret == GivePopularityRes.Success) {
                    target.incPOP(incFame ? 1 : -1, true);
                    target.sendCharacterStat(Request.None, CharacterStatType.POP);
                    target.sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.Notify, getCharacterName(), incFame));

                    sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.Success, target.getCharacterName(), incFame));
                } else {
                    sendPacket(WvsContext.onGivePopularityResult(ret, null, false));
                }
            }
        }
    }

    public void onHit(InPacket packet) {
        packet.decodeInt();// time
        byte mobAttackIdx = packet.decodeByte();
        packet.decodeByte();// magic elem attr (not sure if obstacle hit has it)

        int obstacleData = 0;
        int clientDamage = 0;
        int mobTemplateID = 0;
        byte left = 0;
        byte reflect = 0;
        int mobID = 0;
        byte hitAction = 0;
        int damage = 0;
        boolean guard = false;
        Point hit = new Point(0, 0);
        if (mobAttackIdx <= AttackIndex.Counter) {
            obstacleData = packet.decodeInt();
        } else {
            clientDamage = packet.decodeInt();
            mobTemplateID = packet.decodeInt();
            mobID = packet.decodeInt();
            left = packet.decodeByte();
            reflect = packet.decodeByte();// nX
            guard = packet.decodeBool();// bGuard
            packet.decodeByte();// bKnockback
            packet.decodeByte();// nPowerGuard
            if (reflect != 0) {
                mobID = packet.decodeInt();
                hitAction = packet.decodeByte();
                hit.x = packet.decodeShort();
                hit.y = packet.decodeShort();
                packet.decodeShort();// user x ?
                packet.decodeShort();// user y ?
            }
        }
        if (getField() == null) {
            return;
        }
        if (lock()) {
            try {
                if (getHP() > 0) {
                    if (clientDamage > 0) {
                        if (mobAttackIdx > AttackIndex.Counter) {
                            this.invalidDamageMissCount = 0;
                        }
                        int mp = 0;
                        int magicGuard = secondaryStat.getStatOption(CharacterTemporaryStat.MagicGuard);
                        if (magicGuard > 0) {
                            int inc = (int) (clientDamage * (double) magicGuard / 100.0d);
                            if (character.getCharacterStat().getMP() < inc) {
                                inc = character.getCharacterStat().getMP();
                            }
                            clientDamage -= inc;
                            mp = inc;
                        }
                        MobTemplate template = MobTemplate.getMobTemplate(mobTemplateID);
                        if (template != null) {
                            MobAttackInfo info = null;
                            if (mobAttackIdx >= AttackIndex.Mob_Physical && !template.getAttackInfo().isEmpty() && mobAttackIdx < template.getAttackInfo().size()) {
                                info = template.getAttackInfo().get(mobAttackIdx);
                            }
                            if (info != null) {
                                // deadlyAttack
                                // mpBurn
                            }
                            int powerGuard = secondaryStat.getStatOption(CharacterTemporaryStat.PowerGuard);
                            if (reflect != 0 && powerGuard != 0) {
                                if (powerGuard < reflect) {
                                    reflect = (byte) powerGuard;
                                }
                                int hpDealt = clientDamage;
                                if (hpDealt >= getHP()) {
                                    hpDealt = getHP();
                                }
                                damage = Math.min(reflect * hpDealt / 100, (int) (template.getMaxHP() / 10.0d));
                                if (template.isBoss()) {
                                    damage /= 2;
                                }
                                if (damage > 0) {
                                    // if fixedDamage > 0
                                    //  damage = fixedDamage
                                    // if invincible
                                    //  damage = 0
                                }
                                clientDamage -= damage;
                            }
                        }
                        incHP(-clientDamage, false);
                        int flag = CharacterStatType.HP;
                        if (mp != 0) {
                            incMP(-mp, false);
                            flag |= CharacterStatType.MP;
                        }
                        if (flag != 0) {
                            sendCharacterStat(Request.None, flag);
                        }
                    }
                    if (getHP() == 0) {
                        // This is where Nexon handles onUserDead, we call it in incHP.
                    } else {
                        // TODO: Look into this. ObstacleData might be ObstacleDamage.
                        if (clientDamage > 0) {
                            if (obstacleData > 0) {
                                int slv = obstacleData & 0xFF;
                                int skillID = (obstacleData >> 8) & 0xFF;

                                // Can't continue because MobSkills don't exist yet?
                                // Unless they do, but aren't in Skill.wz? o.O
                            } else if (mobTemplateID != 0) {
                                //onStatChangedByMobAttack(mobTemplateID, mobAttackIdx);
                            }
                        }
                    }
                }
            } finally {
                unlock();
            }
        }
        getField().splitSendPacket(getSplit(), UserRemote.onHit(this.characterID, mobAttackIdx, clientDamage, mobTemplateID, left, reflect, mobID, hitAction, hit), this);
        if (damage != 0) {
            getField().getLifePool().onUserAttack(this, mobID, damage, hit, (short) 100);
        }
    }

    public void onMigrateToCashShopRequest() {
        if (getField() == null || getSocket() == null || this.nexonClubID == null || this.nexonClubID.isEmpty()) {
            return;
        }

        if (lock()) {
            try {
                if (canAttachAdditionalProcess()) {
                    this.onTransferField = true;

                    OutPacket packet = new OutPacket(CenterPacket.ShopMigrateReq);
                    packet.encodeInt(getCharacterID());
                    packet.encodeByte(getWorldID());
                    packet.encodeByte(getChannelID());

                    getChannel().getCenter().sendPacket(packet);
                }
            } finally {
                unlock();
            }
        }
    }

    public void onPortalScrollUseRequest(InPacket packet) {
        if (getField() == null) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        if (lock()) {
            try {
                short pos = packet.decodeShort();
                int itemID = packet.decodeInt();
                ItemSlotBase item = character.getItem(ItemType.Consume, pos);
                PortalScrollItem info = ItemInfo.getPortalScrollItem(itemID);
                if (item == null || item.getItemID() != itemID || info == null || info.getItemID() != itemID) {
                    Logger.logError("Incorrect portal-scroll-item use request nPOS(%d), nItemID(%d), pInfo(%p)", pos, itemID);
                    //Logger.logError("Packet Dump: %s", packet.dumpString());
                    closeSocket();
                    return;
                }
                int fieldID = info.getMoveTo();
                if (fieldID == -1) {
                    fieldID = getField().getReturnFieldID();
                } else {
                    if ((getField().getOption() & FieldOpt.PortalScrollLimit) != 0) {
                        sendCharacterStat(Request.Excl, 0);
                        return;
                    }
                }
                if (!canAttachAdditionalProcess() || fieldID == Field.Invalid || fieldID == getField().getFieldID()) {
                    sendCharacterStat(Request.Excl, 0);
                } else {
                    if (FieldMan.getInstance(getChannelID()).isConnected(getField().getFieldID(), fieldID)) {
                        List<ChangeLog> changeLog = new ArrayList<>();
                        Pointer<Integer> decRet = new Pointer<>(0);
                        if (Inventory.rawRemoveItem(this, ItemType.Consume, pos, (short) 1, changeLog, decRet, null) && decRet.get() == 1) {
                            Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            addCharacterDataMod(DBChar.ItemSlotConsume);
                            postTransferField(fieldID, "", false);
                            sendCharacterStat(Request.Excl, 0);
                            changeLog.clear();
                            return;
                        }
                        Logger.logError("Incorrect portal-scroll-item use request nPOS(%d), nItemID(%d), pInfo(%p)", pos, itemID);
                        //Logger.logError("Packet Dump: %s", packet.dumpString());
                        closeSocket();
                        changeLog.clear();
                    } else {
                        sendPacket(FieldPacket.onTransferFieldReqIgnored());
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void onStatChangeItemUseRequest(InPacket packet) {
        if (getHP() == 0 || getField() == null || secondaryStat.getStatOption(CharacterTemporaryStat.DarkSight) != 0) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        packet.decodeInt();
        short pos = packet.decodeShort();
        int itemID = packet.decodeInt();
        ItemSlotBase item = character.getItem(ItemType.Consume, pos);
        StateChangeItem sci = ItemInfo.getStateChangeItem(itemID);
        if (item == null || item.getItemID() != itemID || sci == null) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        List<ChangeLog> changeLog = new ArrayList<>();
        Pointer<Integer> decRet = new Pointer<>(0);
        if (!InventoryManipulator.rawRemoveItem(character, ItemType.Consume, pos, 1, changeLog, decRet, new Pointer<>()) || decRet.get() != 1) {
            changeLog.clear();
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        sendPacket(InventoryManipulator.makeInventoryOperation(Request.None, changeLog));

        Flag flag = sci.getInfo().apply(this, sci.getItemID(), character, basicStat, secondaryStat, System.currentTimeMillis(), false);
        addCharacterDataMod(DBChar.ItemSlotConsume | DBChar.Character);
        sendCharacterStat(Request.Excl, sci.getInfo().getFlag());
        sendTemporaryStatSet(flag);
    }

    public void onScriptMessageAnswer(InPacket packet) {
        if (lock()) {
            try {
                if (runningVM != null) {
                    runningVM.getScriptSys().onScriptMessageAnswer(this, packet);
                }
            } finally {
                unlock();
            }
        }
    }

    public void onSelectNpc(InPacket packet) {
        if (getField() != null) {
            long time = System.currentTimeMillis();
            if (lastSelectNPCTime == 0 || time <= lastSelectNPCTime || lastSelectNPCTime <= time - 500) {
                this.lastSelectNPCTime = time;

                Npc npc = getField().getLifePool().getNpc(packet.decodeInt());
                if (npc != null) {
                    if (npc.getNpcTemplate().getScript() != null && !npc.getNpcTemplate().getScript().isEmpty()) {
                        ScriptVM script = new ScriptVM();
                        if (script.setScript(this, "npcs/" + npc.getNpcTemplate().getScript(), npc)) {
                            script.run(this);
                        }
                    } else {
                        if (lock()) {
                            try {
                                if (!canAttachAdditionalProcess()) {
                                    return;
                                }

                                if (!npc.getNpcTemplate().getShopItem().isEmpty()) {
                                    this.tradingNpc = npc;
                                    sendPacket(ShopDlg.onOpenShopDlg(this, npc.getNpcTemplate()));
                                }
                            } finally {
                                unlock();
                            }
                        }
                    }
                }
            }
        }
    }

    public void onShopRequest(InPacket packet) {
        if (lock(1000)) {
            try {
                if (tradingNpc == null) {
                    return;
                }
                Npc npc = tradingNpc;
                NpcTemplate npcTemplate = npc.getNpcTemplate();
                byte mode = packet.decodeByte();
                switch (mode) {
                    case ShopResCode.Buy: {
                        short pos = packet.decodeShort();
                        int itemID = packet.decodeInt();
                        short count = packet.decodeShort();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        ShopItem shopItem = null;
                        if (pos < 0 || npcTemplate.getShopItem().isEmpty() || pos >= npcTemplate.getShopItem().size()
                                || (shopItem = npcTemplate.getShopItem().get(pos)).itemID != itemID || count <= 0
                                || (!ItemAccessor.isBundleTypeIndex(ti)
                                || ItemAccessor.isRechargeableItem(itemID)) && count != 1) {
                            Logger.logError("Incorrect shop request");
                            //Logger.logError("Packet Dump: %s", packet.dumpString());
                            closeSocket();
                            return;
                        }
                        int price = shopItem.price;
                        int stockPrice = count * price;
                        if (character.getCharacterStat().getLevel() <= 15) {
                            //checkTradeLimitTime();
                            if (stockPrice + getTradeMoneyLimit() > 1000000) {
                                return;
                            }
                            setTempTradeMoney(stockPrice);
                        }
                        if (stockPrice <= 0 || character.getCharacterStat().getMoney() - character.getMoneyTrading() < stockPrice) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown2));
                        } else {
                            if (npc.decShopItemCount(shopItem.itemID, count)) {
                                List<ExchangeElem> exchange = new ArrayList<>();
                                ExchangeElem exchangeElem = new ExchangeElem();
                                ItemSlotBase item = ItemInfo.getItemSlot(shopItem.itemID, ItemVariationOption.None);
                                if (item == null) {
                                    npc.incShopItemCount(shopItem.itemID, count);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                    return;
                                }
                                if (ItemAccessor.isRechargeableItem(shopItem.itemID)) {
                                    int number = SkillInfo.getInstance().getBundleItemMaxPerSlot(shopItem.itemID, character);
                                    item.setItemNumber(number);
                                } else {
                                    //I believe this is actually pShopItem.nQuantity
                                    int number = item.getItemNumber();// + pShopItem.
                                    if (number <= 1)
                                        number = count;
                                    item.setItemNumber(number);
                                }
                                exchangeElem.initAdd((short) 0, (short) 0, item);
                                exchange.add(exchangeElem);
                                if (!Inventory.exchange(this, -stockPrice, exchange, null, null)) {
                                    npc.incShopItemCount(shopItem.itemID, count);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                    return;
                                }
                                if (character.getCharacterStat().getLevel() <= 15) {
                                    setTradeMoneyLimit(getTradeMoneyLimit() + getTempTradeMoney());
                                }
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Success));//BuySuccess
                            } else {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.NoStock));//BuyNoStock
                            }
                        }
                        break;
                    }
                    case ShopResCode.Sell: {
                        short pos = packet.decodeShort();
                        int itemID = packet.decodeInt();
                        short count = packet.decodeShort();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        if (pos <= 0 || count <= 0 || ti <= ItemType.NotDefine || ti >= ItemType.NO
                                || (!ItemAccessor.isBundleTypeIndex(ti) || ItemAccessor.isRechargeableItem(itemID)) && count != 1) {
                            Logger.logError("Incorrect shop request");
                            //Logger.logError("Packet Dump: %s", packet.dumpString());
                            closeSocket();
                            return;
                        }
                        ItemSlotBase item = character.getItem(ti, pos);
                        if (item == null || item.getItemID() != itemID) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown3));
                            return;
                        } else if (ItemInfo.isCashItem(item.getItemID()) || item.isCashItem()) {
                            Logger.logError("Selling Invalid Item in Shop [%s] (%d,%d:%d)", characterName, pos, itemID, count);
                            closeSocket();
                            return;
                        }
                        int inc;
                        long uInc = 0;
                        if (ti == ItemType.Equip) {
                            EquipItem pInfo = ItemInfo.getEquipItem(itemID);
                            if (pInfo == null) {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                return;
                            }
                            inc = pInfo.getSellPrice();
                        } else {
                            BundleItem info = ItemInfo.getBundleItem(item.getItemID());
                            if (info == null) {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                return;
                            }
                            if (ItemAccessor.isRechargeableItem(itemID)) {
                                int number = item.getItemNumber();
                                double unitPrice = Math.ceil((double) number * info.getUnitPrice());
                                uInc = (info.getSellPrice() + (long) unitPrice) >> 32;
                                inc = (int) (info.getSellPrice() + (long) unitPrice);
                            } else {
                                inc = count * info.getSellPrice();
                                uInc = (info.getSellPrice() + (long) count) >> 32;
                            }
                        }
                        int money = inc + character.getCharacterStat().getMoney();
                        if (money > 0 && uInc == 0 && inc >= 0) {
                            List<ChangeLog> changeLog = new ArrayList<>();
                            Pointer<Integer> decRet = new Pointer<>(0);
                            ItemSlotBase itemRemoved = item.makeClone();
                            if (!InventoryManipulator.rawRemoveItem(character, ti, pos, count, changeLog, decRet, null) || decRet.get() < count) {
                                character.setItem(ti, pos, itemRemoved);
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown2));
                                return;
                            }
                            addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                            Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            incMoney(inc, false, true);
                            sendCharacterStat(Request.None, CharacterStatType.Money);
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Success));//BuySuccess
                            changeLog.clear();
                        }
                        break;
                    }
                    case ShopResCode.Recharge: {
                        short pos = packet.decodeShort();
                        if (pos <= 0) {
                            Logger.logError("Incorrect shop request nPOS(%d)", pos);
                            //Logger.LogError("Packet Dump: %s", packet.DumpString());
                            closeSocket();
                            return;
                        }
                        ItemSlotBase item = character.getItem(ItemType.Consume, pos);
                        if (item == null || !ItemAccessor.isRechargeableItem(item.getItemID())) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown3));
                            return;
                        }
                        int maxPerSlot = SkillInfo.getInstance().getBundleItemMaxPerSlot(item.getItemID(), character);
                        int count = maxPerSlot - item.getItemNumber();
                        double price = npc.getShopRechargePrice(item.getItemID());
                        if (count <= 0 || price <= 0.0) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown3));
                            return;
                        }
                        if (npc.decShopItemCount(item.getItemID(), count)) {
                            double inc = Math.ceil((double) count * price);
                            if (((long) inc >> 32) != 0 || inc <= 0 || character.getCharacterStat().getMoney() - character.getMoneyTrading() < inc) {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                            } else {
                                List<ChangeLog> changeLog = new ArrayList<>();
                                if (Inventory.rawRechargeItem(this, pos, changeLog)) {
                                    addCharacterDataMod(DBChar.ItemSlotConsume);
                                    Inventory.sendInventoryOperation(this, Request.None, changeLog);
                                    incMoney(-(int) inc, false, true);
                                    sendCharacterStat(Request.None, CharacterStatType.Money);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Success));//RechargeSuccess
                                } else {
                                    npc.incShopItemCount(item.getItemID(), count);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                }
                                changeLog.clear();
                            }
                        } else {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.NoStock));//RechargeNoStock
                        }
                        break;
                    }
                    case ShopResCode.Close: {
                        this.tradingNpc = null;
                        break;
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void onTransferFieldRequest(InPacket packet) {
        if (getField() == null || socket == null) {
            closeSocket();
            return;
        }
        postTransferField(null, null, false, null, false, packet);
    }

    public void onMove(InPacket packet) {
        packet.decodeInt();// drInfo.dr0
        packet.decodeInt();// drInfo.dr1
        int fieldKey = packet.decodeByte(); // CField::GetFieldKey(get_field())
        packet.decodeInt();// drInfo.dr2
        packet.decodeInt();// drInfo.dr3
        if (getField() != null) {
            Rect move = new Rect();
            getField().onUserMove(this, packet, move);
        }
    }

    public void onWhisper(InPacket packet) {
        byte flag = packet.decodeByte();
        String target = packet.decodeString();
        if (flag == WhisperFlags.ReplyRequest) {
            String text = packet.decodeString();

            User user = getChannel().findUserByName(target, true);
            boolean success = false;
            if (user != null && user.getField() != null && user.getField().getFieldID() != 0) {
                user.sendPacket(FieldPacket.onWhisper(WhisperFlags.ReplyReceive, null, getCharacterName(), text, -1, false));

                target = user.getCharacterName();
                success = true;
            }
            sendPacket(FieldPacket.onWhisper(WhisperFlags.ReplyResult, target, null, null, -1, success));
        } else if (flag == WhisperFlags.FindRequest) {
            User user = getChannel().findUserByName(target, true);
            int fieldID = 0;
            if (user != null && user.getField() != null) {
                fieldID = user.getField().getFieldID();
                if (user.isGM()) {
                    fieldID = -1;
                }
                target = user.getCharacterName();
            }
            if (fieldID > 0) {
                sendPacket(FieldPacket.onWhisper(WhisperFlags.FindResult, target, null, null, fieldID, false));
            }
        } else if (flag == WhisperFlags.BlockedResult) {
            sendPacket(FieldPacket.onWhisper(WhisperFlags.BlockedResult, target, null, null, -1, false));
        }
    }

    public void onTransferField(Field field, int x, int y, byte portal) {
        getField().onLeave(this);
        if (lock()) {
            try {
                setField(field);
                setPosMap(field.getFieldID());
                setPortal((byte) (portal & 0x7F));
                setMovePosition(x, y, (byte) 0, (short) 0);
                addCharacterDataMod(DBChar.Character);
                avatarModFlag = 0;
            } finally {
                unlock();
            }
        }
        sendSetFieldPacket(false);
        if (getField().onEnter(this)) {
            this.attackCheckIgnoreCnt = 3;
            this.onTransferField = false;
            //this.attackTimeCheckAlert = 0;

            String firstUserEnter = getField().getFirstUserEnter();
            if (!getField().isUserEntered() && firstUserEnter != null && !firstUserEnter.isEmpty()) {
                ScriptVM script = new ScriptVM();
                if (script.setScript(this, "field/first/" + firstUserEnter, this)) {
                    script.run(this);
                }
                getField().setUserEntered(true);
            }
            String userEnter = getField().getUserEnter();
            if (userEnter != null && !userEnter.isEmpty()) {
                ScriptVM script = new ScriptVM();
                if (script.setScript(this, "field/" + userEnter, this)) {
                    script.run(this);
                }
            }
        } else {
            Logger.logError("Failed in entering field");
            closeSocket();
        }
    }

    public void onTransferField(Field field, Portal portal) {
        if (lock()) {
            try {
                if (field != null) {
                    if (!field.getPortal().getPortal().contains(portal)) {
                        portal = field.getPortal().getPortal(0);
                    }
                    onTransferField(field, portal.getPortalPos().x, portal.getPortalPos().y, portal.getPortalIdx());
                }
            } finally {
                unlock();
            }
        }
    }

    public void postTransferField(int fieldID, String portal, boolean force) {
        postTransferField(fieldID, portal, force, new Point(0, 0), true, null);
    }

    public void postTransferField(Integer fieldID, String portal, boolean force, Point pos, boolean loopback, InPacket packet) {
        if (lock()) {
            try {
                if (!onTransferField || force) {
                    if (fieldID != null && FieldMan.getInstance(getChannelID()).isBlockedMap(fieldID)) {
                        Logger.logError("User tried to enter the Blocked Map #3 (From:%d,To:%d)", getField().getFieldID(), fieldID);
                        sendPacket(FieldPacket.onTransferFieldReqIgnored());
                    } else {
                        if (getField() == null || socket == null) {
                            closeSocket();
                            return;
                        }
                        if (pos == null) {
                            pos = new Point(0, 0);
                        }
                        int fieldKey = 0;
                        if (!loopback) {
                            fieldKey = packet.decodeByte();
                            fieldID = packet.decodeInt();
                            portal = packet.decodeString();
                        }
                        boolean isDead = false;
                        Portal pt = getField().getPortal().findPortal(portal);
                        if (getHP() == 0) {
                            if (getField().getForcedReturnFieldID() != Field.Invalid) {
                                fieldID = getField().getReturnFieldID();
                            } else {
                                fieldID = getField().getFieldID();
                            }
                            portal = "";
                            character.getCharacterStat().setHP(50);
                            basicStat.setFrom(character, character.getEquipped(), character.getEquipped2(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                            secondaryStat.clear();
                            secondaryStat.setFrom(basicStat, character.getEquipped(), character);
                            validateStat(false);
                            addCharacterDataMod(DBChar.Character);
                            isDead = true;
                        }
                        if (fieldID != -1 && !loopback && !isDead && !isGM()) {
                            Logger.logError("lol 1");
                            sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            return;
                        }
                        if (!loopback && getCurFieldKey() != fieldKey) {
                            return;
                        }
                        if (!isDead && !loopback && !canAttachAdditionalProcess()) {
                            Logger.logError("lol 2");
                            sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            return;
                        }
                        if (fieldID == -1 && (portal == null || portal.isEmpty())) {
                            if (!loopback) {
                                Logger.logError("lol 3");
                                sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            }
                            return;
                        }
                        this.onTransferField = true;
                        if (fieldID == -1) {
                            pt = getField().getPortal().findPortal(portal);
                            if (pt == null || pt.tmap == Field.Invalid) {
                                Logger.logError("Incorrect portal");
                                closeSocket();
                                return;
                            }
                            if (!pt.enable) {
                                Logger.logError("lol 4");
                                sendPacket(FieldPacket.onTransferFieldReqIgnored());
                                this.onTransferField = false;
                                return;
                            }
                            fieldID = pt.tmap;
                            portal = pt.tname;
                            if (fieldID == getField().getFieldID()) {
                                this.onTransferField = false;
                                return;
                            }
                        }
                        Field field = FieldMan.getInstance(getChannelID()).getField(fieldID);
                        if (field == null) {
                            Logger.logError("Invalid Field ID *1* : %d (Old: %d, IsDead: %b)", fieldID, getField().getFieldID(), isDead);
                            closeSocket();
                            return;
                        }
                        if (FieldMan.getInstance(getChannelID()).isBlockedMap(fieldID)) {
                            Logger.logError("User tried to enter the Blocked Map #1 (From:%d,To:%d)", getField().getFieldID(), fieldID);
                            Logger.logError("lol 5");
                            sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            this.onTransferField = false;
                            return;
                        }
                        if (portal == null || portal.isEmpty() || (pt = field.getPortal().findPortal(portal)) == null) {
                            if (pos.x == 0 && pos.y == 0) {
                                pt = field.getPortal().getRandStartPoint();
                            } else {
                                pt = field.getPortal().findCloseStartPoint(pos.x, pos.y);
                            }
                        }
                        pos.x = pt.getPortalPos().x;
                        pos.y = pt.getPortalPos().y;
                        onTransferField(field, pos.x, pos.y, pt.getPortalIdx());
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void sendCharacterStat(byte request, int flag) {
        lock.lock();
        try {
            character.getCharacterStat().setMoney(character.getCharacterStat().getMoney() - character.getMoneyTrading());
            sendPacket(WvsContext.onStatChanged(request, character.getCharacterStat(), flag));
            character.getCharacterStat().setMoney(character.getCharacterStat().getMoney() + character.getMoneyTrading());
        } finally {
            unlock();
        }
    }

    public boolean sendDropPickUpResultPacket(Drop pr, byte onExclRequest) {
        boolean pickUp = false;
        lock.lock();
        try {
            if (pr == null) {
                Inventory.sendInventoryOperation(this, onExclRequest, null);
                return pickUp;
            }
            List<ChangeLog> changeLog = new ArrayList<>();
            Pointer<Integer> incRet = new Pointer<>(0);
            if (pr.isMoney()) {
                int money = pr.getMoney();
                if (pr.getMoney() == 0) {
                    if (pr.getItem() != null)
                        money = pr.getItem().getItemID();//wtf u doing here nexon
                    else
                        money = 0;
                }
                incMoney(money, false, true);
                sendCharacterStat(onExclRequest, CharacterStatType.Money);
                pickUp = true;
            } else {
                if (pr.getItem() != null) {
                    ItemSlotBase item = pr.getItem().makeClone();
                    byte ti = ItemAccessor.getItemTypeIndexFromID(item.getItemID());
                    if (InventoryManipulator.rawAddItem(character, ti, item, changeLog, incRet)) {
                        pr.getItem().setItemNumber(pr.getItem().getItemNumber() - incRet.get());//-= nIncRet

                        if (ItemAccessor.isTreatSingly(pr.getItem()) || pr.getItem().getItemNumber() <= 0) {
                            pickUp = true;
                        }
                        characterDataModFlag |= ItemAccessor.getItemTypeFromTypeIndex(ti);
                        //if (ItemAccessor.isJavelinItem(pr.getItem().getItemID()) && incRet.Get() == 0)
                        //    consumeOnPickup = true;
                        if (pr.getSourceID() == 0 /*&& pr.getOwnType() == Drop.UserOwn*/) {
                            User user = getChannel().findUser(pr.getOwnerID());
                            if (user != null) {
                                user.flushCharacterData(0, true);
                            }
                        }
                    }
                }
                Inventory.sendInventoryOperation(this, onExclRequest, changeLog);
                changeLog.clear();
            }
            if (pr.isMoney() || incRet.get() > 0) {
                sendPacket(WvsContext.onDropPickUpMessage(pr.isMoney() ? DropPickup.Messo : DropPickup.AddInventoryItem, pr.getDropInfo(), pr.getItem() != null ? pr.getItem().getItemID() : 0, incRet.get()));
            } else {
                sendPacket(WvsContext.onDropPickUpMessage(DropPickup.Done, 0, 0, 0));
            }
            return pickUp;
        } finally {
            lock.unlock();
        }
    }

    public void sendDropPickUpFailPacket(byte onExclRequest) {
        sendCharacterStat(onExclRequest, 0);
        sendPacket(WvsContext.onDropPickUpMessage(DropPickup.Done, 0, 0, 0));
    }

    public void sendSetFieldPacket(boolean characterData) {
        if (characterData) {
            int s1 = Rand32.getInstance().random().intValue();
            int s2 = Rand32.getInstance().random().intValue();
            int s3 = Rand32.getInstance().random().intValue();
            calcDamage.setSeed(s1, s2, s3);
            sendPacket(Stage.onSetField(this, true, s1, s2, s3));
        } else {
            sendPacket(Stage.onSetField(this, false, -1, -1, -1));
        }
    }

    public void sendPacket(OutPacket packet) {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.sendPacket(packet, false);
            }
        } finally {
            lockSocket.unlock();
        }
    }

    public void sendSystemMessage(String msg) {
        sendPacket(WvsContext.onBroadcastMsg(BroadcastMsg.NOTICE, msg));
    }

    public void sendDebugMessage(String format, Object... args) {
        String text = String.format(format, args);
        sendPacket(WvsContext.onBroadcastMsg(BroadcastMsg.NOTICE, text));
        Logger.logReport("[DEBUG Message] %s", text);
    }

    public boolean isWearItemOnNeed(int necessaryItemID) {
        int i = 0;
        while (character.getEquipped().get(i) == null || character.getEquipped().get(i).getItemID() != necessaryItemID) {
            ++i;
            if (i > BodyPart.BP_Count)
                return false;
        }
        return true;
    }

    public boolean isItemExist(byte ti, int itemID) {
        boolean exist = InventoryManipulator.isItemExist(character, ti, itemID);
        if (!exist) {
            if (ti == ItemType.Equip) {
                Pointer<Integer> bodyPart = new Pointer<>(0);
                ItemAccessor.getBodyPartFromItem(itemID, character.getCharacterStat().getGender(), bodyPart, false);
                ItemSlotBase item = character.getItem(ti, -bodyPart.get());
                if (item != null) {
                    if (item.getItemID() == itemID)
                        exist = true;
                }
            }
        }
        return exist;
    }

    private void onUserDead() {
        sendTemporaryStatReset(secondaryStat.reset());

        ExpAccessor.decreaseExp(character, basicStat, getField().getLifePool().getMobGenCount() <= 0 || getField().isTown());
        sendCharacterStat(Request.None, CharacterStatType.EXP);
    }

    public void onLevelUp() {
        onUserEffect(true, true, UserEffect.LevelUp);
    }

    public void setMaxLevelReach() {
        if (!isGM()) {
            String notice = String.format("[Congrats] %s has reached Level 200! Congratulate %s on such an amazing achievement!", characterName, characterName);
            getChannel().broadcast(WvsContext.onBroadcastMsg(BroadcastMsg.NOTICE, notice));
        }
    }

    public byte tryChangeHairOrFace(int couponItemID, int param) {
        if (ItemAccessor.getItemTypeIndexFromID(couponItemID) != ItemType.Cash) {
            return -1;
        }
        boolean valid = false;
        byte type = (byte) (couponItemID / 1000 % 10);
        switch (type) {
            case 0://Hair
                valid = (param != character.getCharacterStat().getHair());
                //valid &= ItemInfo.isValidHairID(param);
                break;
            case 1://Hair Color(?)
                valid = (param != character.getCharacterStat().getHair());
                //valid &= ItemInfo.isValidHairID(param);
                break;
            case 2://Face
                valid = (param != character.getCharacterStat().getFace());
                //valid &= ItemInfo.isValidFaceID(param);
                break;
        }
        if (!valid) {
            return -3;
        }
        ItemSlotBase item;
        int slotCount = character.getItemSlotCount(ItemType.Cash);
        for (int pos = 1; pos <= slotCount; pos++) {
            item = character.getItem(ItemType.Cash, pos);
            if (item != null && item.getItemID() == couponItemID && item.isCashItem() && character.getItemTrading().get(ItemType.Cash).get(pos) == 0) {
                Pointer<Integer> decRet = new Pointer<>(0);
                List<ChangeLog> changeLog = new ArrayList<>();
                if (Inventory.rawRemoveItem(this, ItemType.Cash, (short) pos, (short) 1, changeLog, decRet, null) && decRet.get() == 1) {
                    addCharacterDataMod(DBChar.ItemSlotEtc);
                    Inventory.sendInventoryOperation(this, Request.None, changeLog);
                    if (type == 0 || type == 1) {
                        setHair(param);
                        sendCharacterStat(Request.None, CharacterStatType.Hair);
                        postAvatarModified(AvatarLook.Look);
                    } else if (type == 2) {
                        setFace(param);
                        sendCharacterStat(Request.None, CharacterStatType.Face);
                        postAvatarModified(AvatarLook.Face);
                    }
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    public void update(long time) {
        resetTemporaryStat(time, 0);
        applyTemporaryStat(time);
        flushCharacterData(time, false);
        checkCashItemExpire(time);
        checkGeneralItemExpire(time);
    }

    public void checkGeneralItemExpire(long time) {
        // TODO: Item Expirations
    }

    public void checkCashItemExpire(long time) {
        if (time - nextCheckCashItemExpire >= 0) {
            // TODO: Cash Item Expiration
        }
    }

    public void flushCharacterData(long time, boolean force) {
        if (lock()) {
            try {
                if (force || time - lastCharacterDataFlush >= 300000) {
                    // Best way to constantly update the ItemSN's without over-saving
                    // is by simply updating the SN upon each user save. This is either
                    // every 5 minutes, or whenever a user logs out.
                    GameApp.getInstance().updateItemInitSN();
                    if (characterDataModFlag != 0) {
                        if ((characterDataModFlag & DBChar.Character) != 0) {
                            GameDB.rawSaveCharacter(character.getCharacterStat());
                        }
                        if ((characterDataModFlag & DBChar.SkillRecord) != 0) {
                            GameDB.rawSaveSkillRecord(getCharacterID(), character.getSkillRecord());
                        }
                        if ((characterDataModFlag & DBChar.QuestRecord) != 0) {
                            GameDB.rawSaveQuestRecord(getCharacterID(), character.getQuestRecord());
                        }
                        if ((characterDataModFlag & DBChar.QuestRecordEx) != 0) {
                            GameDB.rawSaveQuestRecordEx(getCharacterID(), character.getQuestRecordEx());
                        }
                        if ((characterDataModFlag & DBChar.QuestComplete) != 0) {
                            GameDB.rawSaveQuestComplete(getCharacterID(), character.getQuestComplete());
                        }
                        if ((characterDataModFlag & DBChar.InventorySize) != 0) {
                            List<Integer> inventorySize = new ArrayList<>();
                            for (int ti = ItemType.Equip; ti <= ItemType.Cash; ti++) {
                                inventorySize.add(character.getItemSlotCount(ti));
                            }
                            GameDB.rawSetInventorySize(getCharacterID(), inventorySize);
                        }
                        if ((characterDataModFlag & DBChar.ItemSlotEquip) != 0) {
                            CommonDB.rawUpdateItemEquip(characterID, character.getEquipped(), character.getEquipped2(), character.getItemSlot().get(ItemType.Equip));
                        }
                        if ((characterDataModFlag & DBChar.ItemSlotConsume) != 0 || (characterDataModFlag & DBChar.ItemSlotInstall) != 0
                                || (characterDataModFlag & DBChar.ItemSlotEtc) != 0) {
                            CommonDB.rawUpdateItemBundle(characterID, character.getItemSlot());
                        }
                        characterDataModFlag = 0;
                    }
                    if (miniRoom != null) {
                        //miniRoom.save();
                    }
                    lastCharacterDataFlush = time;
                }
            } finally {
                unlock();
            }
        }
    }

    public void resetTemporaryStat(long time, int reasonID) {
        lock.lock();
        try {
            Flag reset;
            if (reasonID > 0)
                reset = secondaryStat.resetByReasonID(reasonID);
            else
                reset = secondaryStat.resetByTime(time);
            if (!reset.isZero()) {
                validateStat(false);
                sendTemporaryStatReset(reset);
            }
        } finally {
            unlock();
        }
    }

    public void sendTemporaryStatSet(Flag set) {
        Logger.logReport("Is Set = [%S]", set.isSet());
        if (set.isSet()) {
            lock.lock();
            try {
                sendPacket(WvsContext.onTemporaryStatSet(secondaryStat, set));
                if (getField() != null) {
                    getField().splitSendPacket(getSplit(), UserRemote.onTemporaryStatSet(characterID, secondaryStat, set), this);
                }
                if (set.operatorAND(CharacterTemporaryStat.getMask(CharacterTemporaryStat.MaxHP)).isSet() || set.operatorAND(CharacterTemporaryStat.getMask(CharacterTemporaryStat.EMHP)).isSet() || set.operatorAND(CharacterTemporaryStat.getMask(CharacterTemporaryStat.MorewildMaxHP)).isSet()) {
                    hpChanged(true);
                }
            } finally {
                unlock();
            }
        }
    }

    public void sendTemporaryStatReset(Flag reset) {
        if (reset.isSet()) {
            lock.lock();
            try {
                sendPacket(WvsContext.onTemporaryStatReset(reset));
                if (getField() != null) {
                    getField().splitSendPacket(getSplit(), UserRemote.onResetTemporaryStat(characterID, reset), this);
                }
                if (reset.operatorAND(CharacterTemporaryStat.getMask(CharacterTemporaryStat.MaxHP)).isSet() || reset.operatorAND(CharacterTemporaryStat.getMask(CharacterTemporaryStat.EMHP)).isSet() || reset.operatorAND(CharacterTemporaryStat.getMask(CharacterTemporaryStat.MorewildMaxHP)).isSet()) {
                    hpChanged(true);
                }
            } finally {
                unlock();
            }
        }
    }

    public void onUserEffect(boolean local, boolean remote, byte effect, int... args) {
        onUserEffect(local, remote, effect, null, args);
    }

    public void onUserEffect(boolean local, boolean remote, byte effect, String str, int... args) {
        int skillID = 0;
        int slv = 0;
        if (args.length > 0) {
            skillID = args[0];
            if (args.length > 1) {
                slv = args[1];
            }
        }
        if (remote) {
            getField().splitSendPacket(getSplit(), UserRemote.onEffect(getCharacterID(), effect, skillID, slv), this);
        }
        if (local) {
            sendPacket(UserLocal.onEffect(effect, str, skillID, slv));
        }
    }

    public void postAvatarModified(int flag) {
        if (((flag | avatarModFlag) != avatarModFlag) || flag == AvatarLook.Look) {
            avatarModFlag |= flag;
            avatarLook.load(character.getCharacterStat(), character.getEquipped(), character.getEquipped2());
            if (msMessenger)
                userMSM.notifyAvatarChanged();
            getField().splitSendPacket(getSplit(), UserRemote.onAvatarModified(this, flag), this);
            if (miniRoom != null) {
                //miniRoom.onAvatarChanged(this);
            }
            avatarModFlag = 0;
        }
    }

    public final void validateStat(boolean calledByConstructor) {
        lock.lock();
        try {
            AvatarLook avatarOld = avatarLook.makeClone();
            ItemAccessor.getRealEquip(character, realEquip, 0, 0);
            avatarLook.load(character.getCharacterStat(), character.getEquipped(), character.getEquipped2());
            maxGMSkills();
            checkEquippedSetItem();
            updatePassiveSkillData();
            int pdsMHPr = 0;
            int pdsMMPr = 0;
            if (passiveSkillData != null) {
                pdsMHPr = passiveSkillData.getMHPr();
                pdsMMPr = passiveSkillData.getMMPr();
            }
            
            int maxHPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MaxHP);
            int maxMPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MaxMP);
            int basicStatInc = secondaryStat.getStatOption(CharacterTemporaryStat.BasicStatUp);
            int maxHPInc = secondaryStat.getStatOption(CharacterTemporaryStat.EMHP);
            int maxMPInc = secondaryStat.getStatOption(CharacterTemporaryStat.EMMP);
            int swallowMaxMPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.SwallowMaxMP);
            int conversionMaxHPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.Conversion);
            int morewildMaxHPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MorewildMaxHP);
            int jaguarRidingHPIncRate = 0;
            int speed = secondaryStat.speed;
            int weaponID = 0;
            if (character.getItem(ItemType.Equip, -BodyPart.Weapon) != null) {
                weaponID = character.getItem(ItemType.Equip, -BodyPart.Weapon).getItemID();
            }

            basicStat.setFrom(character, realEquip, character.getEquipped2(), maxHPIncRate, maxMPIncRate, basicStatInc, maxHPInc, maxMPInc, swallowMaxMPIncRate, conversionMaxHPIncRate, morewildMaxHPIncRate, pdsMHPr, pdsMMPr, jaguarRidingHPIncRate);
            secondaryStat.setFrom(basicStat, realEquip, character);

            int flag = 0;
            if (character.getCharacterStat().getHP() > basicStat.getMHP()) {
                incHP(0, false);
                flag |= CharacterStatType.HP;
            }
            if (character.getCharacterStat().getMP() > basicStat.getMMP()) {
                incMP(0, false);
                flag |= CharacterStatType.MP;
            }

            if (!calledByConstructor) {
                if (flag != 0)
                    sendCharacterStat(Request.None, flag);
                if (!avatarLook.getEquipped().get(BodyPart.Weapon).equals(weaponID)) {
                    Flag reset = new Flag(Flag.INT_128);
                    if (secondaryStat.getStatOption(CharacterTemporaryStat.Booster) != 0) {
                        int reasonID = secondaryStat.getStatReason(CharacterTemporaryStat.Booster);
                        reset.performOR(secondaryStat.resetByReasonID(reasonID));
                    }
                    if (secondaryStat.getStatOption(CharacterTemporaryStat.SoulArrow) != 0) {
                        int reasonID = secondaryStat.getStatReason(CharacterTemporaryStat.SoulArrow);
                        reset.performOR(secondaryStat.resetByReasonID(reasonID));
                    }
                    sendTemporaryStatReset(reset);
                }
                flag = 0;
                if (!avatarOld.equals(avatarLook)) {
                    //addCharacterDataMod(DBChar.Avatar);
                    flag = AvatarLook.Look;
                }
                if (speed != secondaryStat.speed) {
                    flag |= AvatarLook.Unknown2;//idk, just a guess
                }
                postAvatarModified(flag);
            }
            avatarOld.getEquipped().clear();
        } finally {
            unlock();
        }
    }

    public int getTradeMoneyLimit() {
        return tradeMoneyLimit;
    }

    public void setTradeMoneyLimit(int tradeMoneyLimit) {
        this.tradeMoneyLimit += tradeMoneyLimit;
    }

    public int getTempTradeMoney() {
        return tempTradeMoney;
    }

    public void setTempTradeMoney(int tempTradeMoney) {
        this.tempTradeMoney = tempTradeMoney;
    }

    public boolean isMSMessenger() {
        return msMessenger;
    }

    public void setMSMessenger(boolean msMessenger) {
        this.msMessenger = msMessenger;
    }

    public void postQuestEffect(boolean exchange, List<Integer> items, List<Integer> counts, String msg, int effect) {
        if (items.size() != counts.size()) {
            return;
        }
        OutPacket packet = new OutPacket(LoopbackPacket.UserEffectLocal);
        packet.encodeByte(UserEffect.Quest);
        if (!exchange) {
            packet.encodeByte(0);
            packet.encodeString(msg);
            packet.encodeInt(effect);
        } else {
            packet.encodeByte(items.size());
            for (int i = 0; i < items.size(); i++) {
                packet.encodeInt(items.get(i));
                packet.encodeInt(counts.get(i));
            }
        }
        sendPacket(packet);
    }

    public void sendIncExpMessage(boolean isLastHit, int incExp, boolean onQuest, int incEXPbySMQ, int eventPrecentage, int partyBonusPercentage, int playTimeHour, int questBonusRate, int questBonusRemainConut, int partyBonusEventRate, int weddingBonusEXP, int partyBonusEXP, int itemBonusEXP, int premiumIpEXP, int rainbowWeekEventEXP, int partyEXPRingEXP, int cakePieEventBonus) {
        if (lock()) {
            try {
                sendPacket(WvsContext.onIncExpMessage(isLastHit, incExp, onQuest, incEXPbySMQ, eventPrecentage, partyBonusPercentage, playTimeHour, questBonusRate, questBonusRemainConut, partyBonusEventRate, weddingBonusEXP, partyBonusEXP, itemBonusEXP, premiumIpEXP, rainbowWeekEventEXP, partyEXPRingEXP, cakePieEventBonus));
            } finally {
                unlock();
            }
        }
    }

    public void sendIncMoneyMessage(int incMoney) {
        if (incMoney != 0) {
            if (lock()) {
                try {
                    sendPacket(WvsContext.onIncMoneyMessage(incMoney));
                } finally {
                    unlock();
                }
            }
        }
    }

    public void sendIncPOPMessage(int incPOP) {
        if (lock()) {
            try {
                sendPacket(WvsContext.onIncPOPMessage(incPOP));
            } finally {
                unlock();
            }
        }
    }

    public int getCurFieldKey() {
        return curFieldKey;
    }

    public void setCurFieldKey(int curFieldKey) {
        this.curFieldKey = curFieldKey;
    }

    public int getTeamForMCarnival() {
        return teamForMCarnival;
    }

    public void setTeamForMCarnival(int teamForMCarnival) {
        this.teamForMCarnival = teamForMCarnival;
    }

    public boolean isPartyInvitedCharacterID(int characterID) {
        partyInviteLock.lock();
        try {
            for (int i : partyInvitedCharacterID) {
                if (i == characterID) {
                    return true;
                }
            }
        } finally {
            partyInviteLock.unlock();
        }
        return false;
    }

    public void removePartyInviteCharacterID(int characterID) {
        partyInviteLock.lock();
        try {
            int index = partyInvitedCharacterID.indexOf(characterID);
            if (index != -1) {
                partyInvitedCharacterID.remove(index);
            }
        } finally {
            partyInviteLock.unlock();
        }
    }

    public void addPartyInvitedCharacterID(int characterID) {
        partyInviteLock.lock();
        try {
            partyInvitedCharacterID.addLast(characterID);
            if (partyInvitedCharacterID.size() > 100) {
                partyInvitedCharacterID.removeFirst();
            }
        } finally {
            partyInviteLock.unlock();
        }
    }

    public void hpChanged(boolean sendOnly) {
        PartyMan.getInstance().notifyUserHPChanged(this, sendOnly);
    }

    public void onPartyRequest(InPacket packet) {
        int type = packet.decodeByte();
        if (type <= 0) {
            return;
        }
        if (type == PartyResCode.CreateNewParty) {
            if (false && isGM()) {
                sendPacket(PartyPacket.adminCannotCreate());
                return;
            }
            PartyMan.getInstance().postCreateNewParty(getCharacterID());
        } else if (type == PartyResCode.WithdrawParty) {
            PartyMan.getInstance().postWithdrawParty(getCharacterID(), packet.decodeBool());
        } else if (type == PartyResCode.JoinParty) {
            User user = GameApp.getInstance().getChannel(getChannelID()).findUserByName(packet.decodeString(), true);
            if (user == null) {
                return;
            }
            if (!isPartyInvitedCharacterID(getCharacterID())) {
                Logger.logError("Uninvited User Tried to join party.");
                return;
            }
            PartyMan.getInstance().postJoinParty(getCharacterID(), user.getCharacterID());
        } else if (type == PartyResCode.InviteParty) {
            int partyID = PartyMan.getInstance().charIdToPartyID(getCharacterID());
            if (partyID == 0 || PartyMan.getInstance().isPartyBoss(getCharacterID())) {
                User user = GameApp.getInstance().getChannel(getChannelID()).findUserByName(packet.decodeString(), true);
                if (user == null) {
                    sendPacket(PartyPacket.partyResult(PartyResCode.JoinParty_UnknownUser));
                    return;
                }
                int invitedPartyID = PartyMan.getInstance().charIdToPartyID(user.getCharacterID());
                if (invitedPartyID != 0) {
                    sendPacket(PartyPacket.partyResult(PartyResCode.JoinParty_AlreadyJoined));
                    return;
                }
                user.sendPacket(PartyPacket.inviteParty(getCharacterID(), getCharacterName(), getLevel(), getCharacter().getCharacterStat().getJob()));
                addPartyInvitedCharacterID(user.getCharacterID());
            }
        } else if (type == PartyResCode.KickParty) {
            int memberID = packet.decodeInt();
            int partyID = PartyMan.getInstance().charIdToPartyID(getCharacterID());
            if (partyID != 0 && PartyMan.getInstance().isPartyBoss(partyID, getCharacterID()) && PartyMan.getInstance().isPartyMember(partyID, memberID)) {
                PartyMan.getInstance().postWithdrawParty(memberID, true);
                return;
            }
            sendPacket(PartyPacket.partyResult(PartyResCode.KickParty_Unknown));
        } else if (type == PartyResCode.ChangePartyBoss) {
            int memberID = packet.decodeInt();
            User user = GameApp.getInstance().getChannel(getChannelID()).findUser(memberID);
            int partyID = PartyMan.getInstance().charIdToPartyID(getCharacterID());
            if (user != null && partyID != 0 && PartyMan.getInstance().isPartyBoss(partyID, getCharacterID()) && PartyMan.getInstance().isPartyMember(partyID, memberID) && user.getCharacter().getCharacterStat().getJob() != 0) {
                if (getField() == null) {
                    sendPacket(PartyPacket.partyResult(PartyResCode.ChangePartyBoss_NotSameField));
                    return;
                }
                if (user.getField() == null || user.getField().getFieldID() != getField().getFieldID()) {
                    sendPacket(PartyPacket.partyResult(PartyResCode.ChangePartyBoss_NoMemberInSameField));
                    return;
                }
                PartyMan.getInstance().postChangePartyBoss(memberID, false, true);
            }
            // todo: check field party boss change limit
        }
    }

    public void onPartyResult(InPacket packet) {
        int type = packet.decodeByte();
        int inviterID = packet.decodeInt();
        User inviter = GameApp.getInstance().getChannel(getChannelID()).findUser(inviterID);
        if (inviter == null || PartyMan.getInstance().charIdToPartyID(inviterID) == 0) {
            return;
        }
        if (!inviter.isPartyInvitedCharacterID(getCharacterID())) {
            Logger.logError("Invite Party requested without being invited");
            return;
        }
        if (type == PartyResCode.InviteParty_Sent) {
            inviter.sendPacket(PartyPacket.invitePartySent(getCharacterName()));
            return;// skip remove part
            // not sure if need remove pt inv
        } else if (type == PartyResCode.InviteParty_AlreadyInvitedByInviter) {
            inviter.sendPacket(PartyPacket.serverMsg(String.format("You have already invited '%s' to your party.", getCharacterName())));
        } else if (type == PartyResCode.InviteParty_Rejected) {
            inviter.sendPacket(PartyPacket.serverMsg(String.format("'%s' have denied request to the party.", getCharacterName())));
        } else if (type == PartyResCode.InviteParty_Accepted) {
            PartyMan.getInstance().postJoinParty(inviterID, getCharacterID());
        } else {
            sendSystemMessage("Party Result Type = " + type);
        }
        inviter.removePartyInviteCharacterID(getCharacterID());
    }

    public void lostQuestItem(InPacket packet, int questID) {
    }

    public void acceptQuest(InPacket packet, int questID, int npcTemplateID, Npc npc) {
        int fieldID = getField().getFieldID();
        int tamingMobLevel = 0;
        boolean monsterRiding = false;
        if (!QuestMan.getInstance().checkStartDemand(questID, npcTemplateID, character, monsterRiding, tamingMobLevel, fieldID)) {
            sendPacket(UserLocal.QuestResult.onActFailed(questID, QuestFlag.QuestRes_Act_Failed_Unknown));
            sendSystemMessage("start demand failed");
            return;
        }
        int result = tryQuestStartAct(questID, npc);
        if (result != QuestFlag.QuestRes_Act_Failed_Unknown) {
            if (questID != 9800 && QuestMan.getInstance().isAutoCompleteQuest(questID)) {
                boolean scriptLinkedQuest = QuestMan.getInstance().isCompleteScriptLinkedQuest(questID);
                if (scriptLinkedQuest) {
                    scriptLinkedQuest(packet, questID, npcTemplateID, npc, 1);
                } else {
                    completeQuest(packet, questID, npcTemplateID, npc, true);
                }
                return;
            }
            if (result == QuestFlag.QuestRes_Act_Success) {
                Logger.logReport("QUEST Success");
                sendPacket(UserLocal.QuestResult.onActSuccess(questID, npcTemplateID, 0));
            } else {
                Logger.logReport("QUEST Fail [%d]", result);
                sendPacket(UserLocal.QuestResult.onActFailed(questID, result));
            }
        }
    }

    public void completeQuest(InPacket packet, int questID, int npcTemplateID, Npc npc, boolean autoComplete) {
        int select = autoComplete ? -1 : packet.decodeInt();
        int lolres = QuestMan.getInstance().checkCompleteDemand(questID, npcTemplateID, getCharacter());
        if (lolres != QuestMan.SUCCESS) {
            sendPacket(UserLocal.QuestResult.onActFailed(questID, QuestFlag.QuestRes_Act_Failed_Unknown));
            sendSystemMessage("check " + lolres);
            return;
        }
        int result = tryQuestCompleteAct(questID, select, npc);
        if (result == QuestFlag.QuestRes_Act_Failed_Unknown) {
            sendSystemMessage("try complete failed");
            sendPacket(UserLocal.QuestResult.onActFailed(questID, QuestFlag.QuestRes_Act_Failed_Unknown));
            return;
        }
        if (result == QuestFlag.QuestRes_Act_Success) {
            Logger.logReport("QUEST Success");
            int nextQuest = 0;
            QuestAct a = QuestMan.getInstance().getCompleteAct(questID);
            if (a != null) {
                nextQuest = a.getNextQuest();
            }
            sendPacket(UserLocal.QuestResult.onActSuccess(questID, npcTemplateID, nextQuest));
            onUserEffect(true, true, UserEffect.QuestComplete);
        } else {
            Logger.logReport("QUEST Fail [%d]", result);
            sendPacket(UserLocal.QuestResult.onActFailed(questID, result));
        }
    }

    public void resignQuest(InPacket packet, int questID) {

    }

    public void scriptLinkedQuest(InPacket packet, int questID, int npcTemplateID, Npc npc, int scriptActCategory) {
        String scriptName = null;
        if (scriptActCategory != 0) {
            if (scriptActCategory != 1 || QuestMan.getInstance().checkCompleteDemand(questID, npcTemplateID, getCharacter()) != QuestMan.SUCCESS || !QuestMan.getInstance().isCompleteScriptLinkedQuest(questID)) {
                sendPacket(UserLocal.QuestResult.onActFailed(questID, QuestFlag.QuestRes_Act_Failed_Unknown));
                return;
            }
            scriptName = QuestMan.getInstance().getCompleteScriptName(questID);
        } else {
            if (!QuestMan.getInstance().checkStartDemand(questID, npcTemplateID, getCharacter(), false, 0, getField().getFieldID()) || !QuestMan.getInstance().isStartScriptLinkedQuest(questID)) {
                sendPacket(UserLocal.QuestResult.onActFailed(questID, QuestFlag.QuestRes_Act_Failed_Unknown));
                return;
            }
            scriptName = QuestMan.getInstance().getStartScriptName(questID);
        }
        if (scriptName == null || scriptName.isEmpty()) {
            sendPacket(UserLocal.QuestResult.onActFailed(questID, QuestFlag.QuestRes_Act_Failed_Unknown));
            return;
        }
        ScriptVM script = new ScriptVM();
        if (script.setScript(this, "quests/" + scriptName, npc)) {
            script.run(this);
        }
    }

    public int tryQuestStartAct(int questID, Npc npc) {
        QuestAct act = QuestMan.getInstance().getStartAct(questID);
        if (act == null) {
            return QuestFlag.QuestRes_Act_Success;
        }
        sendDebugMessage("Trying qust start act [%d]", questID);
        int result = Inventory.tryExchange(this, act.getIncMoney(), act.getActItem());
        if (result == QuestFlag.QuestRes_Act_Success) {
            // TODO handle pet tameness
            // TODO handle npc special action
            UserQuestRecord.set(this, questID, act.getInfo());
            return QuestFlag.QuestRes_Act_Success;
        }
        return result;
    }

    public int tryQuestCompleteAct(int questID, int select, Npc npc) {
        QuestAct act = QuestMan.getInstance().getCompleteAct(questID);
        if (act == null) {
            return QuestFlag.QuestRes_Act_Success;
        }
        boolean petNull = true;
        if ((act.getIncPetTameness() != 0 || act.isPetSpeed()) && petNull) {
            return QuestFlag.QuestRes_Act_Failed_Pet;
        }
        List<ActItem> baseReward = new ArrayList<>();
        List<ActItem> randomReward = new ArrayList<>();
        List<ActItem> selectReward = new ArrayList<>();
        if (act.getActItem() != null && act.getActItem().size() > 0) {
            for (ActItem actItem : act.getActItem()) {
                QuestItemOption option = actItem.getOption();
                int gender = option.getGender();
                int job = getCharacter().getCharacterStat().getJob();
                if (((JobAccessor.getJobBitflag(job) & option.getJobFlag()) != 0 || getGradeCode() >= UserGradeCode.GM.getGrade() && JobAccessor.getJobCategory(job) == JobCategory.ADMIN) && (gender == 2 || gender == getCharacter().getCharacterStat().getGender())) {
                    int prop = actItem.getOption().getProbRate();
                    if (prop == -1) {
                        selectReward.add(actItem);
                    } else if (prop == 0) {
                        baseReward.add(actItem);
                    } else if (prop > 0) {
                        randomReward.add(actItem);
                    }
                }
            }
        }
        if (selectReward.size() > 0) {
            if (select < 0 || select >= selectReward.size()) {
                return QuestFlag.QuestRes_Act_Failed_Unknown;
            }
            baseReward.add(selectReward.get(select));
        }

        List<Integer> itemTypes = new ArrayList<>();
        List<Integer> props = new ArrayList<>();

        int lastProp = 0;
        int lastItemType = -1;
        for (ActItem randomItem : randomReward) {
            int p = randomItem.getOption().getProbRate() + lastProp;
            props.add(p);
            int itemType = randomItem.getInfo().getItemID() / 1000000;
            if (itemType != lastItemType) {
                lastItemType = itemType;
                itemTypes.add(itemType);
            }
            lastProp = p;
        }

        for (Integer itemType : itemTypes) {
            if (itemType < ItemType.Equip || itemType > ItemType.Cash) {
                return QuestFlag.QuestRes_Act_Failed_Unknown;
            }
            int itemSlotCount = getCharacter().getItemSlotCount(itemType);
            int usedSlots = 0;
            if (itemSlotCount >= 1) {
                for (int i = 1; i <= itemSlotCount; i++) {
                    if (getCharacter().getItemSlot(itemType).get(i) != null) {
                        usedSlots++;
                    }
                }
            }
            if (itemSlotCount <= usedSlots) {
                return QuestFlag.QuestRes_Act_Failed_Inventory;
            }
        }

        if (act.getIncMoney() < 0 && getCharacter().getCharacterStat().getMoney() < -act.getIncMoney()) {
            return QuestFlag.QuestRes_Act_Failed_Meso;
        }

        int rand;
        if (lastProp != 0) {
            rand = Math.abs(Rand32.getInstance().random().intValue()) % lastProp;
        } else {
            rand = Math.abs(Rand32.getInstance().random().intValue());
        }
        for (int i = 0; i < props.size(); i++) {
            if (rand <= props.get(i)) {
                baseReward.add(randomReward.get(i));
                break;
            }
        }
        int result = Inventory.tryExchange(this, act.getIncMoney(), baseReward);
        if (result != QuestFlag.QuestRes_Act_Success) {
            return result;
        }
        int incExp = act.getIncExp();
        Logger.logReport("Inc Exp Count [%d]", incExp);
        if (incExp != 0) {
            int remainBonusCount = 0;
            int questBonusRate = 0;// = CQWUQuestRecord::UpdateBonusInfo(v59, v28, &nRemainBonusCount);
            int flag = incEXP(incExp + incExp * questBonusRate / 100, false);
            if (flag != 0) {
                sendCharacterStat(Request.None, flag);
                sendIncExpMessage(true, incExp, true, 0, 0, 0, 0, questBonusRate, remainBonusCount, 0, 0, 0, 0, 0, 0, 0, 0);
            }
        }
        int incPop = act.getIncPop();
        if (incPop != 0 && incPOP(incPop, false)) {
            sendCharacterStat(Request.None, CharacterStatType.POP);
            sendIncPOPMessage(incPop);
        }
        UserQuestRecord.setComplete(this, questID);
        // todo skills, npc actions, map msgs, buffs
        return QuestFlag.QuestRes_Act_Success;
    }

    public boolean isMarried() {
        return false;
    }

    public boolean isInParty() {
        return PartyMan.getInstance().charIdToPartyID(getCharacterID()) != 0;
    }

    public boolean isPartyBoss() {
        int partyID = PartyMan.getInstance().charIdToPartyID(getCharacterID());
        return PartyMan.getInstance().isPartyBoss(partyID, getCharacterID());
    }

    public void checkEquippedSetItem() {
        character.getEquippedSetItem().clear();
        for (int i = 0; i < BodyPart.BP_Count; i++) {
            ItemSlotBase item = realEquip.get(i);
            if (item != null) {
                EquipItem equipItem = ItemInfo.getEquipItem(item.getItemID());
                int setItemID;
                if (equipItem != null && (setItemID = equipItem.getSetItemID()) != 0) {
                    EquippedSetItem equippedSetItem = character.getEquippedSetItem().getOrDefault(setItemID, new EquippedSetItem(setItemID));
                    equippedSetItem.setPartsCount(equippedSetItem.getPartsCount() + 1);
                    equippedSetItem.getItems().add(item.getItemID());
                    character.getEquippedSetItem().put(equippedSetItem.getSetItemID(), equippedSetItem);
                }
            }
        }
    }

    public PassiveSkillData getPassiveSkillData() {
        return passiveSkillData;
    }

    public void updatePassiveSkillData() {
        if (passiveSkillData == null) {
            return;
        }
        passiveSkillData.clear();
        // guild skills
        for (Iterator<Integer> it = character.getSkillRecord().keySet().iterator(); it.hasNext(); ) {
            int skillID = it.next();
            Pointer<SkillEntry> skill = new Pointer<>();
            int slv = SkillInfo.getInstance().getSkillLevel(character, skillID, skill);
            if (skill.get() != null && skill.get().getPsdSkill() != 0 && (skill.get().getSkillID() != 35101007 /* || pUser->m_nRidingVehicleID == 1932016)*/)) {
                if (skill.get().getSkillID() == 35121013) {
                    slv = SkillInfo.getInstance().getSkillLevel(character, 35111004, skill);
                    setPassiveSkillData(skill.get(), slv);
                } else if (slv > 0) {
                    setPassiveSkillData(skill.get(), slv);
                }
            }
        }
        if (character.getCharacterStat().getJob() / 100 == 35) {
            if (SkillInfo.getInstance().getSkillLevel(character, 35121005, null) > 0) {
                int slv = SkillInfo.getInstance().getSkillLevel(character, 35111004, null);
                SkillEntry skill = SkillInfo.getInstance().getSkill(35121013);
                setPassiveSkillData(skill, slv);
            }
        }
        if (secondaryStat.getStat(CharacterTemporaryStat.Dice).getOption() > 1) {
            passiveSkillData.setMHPr(passiveSkillData.getMHPr() + secondaryStat.getDiceInfo()[DiceFlags.MHP_R]);
            passiveSkillData.setMMPr(passiveSkillData.getMMPr() + secondaryStat.getDiceInfo()[DiceFlags.MMP_R]);
            passiveSkillData.setCr(passiveSkillData.getCr() + secondaryStat.getDiceInfo()[DiceFlags.CR]);
            passiveSkillData.setCDMin(passiveSkillData.getCDMin() + secondaryStat.getDiceInfo()[DiceFlags.CD_MIN]);
            passiveSkillData.setACCr(passiveSkillData.getACCr() + secondaryStat.getDiceInfo()[DiceFlags.ACC_R]);
            passiveSkillData.setEVAr(passiveSkillData.getEVAr() + secondaryStat.getDiceInfo()[DiceFlags.EVA_R]);
            passiveSkillData.setAr(passiveSkillData.getAr() + secondaryStat.getDiceInfo()[DiceFlags.AR]);
            passiveSkillData.setEr(passiveSkillData.getEr() + secondaryStat.getDiceInfo()[DiceFlags.ER]);
            passiveSkillData.setPDDr(passiveSkillData.getPDDr() + secondaryStat.getDiceInfo()[DiceFlags.PDD_R]);
            passiveSkillData.setMDDr(passiveSkillData.getMDDr() + secondaryStat.getDiceInfo()[DiceFlags.MDD_R]);
            passiveSkillData.setPDr(passiveSkillData.getPDr() + secondaryStat.getDiceInfo()[DiceFlags.PD_R]);
            passiveSkillData.setMDr(passiveSkillData.getMDr() + secondaryStat.getDiceInfo()[DiceFlags.MD_R]);
            passiveSkillData.setDIPr(passiveSkillData.getDIPr() + secondaryStat.getDiceInfo()[DiceFlags.DIP_R]);
            passiveSkillData.setPDamR(passiveSkillData.getPDamR() + secondaryStat.getDiceInfo()[DiceFlags.PDAM_R]);
            passiveSkillData.setMDamR(passiveSkillData.getMDamR() + secondaryStat.getDiceInfo()[DiceFlags.MDAM_R]);
            passiveSkillData.setPADr(passiveSkillData.getPADr() + secondaryStat.getDiceInfo()[DiceFlags.PAD_R]);
            passiveSkillData.setMADr(passiveSkillData.getMADr() + secondaryStat.getDiceInfo()[DiceFlags.MAD_R]);
            passiveSkillData.setEXPr(passiveSkillData.getEXPr() + secondaryStat.getDiceInfo()[DiceFlags.EXP_R]);
            passiveSkillData.setIMPr(passiveSkillData.getIMPr() + secondaryStat.getDiceInfo()[DiceFlags.IMP_R]);
            passiveSkillData.setASRr(passiveSkillData.getASRr() + secondaryStat.getDiceInfo()[DiceFlags.ASR_R]);
            passiveSkillData.setTERr(passiveSkillData.getTERr() + secondaryStat.getDiceInfo()[DiceFlags.TER_R]);
            passiveSkillData.setMESOr(passiveSkillData.getMHPr() + secondaryStat.getDiceInfo()[DiceFlags.MESO_R]);
        }
        revisePassiveSkillData();
    }

    public void setPassiveSkillData(SkillEntry skill, int slv) {
        if (skill == null || slv <= 0) {
            return;
        }
        SkillLevelData sd = skill.getLevelData(slv);
        passiveSkillData.setMHPr(passiveSkillData.getMHPr() + sd.MHPr);
        passiveSkillData.setMMPr(passiveSkillData.getMMPr() + sd.MMPr);
        passiveSkillData.setACCr(passiveSkillData.getACCr() + sd.ACCr);
        passiveSkillData.setEVAr(passiveSkillData.getEVAr() + sd.EVAr);
        passiveSkillData.setEr(passiveSkillData.getEr() + sd.Er);
        passiveSkillData.setPDDr(passiveSkillData.getPDDr() + sd.PDDr);
        passiveSkillData.setMDDr(passiveSkillData.getMDDr() + sd.MDDr);
        passiveSkillData.setPDr(passiveSkillData.getPDr() + sd.PDr);
        passiveSkillData.setMDr(passiveSkillData.getMDr() + sd.MDr);

        passiveSkillData.setPADr(passiveSkillData.getPADr() + sd.PADr);
        passiveSkillData.setMADr(passiveSkillData.getMADr() + sd.MADr);
        passiveSkillData.setEXPr(passiveSkillData.getEXPr() + sd.EXPr);
        passiveSkillData.setIMPr(passiveSkillData.getIMPr() + sd.IMPr);
        passiveSkillData.setASRr(passiveSkillData.getASRr() + sd.ASRr);
        passiveSkillData.setTERr(passiveSkillData.getTERr() + sd.TERr);
        passiveSkillData.setMESOr(passiveSkillData.getMESOr() + sd.MESOr);
        passiveSkillData.setPADx(passiveSkillData.getPADx() + sd.PADx);
        passiveSkillData.setMADx(passiveSkillData.getMADx() + sd.MADx);
        passiveSkillData.setIMDr(passiveSkillData.getIMDr() + sd.IMDr);
        passiveSkillData.setPsdJump(passiveSkillData.getPsdJump() + sd.PsdJump);
        passiveSkillData.setPsdSpeed(passiveSkillData.getMHPr() + sd.PsdSpeed);
        passiveSkillData.setOCr(passiveSkillData.getOCr() + sd.OCr);
        passiveSkillData.setDCr(passiveSkillData.getDCr() + sd.DCr);

        if (skill.getAdditionPsdOffset().isEmpty() || skill.getPsdSkill() == 2) {
            passiveSkillData.setCr(passiveSkillData.getCr() + sd.Cr);
            passiveSkillData.setCDMin(passiveSkillData.getCDMin() + sd.CDMin);
            passiveSkillData.setAr(passiveSkillData.getAr() + sd.Ar);
            passiveSkillData.setDIPr(passiveSkillData.getDIPr() + sd.DIPr);
            passiveSkillData.setPDamR(passiveSkillData.getPDamR() + sd.PDamr);
            passiveSkillData.setMDamR(passiveSkillData.getMDamR() + sd.MDamr);
        }
        if (skill.getAdditionPsdOffset().size() > 0) {
            for (Map.Entry<Integer, AdditionPsd> psdData : skill.getAdditionPsdOffset().entrySet()) {
                AdditionPsd apsd = new AdditionPsd();

                AdditionPsd apsdOffset = psdData.getValue();
                apsd.setCr(Math.max(sd.Cr + apsdOffset.getCr(), 0));
                apsd.setCDMin(Math.max(sd.CDMin + apsdOffset.getCDMin(), 0));
                apsd.setAr(Math.max(sd.Ar + apsdOffset.getAr(), 0));
                apsd.setDIPr(Math.max(sd.DIPr + apsdOffset.getDIPr(), 0));
                apsd.setPDamr(Math.max(sd.PDamr + apsdOffset.getPDamr(), 0));
                apsd.setMDamr(Math.max(sd.MDamr + apsdOffset.getMDamr(), 0));
                apsd.setIMPr(Math.max(sd.IMPr + apsdOffset.getIMPr(), 0));
                passiveSkillData.getAdditionPsd().put(psdData.getKey(), apsd);
            }
        }
    }

    public void revisePassiveSkillData() {
        passiveSkillData.setMESOr(Math.min(Math.max(passiveSkillData.getMESOr(), 0), 100));
        passiveSkillData.setOCr(Math.min(Math.max(passiveSkillData.getOCr(), 0), 50));
        passiveSkillData.setDCr(Math.min(Math.max(passiveSkillData.getDCr(), 0), 50));
    }

    public void maxGMSkills() {
        if (isGM() && character.getSkillRecord().isEmpty()) {
            for (JobAccessor job : JobAccessor.values()) {
                SkillRoot visibleSR = SkillInfo.getInstance().getSkillRoot(job.getJob());
                if (visibleSR != null) {
                    for (SkillEntry skill : visibleSR.getSkills()) {
                        int skillID = skill.getSkillID();
                        int skillRoot = skillID / 10000;
                        if (skill != null && skill.getMaxLevel() != 0) {
                            if (JobAccessor.isCorrectJobForSkillRoot(job.getJob(), skillRoot)) {
                                character.getSkillRecord().put(skillID, skill.getMaxLevel());
                            }
                        }
                    }
                }
            }
        }
    }

    public void applyTemporaryStat(long time) {
        if (time - lastDragonBloodUpdate < 4000) {
            return;
        }
        List<Integer> removeSkills = new ArrayList<>();
        int dragonBlood = getSecondaryStat().getStatOption(CharacterTemporaryStat.DragonBlood);
        if (dragonBlood != 0) {
            if (incHP(-dragonBlood, true)) {
                if (getCharacter().getCharacterStat().getHP() <= 0) {
                    getCharacter().getCharacterStat().setHP(1);
                    removeSkills.add(DragonKnight.DRAGON_BLOOD);
                }
                sendCharacterStat(Request.None, CharacterStatType.HP);
                onUserEffect(true, true, UserEffect.SkillSpecial, DragonKnight.DRAGON_BLOOD);
            } else {
                removeSkills.add(DragonKnight.DRAGON_BLOOD);
            }
        }
        for (int skillID : removeSkills) {
            resetTemporaryStat(0, skillID);
        }
        removeSkills.clear();
        lastDragonBloodUpdate = System.currentTimeMillis();
    }

    public Summoned getSummonedBySkillID(int skillID) {
        if (getField() == null) {
            return null;
        }
        return getField().getSummonedPool().getSummoned(getCharacterID(), skillID);
    }

    public Summoned getSummonedBySummonedID(int summonedID) {
        if (getField() == null) {
            return null;
        }
        return getField().getSummonedPool().getSummoned(summonedID);
    }

    public boolean createSummoned(SkillEntry skill, int slv, Point pt, long end, boolean migrate) {
        if (skill == null) {
            return false;
        }
        int skillID = skill.getSkillID();

        int toRemove = 0;
        if (skillID == Priest.SUMMON_DRAGON) {
            toRemove = Bishop.BAHAMUT;
        } else if (skillID == Bishop.BAHAMUT) {
            toRemove = Priest.SUMMON_DRAGON;
        } else if (skillID == Ranger.SILVER_HAWK) {
            toRemove = Bowmaster.PHOENIX;
        } else if (skillID == Bowmaster.PHOENIX) {
            toRemove = Ranger.SILVER_HAWK;
        } else if (skillID == Sniper.GOLDEN_EAGLE) {
            toRemove = CrossbowMaster.FREEZER;
        } else if (skillID == CrossbowMaster.FREEZER) {
            toRemove = Sniper.GOLDEN_EAGLE;
        }

        lock.lock();
        try {
            removeSummoned(skillID, 4, 0);
            if (toRemove != 0) {
                removeSummoned(toRemove, 4, 0);
            }
            Summoned summoned = getField().getSummonedPool().createSummoned(getCharacterID(), skillID, slv, getLevel(), pt, end, migrate);
            if (summoned == null) {
                return false;
            }
            summoneds.add(summoned);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public void removeSummoned(int skillID, int leaveType, long cur) {
        List<Integer> removeList = new ArrayList<>();
        lock.lock();
        try {
            for (Iterator<Summoned> it = summoneds.iterator(); it.hasNext(); ) {
                Summoned summoned = it.next();
                if ((skillID == 0 || summoned.getSkillID() == skillID) && (cur == 0 || cur - summoned.getEnd() >= 0)) {
                    if (leaveType == 2) {
                        removeList.add(summoned.getSkillID());
                    }
                    it.remove();
                }
            }

            for (Integer summoned : removeList) {
                getField().getSummonedPool().removeSummoned(getCharacterID(), summoned, 0);
            }
            getField().getSummonedPool().removeSummoned(getCharacterID(), skillID, leaveType);
        } finally {
            lock.unlock();
        }
        removeList.clear();
    }

    public void reregisterSummoned() {
        lock.lock();
        try {
            for (Iterator<Summoned> it = summoneds.iterator(); it.hasNext(); ) {
                Summoned summoned = it.next();
                if (summoned.getMoveAbility() != MoveAbility.Stop) {
                    getField().getSummonedPool().createSummoned(summoned, getCurrentPosition());
                } else {
                    it.remove();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public int getPreparedSkill() {
        return preparedSkill;
    }

    public void setPreparedSkill(int preparedSkill) {
        this.preparedSkill = preparedSkill;
    }

    public long getLastKeyDown() {
        return lastKeyDown;
    }

    public void setLastKeyDown(long lastKeyDown) {
        this.lastKeyDown = lastKeyDown;
    }

    public boolean isKeyDown() {
        return keyDown;
    }

    public void setKeyDown(boolean keyDown) {
        this.keyDown = keyDown;
    }

    public void onStatChangeByMobSkill(int skillID, int slv, MobSkillLevelData level, int delay, int mobTemplateID) {
        int prop = level.getProp();
        if (prop == 0) {
            prop = 100;
        }
        if (Math.abs(Rand32.genRandom().intValue()) % 100 >= prop) {
            return;
        }
        Flag tempSet = new Flag(Flag.INT_128);
        Flag tempReset = new Flag(Flag.INT_128);
        int duration = level.getDuration();
        int reason = skillID | (slv << 16);
        if (duration <= 0) {
            if (skillID == MobSkills.DISPEL) {
                tempReset = secondaryStat.resetByUserSkill();
                validateStat(false);
            }
        } else {
            long endTime = delay + System.currentTimeMillis() + duration;
            boolean holyShield = secondaryStat.getStatOption(CharacterTemporaryStat.Holyshield) != 0;
            switch (skillID) {
                case MobSkills.SEAL:
                    if (holyShield) {
                        break;
                    }
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Seal, new SecondaryStatOption(1, reason, endTime)));
                case MobSkills.DARKNESS:
                    if (holyShield) {
                        break;
                    }
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Darkness, new SecondaryStatOption(1, reason, endTime)));
                    break;
                case MobSkills.WEAKNESS:
                    if (holyShield) {
                        break;
                    }
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Weakness, new SecondaryStatOption(1, reason, endTime)));
                    break;
                case MobSkills.STUN:
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Stun , new SecondaryStatOption(1, reason, endTime)));
                    break;
                case MobSkills.CURSE:
                    if (holyShield) {
                        break;
                    }
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Curse, new SecondaryStatOption(1, reason, endTime)));
                    break;
                case MobSkills.POISON:
                    if (holyShield) {
                        break;
                    }
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Poison, new SecondaryStatOption(level.getX(), reason, endTime)));
                    break;
                case MobSkills.SLOW:
                    if (holyShield) {
                        break;
                    }
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Slow, new SecondaryStatOption(level.getX(), reason, endTime)));
                    break;
                case MobSkills.ATTRACT:
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.Attract, new SecondaryStatOption(level.getX(), reason, endTime)));
                    break;
                case MobSkills.BANMAP:
                    SecondaryStatOption opt = new SecondaryStatOption(1, reason, endTime);
                    opt.setModOption(mobTemplateID);
                    tempSet.performOR(secondaryStat.setStat(CharacterTemporaryStat.BanMap, opt));
                    break;
            }
        }
        sendTemporaryStatReset(tempReset);
        sendTemporaryStatSet(tempSet);
    }

    public void banMapByMob(int mobTemplateID) {
        MobTemplate template = MobTemplate.getMobTemplate(mobTemplateID);
        if (template == null) {
            return;
        }
        if (template.getBanMap() == null || template.getBanMap().size() <= 0) {
            Logger.logError("Incorrect MobTemplateID in CUser::BanMapByMob (MID:%d, FID:%d, CID:%d)", template.getTemplateID(), getField().getFieldID(), getCharacterID());
            return;
        }
        MobBanMap banMap = template.getBanMap().get(Math.abs(Rand32.genRandom().intValue()) % template.getBanMap().size());
        postTransferField(banMap.getFieldID(), banMap.getPortalName(), false);
        sendSystemMessage(template.getBanMsg());
    }
}
