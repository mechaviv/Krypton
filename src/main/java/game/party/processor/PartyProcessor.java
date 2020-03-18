package game.party.processor;

import game.GameApp;
import game.party.PartyMan;
import game.party.PartyMember;
import game.party.PartyResCode;
import game.user.User;
import network.packet.ClientPacket;
import network.packet.OutPacket;
import util.Logger;
import util.TimerThread;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 2/5/2020.
 */
public class PartyProcessor {
    private final ReentrantLock lock;
    private final AtomicInteger partyIDCounter;
    private final Map<Integer, Integer> idEntry;
    private final Map<Integer, PartyMember> partyEntry;
    private final LinkedList<PartyRequest> requests;
    public PartyProcessor() {
        this.lock = new ReentrantLock();
        this.partyIDCounter = new AtomicInteger();
        this.idEntry = new HashMap<>();
        this.partyEntry = new HashMap<>();
        this.requests = new LinkedList<>();
        TimerThread.Party.Register(() -> {
            run();
        }, 1000, 1000);
    }

    public void run() {
        if (requests.isEmpty()) {
            return;
        }
        PartyRequest request;
        lock.lock();
        try {
            request = requests.removeFirst();
        } finally {
            lock.unlock();
        }

        if (request != null) {
            switch (request.getType()) {
                case PartyRequest.LOAD_PARTY:
                    loadParty(request.getCharacterID(), request.isSend());
                    break;
                case PartyRequest.CREATE_PARTY:
                    createNewParty(request.getCharacterID());
                    break;
                case PartyRequest.WITHDRAW_PARTY:
                    withdrawParty(request.getCharacterID(), request.isKicked());
                    break;
                case PartyRequest.JOIN_PARTY:
                    joinParty(request.getCharacterID(), request.getCharacterID1());
                    break;
                case PartyRequest.NOTIFY_MIGRATION:
                    notifyMigration(request.getCharacterID(), request.getChannelID());
                    break;
                case PartyRequest.CHANGE_LEVEL_OR_JOB:
                    changeLevelOrJob(request.getCharacterID(), request.getVal(), request.isLevelChanged());
                    break;
                case PartyRequest.CHANGE_PARTY_BOSS:
                    changePartyBoss(request.getCharacterID(), request.isSuccess(), request.isForced());
                    break;
            }
        }
    }

    public void addRequest(PartyRequest request) {
        lock.lock();
        try {
            requests.addLast(request);
        } finally {
            lock.unlock();
        }
    }

    public int findIndex(PartyMember party, int characterID) {
        for (int i = 0; i < 6; i++) {
            if (party.getCharacterID().get(i) == characterID) {
                return i;
            }
        }
        return -1;
    }

    public void loadParty(int characterID, boolean send) {
        int partyID = idEntry.getOrDefault(characterID, 0);
        if (partyID == 0) {
            return;
        }
        PartyMember party = partyEntry.getOrDefault(partyID, null);
        if (send) {
            OutPacket packet = new OutPacket(0);
            packet.encodeByte(PartyResCode.LoadParty_Done);
            packet.encodeInt(partyID);
            packet.encodeInt(characterID);
            party.encode(packet);
            sendPacket(packet);
        }
    }

    public void createNewParty(int characterID) {
        User user = GameApp.getInstance().findUser(characterID);
        if (user == null) {
            return;
        }
        int retCode = PartyResCode.CreateNewParty_Done;
        int partyID = 0;
        if (idEntry.containsKey(characterID)) {
            retCode = PartyResCode.CreateNewParty_AlreadyJoined;
        } else {
            partyID = partyIDCounter.incrementAndGet();
            PartyMember party = new PartyMember();
            party.getCharacterID().set(0, characterID);
            party.getCharacterName().set(0, user.getCharacterName());
            party.getJob().set(0, (int) user.getCharacter().getCharacterStat().getJob());
            party.getLevel().set(0, (int) user.getLevel());
            party.getChannelID().set(0, (int) user.getChannelID());
            party.setPartyBossCharacterID(characterID);
            partyEntry.put(partyID, party);
            idEntry.put(characterID, partyID);
        }
        sendPacket(retCode, user, retCode != PartyResCode.CreateNewParty_Done ? 0 : partyID);
    }

    public void withdrawParty(int characterID, boolean kicked) {
        User user = GameApp.getInstance().findUser(characterID);
        int partyID = idEntry.getOrDefault(characterID, 0);
        PartyMember party = partyEntry.getOrDefault(partyID, null);

        int retCode = PartyResCode.WithdrawParty_Done;
        if (party == null ) {
            retCode = PartyResCode.WithdrawParty_NotJoined;
        }
        int idX = findIndex(party, characterID);
        if (idX < 0) {
            retCode = PartyResCode.WithdrawParty_NotJoined;
        }
        if (retCode == PartyResCode.WithdrawParty_Done) {
            OutPacket packet = new OutPacket(0);
            packet.encodeByte(PartyResCode.WithdrawParty_Done);
            packet.encodeInt(partyID);
            packet.encodeInt(characterID);
            if (party.getPartyBossCharacterID() == characterID) {
                partyEntry.remove(partyID);
                for (int i = 0; i < 6; i++) {
                    idEntry.remove(party.getCharacterID().get(i));
                }
                packet.encodeByte(0);
            } else {
                idEntry.remove(characterID);
                packet.encodeByte(1);
                packet.encodeBool(kicked);
                packet.encodeString(party.getCharacterName().get(idX));
                party.getCharacterID().set(idX, 0);
                party.getCharacterName().set(idX, "");
                party.getJob().set(idX, 0);
                party.getLevel().set(idX, 0);
                party.getChannelID().set(idX, -2);
                party.encode(packet);
            }
            sendPacket(packet);
        } else if (user != null) {
            sendPacket(retCode, user, 0);
        }
    }

    public void joinParty(int characterID0, int characterID1) {
        User user = GameApp.getInstance().findUser(characterID1);
        if (user == null) {
            return;
        }
        int partyID = idEntry.getOrDefault(characterID0, 0);
        if (partyID == 0) {
            createNewParty(characterID0);
            idEntry.getOrDefault(characterID0, 0);
        }
        int retCode = PartyResCode.JoinParty_Done;
        int idX = -1;
        PartyMember party = partyEntry.getOrDefault(partyID, null);
        if (party != null) {
            idX = findIndex(party, 0);
            if (idX >= 0) {
                if (idEntry.containsKey(characterID1)) {
                    retCode = PartyResCode.JoinParty_AlreadyJoined;
                }
            } else {
                retCode = PartyResCode.JoinParty_AlreadyFull;
            }
        } else {
            retCode = PartyResCode.JoinParty_UnknownUser;
        }
        if (retCode == PartyResCode.JoinParty_Done) {
            idEntry.put(characterID1, partyID);
            party.getCharacterID().set(idX, characterID1);
            party.getCharacterName().set(idX, user.getCharacterName());
            party.getJob().set(idX, (int) user.getCharacter().getCharacterStat().getJob());
            party.getLevel().set(idX, (int) user.getLevel());
            party.getChannelID().set(idX, (int) user.getChannelID());

            OutPacket packet = new OutPacket(0);
            packet.encodeByte(PartyResCode.JoinParty_Done);
            packet.encodeInt(partyID);
            packet.encodeInt(characterID1);
            packet.encodeString(user.getCharacterName());
            party.encode(packet);
            sendPacket(packet);
        } else {
            sendPacket(retCode, user, 0);
        }
    }

    public void notifyMigration(int characterID, int channelID) {
        User user = GameApp.getInstance().findUser(characterID);
        if (channelID != -2 && user == null) {
            return;
        }
        int partyID = idEntry.getOrDefault(characterID, 0);
        if (partyID == 0) {
            return;
        }
        PartyMember party = partyEntry.getOrDefault(partyID, null);
        if (party == null) {
            return;
        }
        int idX = findIndex(party, characterID);
        if (idX < 0 || idX >= 6) {
            return;
        }
        party.getChannelID().set(idX, channelID);
        OutPacket packet = new OutPacket(0);
        packet.encodeByte(PartyResCode.UserMigration);
        packet.encodeInt(partyID);
        packet.encodeInt(characterID);
        packet.encodeByte(channelID);
        sendPacket(packet);
    }

    public void changeLevelOrJob(int characterID, int val, boolean levelChanged) {
        int partyID = idEntry.getOrDefault(characterID, 0);
        if (partyID == 0) {
            Logger.logReport("return 1");
            return;
        }
        PartyMember party = partyEntry.getOrDefault(partyID, null);
        if (party == null) {
            Logger.logReport("return 2");
            return;
        }
        int idX = findIndex(party, characterID);
        if (levelChanged) {
            party.getLevel().set(idX, val);
        } else {
            party.getJob().set(idX, val);
        }
        OutPacket packet = new OutPacket(0);
        packet.encodeByte(PartyResCode.ChangeLevelOrJob);
        packet.encodeInt(partyID);
        packet.encodeInt(characterID);
        packet.encodeInt(party.getLevel().get(idX));
        packet.encodeInt(party.getJob().get(idX));
        sendPacket(packet);
    }

    public void changePartyBoss(int characterID, boolean success, boolean forced) {
        int partyID = idEntry.getOrDefault(characterID, 0);
        if (partyID == 0) {
            return;
        }
        PartyMember party = partyEntry.getOrDefault(partyID, null);
        if (party == null) {
            return;
        }
        if (success) {
            party.setPartyBossCharacterID(characterID);
            OutPacket packet = new OutPacket(0);
            packet.encodeByte(PartyResCode.ChangePartyBoss_Done);
            packet.encodeInt(partyID);
            packet.encodeInt(characterID);
            packet.encodeBool(forced);
            sendPacket(packet);
        } else {
            // reason ?
        }
    }

    public void sendPacket(int retCode, User user, int partyID) {
        OutPacket packet = new OutPacket(ClientPacket.PartyRequest);
        packet.encodeByte(retCode);
        packet.encodeInt(partyID);
        packet.encodeInt(user.getCharacterID());
        sendPacket(packet);
    }

    public void sendPacket(OutPacket packet) {
        PartyMan.getInstance().onPacket(packet.toInPacket());
    }
}
