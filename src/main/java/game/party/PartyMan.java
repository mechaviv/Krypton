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
package game.party;

import game.GameApp;
import game.field.Field;
import game.party.processor.PartyProcessor;
import game.party.processor.PartyRequest;
import game.user.User;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eric & SpongeBob
 */
public class PartyMan {
    private static final PartyMan instance = new PartyMan();
    private final ReentrantLock lock;
    private final Map<Integer, Integer> charIDtoPartyID;
    private final Map<Integer, PartyData> party;
    private final PartyProcessor processor;
    private long lastUpdate;

    public PartyMan() {
        this.lock = new ReentrantLock();
        this.charIDtoPartyID = new HashMap<>();
        this.party = new HashMap<>();
        this.processor = new PartyProcessor();
        this.lastUpdate = System.currentTimeMillis();
    }

    public static PartyMan getInstance() {
        return instance;
    }

    // packet methods
    public void onPacket(InPacket packet) {
        long time = System.currentTimeMillis();
        if ((time - lastUpdate) > 180000) {
            update();
            lastUpdate = time;
        }
        packet.decodeShort();// header
        int type = packet.decodeByte();
        switch (type) {
            case PartyResCode.LoadParty_Done:
                onLoadPartyDone(packet);// DONE
                break;
            case PartyResCode.CreateNewParty_Done:
                onCreateNewPartyDone(packet);
                break;
            case PartyResCode.WithdrawParty_Done:
                onWithdrawPartyDone(packet);
                break;
            case PartyResCode.JoinParty_Done:
                onJoinPartyDone(packet);
                break;
            case PartyResCode.UserMigration:
                onUserMigration(packet);
                break;
            case PartyResCode.ChangeLevelOrJob:
                onChangeLevelOrJob(packet);
                break;
            case PartyResCode.ChangePartyBoss_Done:
                onChangePartyBoss(packet);
                break;
            default:
                Logger.logReport("Unhanadled Party Request [%d]", type);
                break;
        }
    }

    public void onLoadPartyDone(InPacket packet) {
        int value = packet.decodeInt();
        int key = packet.decodeInt();
        lock.lock();
        try {
            if (charIDtoPartyID.containsKey(key)) {
                Logger.logError("Already exist party information");
            }
            charIDtoPartyID.put(key, value);

            PartyData pd = party.getOrDefault(value, null);
            if (pd == null) {
                pd = new PartyData();
                party.put(value, pd);
            }
            pd.getParty().decode(packet);

            int idX = findUser(key, pd);
            if (idX < 0) {
                Logger.logError("Invalid party information from party processor");
                return;
            }
            User user = GameApp.getInstance().findUser(key);
            int fieldID = Field.Invalid;
            if (user != null) {
                fieldID = user.getField().getFieldID();
            }
            pd.getFieldID().set(idX, fieldID);
            // set town portal go here
            broadcast(PartyPacket.loadParty(value, pd), pd.getParty().getCharacterID(), 0);
            if (user != null) {
                user.hpChanged(false);
            }
        } finally {
            lock.unlock();
        }
    }

    public void onCreateNewPartyDone(InPacket packet) {
        int value = packet.decodeInt();
        int key = packet.decodeInt();
        lock.lock();
        try {
            User user = GameApp.getInstance().findUser(key);
            if (user == null) {
                return;
            }
            if (charIDtoPartyID.containsKey(key)) {
                Logger.logError("Already joined party");
            }
            charIDtoPartyID.put(key, value);

            PartyData pd = new PartyData();
            pd.getParty().getCharacterID().set(0, key);
            pd.getParty().getCharacterName().set(0, user.getCharacterName());
            int fieldID = Field.Invalid;
            if (user.getField() != null) {
                fieldID = user.getField().getFieldID();
            }
            pd.getFieldID().set(0, fieldID);
            pd.getParty().getJob().set(0, (int) user.getCharacter().getCharacterStat().getJob());
            pd.getParty().getLevel().set(0, (int) user.getLevel());
            pd.getParty().getChannelID().set(0, (int) user.getChannelID());
            pd.getParty().setPartyBossCharacterID(key);
            // set up town portal here
            party.put(value, pd);
            user.sendPacket(PartyPacket.createNewParty(value, pd.getTownPortal().get(0)));
        } finally {
            lock.unlock();
        }
    }

    public void onWithdrawPartyDone(InPacket packet) {
        int key = packet.decodeInt();
        int n = packet.decodeInt();

        lock.lock();
        try {
            PartyData pd = party.getOrDefault(key, null);
            if (pd == null) {
                Logger.logError("Party information does not exist");
                return;
            }
            OutPacket outPacket = new OutPacket(LoopbackPacket.PartyResult);
            outPacket.encodeByte(PartyResCode.WithdrawParty_Done);
            outPacket.encodeInt(key);
            outPacket.encodeInt(n);
            if (packet.decodeBool()) {
                charIDtoPartyID.remove(n);
                outPacket.encodeByte(1);
                outPacket.encodeByte(packet.decodeByte());
                outPacket.encodeString(packet.decodeString());
                pd.getParty().decode(packet);
                // clear town portals
                for (int i = 0; i < 6; i++) {
                    if (pd.getParty().getCharacterID().get(i) == 0) {
                        pd.getTownPortal().get(i).setTownID(Field.Invalid);
                        pd.getTownPortal().get(i).setFieldID(Field.Invalid);
                        pd.getTownPortal().get(i).setSkillID(0);
                        pd.getTownPortal().get(i).setFieldPortal(new Point(-1, -1));
                    }
                }
                pd.encode(outPacket);
                broadcast(outPacket, pd.getParty().getCharacterID(), n);
            } else {
                for (int i = 0; i < 6; i++) {
                    if (pd.getParty().getCharacterID().get(i) != 0) {
                        charIDtoPartyID.remove(pd.getParty().getCharacterID().get(i));
                    }
                }
                outPacket.encodeByte(0);
                outPacket.encodeInt(pd.getParty().getCharacterID().get(0));
                broadcast(outPacket, pd.getParty().getCharacterID(), 0);
                party.remove(key);
            }
        } finally {
            lock.unlock();
        }
    }

    public void onJoinPartyDone(InPacket packet) {
        int value = packet.decodeInt();
        int key = packet.decodeInt();
        String name = packet.decodeString();

        lock.lock();
        try {
            if (charIDtoPartyID.containsKey(key)) {
                Logger.logError("Already joined party");
            }
            charIDtoPartyID.put(key, value);
            PartyData pd = party.getOrDefault(value, null);
            if (pd == null) {
                return;
            }
            pd.getParty().decode(packet);

            int idX = findUser(key, pd);
            if (idX < 0) {
                return;
            }
            User user = GameApp.getInstance().findUser(key);

            int fieldID = Field.Invalid;
            if (user != null) {
                fieldID = user.getField().getFieldID();
            }
            pd.getFieldID().set(idX, fieldID);
            // set town portal go here
            broadcast(PartyPacket.joinParty(value, name, pd), pd.getParty().getCharacterID(), 0);
            if (user != null) {
                user.hpChanged(false);
            }
        } finally {
            lock.unlock();
        }
    }

    public void onUserMigration(InPacket packet) {
        int key = packet.decodeInt();
        int characterID = packet.decodeInt();
        int channelID = packet.decodeByte();

        lock.lock();
        try {
            PartyData pd = party.getOrDefault(key, null);
            if (pd == null) {
                return;
            }
            int idX = findUser(key, pd);
            if (idX < 0) {
                return;
            }
            pd.getParty().getChannelID().set(idX, channelID);
            broadcast(PartyPacket.userMigration(key, pd), pd.getParty().getCharacterID(), 0);
        } finally {
            lock.unlock();
        }
    }

    public void onChangeLevelOrJob(InPacket packet) {
        int key = packet.decodeInt();
        int characterID = packet.decodeInt();
        int level = packet.decodeInt();
        int job = packet.decodeInt();

        lock.lock();
        try {
            PartyData pd = party.getOrDefault(key, null);
            if (pd == null) {
                Logger.logReport("Null party when change level or job");
                return;
            }
            int idX = findUser(key, pd);
            if (idX < 0) {
                return;
            }
            pd.getParty().getJob().set(idX, job);
            pd.getParty().getLevel().set(idX, level);
            broadcast(PartyPacket.changeLevelOrJob(characterID, level, job), pd.getParty().getCharacterID(), 0);
        } finally {
            lock.unlock();
        }
    }

    public void onChangePartyBoss(InPacket packet) {
        int key = packet.decodeInt();
        int characterID = packet.decodeInt();
        boolean forced = packet.decodeBool();
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(key, null);
            if (pd == null || findUser(key, pd) < 0) {
                return;
            }
            pd.getParty().setPartyBossCharacterID(characterID);
            broadcast(PartyPacket.changePartyBossDone(characterID, forced), pd.getParty().getCharacterID(), 0);
        } finally {
            lock.unlock();
        }
    }

    public void onLeave(User user, boolean migrate) {
        lock.lock();
        try {
            if (migrate) {
                notifyTransferField(user.getCharacterID(), Field.Invalid);
                charIDtoPartyID.remove(user.getCharacterID());
                return;
            }
            int key = charIdToPartyID(user.getCharacterID());
            if (key == 0) {
                notifyTransferField(user.getCharacterID(), Field.Invalid);
                charIDtoPartyID.remove(user.getCharacterID());
                return;
            }
            PartyData pd = party.getOrDefault(key, null);
            if (pd == null) {
                notifyTransferField(user.getCharacterID(), Field.Invalid);
                charIDtoPartyID.remove(user.getCharacterID());
                return;
            }
            int idX = findUser(key, pd);
            if (idX < 0) {
                notifyTransferField(user.getCharacterID(), Field.Invalid);
                charIDtoPartyID.remove(user.getCharacterID());
                return;
            }
            if (pd.getParty().getCharacterID().get(idX) == pd.getParty().getPartyBossCharacterID()) {
                int maxLevel = 0;
                int newLeaderIndex = -1;
                for (int i = 0; i < 6; i++) {
                    if (i == idX || pd.getFieldID().get(i) != pd.getFieldID().get(idX)) {
                        continue;
                    }
                    int level = pd.getParty().getLevel().get(i);
                    if (level > maxLevel) {
                        newLeaderIndex = i;
                        maxLevel = level;
                    }
                }
                if (newLeaderIndex < 0) {
                    postChangePartyBoss(pd.getParty().getPartyBossCharacterID(), true, false);
                } else {
                    postChangePartyBoss(pd.getParty().getCharacterID().get(newLeaderIndex), true, true);
                }
            }
            notifyTransferField(user.getCharacterID(), Field.Invalid);
            charIDtoPartyID.remove(user.getCharacterID());
        } finally {
            lock.unlock();
        }
    }

    // main methods
    public void update() {
        Map<Integer, Integer> toRemove = new HashMap<>();
        lock.lock();
        try {
            // Add all parties to the map
            for (Integer key : party.keySet()) {
                toRemove.put(key, 0);
            }
            // Keep only used parties
            for (Integer key : charIDtoPartyID.values()) {
                toRemove.remove(key);
            }
            // Remove unused parties
            for (Integer key : toRemove.keySet()) {
                party.remove(key);
            }
        } finally {
            lock.unlock();
        }
        toRemove.clear();
    }

    public int charIdToPartyID(int characterID) {
        lock.lock();
        try {
            return charIDtoPartyID.getOrDefault(characterID, 0);
        } finally {
            lock.unlock();
        }
    }

    // on methods

    public int findUser(int characterID, PartyData pd) {
        int index = 0;
        while (characterID != pd.getParty().getCharacterID().get(index)) {
            index++;
            if (index >= 6) {
                return -1;
            }
        }
        return index;
    }

    // notify methods
    public void notifyTownPortalChanged(int characterID, int townID, int fieldID, int skillID, Point fieldPortal) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(charIdToPartyID(characterID), null);
            if (pd == null) {
                return;
            }
            int idX = findUser(characterID, pd);
            if (idX == 0) {
                return;
            }
            PartyData.TownPortal townPortal = pd.getTownPortal().get(idX);
            townPortal.setTownID(townID);
            townPortal.setFieldID(fieldID);
            townPortal.setSkillID(skillID);
            townPortal.setFieldPortal(fieldPortal);
            pd.getTownPortal().set(idX, townPortal);

            broadcast(PartyPacket.townPortalChanged(idX, townPortal), pd.getParty().getCharacterID(), 0);
        } finally {
            lock.unlock();
        }
    }

    public void notifyTransferField(int characterID, int fieldID) {
        lock.lock();
        try {
            int partyID = charIdToPartyID(characterID);

            PartyData pd = party.getOrDefault(partyID, null);
            if (pd == null) {
                User user = GameApp.getInstance().findUser(characterID);
                if (user != null) {
                    user.sendPacket(PartyPacket.loadParty(0, new PartyData()));
                }
                return;
            }
            int idX = findUser(characterID, pd);
            if (idX >= 0) {
                pd.getFieldID().set(idX, fieldID);
                if (fieldID == Field.Invalid) {
                    PartyData.TownPortal townPortal = pd.getTownPortal().get(idX);
                    townPortal.setFieldID(Field.Invalid);
                    townPortal.setTownID(Field.Invalid);
                    townPortal.setSkillID(-1);
                    townPortal.setFieldPortal(new Point(-1, -1));
                    pd.getTownPortal().set(idX, townPortal);
                }
                broadcast(PartyPacket.loadParty(partyID, pd), pd.getParty().getCharacterID(), 0);
            }
        } finally {
            lock.unlock();
        }
    }

    public void notifyUserHPChanged(User user, boolean sendOnly) {
        if (user.getField() == null) {
            return;
        }
        if (user.getCharacter().getCharacterStat().getHP() == 0) {
            // CUser::RemoveSummoned(this, 0, 3, 0);
        }
        int partyID = charIdToPartyID(user.getCharacterID());
        if (partyID == 0) {
            return;
        }
        List<Integer> characterID = new ArrayList<>();
        getSnapshot(partyID, characterID);

        OutPacket local = PartyPacket.userHP(user.getCharacterID(), user.getCharacter().getCharacterStat().getHP(), user.getBasicStat().getMHP());
        for (int i = 0; i < 6; i++) {
            int partyUserID = characterID.get(i);
            if (partyUserID == 0) {
                continue;
            }
            User remoteUser = GameApp.getInstance().findUser(partyUserID);
            if (remoteUser == null) {
                continue;
            }
            if (user.getChannelID() == remoteUser.getChannelID() && user.getField().getFieldID() == remoteUser.getField().getFieldID()) {
                if (!sendOnly) {
                    user.sendPacket(PartyPacket.userHP(partyUserID, remoteUser.getCharacter().getCharacterStat().getHP(), remoteUser.getBasicStat().getMHP()));
                }
                remoteUser.sendPacket(local);
            }
        }
    }

    // broadcast methods
    public void broadcast(OutPacket packet, int partyID) {
        PartyData partyData = party.getOrDefault(partyID, null);
        if (party != null) {
            broadcast(packet, partyData.getParty().getCharacterID(), 0);
        }
    }

    public void broadcast(OutPacket packet, List<Integer> memberIDs, int plusOne) {
        lock.lock();
        try {
            if (plusOne != 0) {
                User user = GameApp.getInstance().findUser(plusOne);
                if (user != null) {
                    user.sendPacket(packet);
                }
            }
            for (int i = 0; i < 6; i++) {
                int characterID = memberIDs.get(i);
                if (characterID != 0) {
                    User user = GameApp.getInstance().findUser(characterID);
                    if (user != null) {
                        user.sendPacket(packet);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // getters methods
    public int getMemberIDx(int characterID) {
        int partyID = charIdToPartyID(characterID);
        List<Integer> characterIDs = new ArrayList<>(6);
        getSnapshot(partyID, characterIDs);
        for (int i = 0; i < 6; i++) {
            if (characterIDs.get(i) == characterID) {
                return i;
            }
        }
        return -1;
    }

    public int getPartyBossID(int partyID) {
        PartyData pd = party.getOrDefault(party, null);
        if (pd == null) {
            return 0;
        }
        return pd.getParty().getPartyBossCharacterID();
    }

    public int getPartyMemberID(int partyID, int idX) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd == null) {
                return -1;
            }
            User user = GameApp.getInstance().findUser(pd.getParty().getCharacterID().get(idX));
            User boss = GameApp.getInstance().findUser(pd.getParty().getPartyBossCharacterID());
            if (user != null && boss != null && user.getChannelID() == boss.getChannelID() && user.getField().getFieldID() == boss.getField().getFieldID()) {
                return pd.getParty().getCharacterID().get(idX);
            }
        } finally {
            lock.unlock();
        }
        return -1;
    }

    public int getPartyMemberJob(int partyID, int idX) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd == null) {
                return -1;
            }
            User user = GameApp.getInstance().findUser(pd.getParty().getCharacterID().get(idX));
            User boss = GameApp.getInstance().findUser(pd.getParty().getPartyBossCharacterID());
            if (user != null && boss != null && user.getChannelID() == boss.getChannelID() && user.getField().getFieldID() == boss.getField().getFieldID()) {
                return pd.getParty().getJob().get(idX);
            }
        } finally {
            lock.unlock();
        }
        return -1;
    }

    public int getPartyMemberLevel(int partyID, int idX) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd == null) {
                return 0;
            }
            User user = GameApp.getInstance().findUser(pd.getParty().getCharacterID().get(idX));
            User boss = GameApp.getInstance().findUser(pd.getParty().getPartyBossCharacterID());
            if (user != null && boss != null && user.getChannelID() == boss.getChannelID() && user.getField().getFieldID() == boss.getField().getFieldID()) {
                return pd.getParty().getLevel().get(idX);
            }
        } finally {
            lock.unlock();
        }
        return 0;
    }

    public String getPartyMemberName(int partyID, int idX) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd == null) {
                return null;
            }
            User user = GameApp.getInstance().findUser(pd.getParty().getCharacterID().get(idX));
            User boss = GameApp.getInstance().findUser(pd.getParty().getPartyBossCharacterID());
            if (user != null && boss != null && user.getChannelID() == boss.getChannelID() && user.getField().getFieldID() == boss.getField().getFieldID()) {
                return pd.getParty().getCharacterName().get(idX);
            }
        } finally {
            lock.unlock();
        }
        return null;// or just "" ?
    }

    public boolean getSnapshot(int partyID, List<Integer> characterIDs) {
        characterIDs.clear();
        if (partyID == 0) {
            for (int i = 0; i < 6; i++) characterIDs.add(0);
            return false;
        }
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd == null) {
                for (int i = 0; i < 6; i++) characterIDs.add(0);
                return false;
            }
            characterIDs.addAll(pd.getParty().getCharacterID());
        } finally {
            lock.unlock();
        }
        return true;
    }

    public PartyData.TownPortal getTownPortal(int characterID) {
        PartyData pd = party.getOrDefault(charIdToPartyID(characterID), null);
        if (pd != null) {
            return pd.getTownPortal().get(getMemberIDx(characterID));
        }
        return null;
    }

    // is methods
    public boolean isPartyBoss(int partyID, int characterID) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd != null) {
                return pd.getParty().getPartyBossCharacterID() == characterID;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean isPartyBoss(int characterID) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(charIdToPartyID(characterID), null);
            if (pd != null) {
                return pd.getParty().getPartyBossCharacterID() == characterID;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean isPartyMember(int partyID, int characterID) {
        lock.lock();
        try {
            PartyData pd = party.getOrDefault(partyID, null);
            if (pd != null) {
                return findUser(characterID, pd) >= 0;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public void postLoadParty(int characterID, boolean send) {
        PartyRequest request = new PartyRequest(PartyRequest.LOAD_PARTY);
        request.setCharacterID(characterID);
        request.setSend(send);
        processor.addRequest(request);
    }

    public void postCreateNewParty(int characterID) {
        PartyRequest request = new PartyRequest(PartyRequest.CREATE_PARTY);
        request.setCharacterID(characterID);
        processor.addRequest(request);
    }

    public void postWithdrawParty(int characterID, boolean kicked) {
        PartyRequest request = new PartyRequest(PartyRequest.WITHDRAW_PARTY);
        request.setCharacterID(characterID);
        request.setKicked(kicked);
        processor.addRequest(request);
    }

    public void postJoinParty(int characterID0, int characterID1) {
        PartyRequest request = new PartyRequest(PartyRequest.JOIN_PARTY);
        request.setCharacterID(characterID0);
        request.setCharacterID1(characterID1);
        processor.addRequest(request);
    }

    public void postNotifyMigration(int characterID, int channelID) {
        PartyRequest request = new PartyRequest(PartyRequest.NOTIFY_MIGRATION);
        request.setCharacterID(characterID);
        request.setChannelID(channelID);
        processor.addRequest(request);
    }

    public void postChangeLevelOrJob(int characterID, int val, boolean levelChanged) {
        PartyRequest request = new PartyRequest(PartyRequest.CHANGE_LEVEL_OR_JOB);
        request.setCharacterID(characterID);
        request.setVal(val);
        request.setLevelChanged(levelChanged);
        processor.addRequest(request);
    }

    public void postChangePartyBoss(int characterID, boolean forced, boolean success) {
        PartyRequest request = new PartyRequest(PartyRequest.CHANGE_PARTY_BOSS);
        request.setCharacterID(characterID);
        request.setSuccess(success);
        request.setForced(forced);
        processor.addRequest(request);
    }

}
