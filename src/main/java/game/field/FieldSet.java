package game.field;

import common.BroadcastMsg;
import common.Request;
import common.game.field.MobAppearType;
import game.GameApp;
import game.field.event.EventInfo;
import game.field.event.EventManager;
import game.field.event.Timer;
import game.field.life.npc.Npc;
import game.field.reactor.ReactorPool;
import game.field.reactor.action.ActionType;
import game.field.reactor.action.ReactorActionInfo;
import game.field.reactor.action.ReactorInfo;
import game.party.PartyMan;
import game.script.ScriptVM;
import game.user.User;
import game.user.WvsContext;
import game.user.quest.UserQuestRecord;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 2/2/2020.
 */
public class FieldSet extends EventManager {
    private final Map<String, String> variable;
    private final List<Field> affectedFields;
    private final List<Field> allFields;
    private final List<ReactorActionInfo> reactorActionInfos;
    private final List<EventProgress> eventProgresses;
    private final Lock lock;
    private final Lock lockVariable;
    private final Timer occupied;
    private final Timer enter;
    private final Timer timeOut;
    private String name;
    private String initScript;
    private boolean shuffleReactor;
    private boolean fieldSetStart;
    private boolean endFieldSetAct;
    private boolean reactorRegen;
    private boolean checkParty;
    private boolean tryToRunInitScript;
    private boolean castOut;
    private int checkTimeOut;
    private int targetFieldID;
    private int count;
    private int affectedCount;
    private long eventStart;
    private long lastParty;
    private int channelID;

    public FieldSet(String name, int channelID) {
        super();
        this.name = name;
        this.channelID = channelID;

        this.fieldSetStart = false;
        this.initScript = null;
        this.endFieldSetAct = false;
        this.targetFieldID = 0;
        this.affectedFields = new ArrayList<>();
        this.allFields = new ArrayList<>();
        this.lock = new ReentrantLock();

        this.occupied = new Timer();
        this.occupied.setFlag(0);

        this.enter = new Timer();
        this.enter.setFlag(0);

        this.timeOut = new Timer();
        this.timeOut.setFlag(0);

        this.tryToRunInitScript = false;

        this.lockVariable = new ReentrantLock();
        this.variable = new HashMap<>();
        this.reactorActionInfos = new ArrayList<>();
        this.eventProgresses = new ArrayList<>();
        this.castOut = false;
    }

    public int enter(int characterID, int fieldInfo) {
        User user = GameApp.getInstance().getChannel(channelID).findUser(characterID);
        if (user == null) {
            Logger.logReport("Return 9");
            return 9;
        }
        Field field = user.getField();
        if (field == null) {
            Logger.logReport("Return -1");
            return -1;
        }
        List<User> users = new ArrayList<>();// loads the users from party
        users.add(user);
        if (name.equals("Party1")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() != 4) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 21 || level > 30) {
                    return 3;
                }
            }
        } else if (name.equals("Party2")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            for (User partyUser : users) {
                String value = UserQuestRecord.get(partyUser, 7000);
                if (value == null) {
                    return 3;
                }
            }
        } else if (name.equals("Party3")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() != 6) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 35 || level > 50) {
                    return 3;
                }
            }
        } else if (name.equals("Party4")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
        } else if (name.equals("Party5")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() != 6) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 51 || level > 70) {
                    return 3;
                }
            }
        } else if (name.equals("PartyAmoria")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() != 6) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 40 || !user.isMarried()) {
                    return 3;
                }
            }
        } else if (name.equals("PartyAmoriaBo")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
        } else if (name.equals("PartyLudiMaze")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 3) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 51 || level > 70) {
                    return 3;
                }
                if (partyUser.isItemExist((byte) 4, 4001106)) {
                    return 6;
                }
            }
        } else if (name.equals("Wedding1") || name.equals("Wedding2")) {
            // TODO
        } else if (name.equals("Wedding4")) {
            // TODO
        } else if (name.length() >= 7 && name.substring(0, 7).equals("Wedding")) {
            // TODO
        } else if (name.equals("MoonRabbit")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 1) {// 3 temp 1
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 10) {
                    return 3;
                }
            }
        } else if (name.equals("MoonPig")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 3) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 10) {
                    return 3;
                }
            }
        } else if (name.equals("Hontale1") || name.equals("Hontale3")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            int reqCount = name.equals("Hontale1") ? 6 : 1;
            if (users.size() < reqCount) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 80) {
                    return 3;
                }
            }
        } else if (name.equals("S4efreet") || name.equals("S4snipe") || name.equals("S4rush")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 2) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 120) {
                    return 3;
                }
            }
        } else if (name.equals("S4common1") || name.equals("S4common2")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() > 6) {// ?
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 120) {
                    return 3;
                }
            }
        } else if (name.equals("DavyJohn1")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 3 || users.size() > 6) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 55 || level > 100) {
                    return 3;
                }
            }
        } else if (name.equals("MCarnival100") || name.equals("MCarnival110") || name.equals("MCarnival120") || name.equals("MCarnival130")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 2 || users.size() > 4 || users.size() > 6) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 30 || level > 50) {
                    return 3;
                }
            }
        } else if (name.equals("MCarnival140") || name.equals("MCarnival150")) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() < 3 || users.size() > 6) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 30 || level > 50) {
                    return 3;
                }
            }
        } else if (isRomioJulietFieldSet()) {
            List<Integer> characterIDs = new ArrayList<>();
            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
                return 1;
            }
            for (Integer partyUserID : characterIDs) {
                User partyUser = GameApp.getInstance().getChannel(user.getChannelID()).findUser(partyUserID);
                if (partyUser != null && partyUser.getField() == field) {
                    users.add(partyUser);
                }
            }
            if (users.size() != 4) {
                return 2;
            }
            for (User partyUser : users) {
                int level = partyUser.getLevel();
                if (level < 71 || level > 85) {
                    return 3;
                }
            }
        } else {
            users.add(user);
        }

        int result = tryEnter(users, fieldInfo, characterID);
        if (result == 0) {
            switch (name) {
                case "Guild1": {
                    // TODO
                    break;
                }
                case "Party5":
                case "TamePig":
                case "S4common1":
                case "DavyJohn1":
                case "DavyJohn2":
                case "DavyJohn3":
                case "DavyJohn3_hd":
                case "DavyJohn4":
                case "DavyJohn4_hd":
                case "DavyJohn5": {
                    startEvent(user);
                    break;
                }
            }
            if (name.equals("Wedding1"))
                if (isMCarnivalWaitingFieldSet() || isWatermelonFieldSet() || isRomioJulietFieldSet()) {
                    startEvent(user);
                }
            tryToRunInitScript = true;
        }
        if (name.equals("Wedding1") || name.equals("Wedding2") && result == 0) {
            // TODO
        } else if (!name.equals("Wedding30") || result != 0) {
            if (name.equals("Wedding4") && result == 0) {
                lastParty = System.currentTimeMillis();
                // TODO
            }
        } else {
            // TODO (wedding 30)
        }
        Logger.logReport("Return %d", result);
        return result;
    }

    public int tryEnter(List<User> users, int fieldIDx, int enterChar) {
        Logger.logReport("(%s) [%d] Trying to enter [%d].", name, enterChar, allFields.get(0).getFieldID());
        lock.lock();
        try {
            long time = System.currentTimeMillis();
            if (time - occupied.getStart() > occupied.getFlag() << 10) {
                occupied.setFlag(occupied.getFlag() & 0x7FFFFFFF);
            }
            if ((occupied.getFlag() & 0x80000000) != 0) {
                return 4;
            }
            if (time - enter.getStart() > enter.getFlag() << 10) {
                enter.setFlag(enter.getFlag() & 0x7FFFFFFF);
            }
            if ((enter.getFlag() & 0x80000000) != 0) {
                return 4;
            }
            enter.setStart(System.currentTimeMillis());
            enter.setFlag(0x80000014);
            onEnter(users);
        } finally {
            lock.unlock();
        }
        for (User user : users) {
            user.postTransferField(allFields.get(fieldIDx).getFieldID(), "", false);
        }
        return 0;
    }

    public void doEventAction(EventProgress event) {
        OutPacket packet;
        int fieldIDx;
        switch (event.getActionOnField()) {
            case 0:
                packet = FieldPacket.onDesc();
                for (Field field : affectedFields) {
                    field.broadcastPacket(packet, false);
                }
                break;
            case 1:
                packet = FieldPacket.setClockEventTimer(Integer.parseInt(event.getArgs().get(0)));
                for (Field field : affectedFields) {
                    field.broadcastPacket(packet, false);
                }
                break;
            case 2:
                fieldIDx = Integer.parseInt(event.getArgs().get(0));
                if (fieldIDx < allFields.size()) {
                    Field field = allFields.get(fieldIDx);
                    if (field != null) {
                        if (field.getPortal() != null) {
                            field.getPortal().enablePortal(event.getArgs().get(1), Integer.parseInt(event.getArgs().get(2)) != 0);
                        }
                    }
                }
                break;
            case 3:
                int type = Integer.parseInt(event.getArgs().get(0));
                if (type >= 0) {
                    int templateID = 0;
                    if (type == BroadcastMsg.UTIL_DLG_EX) {
                        templateID = Integer.parseInt(event.getArgs().get(2));
                    }
                    broadcastMsg(type, event.getArgs().get(1), templateID);
                }
                break;
            case 4:
                for (Field field : affectedFields) {
                    // snowballfield.reset(0);
                }
                break;
            case 5:
                // snowballfield.conclude(0);
                break;
            case 6:
                // guild thing
                break;
            case 7:
                // some message : for ex {Aramia from Henesys park will shoot up the firecrackers soon!}
                break;
            case 8:
                fieldIDx = Integer.parseInt(event.getArgs().get(0));
                if (fieldIDx < allFields.size()) {
                    Field field = allFields.get(fieldIDx);
                    if (field != null) {
                        int templateID = Integer.parseInt(event.getArgs().get(1));
                        int x = Integer.parseInt(event.getArgs().get(2));
                        Pointer<Integer> y = new Pointer<>(Integer.parseInt(event.getArgs().get(3)));
                        StaticFoothold fh = field.getSpace2D().getFootholdUnderneath(x, y.get(), y);
                        if (fh != null) {
                            field.getLifePool().createMob(templateID, null, x, y.get(), (short) fh.getSN(), false, MobAppearType.NORMAL, 0, (byte) 1, 0, null, false);
                        }
                    }
                }
                break;
            case 9:
                fieldIDx = Integer.parseInt(event.getArgs().get(0));
                if (fieldIDx < allFields.size()) {
                    Field field = allFields.get(fieldIDx);
                    if (field != null) {
                        field.onWeather(Integer.parseInt(event.getArgs().get(1)), event.getArgs().get(2), 8000);// default
                    }
                }
                break;
            case 10:
                fieldIDx = Integer.parseInt(event.getArgs().get(0));
                if (fieldIDx < allFields.size()) {
                    Field field = allFields.get(fieldIDx);
                    if (field != null) {
                        Npc npc = field.getLifePool().getNpc(event.getArgs().get(1));
                        if (npc != null) {
                            // CNpc::SetSpecialAction(v48, v57._m_pStr);
                        }
                    }
                }
                break;
        }
    }

    public void banishUser(boolean exceptAdmin) {
        lock.lock();
        try {
            List<User> allUsers = new ArrayList<>();
            getUserList(allUsers);

            List<User> affectedUsers = new ArrayList<>();
            for (User user : allUsers) {
                if (user == null || exceptAdmin && user.isGM() && user.getCharacter().getCharacterStat().getJob() / 100 == 9) {
                    continue;
                }
                affectedUsers.add(user);
            }
            if (affectedUsers.size() != 0) {
                Logger.logReport("Casting out [3]");
                castOut(affectedUsers, 0, "");
            }
            allUsers.clear();
            affectedUsers.clear();
        } finally {
            lock.unlock();
        }
    }

    public void castOut(List<User> users, int targetFieldID, String portal) {
        // not sure if it should be tail or head
        // (*this->m_apField_Affected.a)->m_dwFieldReturn;
        int fieldReturn = affectedFields.get(0).getReturnFieldID();

        for (User user : users) {
            if (user == null) {
                continue;
            }
            int fieldID = 0;
            boolean force = false;
            if (targetFieldID != 0) {
                if (isMCarnivalWaitingFieldSet()) {
                    /*int team = user.getTeamForMCarnival();
                    if (team != MCarnivalTeam.NONE) {
                        if (team == MCarnivalTeam.RED) {
                            portal = "red00";
                        } else if (team == MCarnivalTeam.BLUE) {
                            portal = "blue00";
                        }
                    }*/
                }
                fieldID = targetFieldID;
            } else {
                String fieldSetName = getFieldSetName();
                if (fieldSetName.equals("Guild1")) {
                    force = true;
                    fieldID = user.getField().getForcedReturnFieldID();
                } else if (fieldSetName.length() >= 7) {
                    String newName = fieldSetName.substring(0, 7);
                    force = true;
                    if (!newName.equals("Wedding")) {
                        fieldID = fieldReturn;
                    }
                }
            }
            Logger.logReport("Target Field ID = [%d]", fieldID);
            user.postTransferField(fieldID, portal, force);
        }
    }

    public void incExpAll(int exp, int quest) {
        List<User> users = new ArrayList<>();
        getUserList(users);
        for (User user : users) {
            int inc = exp;
            if (quest > 0 && UserQuestRecord.get(user, quest) != null) {
                inc *= 0.7;
            }
            int flag = user.incEXP(inc, false);
            if (flag != 0) {
                user.sendCharacterStat(Request.None, flag);
                user.sendIncExpMessage(true, inc, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
        }
        users.clear();
    }

    public void postTransferFieldAll(int fieldID, String portal) {
        boolean affected = false;
        for (Field field : affectedFields) {
            if (field.getFieldID() == fieldID) {
                affected = true;
            }
        }
        List<User> users = new ArrayList<>();
        getUserList(users);
        if (!affected) {
            if (name.equals("Guild1")) {
                // CGuildMan::RemoveGuildQuestWaitingList(TSingleton<CGuildMan>::ms_pInstance, abs(TSingleton<CGuildMan>::ms_pInstance->m_nGulidIDCanEnterQuest));
            }
        }
        Logger.logReport("Casting out [2]");
        castOut(users, fieldID, portal);
        users.clear();
    }

    public boolean resetTimeOut(int sec) {
        lock.lock();
        try {
            if (checkTimeOut == 0 || !fieldSetStart) {
                return false;
            }
            if (System.currentTimeMillis() - timeOut.getStart() > timeOut.getFlag() << 10) {
                timeOut.setFlag(timeOut.getFlag() & 0x7FFFFFFF);
            }
            if ((timeOut.getFlag() & 0x80000000) == 0) {
                return false;
            }
            timeOut.setStart(System.currentTimeMillis() - ((checkTimeOut - sec) << 10));
            broadcastClock();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean runScript(User user, String script) {
        if (user != null && script != null && !script.isEmpty()) {
            ScriptVM scriptVM = new ScriptVM();
            if (scriptVM.setScript(user, "field/set/" + script, user)) {
                scriptVM.run(user);
                return true;
            }
        }
        return false;
    }

    public void startEvent(User user) {
        long time = System.currentTimeMillis();
        eventStart = time;
        runScript(user, initScript);

        while (eventProgresses.size() > 0 && eventProgresses.get(eventProgresses.size() - 1).getActionOnField() == 6) {
            eventProgresses.remove(eventProgresses.size() - 1);
        }
        EventManager.resetEvent(name);
        for (int i = 0; i < eventProgresses.size(); i++) {
            EventProgress progress = eventProgresses.get(i);
            EventManager.lock.lock();
            try {
                EventInfo event = new EventInfo();
                event.setEventSN(EventManager.setTime(this, time + progress.getTime()));
                event.getArgs().clear();
                event.getArgs().add(i);// index
                event.getArgs().add(1);// type
                getEventInfos().put(event.getEventSN(), event);
            } finally {
                EventManager.lock.unlock();
            }
        }
    }

    public void startFieldSetManually() {
        if (checkTimeOut != 0) {
            long time = System.currentTimeMillis();
            timeOut.setFlag(checkTimeOut | 0x80000000);
            timeOut.setStart(time);
        }
        fieldSetStart = true;
    }

    public boolean isMCarnivalWaitingFieldSet() {
        switch (getFieldSetName()) {
            case "MCarnival100":
            case "MCarnival110":
            case "MCarnival120":
            case "MCarnival130":
            case "MCarnival140":
            case "MCarnival150":
                return true;
        }
        return false;
    }

    public boolean isRomioJulietFieldSet() {
        switch (getFieldSetName()) {
            case "Romio":
            case "Juliet":
                return true;
        }
        return false;
    }

    public boolean isWatermelonFieldSet() {
        switch (getFieldSetName()) {
            case "Watermelon1":
            case "Watermelon2":
            case "Watermelon3":
            case "Watermelon4":
            case "Watermelon5":
            case "Watermelon6":
            case "Watermelon7":
            case "Watermelon8":
            case "Watermelon9":
                return true;
        }
        return false;
    }

    public boolean isWeddingFieldSet() {
        switch (getFieldSetName()) {
            case "Wedding2":
            case "Wedding30":
            case "Wedding4":
                return true;
        }
        return false;
    }

    public boolean isFieldSetStart() {
        return fieldSetStart;
    }

    public int getFieldIndex(Field field) {
        for (int i = 0; i < allFields.size(); i++) {
            if (allFields.get(i).getFieldID() == field.getFieldID()) {
                return i;
            }
        }
        return -1;
    }

    public String getFieldSetName() {
        return name;
    }

    public int getUserCount() {
        int userCount = 0;
        for (int i = 0; i < affectedCount; i++) {
            userCount += affectedFields.get(i).getUsers().size();
        }
        return userCount;
    }

    public int getUserList(List<User> users) {
        users.clear();
        for (int i = 0; i < affectedCount; i++) {
            users.addAll(affectedFields.get(i).getUsers());
        }
        return users.size();
    }

    public long getEventStart() {
        return eventStart;
    }

    public void setEventStart(long eventStart) {
        this.eventStart = eventStart;
    }

    public String getVariable(String key) {
        lockVariable.lock();
        try {
            return variable.getOrDefault(key, "");
        } finally {
            lockVariable.unlock();
        }
    }

    public void setVariable(String varName, String value) {
        lockVariable.lock();
        try {
            variable.put(varName, value);
        } finally {
            lockVariable.unlock();
        }
        if (varName.equals("statueQuestion") && value != null && !value.isEmpty()) {
            // v6 = CFieldMan::GetField(TSingleton<CFieldMan>::ms_pInstance, 990000300u, 1, 0);
            //    ZFatalSection::Lock(&v6->m_lock);
            //    v6->m_reactorPool.m_bReactorHitEnable = 0;
            //    v6->m_reactorPool.m_nReactorTotalHit = 0;
            //    v5 = v6->m_lock._m_nRef-- == 1;
            //    if ( v5 )
            //      v6->m_lock._m_pTIB = 0;
        }
    }

    public void setTargetFieldID(int targetFieldID) {
        this.targetFieldID = targetFieldID;
    }

    // | Broadcast functions:
    public void broadcastClock() {
        if (checkTimeOut == 0 || !fieldSetStart) {
            return;
        }
        List<User> users = new ArrayList<>();
        getUserList(users);

        int duration = (int) (System.currentTimeMillis() - timeOut.getStart());
        if (duration > timeOut.getFlag() << 10) {
            timeOut.setFlag(timeOut.getFlag() & 0x7FFFFFFF);
        }
        int time = (checkTimeOut - (((timeOut.getFlag() & 0x80000000) != 0 ? duration : 0) >> 10));

        OutPacket packet = FieldPacket.setClockTimer(time);
        for (User user : users) {
            user.sendPacket(packet);
        }
    }

    public void destroyClock() {
        List<User> users = new ArrayList<>();
        getUserList(users);

        OutPacket packet = FieldPacket.destroyClock();

        for (User user : users) {
            user.sendPacket(packet);
        }
    }

    public void broadcastMsg(int type, String msg, int npcTemplateID) {
        if (type >= 0) {
            OutPacket packet = WvsContext.onBroadcastMsg((byte) type, msg, npcTemplateID);
            for (Field field : affectedFields) {
                field.broadcastPacket(packet, false);
            }
        }
    }

    public void onEnter(List<User> users) {
        lockVariable.lock();
        try {
            variable.clear();
        } finally {
            lockVariable.unlock();
        }
        for (int i = 0; i < affectedCount; i++) {
            affectedFields.get(i).reset(shuffleReactor);
        }
        if (checkTimeOut != 0) {
            long time = System.currentTimeMillis();
            timeOut.setFlag(checkTimeOut | 0x80000000);
            timeOut.setStart(time);
        }
        targetFieldID = 0;
    }

    @Override
    public void onTime(int eventSN) {
        EventManager.lock.lock();
        try {
            EventInfo event = getEventInfos().getOrDefault(eventSN, null);
            if (event == null) {
                return;
            }
            int index = event.getArgs().get(0);
            boolean reactor = event.getArgs().get(1) == 0;
            if (reactor) {
                ReactorActionInfo reactorActionInfo = reactorActionInfos.get(index);
                if (reactorActionInfo == null) {
                    return;
                }
                doReactorAction(reactorActionInfo);
            } else {
                EventProgress eventProgress = eventProgresses.get(index);
                if (eventProgress == null) {
                    return;
                }
                doEventAction(eventProgress);
            }
            getEventInfos().remove(eventSN);
        } finally {
            EventManager.lock.unlock();
        }
    }

    public void onUserEnterField(Field field, User user) {
        if (checkTimeOut == 0 || !fieldSetStart) {
            return;
        }
        int duration = (int) (System.currentTimeMillis() - timeOut.getStart());
        if (duration > timeOut.getFlag() << 10) {
            timeOut.setFlag(timeOut.getFlag() & 0x7FFFFFFF);
        }
        int time = (checkTimeOut - (((timeOut.getFlag() & 0x80000000) != 0 ? duration : 0) >> 10));
        user.sendPacket(FieldPacket.setClockTimer(time));
    }

    //~~~~~~~~~~~~~~~~ TODO functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Reactor methods:
    public void checkBossMap() {
        if (affectedFields.get(0).getReactorPool().getState("boss") != 0 && getUserCount() == 0) {
            lockVariable.lock();
            try {
                variable.clear();
            } finally {
                lockVariable.unlock();
            }
            for (int i = 0; i < affectedCount; i++) {
                affectedFields.get(i).reset(shuffleReactor);
            }
        }
    }

    public boolean checkReactorAction(Field field, String reactorName, long eventTime) {
        int idX = getFieldIndex(field);
        if (idX < 0) {
            return false;
        }
        int index = 0;
        for (ReactorActionInfo actionInfo : reactorActionInfos) {
            if (actionInfo.getFieldIDx() != idX) {
                continue;
            }
            boolean correctState = true, found = false;
            for (ReactorInfo reactorInfo : actionInfo.getReactorInfos()) {
                int state = field.getReactorPool().getState(reactorInfo.getName());
                if (state < 0 || state != reactorInfo.getEventState()) {
                    correctState = false;
                    break;
                }
                if (reactorInfo.getName().equals(reactorName)) {
                    found = true;
                }
            }
            if (found && correctState) {
                EventManager.lock.lock();
                try {
                    EventInfo eventInfo = new EventInfo();
                    eventInfo.setEventSN(EventManager.setTime(this, eventTime));
                    eventInfo.getArgs().clear();
                    eventInfo.getArgs().add(index);
                    eventInfo.getArgs().add(0);// reactor = 0 | event = 1
                    Logger.logReport("[Field Set] Adding reactor event");
                    getEventInfos().put(eventInfo.getEventSN(), eventInfo);
                } finally {
                    EventManager.lock.unlock();
                }
            }
            index++;
        }
        return true;
    }

    public void checkShouwaBossMap() {
        if (affectedFields.get(0).getReactorPool().getState("shouwaBoss") != 0 && getUserCount() == 0) {
            lockVariable.lock();
            try {
                variable.clear();
            } finally {
                lockVariable.unlock();
            }
            for (int i = 0; i < affectedCount; i++) {
                affectedFields.get(i).reset(shuffleReactor);
            }
        }
    }

    public void doReactorAction(ReactorActionInfo reactorActionInfo) {
        int type = reactorActionInfo.getType();
        List<String> args = reactorActionInfo.getArgs();
        Logger.logReport("Doing reactor action [%d]", type);
        if (type == ActionType.SetReactorState) {
            if (args == null || args.isEmpty() || args.size() < 3) {
                return;
            }
            int fieldIDx = Integer.parseInt(args.get(0));
            if (fieldIDx < 0 || fieldIDx >= this.count) {
                return;
            }
            Field field = allFields.get(fieldIDx);
            if (field == null || field.getReactorPool() == null) {
                return;
            }
            ReactorPool reactorPool = field.getReactorPool();

            String name = args.get(1);
            if (name == null || name.isEmpty()) {
                return;
            }
            int state = Integer.parseInt(args.get(2));
            if (state < 0) {
                state = reactorPool.getState(name) + 1;
            }
            reactorPool.setState(name, state);

            if (args.size() > 3) {
                int statueVal = Integer.parseInt(args.get(3));
                String variable = getVariable("statueQuestion");

                int statues = 1;
                while (true) {
                    if (variable.indexOf("" + statues) < 0) {
                        break;
                    }
                    statues++;
                }
                if (statueVal == statues - 1) {
                    setVariable("statueAnswer", "00000000000000000000");
                    reactorPool.setReactorHitEnable(true);
                }
            }
        } else if (type == ActionType.SetVariable) {
            if (args == null || args.isEmpty() || args.size() < 2) {
                return;
            }
            String varName = args.get(0);
            String varValue = args.get(1);
            if (varName == null || varName.isEmpty() || varValue == null || varValue.isEmpty()) {
                return;
            }
            if (getFieldSetName().equals("ZakumBoss") || varName.equals("boss") || getUserCount() != 0) {
                setVariable(varName, varValue);
            }
        } else if (type == ActionType.ChangeBGM) {
            Logger.logReport("Changing BGM");
            if (args == null || args.isEmpty() || args.size() < 2) {
                Logger.logReport("Return args length = [%d]", args.size());
                return;
            }
            int fieldIDx = Integer.parseInt(args.get(0));
            if (fieldIDx < 0 || fieldIDx >= this.count) {
                return;
            }
            Field field = allFields.get(fieldIDx);
            if (field == null) {
                return;
            }
            String BGM = args.get(1);
            if (BGM != null && !BGM.isEmpty()) {
                field.effectChangeBGM(BGM);
            }
        } else if (type == ActionType.TrembleEffect) {
            if (args == null || args.isEmpty() || args.size() < 3) {
                return;
            }
            int heavyNShortTremble = Integer.parseInt(args.get(0)) != 0 ? 1 : 0;
            int delay = Integer.parseInt(args.get(1));
            for (int i = 2; ; i++) {
                if (args.size() < i + 1) {
                    break;
                }
                Field field = allFields.get(i);
                if (field != null) {
                    field.effectTremble(heavyNShortTremble, delay);
                }
            }
        } else if (type == ActionType.RemoveAllMob) {
            if (args == null || args.isEmpty() || args.size() < 1) {
                return;
            }
            int fieldIDx = Integer.parseInt(args.get(0));
            if (fieldIDx < 0 || fieldIDx >= this.count) {
                return;
            }
            Field field = allFields.get(fieldIDx);
            if (field == null) {
                return;
            }
            field.getLifePool().removeAllMob(false);
            field.getLifePool().setMobGen(false, 0);
        } else if (type == ActionType.SetObjectState) {
            if (args == null || args.isEmpty() || args.size() < 3) {
                return;
            }
            int fieldIDx = Integer.parseInt(args.get(0));
            if (fieldIDx < 0 || fieldIDx >= this.count) {
                return;
            }
            Field field = allFields.get(fieldIDx);
            if (field == null) {
                return;
            }
            String name = args.get(1);
            int state = Integer.parseInt(args.get(2));
            if (name == null || name.isEmpty()) {
                return;
            }
            field.setObjectState(name, state);
        }
    }

    public int getReactorState(int fieldIDx, String name) {
        Field field = allFields.get(fieldIDx);
        if (field != null && field.getReactorPool() != null) {
            return field.getReactorPool().getState(name);
        }
        return -1;
    }

    public void setReactorState(int fieldIDx, String name, int state, int order) {
        EventManager.lock.lock();
        try {
            ReactorActionInfo reactorActionInfo = new ReactorActionInfo();
            reactorActionInfo.setType(ActionType.SetReactorState);
            reactorActionInfo.getArgs().add("" + fieldIDx);
            reactorActionInfo.getArgs().add(name);
            reactorActionInfo.getArgs().add("" + state);
            reactorActionInfo.getArgs().add("" + order);
            reactorActionInfos.add(reactorActionInfo);
        } finally {
            EventManager.lock.unlock();
        }
        EventInfo eventInfo = new EventInfo();
        eventInfo.setEventSN(EventManager.setTime(this, System.currentTimeMillis()));
        eventInfo.getArgs().clear();
        eventInfo.getArgs().add(reactorActionInfos.size() - 1);
        eventInfo.getArgs().add(0);
        getEventInfos().put(eventInfo.getEventSN(), eventInfo);
    }

    // Party methods:
    public void checkParty(List<User> users) {
        /*if (!name.equals("Party1") && !name.equals("Party3") && users.size() <= 1) {
            castOut(users, 0, "");
            return;
        }

        int characterID = 0;
        int members = 0;
        for (User user : users) {
            if (user != null) {
                if (members >= 6) {
                    castOut(users, 0, "");
                    return;
                }
                characterID = user.getCharacterID();
                members++;
            }
        }

        List<Integer> characterIDs = new ArrayList<>();
        int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
        if (partyID == 0 || !PartyMan.getInstance().getSnapshot(partyID, characterIDs)) {
            castOut(users, 0, "");
            return;
        }*/
        // more cast out stuff i don't understand their usage for now I leave it like this
    }

    // Guild methods:
    public void clearGuildQuest() {
    }

    public void sendIncGPMessage(int incGP) {
    }
    // ~~~~~~~~~~~~~~ Loading Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void updateFieldSet(long time) {
        List<User> users = new ArrayList<>();
        int userCount = getUserList(users);
        if (name.equals("ZakumBoss") || name.equals("Populatus")) {
            checkBossMap();
        }
        if (name.equals("Guild1") && !castOut) { // && TSingleton<CGuildMan>::ms_pInstance->m_nGulidIDCanEnterQuest < 0
            // TODO
        }
        if ((name.equals("Wedding1") || name.equals("Wedding2")) /*&& && TSingleton<CWeddingMan>::ms_pInstance->m_nCurrentWeddingState == 1*/) {
            // TODO
        }
        if (name.equals("Wedding4") /*&& TSingleton<CWeddingMan>::ms_pInstance->m_nCurrentWeddingState == 4*/) {
            // TODO
        }
        if (name.equals("shouwaBoss")) {
            checkShouwaBossMap();
        }
        if (!endFieldSetAct && userCount == 0) {
            return;
        }
        if (userCount != 0) {
            long newTime = System.currentTimeMillis();
            occupied.setFlag(0x80000014);
            occupied.setStart(newTime);
        }
        if ((name.length() >= 5 && name.substring(0, 5).equals("Party")) || name.equals("MoonRabbit")) {
            checkParty(users);
        }
        if (name.length() >= 8 && name.substring(0, 8).equals("DavyJohn")) {
            // TODO
        }
        if (isMCarnivalWaitingFieldSet() || isRomioJulietFieldSet()) {
            // TODO
        }
        boolean wa = timeOut.isWaiting(System.currentTimeMillis());
        if (checkTimeOut == 0 || wa) {
            if (tryToRunInitScript) {
                if (userCount > 0) {
                    // should iterate ?
                    tryToRunInitScript = runScript(users.get(0), initScript) == false;
                } else {
                    tryToRunInitScript = false;
                }
            }
        } else {
            // end
            if (name.equals("Wedding1") || name.equals("Wedding2")) {
                // TODO
            } else if (name.equals("Wedding4")) {
                // TODO
            } else if (name.equals("Wedding30")) {
                // TODO
            } else if (name.equals("Guild1")) {
                // TODO
            }
            if (endFieldSetAct) {
                destroyClock();
                fieldSetStart = false;
            } else {
                Logger.logReport("Casting out [1]");
                castOut(users, targetFieldID, "");
            }
            if (tryToRunInitScript) {
                if (userCount > 0) {
                    // should iterate ?
                    tryToRunInitScript = runScript(users.get(0), initScript) == false;
                } else {
                    tryToRunInitScript = false;
                }
            }
        }
    }

    public boolean init(WzProperty set) {
        this.affectedCount = 0;
        this.count = 0;
        while (true) {
            int fieldID = WzUtil.getInt32(set.getNode("" + this.count), Field.Invalid);
            if (fieldID == Field.Invalid) {
                break;
            }

            Field field = FieldMan.getInstance(channelID).getField(fieldID);
            if (field == null) {
                Logger.logReport("Cannot read FieldSet data [%s] | Field ID = [%d]", getFieldSetName(), fieldID);
                return false;
            }
            WzProperty unaffectedProp = set.getNode("unaffected");
            field.setParentFieldSet(this);
            this.allFields.add(field);
            boolean unaffected = false;
            if (unaffectedProp != null) {
                unaffected = WzUtil.getInt32(unaffectedProp.getNode("" + this.count), 0) != 0;
            }
            if (!unaffected) {
                this.affectedCount++;
                this.affectedFields.add(field);
            }
            if (unaffectedProp != null) {
                unaffectedProp.release();
            }
            this.count++;
        }
        if (count == 0) {
            if (set != null) {
                set.release();
            }
            Logger.logReport("Cannot read FieldSet data [%s] | Count = [%d]", getFieldSetName(), count);
            return false;
        }
        this.checkTimeOut = WzUtil.getInt32(set.getNode("timeOut"), 0);
        this.shuffleReactor = WzUtil.getInt32(set.getNode("shuffle"), 0) != 0;
        this.initScript = WzUtil.getString(set.getNode("script"), null);
        this.fieldSetStart = WzUtil.getInt32(set.getNode("manualstart"), 0) == 0;
        this.endFieldSetAct = WzUtil.getInt32(set.getNode("endfieldset"), 0) != 0;
        this.reactorRegen = WzUtil.getInt32(set.getNode("reactorRegen"), 0) != 0;

        WzProperty action = set.getNode("action");
        if (action != null) {
            loadReactorAction(action);
        }

        WzProperty event = set.getNode("event");
        if (event != null) {
            loadEventProgress(event);
        }
        return true;
    }

    public void loadReactorAction(WzProperty actionData) {
        for (int i = 0; i < allFields.size(); i++) {
            WzProperty action = actionData.getNode("" + i);
            if (action == null) {
                continue;
            }
            for (int j = 0; ; j++) {
                WzProperty subAct = action.getNode("" + j);
                if (subAct == null) {
                    break;
                }
                loadEachAction(subAct, i);
                if (subAct != null) subAct.release();
            }
            if (action != null) action.release();
        }
        if (actionData != null) actionData.release();
    }

    public void loadEachAction(WzProperty subData, int fieldIDx) {
        ReactorActionInfo actionInfo = new ReactorActionInfo();
        actionInfo.setFieldIDx(fieldIDx);

        Field field = allFields.get(fieldIDx);
        if (field != null) {
            for (WzProperty sub : subData.getChildNodes()) {
                if (sub.getNodeName().equals("info")) {
                    int type = WzUtil.getInt32(sub.getNode("type"), -1);
                    if (type == -1) {
                        continue;
                    }
                    actionInfo.setType(type);
                    for (int i = 0; ; i++) {
                        WzProperty act = sub.getNode("" + i);
                        if (act == null) {
                            break;
                        }
                        String arg = WzUtil.getString(act, null);
                        if (arg == null) {
                            int intArg = WzUtil.getInt32(act, -1);
                            if (intArg != -1) {
                                arg = "" + intArg;
                            }
                        }
                        actionInfo.getArgs().add(arg);
                    }
                } else {
                    ReactorInfo ri = new ReactorInfo();
                    ri.setName(sub.getNodeName());
                    ri.setEventState(WzUtil.getInt32(sub, -1));
                    actionInfo.getReactorInfos().add(ri);
                }
            }
            reactorActionInfos.add(actionInfo);
        }
    }

    public void loadEventProgress(WzProperty eventData) {
        for (int i = 0; ; i++) {
            WzProperty event = eventData.getNode("" + i);
            if (event == null) {
                break;
            }
            EventProgress progress = new EventProgress();
            progress.setTime(WzUtil.getInt32(event.getNode("timeAfter"), 0) * 1000);
            progress.setActionOnField(WzUtil.getInt32(event.getNode("action"), 0));
            for (int j = 0; ; j++) {
                WzProperty action = event.getNode("" + j);
                if (action == null) {
                    break;
                }
                String arg = WzUtil.getString(action, null);
                if (arg == null) {
                    int intArg = WzUtil.getInt32(action, -1);
                    if (intArg != -1) {
                        arg = "" + intArg;
                    }
                }
                progress.getArgs().add(arg);
            }
        }
    }
}
