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
package shop.user;

import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemSlotType;
import common.item.ItemType;
import common.user.CharacterData;
import game.user.command.UserGradeCode;
import game.user.item.ItemInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.database.CommonDB;
import network.database.ShopDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import shop.Commodity;
import shop.ShopApp;
import shop.ShopPacket;
import shop.field.Stage;
import shop.user.inventory.Inventory;
import util.FileTime;
import util.Logger;
import util.Rand32;
import util.SystemTime;

/**
 *
 * @author sunnyboy
 */
public class User {

    private int accountID;
    private byte authenCode;
    private int birthDate;
    private List<CashItemInfo> cashItemInfo;
    private int cashKey;
    private boolean cashShopAuthorized;
    private final CharacterData character;
    private int characterID;
    private String characterName;
    private boolean doCheckCashItemExpire;
    private int gender;
    private byte gradeCode;

    private int kssn;
    private long lastCharacterDataFlush;
    private int localSocketSN;
    private final Lock lock;
    private final Lock lockSocket;

    private int modFlag;
    private int price;
    private byte delta;
    private int nexonCash;
    private String nexonClubID;

    private long nextCheckCashItemExpire;

    private String rcvCharacterName;

    private int slotCount;
    private ClientSocket socket;
    private byte typeIndex;
    private static final Lock lockUser = new ReentrantLock();

    private static final Map<String, User> userByName = new LinkedHashMap<>();
    private static final Map<Integer, User> users = new LinkedHashMap<>();

    protected User(int characterID) {
        super();
        this.cashKey = 0;

        this.cashItemInfo = new ArrayList<>();
        this.nexonCash = 0;
        this.gender = -1;
        this.modFlag = 0;
        ShopDB.rawLoadAccount(characterID, User.this);
        this.character = ShopDB.rawLoadCharacter(characterID, User.this);

        if (this.nexonClubID != null && !this.nexonClubID.isEmpty()) {
            this.cashShopAuthorized = true;
            Logger.logReport("User is cashShopAuthorized");
        }
        this.cashKey = Rand32.getInstance().random();
        this.lock = new ReentrantLock();
        this.lockSocket = new ReentrantLock();
    }

    public User(ClientSocket socket) {
        this(socket.getCharacterID());
        this.socket = socket;
        this.localSocketSN = socket.getLocalSocketSN();
        this.characterID = character.getCharacterStat().getCharacterID();
        this.characterName = character.getCharacterStat().getName();
    }

    public static final void broadcast(OutPacket packet) {
        lockUser.lock();
        try {
            for (User user : users.values()) {
                if (user != null) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lockUser.unlock();
        }
    }

    public static final void broadcastGMPacket(OutPacket packet) {
        lockUser.lock();
        try {
            for (User user : users.values()) {
                if (user != null && user.isGM()) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lockUser.unlock();
        }
    }

    public static final synchronized User findUser(int characterID) {
        lockUser.lock();
        try {
            if (users.containsKey(characterID)) {
                User user = users.get(characterID);
                if (user != null) {
                    return user;
                }
            }
            return null;
        } finally {
            lockUser.unlock();
        }
    }

    public static final synchronized User findUserByName(String name, boolean makeLower) {
        lockUser.lock();
        try {
            if (makeLower) {
                name = name.toLowerCase();
            }
            if (userByName.containsKey(name)) {
                User user = userByName.get(name);
                if (user != null) {
                    return user;
                }
            }
            return null;
        } finally {
            lockUser.unlock();
        }
    }

    public static final Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    public static final synchronized boolean registerUser(User user) {
        lockUser.lock();
        try {
            if (users.containsKey(user.characterID)) {
                return false;
            } else {
                users.put(user.characterID, user);
                userByName.put(user.characterName.toLowerCase(), user);
                return true;
            }
        } finally {
            lockUser.unlock();
        }
    }

    public static final void unregisterUser(User user) {
        lockUser.lock();
        try {
            users.remove(user.characterID);
            userByName.remove(user.characterName.toLowerCase());
        } finally {
            lockUser.unlock();
        }
    }

    public List<CashItemInfo> getCashItemInfo() {
        return this.cashItemInfo;
    }

    private void checkCashItemExpire(long time) {
        // when you enter CS, it should check if your cs items if they expired or not. Needs packet to update inventory (?)
        if (time - this.nextCheckCashItemExpire >= 0 && this.doCheckCashItemExpire != true) {
            FileTime cur;
            if ((cur = SystemTime.getLocalTime().systemTimeToFileTime()) != null) {
                this.nextCheckCashItemExpire = time + 180000;
                for (Iterator<CashItemInfo> it = this.cashItemInfo.iterator(); it.hasNext();) {
                    CashItemInfo cashItem = it.next();
                    if (FileTime.compareFileTime(cashItem.getDateExpire(), cur) <= 0) {
                        this.doCheckCashItemExpire = true;
                        it.remove();
                    }
                }
            }
        }
    }

    private void closeSocket() {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.postClose();
            }
        } finally {
            lockSocket.unlock();
        }
    }

    public final void destructUser() {
        flushCharacterData(0, true);
        Logger.logReport("User logout");
    }

    private void flushCharacterData(int cur, boolean force) {
        if (lock()) {
            try {
                if (force || cur - lastCharacterDataFlush >= 300000) {
                    ShopApp.getInstance().updateItemInitSN();
                    if (this.modFlag != 0) {
                        if ((modFlag & ModFlag.NexonCash) != 0) {
                            Logger.logReport("Updating NexonCash");
                            if (this.nexonCash < 0) {
                                this.nexonCash = 0;
                            }
                            ShopDB.rawUpdateNexonCash(this.accountID, this.nexonCash);
                        }
                        if ((modFlag & ModFlag.ItemLocker) != 0) {
                            Logger.logReport("Updating ItemLocker");
                            ShopDB.rawUpdateItemLocker(this.characterID, this.cashItemInfo);
                        }
                        if ((modFlag & ModFlag.ItemSlotEquip) != 0) {
                            Logger.logReport("Updating SlotEquip");
                            CommonDB.rawUpdateItemEquip(this.characterID, this.character.getEquipped(), this.character.getEquipped2(), this.character.getItemSlot(ItemType.Equip));
                        }
                        if ((modFlag & ModFlag.ItemSlotBundle) != 0 || (modFlag & ModFlag.ItemSlotEtc) != 0) {
                            Logger.logReport("Updating Bundle");
                            CommonDB.rawUpdateItemBundle(this.characterID, this.character.getItemSlot());
                        }
                        if ((modFlag & ModFlag.InventorySize) != 0) {
                            Logger.logReport("Updating SlotCount");
                            ShopDB.rawIncreaseItemSlotCount(this.characterID, this.typeIndex, this.slotCount);
                        }
                        modFlag = 0;
                    }
                    lastCharacterDataFlush = cur;
                }
            } finally {
                unlock();
            }
        }
    }

    public int getAccountID() {
        return this.accountID;
    }

    public CharacterData getCharacter() {
        return character;
    }

    public int getCharacterID() {
        return characterID;
    }

    public int getKSSN() {
        return kssn;
    }

    public int getNexonCash() {
        return nexonCash;
    }

    private boolean isBlockedMachineID() {
        return ((authenCode & 8) != 0);
    }

    public boolean isGM() {
        return gradeCode >= UserGradeCode.GM.getGrade();
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

    private void onBuy(InPacket packet) {
        int commoditySN = packet.decodeInt();
        Commodity comm = ShopApp.getInstance().findCommodity(commoditySN);
        if (comm != null && ItemInfo.isCashItem(comm.getItemID())) {
            if (this.getNexonCash() < comm.getPrice()) {
                sendPacket(ShopPacket.onBuyFailed((byte) 31));
                return;
            }
            CashItemInfo cashItem = new CashItemInfo();
            cashItem.setCashItemSN(ShopApp.getInstance().getNextCashSN());
            cashItem.setAccountID(this.accountID);
            cashItem.setCharacterID(this.characterID);
            cashItem.setItemID(comm.getItemID());
            cashItem.setCommodityID(commoditySN);
            cashItem.setNumber(comm.getCount());
            cashItem.setBuyCharacterName(this.characterName);
            FileTime ftExpire = FileTime.systemTimeToFileTime();
            ftExpire.add(FileTime.FILETIME_DAY, comm.getPeriod());
            cashItem.setDateExpire(ftExpire);
            this.cashItemInfo.add(cashItem);
            this.nexonCash -= comm.getPrice();
            this.modFlag |= ModFlag.NexonCash | ModFlag.ItemLocker;
            sendPacket(ShopPacket.onBuyDone(cashItem));
            this.sendRemainCashRequest();

            // Always keep the shop server's initSN up-to-date.
            ShopApp.getInstance().updateItemInitSN();
        }
    }

    private void onCashItemRequest(InPacket packet) {
        int type = packet.decodeByte(true);
        switch (type) {
            case CashItemRequest.Buy:
                onBuy(packet);
                break;
            case CashItemRequest.Gift:
                onGift(packet);
                break;
            case CashItemRequest.IncSlotCount:
                onIncSlotCount(packet);
                break;
            case CashItemRequest.MoveLtoS:
                onMoveLToS(packet);
                break;
            case CashItemRequest.MoveStoL:
                onMoveSToL(packet);
                break;
            default:
                Logger.logReport("Unhandled CashItemRequest Type %d", type);
                break;
        }
    }

    private void onChargeParamRequest(InPacket packet) {
        sendPacket(ShopPacket.onQueryCash(this));
    }

    private void onGift(InPacket packet) {
        int commoditySN = packet.decodeInt();
        String reciever = packet.decodeString();
        Commodity comm = ShopApp.getInstance().findCommodity(commoditySN);
        if (comm != null && ItemInfo.isCashItem(comm.getItemID()) && this.getNexonCash() >= comm.getPrice()) {
            this.rcvCharacterName = reciever;
            if (this.rcvCharacterName != null && rcvCharacterName.length() <= 12) {
                ReceivedGift receivedGift = ShopDB.rawLoadAccountByNameForGift(rcvCharacterName);
                if (receivedGift != null) {
                    if (this.getNexonCash() < comm.getPrice()) {
                        sendPacket(ShopPacket.onGiftFailed((byte) 31));
                        return;
                    }
                    SystemTime st = SystemTime.getLocalTime();
                    if (st != null && (st.getYear() - Integer.parseInt(String.valueOf(this.birthDate).substring(0, 4))) < 14) {
                        sendPacket(ShopPacket.onGiftFailed((byte) 32));
                        return;
                    }
                    if (ItemAccessor.getGenderFromID(comm.getItemID()) != receivedGift.getGender()) {
                        sendPacket(ShopPacket.onGiftFailed((byte) 35));
                        return;
                    }
                    CashItemInfo cashItem = new CashItemInfo();
                    cashItem.setCashItemSN(ShopApp.getInstance().getNextCashSN());
                    cashItem.setAccountID(receivedGift.getAccountID());
                    cashItem.setCharacterID(receivedGift.getCharacterID());
                    cashItem.setItemID(comm.getItemID());
                    cashItem.setCommodityID(commoditySN);
                    cashItem.setNumber(comm.getCount());
                    cashItem.setBuyCharacterName(this.characterName);
                    FileTime ftExpire = FileTime.systemTimeToFileTime();
                    ftExpire.add(FileTime.FILETIME_DAY, comm.getPeriod());
                    cashItem.setDateExpire(ftExpire);

                    this.nexonCash -= comm.getPrice();
                    this.modFlag |= ModFlag.NexonCash;
                    this.sendRemainCashRequest();

                    ShopDB.rawInsertItemLocker(cashItem);
                    sendPacket(ShopPacket.onGiftDone(rcvCharacterName, comm.getItemID(), comm.getCount()));

                    ShopApp.getInstance().updateItemInitSN();
                } else {
                    sendPacket(ShopPacket.onGiftFailed((byte) 35));
                }
            }
        }
    }

    private void onIncSlotCount(InPacket packet) {
        if (!this.cashShopAuthorized) {
            sendPacket(ShopPacket.onIncSlotCountFailed((byte) 30));
            return;
        }
        byte ti = packet.decodeByte();
        if (ti < ItemType.NotDefine || ti > ItemType.Cash) {
            return;
        }
        this.price = 4800;
        this.delta = 4;
        this.typeIndex = ti;
        if (this.price > this.getNexonCash()) {
            sendPacket(ShopPacket.onIncSlotCountFailed((byte) 31));
        } else {
            int newSlotCount = this.delta + this.character.getItemSlotCount(ti);
            if ((newSlotCount - this.delta) * getIncreasedSlotCount(ti, this.character.getCharacterStat().getJob()) > 80) {
                return;
            }
            if (Inventory.incItemSlotCount(this, ti, this.delta)) {
                this.slotCount = newSlotCount;
                this.nexonCash -= this.price;
                this.modFlag |= ModFlag.NexonCash | ModFlag.InventorySize;
                this.sendRemainCashRequest();
                sendPacket(ShopPacket.onIncSlotCountDone(ti, (short) newSlotCount));
            }
        }
    }

    public static int getIncreasedSlotCount(byte ti, short job) {
        int count = 0;
        switch (job / 100) {
            case 1:
                count = 1;
                if (job / 10 % 10 != 0 && (ti == ItemType.Consume || ti == ItemType.Etc)) {
                    ++count;
                }
                break;
            case 2:
                if (job / 10 % 10 != 0 && ti == ItemType.Etc) {
                    count = 1;
                }
                break;
            case 3:
                if (ti == ItemType.Equip || ti == ItemType.Consume) {
                    count = 1;
                }
                if (job / 10 % 10 != 0 && ti == ItemType.Etc) {
                    ++count;
                }
                break;
            case 4:
                if (ti == ItemType.Equip || ti == ItemType.Etc) {
                    count = 1;
                }
                if (job / 10 % 10 != 0 && ti == ItemType.Consume) {
                    ++count;
                }
                break;
            default:
                return count;
        }
        return count;
    }

    public void onMigrateInSuccess() {
        Logger.logReport("User login from (%s)", this.characterName);
        sendPacket(Stage.onSetCashShop(this));
        sendPacket(ShopPacket.onLoadLockerDone(this.cashItemInfo));
        sendPacket(ShopPacket.onQueryCash(this));
    }

    private void onMoveLToS(InPacket packet) {
        if (this.cashShopAuthorized) {
            long sn = packet.decodeLong();
            byte ti = packet.decodeByte();
            short pos = packet.decodeShort();

            if (ti <= ItemType.NotDefine || ti >= ItemType.NO) {
                Logger.logError("Invalid item type index (sn: %d, pos: %d for ti %d)", sn, pos, ti);
                return;
            }
            if (pos <= 0 || pos > this.character.getItemSlotCount(ti) || this.character.getItem(ti, pos) != null) {
                Logger.logError("Invalid item slot position (sn: %d) at pos: %d", sn, pos);
                return;
            }
            if (this.character.findEmptySlotPosition(ti) <= 0) {
                Logger.logError("No valid slot remains for sn: %d", sn);
                return;
            }
            ItemSlotBase item = null;
            if (ti == ItemType.Equip) {
                item = (ItemSlotEquip) ItemSlotBase.createItem(ItemSlotType.Equip);
            } else if (ti == ItemType.Consume || ti == ItemType.Etc) {
                item = (ItemSlotBundle) ItemSlotBase.createItem(ItemSlotType.Bundle);
            }
            if (item != null) {
                for (Iterator<CashItemInfo> it = this.cashItemInfo.iterator(); it.hasNext();) {
                    CashItemInfo cashItem = it.next();
                    if (cashItem != null && cashItem.getCashItemSN() == sn) {
                        item.setCashItemSN(cashItem.getCashItemSN());
                        item.setAccountID(cashItem.getAccountID());
                        item.setCharacterID(cashItem.getCharacterID());
                        item.setItemID(cashItem.getItemID());
                        item.setCommodityID(cashItem.getCommodityID());
                        item.setItemNumber(cashItem.getNumber());
                        item.setBuyCharacterName(cashItem.getBuyCharacterName());
                        item.setDateExpire(cashItem.getDateExpire());
                        it.remove();
                        break;
                    }
                }
                this.character.setItem(ti, pos, item);
                sendPacket(ShopPacket.onMoveLToS(pos, item, ti));
                this.modFlag |= ModFlag.ItemLocker | (ti == ItemType.Equip ? ModFlag.ItemSlotEquip : (ti == ItemType.Consume ? ModFlag.ItemSlotBundle : ModFlag.ItemSlotEtc));
            }
        }
    }

    private void onMoveSToL(InPacket packet) {
        if (this.cashShopAuthorized) {
            long sn = packet.decodeLong();
            byte ti = packet.decodeByte();
            int pos = character.findCashItemSlotPosition(ti, sn);
            if (pos <= 0) {
                Logger.logError("Inexistent cash item(sn: %d, ti: %d) in locker for characterID %d ", sn, ti, this.characterID);
                return;
            }
            ItemSlotBase item = this.character.getItem(ti, pos);
            if (item != null) {
                CashItemInfo cashItem = new CashItemInfo();
                cashItem.setCashItemSN(item.getCashItemSN());
                cashItem.setAccountID(item.getAccountID());
                cashItem.setCharacterID(item.getCharacterID());
                cashItem.setItemID(item.getItemID());
                cashItem.setCommodityID(item.getCommodityID());
                cashItem.setNumber(item.getItemNumber());
                cashItem.setBuyCharacterName(item.getBuyCharacterName());
                cashItem.setDateExpire(item.getDateExpire());
                this.cashItemInfo.add(cashItem);
                this.modFlag |= ModFlag.ItemLocker | (ti == ItemType.Equip ? ModFlag.ItemSlotEquip : (ti == ItemType.Consume ? ModFlag.ItemSlotBundle : ModFlag.ItemSlotEtc));
                this.character.setItem(ti, pos, null);
                sendPacket(ShopPacket.onMoveSToL(cashItem));
            }
        }
    }

    public void onPacket(short type, InPacket packet) {
        Logger.logReport("[Packet Logger] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
        switch (type) {
            case ClientPacket.UserTransferFieldRequest:
                onTransferFieldRequest();
                break;
            case ClientPacket.CashShopChargeParamRequest:
                onChargeParamRequest(packet);
                break;
            case ClientPacket.CashShopQueryCashRequest:
                onQueryCashRequest();
                break;
            case ClientPacket.CashShopCashItemRequest:
                onCashItemRequest(packet);
                break;
        }
    }

    private void onQueryCashRequest() {
        sendRemainCashRequest();
    }

    public void onSocketDestroyed(boolean migrate) {
        User.unregisterUser(this);
        lockSocket.lock();
        try {
            socket = null;
        } finally {
            lockSocket.unlock();
        }
    }

    private void onTransferFieldRequest() {
        sendMigrateOutPacket();
    }

    private void sendMigrateOutPacket() {
        socket.onFilterMigrateOut();
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

    private void sendRemainCashRequest() {
        if (this.cashShopAuthorized) {
            sendPacket(ShopPacket.onQueryCash(this));
        }
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setBirthDate(int birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setKSSN(int kssn) {
        this.kssn = kssn;
    }

    public void setNexonCash(int nexonCash) {
        this.nexonCash = nexonCash;
    }

    public void setNexonClubID(String nexonClubID) {
        this.nexonClubID = nexonClubID;
    }

    public final void unlock() {
        lock.unlock();
    }

    public boolean update(long cur) {
        if (isBlockedMachineID()) {
            closeSocket();
        }
        // checkCashItemExpire(cur); for now
        return true;
    }

    public class CashItemRequest {
        public static final short WebShopOrderGetList = 0x0;
        public static final short LoadLocker = 0x1;
        public static final short LoadWish = 0x2;
        public static final short Buy = 0x3;
        public static final short Gift = 0x4;
        public static final short SetWish = 0x5;
        public static final short IncSlotCount = 0x6;
        public static final short IncTrunkCount = 0x7;
        public static final short IncCharSlotCount = 0x8;
        public static final short IncBuyCharCount = 0x9;
        public static final short EnableEquipSlotExt = 0xA;
        public static final short CancelPurchase = 0xB;
        public static final short ConfirmPurchase = 0xC;
        public static final short Destroy = 0xD;
        public static final short MoveLtoS = 0xE;
        public static final short MoveStoL = 0xF;
        public static final short Expire = 0x10;
        public static final short Use = 0x11;
        public static final short StatChange = 0x12;
        public static final short SkillChange = 0x13;
        public static final short SkillReset = 0x14;
        public static final short DestroyPetItem = 0x15;
        public static final short SetPetName = 0x16;
        public static final short SetPetLife = 0x17;
        public static final short SetPetSkill = 0x18;
        public static final short SetItemName = 0x19;
        public static final short SendMemo = 0x1A;
        public static final short GetMaplePoint = 0x1B;
        public static final short Rebate = 0x1C;
        public static final short UseCoupon = 0x1D;
        public static final short GiftCoupon = 0x1E;
        public static final short Couple = 0x1F;
        public static final short BuyPackage = 0x20;
        public static final short GiftPackage = 0x21;
        public static final short BuyNormal = 0x22;
        public static final short ApplyWishListEvent = 0x23;
        public static final short MovePetStat = 0x24;
        public static final short FriendShip = 0x25;
        public static final short ShopScan = 0x26;
        public static final short LoadPetExceptionList = 0x27;
        public static final short UpdatePetExceptionList = 0x28;
        public static final short FreeCashItem = 0x29;
        public static final short LoadFreeCashItem = 0x2A;
        public static final short Script = 0x2B;
        public static final short PurchaseRecord = 0x2C;
        public static final short TradeDone = 0x2D;
        public static final short BuyDone = 0x2E;
        public static final short TradeSave = 0x2F;
        public static final short TradeLog = 0x30;
        public static final short EvolPet = 0x31;
        public static final short BuyNameChange = 0x32;
        public static final short CancelChangeName = 0x33;
        public static final short BuyTransferWorld = 0x35;
        public static final short CancelTransferWorld = 0x36;
        public static final short CharacterSale = 0x37;
        public static final short ItemUpgrade = 0x3C;
        public static final short ItemUpgradeFail = 0x3E;
        public static final short ItemUpgradeReq = 0x3F;
        public static final short ItemUpgradeDone = 0x40;
        public static final short Vega = 0x43;
        public static final short CashItemGachapon = 0x4A;
        public static final short CashGachaponOpen = 0x4B;
        public static final short CashGachaponCopy = 0x4C;
        public static final short ChangeMaplePoint = 0x4D;
        public static final short CheckFreeCashItemTable = 0x4E;
        public static final short SetFreeCashItemTable = 0x51;
        public static final short Give = 0xBD;
    }

    public class CashItemResult {
        public static final short CancelNameChangeFail = 0x34;
        public static final short CharacterSaleSuccess = 0x38;
        public static final short CharacterSaleFail = 0x39;
        public static final short CharacterSaleInvalidName = 0x3A;
        public static final short CharacterSaleInvalidItem = 0x3B;
        public static final short ItemUpgradeSuccess = 0x3D;
        public static final short ItemUpgradeDone = 0x41;
        public static final short ItemUpgradeErr = 0x42;
        public static final short VegaSuccess1 = 0x44;
        public static final short VegaSuccess2 = 0x45;
        public static final short VegaErr = 0x46;
        public static final short VegaErr2 = 0x47;
        public static final short VegaErr_InvalidItem = 0x48;
        public static final short VegaFail = 0x49;
        public static final short CheckFreeCashItemTable_Done = 0x4F;
        public static final short CheckFreeCashItemTable_Failed = 0x50;
        public static final short SetFreeCashItemTable_Done = 0x52;
        public static final short SetFreeCashItemTable_Failed = 0x53;
        public static final short LimitGoodsCount_Changed = 0x54;
        public static final short WebShopOrderGetList_Done = 0x55;
        public static final short WebShopOrderGetList_Failed = 0x56;
        public static final short WebShopReceive_Done = 0x57;
        public static final short LoadLocker_Done = 0x58;
        public static final short LoadLocker_Failed = 0x59;
        public static final short LoadGift_Done = 0x5A;
        public static final short LoadGift_Failed = 0x5B;
        public static final short LoadWish_Done = 0x5C;
        public static final short LoadWish_Failed = 0x5D;
        public static final short MapleTV_Failed_Wrong_User_Name = 0x5E;
        public static final short MapleTV_Failed_User_Not_Connected = 0x5F;
        public static final short AvatarMegaphone_Queue_Full = 0x60;
        public static final short AvatarMegaphone_Level_Limit = 0x61;
        public static final short SetWish_Done = 0x62;
        public static final short SetWish_Failed = 0x63;
        public static final short Buy_Done = 0x64;
        public static final short Buy_Failed = 0x65;
        public static final short UseCoupon_Done = 0x66;
        public static final short UseCoupon_Done_NormalItem = 0x67;
        public static final short GiftCoupon_Done = 0x68;
        public static final short UseCoupon_Failed = 0x69;
        public static final short UseCoupon_CashItem_Failed = 0x6A;
        public static final short Gift_Done = 0x6B;
        public static final short Gift_Failed = 0x6C;
        public static final short IncSlotCount_Done = 0x6D;
        public static final short IncSlotCount_Failed = 0x6E;
        public static final short IncTrunkCount_Done = 0x6F;
        public static final short IncTrunkCount_Failed = 0x70;
        public static final short IncCharSlotCount_Done = 0x71;
        public static final short IncCharSlotCount_Failed = 0x72;
        public static final short IncBuyCharCount_Done = 0x73;
        public static final short IncBuyCharCount_Failed = 0x74;
        public static final short EnableEquipSlotExt_Done = 0x75;
        public static final short EnableEquipSlotExt_Failed = 0x76;
        public static final short MoveLtoS_Done = 0x77;
        public static final short MoveLtoS_Failed = 0x78;
        public static final short MoveStoL_Done = 0x79;
        public static final short MoveStoL_Failed = 0x7A;
        public static final short Destroy_Done = 0x7B;
        public static final short Destroy_Failed = 0x7C;
        public static final short Expire_Done = 0x7D;
        public static final short Expire_Failed = 0x7E;
        public static final short Use_Done = 0x7F;
        public static final short Use_Failed = 0x80;
        public static final short StatChange_Done = 0x81;
        public static final short StatChange_Failed = 0x82;
        public static final short SkillChange_Done = 0x83;
        public static final short SkillChange_Failed = 0x84;
        public static final short SkillReset_Done = 0x85;
        public static final short SkillReset_Failed = 0x86;
        public static final short DestroyPetItem_Done = 0x87;
        public static final short DestroyPetItem_Failed = 0x88;
        public static final short SetPetName_Done = 0x89;
        public static final short SetPetName_Failed = 0x8A;
        public static final short SetPetLife_Done = 0x8B;
        public static final short SetPetLife_Failed = 0x8C;
        public static final short MovePetStat_Failed = 0x8D;
        public static final short MovePetStat_Done = 0x8E;
        public static final short SetPetSkill_Failed = 0x8F;
        public static final short SetPetSkill_Done = 0x90;
        public static final short SendMemo_Done = 0x91;
        public static final short SendMemo_Warning = 0x92;
        public static final short SendMemo_Failed = 0x93;
        public static final short GetMaplePoint_Done = 0x94;
        public static final short GetMaplePoint_Failed = 0x95;
        public static final short Rebate_Done = 0x96;
        public static final short Rebate_Failed = 0x97;
        public static final short Couple_Done = 0x98;
        public static final short Couple_Failed = 0x99;
        public static final short BuyPackage_Done = 0x9A;
        public static final short BuyPackage_Failed = 0x9B;
        public static final short GiftPackage_Done = 0x9C;
        public static final short GiftPackage_Failed = 0x9D;
        public static final short BuyNormal_Done = 0x9E;
        public static final short BuyNormal_Failed = 0x9F;
        public static final short ApplyWishListEvent_Done = 0xA0;
        public static final short ApplyWishListEvent_Failed = 0xA1;
        public static final short Friendship_Done = 0xA2;
        public static final short Friendship_Failed = 0xA3;
        public static final short LoadExceptionList_Done = 0xA4;
        public static final short LoadExceptionList_Failed = 0xA5;
        public static final short UpdateExceptionList_Done = 0xA6;
        public static final short UpdateExceptionList_Failed = 0xA7;
        public static final short LoadFreeCashItem_Done = 0xA8;
        public static final short LoadFreeCashItem_Failed = 0xA9;
        public static final short FreeCashItem_Done = 0xAA;
        public static final short FreeCashItem_Failed = 0xAB;
        public static final short Script_Done = 0xAC;
        public static final short Script_Failed = 0xAD;
        public static final short Bridge_Failed = 0xAE;
        public static final short PurchaseRecord_Done = 0xAF;
        public static final short PurchaseRecord_Failed = 0xB0;
        public static final short EvolPet_Failed = 0xB1;
        public static final short EvolPet_Done = 0xB2;
        public static final short NameChangeBuy_Done = 0xB3;
        public static final short NameChangeBuy_Failed = 0xB4;
        public static final short TransferWorld_Done = 0xB5;
        public static final short TransferWorld_Failed = 0xB6;
        public static final short CashGachaponOpen_Done = 0xB7;
        public static final short CashGachaponOpen_Failed = 0xB8;
        public static final short CashGachaponCopy_Done = 0xB9;
        public static final short CashGachaponCopy_Failed = 0xBA;
        public static final short ChangeMaplePoint_Done = 0xBB;
        public static final short ChangeMaplePoint_Failed = 0xBC;
        public static final short Give_Done = 0xBE;
        public static final short Give_Failed = 0xBF;
        public static final short GashItemGachapon_Failed = 0xC0;
        public static final short CashItemGachapon_Done = 0xC1;
    }
    
    public class ModFlag {

        private static final byte NexonCash = 0x1;
        private static final byte ItemLocker = 0x2;
        private static final byte ItemSlotEquip = 0x4;
        private static final byte ItemSlotBundle = 0x8;
        private static final byte ItemSlotEtc = 0x10;
        private static final byte InventorySize = 0x20;
    }
}
