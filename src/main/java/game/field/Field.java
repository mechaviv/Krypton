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
package game.field;

import common.BroadcastMsg;
import common.game.field.FieldEffectFlags;
import common.item.ItemAccessor;
import game.GameApp;
import game.field.MovePath.Elem;
import game.field.drop.DropPool;
import game.field.life.LifePool;
import game.field.life.mob.BossIDs;
import game.field.life.mob.Mob;
import game.field.life.mob.MobPool;
import game.field.life.npc.Npc;
import game.field.life.npc.NpcPool;
import game.field.portal.PortalMap;
import game.field.reactor.ReactorPool;
import game.party.PartyMan;
import game.user.User;
import game.user.UserRemote;
import game.user.WvsContext;
import game.user.item.ItemInfo;
import game.user.item.MobSummonItem;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Rect;
import util.Size;
import util.SystemTime;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eric
 */
public class Field {
    public static final int
            // Field IDs
            Henesys = 100000000,
            Basic = 104000000,
            Invalid = 999999999,
            FreeMarket = 910000000,
            SkyTerraceDepot = 922030011,
            EndTour = 950000000,
            MapleIsland_Max = 9999999,

    // Boss Field IDs
    HONTALE_MAP_ID = 240060200,
            BABY_BOSS_MAP_ID = 270050100,
            BALROG_MAP_ID = 105100300,
            BALROG_HUNTED_MAP_ID = 105100301,
            EASY_BALROG_MAP_ID = 105100400,
            EASY_BALROG_HUNTED_MAP_ID = 105100401,
            PARTY_RAID_WIN_MAP_ID = 923020010,
            PARTY_RAID_LOSE_MAP_ID = 923020020,
            ZAKUM_MAP_ID = 280030000,
            CHAOS_ZAKUM_MAP_ID = 280030001,
            CHAOS_HONTALE_MAP_ID = 240060201,
    // The default screen dimensions
    WvsScreenWidth = 800,
            WvsScreenHeight = 600,
    // The formula to construct the screen width/height offsets
    ScreenWidthOffset = (WvsScreenWidth * 75) / 100,
            ScreenHeightOffset = (WvsScreenHeight * 75) / 100;

    private final AtomicInteger fieldObjIdCounter;
    private final int field;
    private final WvsPhysicalSpace2D space2D;
    private final PortalMap portal;
    private final LifePool lifePool;
    private final DropPool dropPool;
    private final ReactorPool reactorPool;
    private final Map<Integer, User> users;
    private int fieldReturn;
    private int forcedReturn;
    private String streetName;
    private String mapName;
    private Point leftTop;
    private Size map;
    private double mobRate;
    private double recoveryRate;
    private int option;
    private int autoDecHP;
    private int autoDecMP;
    private boolean town;
    private boolean clock;
    private boolean swim;
    private String weatherMsg;
    private int weatherItemID;
    private int weatherDuration;
    private long weatherBegin;
    private int splitRowCount;
    private int splitColCount;
    private double incRateEXP;
    private double incRateDrop;
    private FieldSplit splitStart;
    private FieldSplit splitEnd;
    private List<FieldSplit> fieldSplit;
    private FieldSet parentFieldSet;
    private final int channelID;

    public Field(int fieldID, int channelID) {
        this.fieldObjIdCounter = new AtomicInteger(30000);
        this.field = fieldID;
        this.channelID = channelID;
        this.space2D = new WvsPhysicalSpace2D();
        this.portal = new PortalMap();
        this.weatherItemID = 0;
        this.incRateEXP = 1.0d;
        this.incRateDrop = 1.0d;
        this.lifePool = new LifePool(this);
        this.dropPool = new DropPool(this);
        this.reactorPool = new ReactorPool(this);
        this.users = new ConcurrentHashMap<>();
    }

    public void broadcastPacket(OutPacket packet, List<Integer> characters) {
        for (int characterID : characters) {
            User user = users.get(characterID);
            if (user != null) {
                user.sendPacket(packet);
            }
        }
    }

    public void broadcastPacket(OutPacket packet, boolean exceptAdmin) {
        for (User user : users.values()) {
            if (user != null && (!exceptAdmin || !user.isGM()))//!((pUser.nGradeCode & 1) > 0)
                user.sendPacket(packet);
        }
    }

    public void expireDrops(User user) {
        int count = dropPool.getDrops().size();
        dropPool.tryExpire(true);
        if (user != null) {
            user.sendSystemMessage("Items Destroyed: " + count);
        }
    }

    public User findUser(int characterID) {
        return users.get(characterID);
    }

    public DropPool getDropPool() {
        return dropPool;
    }

    public PortalMap getPortal() {
        return portal;
    }

    public LifePool getLifePool() {
        return lifePool;
    }

    public Size getMapSize() {
        return map;
    }

    public void setMapSize(Size sz) {
        this.map = sz;
    }

    public double getMobRate() {
        return mobRate;
    }

    public void setMobRate(double mobRate) {
        this.mobRate = mobRate;
    }

    public WvsPhysicalSpace2D getSpace2D() {
        return space2D;
    }

    public void getEncloseSplit(FieldSplit p, FieldSplit[] split) {
        for (int i = 0; i < split.length; i++) {
            split[i] = null;
        }
        // =============
        // [0]  [1]  [2]    [Row - 1]
        // [3]  {4}  [5]    [Row + 0]
        // [6]  [7]  [8]    [Row + 1]
        // =============
        int row = p.getRow();
        int col = p.getCol();
        if (row != 0) {
            if (col != 0) {
                split[0] = fieldSplit.get((col - 1) + (row - 1) * splitColCount);
            }
            split[1] = fieldSplit.get(col + (row - 1) * splitColCount);
            if (col < splitColCount - 1) {
                split[2] = fieldSplit.get((col + 1) + (row - 1) * splitColCount);
            }
        }
        if (col != 0) {
            split[3] = fieldSplit.get((col - 1) + row * splitColCount);
        }
        split[4] = p;
        if (col < splitColCount - 1) {
            split[5] = fieldSplit.get((col + 1) + row * splitColCount);
        }
        if (row < splitRowCount - 1) {
            if (col != 0) {
                split[6] = fieldSplit.get((col - 1) + (row + 1) * splitColCount);
            }
            split[7] = fieldSplit.get(col + (row + 1) * splitColCount);
            if (col < splitColCount - 1) {
                split[8] = fieldSplit.get((col + 1) + (row + 1) * splitColCount);
            }
        }
    }

    public int getFieldID() {
        return this.field;
    }

    public int getFieldType() {
        return FieldType.Default;
    }

    public int getForcedReturnFieldID() {
        return this.forcedReturn;
    }

    public double getIncEXPRate() {
        return this.incRateEXP;
    }

    public double getIncDropRate() {
        return this.incRateDrop;
    }

    public int getOption() {
        return this.option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public double getRecoveryRate() {
        return this.recoveryRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getReturnFieldID() {
        return this.fieldReturn;
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public List<User> getUsers(boolean exceptAdmin) {
        List<User> users = new ArrayList<>(getUsers());

        users.removeIf(user -> user == null || (exceptAdmin && user.isGM()));
        return users;
    }

    public int incrementIdCounter() {
        if (fieldObjIdCounter.get() > 2000000000) {
            Logger.logError("The FieldObjID counter has exceeded 2billion objects, resetting to 30000.");
            fieldObjIdCounter.set(30000);
        }
        return fieldObjIdCounter.incrementAndGet();
    }

    public boolean isSwim() {
        return swim;
    }

    public void setSwim(boolean swim) {
        this.swim = swim;
    }

    public boolean isTown() {
        return town;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean isUserExist(int characterID) {
        return users.containsKey(characterID);
    }

    public Point makePointInSplit(int x, int y) {
        x = Math.min(Math.max(x, leftTop.x), (leftTop.x + ScreenWidthOffset * splitColCount) - 1);
        y = Math.min(Math.max(y, leftTop.y), (leftTop.y + ScreenHeightOffset * splitRowCount) - 1);
        return new Point(x, y);
    }

    public void makeSplit() {
        this.splitColCount = (map.cx + (ScreenWidthOffset - 1)) / ScreenWidthOffset;
        this.splitRowCount = (map.cy + (ScreenHeightOffset - 1)) / ScreenHeightOffset;
        this.fieldSplit = new ArrayList<>(splitColCount * splitRowCount);
        for (int i = 0; i < splitRowCount; ++i) {
            for (int j = 0; j < splitColCount; ++j) {
                fieldSplit.add(new FieldSplit(i, j, j + i * splitColCount));
            }
        }
        this.splitStart = fieldSplit.get(0);
        this.splitEnd = fieldSplit.get(fieldSplit.size() - 1);
    }

    public boolean onEnter(final User user) {
        if (!dropPool.onEnter(user)) {
            return false;
        }
        if (!splitRegisterFieldObj(user.getCurrentPosition().x, user.getCurrentPosition().y, FieldSplit.User, user)) {
            Logger.logError("Incorrect field position [%09d]", field);
            return false;
        }
        splitRegisterUser(null, user.getSplit(), user);
        user.setPosMap(this.field);
        users.put(user.getCharacterID(), user);
        lifePool.insertController(user);
        if (weatherItemID != 0) {
            user.sendPacket(FieldPacket.onBlowWeather(weatherItemID, weatherMsg));
        }
        // Jukebox
        if (parentFieldSet != null) {
            parentFieldSet.onUserEnterField(this, user);
        }
        PartyMan.getInstance().notifyTransferField(user.getCharacterID(), getFieldID());
        return true;
    }

    public void onLeave(User user) {
        lifePool.removeController(user);
        dropPool.onLeave(user);
        users.remove(user.getCharacterID());
        splitRegisterUser(user.getSplit(), null, user);
        splitUnregisterFieldObj(FieldSplit.User, user);
    }

    public void onMobMove(User ctrl, Mob mob, InPacket packet) {
        short mobCtrlSN = packet.decodeShort();
        byte mobCtrlState = packet.decodeByte();//bDirLeft | (unsigned __int8)(16 * nMobCtrlState)
        boolean nextAttackPossible = (mobCtrlState & 0xF) != 0;
        byte action = packet.decodeByte();//2 * nAction | bLeft & 1
        int data = packet.decodeInt();

        int multiTargetForBall = packet.decodeInt();
        for (int i = 0; i < multiTargetForBall; i++) {
            packet.decodeInt();// x
            packet.decodeInt();// y
        }

        int randTimeforAreaAttack = packet.decodeInt();
        for (int i = 0; i < randTimeforAreaAttack; i++) {
            packet.decodeInt();// randTimeforAreaAttack
        }

        packet.decodeByte();// isCheatMobMoveRand
        packet.decodeInt();// nHackedCode
        packet.decodeInt();// moveCtx.fc.ptTarget.x || 0xFFDDCC
        packet.decodeInt();// moveCtx.fc.ptTarget.y || 0xFFDDCC
        packet.decodeInt();// nHackedCodeCRC

        if (mob.getController().getUser() != ctrl && ((mobCtrlState & 0xF0) == 0 || mob.isNextAttackPossible()
                || !lifePool.changeMobController(ctrl.getCharacterID(), mob, true))) {
            mob.sendChangeControllerPacket(ctrl, (byte) 0);
            return;
        }

        byte left = action;
        if (action < 0)
            action = -1;
        else
            action = (byte) ((action >> 1) & 0xFF);
        if (mob.onMobMove(nextAttackPossible, action, data)) {
            ctrl.sendPacket(MobPool.onCtrlAck(mob.getGameObjectID(), mobCtrlSN, nextAttackPossible, mob.getMP()));
            MovePath mp = new MovePath();
            mp.decode(packet);
            packet.decodeByte();// pMob.bChasing
            packet.decodeByte();// pvcActive.pTarget != 0
            packet.decodeByte();// pvcActive.pTarget.bChasing
            packet.decodeByte();// pvcActive.pTarget.bChasingHack
            packet.decodeInt();// pvcActive.pTarget.tChaseDuration
            if (!mp.getElem().isEmpty()) {
                Elem tail = mp.getElem().getLast();
                FieldSplit splitOld = mob.getSplit();
                FieldSplit centerSplit = null;
                int x = tail.getX();
                int y = tail.getY();
                if (splitOld == null
                        || tail.getX() < (x = ScreenWidthOffset * splitOld.getCol() + leftTop.x - 100)
                        || tail.getX() > (x + WvsScreenWidth)
                        || tail.getY() < (y = ScreenHeightOffset * splitOld.getRow() + leftTop.y - 75)
                        || tail.getY() > (y + WvsScreenHeight)) {
                    centerSplit = splitFromPoint(tail.getX(), tail.getY());
                    if (splitOld == null || centerSplit == null) {
                        Logger.logError("Incorrect field position from mob [%d,%d] [%p,(%d,%d)] [%p,%p,%d]");
                        if (!mob.getTemplate().isBoss()) {//pMob.dwTemplateID / 10000 != 880 && pMob.dwTemplateID / 10000 != 881;
                            lifePool.removeMob(mob);
                        }
                        return;
                    }
                }
                if (!mob.setMovePosition(tail.getX(), tail.getY(), (byte) (tail.getMoveAction() & 0xFF), tail.getFh())) {
                    Logger.logError("Invalid Mob MoveAction (Disconnect), Level : %d", ctrl.getLevel());
                    //ctrl.incHackingCount(HackingAutoBlock.Move);
                }
                if (centerSplit != null) {
                    splitMigrateFieldObj(centerSplit, FieldSplit.Mob, mob);
                }
                splitSendPacket(mob.getSplit(), MobPool.onMove(mob.getGameObjectID(), nextAttackPossible, left, data, mp), ctrl);
            }
            mp.getElem().clear();
        }
    }

    public void onNpcMove(User ctrl, Npc npc, InPacket packet) {
        if (npc.getController() != null && npc.getController().getUser() == ctrl) {
            byte action = packet.decodeByte();
            byte chatIdx = packet.decodeByte();
            MovePath mp = null;
            if (npc.getNpcTemplate().isMove()) {
                mp = new MovePath();
                mp.decode(packet);
                if (!mp.getElem().isEmpty()) {
                    Elem tail = mp.getElem().getLast();
                    if (tail != null) {
                        FieldSplit split = npc.getSplit();
                        FieldSplit centerSplit = null;
                        int x = ScreenHeightOffset * split.getCol() + leftTop.x - 100;
                        int y = ScreenHeightOffset * split.getRow() + leftTop.y - 75;
                        if (tail.getX() < x || tail.getX() > (x + WvsScreenWidth) || tail.getY() < y || tail.getY() > (y + WvsScreenHeight)) {
                            centerSplit = splitFromPoint(tail.getX(), tail.getY());
                            if (centerSplit == null) {
                                Logger.logError("Incorrect field position from NPC [%d]", npc.getTemplateID());
                                return;
                            }
                        }
                        if (tail.getX() < npc.getOriginalPos().x - 50 || tail.getX() > npc.getOriginalPos().x + 50 || tail.getY() < npc.getOriginalPos().y - 50 || tail.getY() > npc.getOriginalPos().y + 50) {
                            Logger.logError("Invalid NPC Position [ id : %d, field : %d, pos : %d, %d ]", npc.getTemplateID(), field, tail.getX(), tail.getY());
                            lifePool.removeNpc(npc);
                            return;
                        }
                        npc.setMovePosition(tail.getX(), tail.getY(), (byte) (tail.getMoveAction() & 0xFF), tail.getFh());
                        if (centerSplit != null) {
                            splitMigrateFieldObj(centerSplit, FieldSplit.Npc, npc);
                        }
                    }
                }
            }
            splitSendPacket(ctrl.getSplit(), NpcPool.onMove(npc.getGameObjectID(), action, chatIdx, mp), ctrl);
            ctrl.sendPacket(NpcPool.onMove(npc.getGameObjectID(), action, chatIdx, mp));
            if (mp != null) {
                mp.getElem().clear();
            }
        }
    }

    public void onPacket(User user, short type, InPacket packet) {
        if (type >= ClientPacket.BEGIN_LIFEPOOL && type <= ClientPacket.END_LIFEPOOL) {
            lifePool.onPacket(user, type, packet);
        } else if (type >= ClientPacket.BEGIN_DROPPOOL && type <= ClientPacket.END_DROPPOOL) {
            dropPool.onPacket(user, type, packet);
        } else if (type >= ClientPacket.BEGIN_REACTORPOOL && type <= ClientPacket.END_REACTORPOOL) {
            reactorPool.onPacket(user, type, packet);
        }
    }

    public void onUserMove(User user, InPacket packet, Rect move) {
        if (user.getMiniRoom() != null) {
            return;
        }
        packet.decodeInt();//CField::GetCrc(get_field())
        // dwKey = get_rand(drInfo.dr0, 0) (CRC Init Key)
        packet.decodeInt();// dwKey

        // bDetect = DR_check(&drInfo, &check, 0);
        packet.decodeInt();// CCrc32::GetCrc32(&bDetect, 4u, dwKey, 0, 0);

        MovePath mp = new MovePath();
        mp.decode(packet);
        if (mp.getElem().isEmpty()) {
            Logger.logError("Received Empty Move Path [%s]", user.getCharacterName());
            return;
        }

        Elem tail = mp.getElem().getLast();
        // Nexon applies the pTail coordinates here, however that will
        // cause an incorrect result on fieldsplits if the movement's
        // element is ever wrong. Since the current coordinates are of
        // the portal destination, they are never wrong.
        final int xPos = user.getCurrentPosition().x;//pTail.x
        final int yPos = user.getCurrentPosition().y;//pTail.y
        user.setMovePosition(tail.getX(), tail.getY(), (byte) (tail.getMoveAction() & 0xFF), tail.getFh());

        FieldSplit splitOld = user.getSplit();
        int x = ScreenWidthOffset * splitOld.getCol() + leftTop.x - 100;
        int y = ScreenHeightOffset * splitOld.getRow() + leftTop.y - 75;
        if (tail.getX() < x || tail.getX() > (x + WvsScreenWidth) || tail.getY() < y || tail.getY() > (y + WvsScreenHeight)) {
            FieldSplit splitNew = splitFromPoint(xPos, yPos);
            if (splitNew == null) {
                user.postTransferField(user.getField().getFieldID(), "", true);
                /*long tCur = System.currentTimeMillis();
                if ((tCur - pUser.tLastIncorrectFieldPositionTime) <= 60000) {
                    ++pUser.nIncorrectFieldPositionCount;
                    if (pUser.nIncorrectFieldPositionCount >= 5) {
                        Logger.logError("Incorrect field position (%d,%d)/(%d,%d,%d,%d)/(Map:%d)");
                        if (!pUser.isGM()) {
                            //pUser.closeSocket();
                            return;
                        }
                    }
                } else {
                    pUser.nIncorrectFieldPositionCount = 1;
                }
                pUser.tLastIncorrectFieldPositionTime = tCur;*/
                return;
            }
            splitMigrateFieldObj(splitNew, FieldSplit.User, user);
            splitRegisterUser(splitOld, splitNew, user);
        }
        splitSendPacket(user.getSplit(), UserRemote.onMove(user.getCharacterID(), mp), user);
        mp.getElem().clear();
    }

    public boolean onWeather(int itemID, String param, int duration) {
        if (!ItemAccessor.isWeatherItem(itemID) || weatherItemID != 0) {
            return false;
        }
        this.weatherItemID = itemID;
        this.weatherMsg = param;
        this.weatherBegin = System.currentTimeMillis();
        if (duration != 0) {
            this.weatherDuration = duration;
        }
        broadcastPacket(FieldPacket.onBlowWeather(itemID, param), false);
        return true;
    }

    public void setLeftTop(Point pt) {
        this.leftTop = pt;
    }

    public void setFieldReturn(int fieldReturn) {
        this.fieldReturn = fieldReturn;
    }

    public void setForcedReturn(int forcedReturn) {
        this.forcedReturn = forcedReturn;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setAutoDecHP(int autoDecHP) {
        this.autoDecHP = autoDecHP;
    }

    public void setAutoDecMP(int autoDecMP) {
        this.autoDecMP = autoDecMP;
    }

    public void setClock(boolean clock) {
        this.clock = clock;
    }

    public void setIncRateEXP(double incRateEXP) {
        this.incRateEXP = incRateEXP;
    }

    public void setIncRateDrop(double incRateDrop) {
        this.incRateDrop = incRateDrop;
    }

    public FieldSplit splitFromPoint(int x, int y) {
        if ((x = x - leftTop.x) < 0 || (y = y - leftTop.y) < 0
                || (x = x / ScreenWidthOffset) >= splitColCount
                || (y = y / ScreenHeightOffset) >= splitRowCount) {
            return null;
        } else {
            return fieldSplit.get(x + y * splitColCount);
        }
    }

    public void splitMigrateFieldObj(FieldSplit centerSplit, int foc, FieldObj obj) {
        FieldSplit[] src = new FieldSplit[9];
        getEncloseSplit(centerSplit, src);
        int i;
        for (FieldSplit split : src) {
            if (split != null) {
                i = 0;
                for (FieldSplit objSplit : obj.getSplits()) {
                    if (split == objSplit)
                        break;
                    ++i;
                }
                if (i >= obj.getSplits().length) {
                    splitRegisterFieldObj(split, foc, obj, obj.makeEnterFieldPacket());
                } else {
                    obj.setSplit(i, null);
                }
            }
        }
        for (FieldSplit pSplit : obj.getSplits()) {
            if (pSplit != null) {
                splitUnregisterFieldObj(pSplit, foc, obj, obj.makeLeaveFieldPacket());
            }
        }
        System.arraycopy(src, 0, obj.getSplits(), 0, src.length);
    }

    public void splitNotifyFieldObj(FieldSplit split, OutPacket packet, FieldObj obj) {
        if (split != null) {
            for (FieldObj objNew : split.getFieldObj(FieldSplit.User)) {
                User user = (User) objNew;
                if (obj == null || obj.isShowTo(user)) {
                    user.sendPacket(packet);
                }
            }
        }
    }

    public void splitRegisterFieldObj(FieldSplit split, int foc, FieldObj objNew, OutPacket packetEnter) {
        if (split != null) {
            for (User user : split.getUser()) {
                if (user != null) {
                    if (objNew.isShowTo(user)) {
                        user.sendPacket(packetEnter);
                    }
                }
            }
            split.getFieldObj(foc).addLast(objNew);
        }
    }

    public boolean splitRegisterFieldObj(int x, int y, int foc, FieldObj obj) {
        FieldSplit centerSplit = splitFromPoint(x, y);
        if (centerSplit != null) {
            getEncloseSplit(centerSplit, obj.getSplits());
            for (FieldSplit split : obj.getSplits()) {
                splitRegisterFieldObj(split, foc, obj, obj.makeEnterFieldPacket());
            }
            return true;
        }
        return false;
    }

    public void splitRegisterUser(FieldSplit splitOld, FieldSplit splitNew, User user) {
        LinkedList<FieldObj> fieldObj;
        for (int i = 0; i < GameObjectType.NO; i++) {
            fieldObj = new LinkedList<>();
            if (splitNew != null) {
                fieldObj.clear();
                fieldObj.addAll(splitNew.getFieldObj(i));
            }
            if (splitOld != null) {
                for (FieldObj pOld : splitOld.getFieldObj(i)) {
                    FieldObj pObj = null;
                    for (FieldObj pNew : fieldObj) {
                        if (pOld == pNew) {
                            pObj = pNew;
                            break;
                        }
                    }
                    if (pObj != null) {
                        fieldObj.remove(pObj);
                    } else if (splitNew != null) {
                        user.sendPacket(pOld.makeLeaveFieldPacket());
                    }
                }
            }
            for (FieldObj objNew : fieldObj) {
                if (objNew != null && objNew.isShowTo(user)) {
                    user.sendPacket(objNew.makeEnterFieldPacket());
                }
            }
            fieldObj.clear();
        }
        if (splitOld != null) {
            splitOld.getUser().remove(user);
        }
        if (splitNew != null) {
            splitNew.getUser().addLast(user);
        }
    }

    public void splitSendPacket(FieldSplit split, OutPacket packet, User except) {
        if (split != null) {
            for (FieldObj obj : split.getFieldObj(FieldSplit.User)) {
                User user = (User) obj;
                if (user != except) {
                    user.sendPacket(packet);
                }
            }
        }
    }

    public void splitUnregisterFieldObj(FieldSplit split, int foc, FieldObj posObj, OutPacket packetLeave) {
        if (split != null) {
            for (User user : split.getUser()) {
                if (user != null) {
                    user.sendPacket(packetLeave);
                }
            }
            split.getFieldObj(foc).remove(posObj);
        }
    }

    public void splitUnregisterFieldObj(int foc, FieldObj obj) {
        for (FieldSplit split : obj.getSplits()) {
            if (split != null) {
                splitUnregisterFieldObj(split, foc, obj, obj.makeLeaveFieldPacket());
            }
        }
    }

    public void update(long time) {
        updateFieldBoss();
        lifePool.update(time);
        dropPool.tryExpire(false);
        //  CMessageBoxPool::TryExpireMessageBox(&v2->m_messageBoxPool);
        //  CSummonedPool::Update(&v2->m_summonedPool, tCur);
        //  CAffectedAreaPool::Update(&v2->m_affectedAreaPool, tCur);
        //  CTownPortalPool::Update(&v2->m_townPortalPool, tCur);
        reactorPool.update(time);
        if (weatherItemID != 0 && ItemAccessor.isWeatherItem(weatherItemID)) {
            if (weatherDuration != 0 && time - weatherBegin > 1000 * weatherDuration
                    || weatherDuration == 0 && time - weatherBegin > 30000) {
                weatherItemID = 0;
                broadcastPacket(FieldPacket.onBlowWeather(0, null), false);
            }
        }
    }

    private void updateFieldBoss() {
        if (this.field == HONTALE_MAP_ID) {

            Mob spiritMob = this.lifePool.getMobByTemplateID(BossIDs.HONTALE_SPIRIT_MOB_ID);
            if (spiritMob == null) {
                return;
            }
            int hp = 0;
            for (int i = 2; i <= 9; i++) {
                Mob hontalePart = this.lifePool.getMobByTemplateID(BossIDs.HONTALE_BASE_MOB_ID + i);
                if (hontalePart != null) {
                    hp += hontalePart.getHP();
                }
            }
            spiritMob.setFieldBossMobHP(hp);

            if (spiritMob.getHP() <= 0) {
                SystemTime st = SystemTime.getLocalTime();
                Logger.logReport("[Hontale: Dead Time] - %04d/%02d/%02d %02d:%02d:%02d", st.getYear(), st.getMonth(), st.getDay(), st.getHour(), st.getMinute(), st.getSecond());

                int huntingTime = (int) (System.currentTimeMillis() - spiritMob.getCreate()) / 1000;
                int hour = huntingTime / 3600;
                int min = huntingTime % 3600 / 60;
                int sec = huntingTime % 3600 % 60;
                Logger.logReport("[Hontale: Hunting Duration] : %02d Hours / %02d Min / %02d Sec", hour, min, sec);

                Point spiritPosition = new Point(spiritMob.getCurrentPos());
                this.lifePool.removeMob(spiritMob);

                int userCount = users.size();
                if (true || userCount >= 10 || huntingTime >= 3600) {
                    spiritMob.giveReward(0, spiritPosition, 0, false);
                } else {
                    Logger.logReport("[Hontale: Hacking] - No Reward ( UserCount %d, HuntingTime %d )", userCount, huntingTime);
                }
                this.lifePool.removeAllMob(false);

                for (User user : getUsers()) {
                    spiritMob.setMobCountQuestInfo(user);
                }
                GameApp.getInstance().broadcastWorld(WvsContext.onBroadcastMsg(BroadcastMsg.NOTICE_WITHOUT_PREFIX, "To the crew that have finally conquered Horned Tail after numerous attempts, I salute thee! You are the true heroes of Leafre!!"));

                String debugMsg = "[Hontale: Killed by] - ";
                int levelSum = 0;
                for (User user : getUsers()) {
                    debugMsg += "(" + user.getCharacterName() + "), ";
                    levelSum += user.getLevel();
                }
                Logger.logReport(debugMsg);

                int avgLevel = 0;
                if (userCount > 0) {
                    avgLevel = levelSum / userCount;
                }
                Logger.logReport("[Hontale: Info] - User Count ( %d ), Avg Level ( %d )", userCount, avgLevel);
            }
        } else if (this.field == BABY_BOSS_MAP_ID) {
            Mob spiritMob = this.lifePool.getMobByTemplateID(BossIDs.BABYBOSS_DUMMY5_MOB_ID);
            if (spiritMob == null) {
                return;
            }
            int hp = 0;
            for (int i = 2; i <= 6; i++) {
                Mob part = this.lifePool.getMobByTemplateID(BossIDs.BABYBOSS_BASE_MOB_ID + i);
                if (part != null) {
                    hp += part.getHP();
                }
            }
            spiritMob.setFieldBossMobHP(hp);
        }
    }

    public void reset(boolean shuffleReactor) {
        // (this->vfptr[10].__vecDelDtor)();
        portal.resetPortal();
        lifePool.reset();
        dropPool.tryExpire(true);
        reactorPool.reset(shuffleReactor);
    }

    public FieldSet getParentFieldSet() {
        return parentFieldSet;
    }

    public void setParentFieldSet(FieldSet parentFieldSet) {
        this.parentFieldSet = parentFieldSet;
    }

    public ReactorPool getReactorPool() {
        return reactorPool;
    }

    public int getChannelID() {
        return channelID;
    }

    public void effectTremble(int heavyNShortTremble, int delay) {
        broadcastPacket(FieldPacket.onFieldEffect(FieldEffectFlags.Tremble, null, heavyNShortTremble, delay), false);
    }

    public void effectChangeBGM(String BGM) {
        broadcastPacket(FieldPacket.onFieldEffect(FieldEffectFlags.ChangeBGM, BGM), false);
    }

    public void setObjectState(String name, int state) {
        if (name == null || name.isEmpty() || state < 0) {
            return;
        }
        broadcastPacket(FieldPacket.setObjectState(name, state), false);
    }

    public boolean checkReactorAction(String reactorName, long eventTime) {
        if (parentFieldSet == null) {
            return false;
        }
        return parentFieldSet.checkReactorAction(this, reactorName, eventTime);
    }

    public void summonMob(int x, int y, int itemID) {
        MobSummonItem item = ItemInfo.getMobSummonItem(itemID);
        if (item != null) {
            lifePool.onMobSummonItemUseRequest(new Point(x, y), item, false);
        }
    }
}
