package game.field.reactor;

import game.GameApp;
import game.field.*;
import game.field.drop.Drop;
import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import game.field.event.EventInfo;
import game.field.event.EventManager;
import game.field.event.EventManager2;
import game.field.life.mob.MobLeaveField;
import game.field.life.mob.MobPool;
import game.field.life.npc.Npc;
import game.field.reactor.action.ActionType;
import game.field.reactor.action.ReactorActionInfo;
import game.party.PartyMan;
import game.script.ScriptVM;
import game.user.User;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;
import util.Rect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 3/19/2020.
 */
public class Reactor extends EventManager2 {
    private final ReactorTemplate template;
    private int templateID;
    private ReactorGen reactorGen;
    private Point pos;
    private int state;
    private int oldState;
    private int timeOut;
    private long stateStart;
    private long stateEnd;
    private int hitCount;
    private int ownerID;
    private int ownPartyID;
    private int ownType;
    private boolean flip;
    private long lastHit;
    private int lastHitCharacterID;
    private Npc npc;

    public Reactor(ReactorTemplate template, Field field) {
        super();
        setField(field);
        this.template = template;
        this.ownerID = 0;
        this.ownPartyID = 0;
        this.lastHit = 0;
        this.npc = null;
    }

    public void doAction(ReactorTemplate.ActionInfo actionInfo, int delay, int dropIDx) {
        int x1 = this.pos.x;
        int y1 = this.pos.y;
        int type = actionInfo.type;

        if(type == ActionType.TransferField) {
            List<Integer> numArgs = actionInfo.args;
            if (numArgs == null || numArgs.isEmpty()) {
                return;
            }
            List<String> strArgs = actionInfo.strArgs;
            if (strArgs == null || strArgs.isEmpty()) {
                return;
            }
            if (strArgs.size() + 1 != numArgs.size()) {
                return;
            }
            int rand = (int) (Rand32.genRandom() % strArgs.size());
            int fieldID = numArgs.get(rand + 1);
            String portal = strArgs.get(rand);
            boolean lastHitOnly = numArgs.get(0) != 0;

            if (fieldID == Field.Invalid) {
                return;
            }
            if (FieldMan.getInstance(getField().getChannelID()).getField(fieldID) == null) {
                return;
            }
            // need add check if field exists ?
            if (lastHitOnly) {
                User user = GameApp.getInstance().getChannel(getField().getChannelID()).findUser(lastHitCharacterID);
                if (user != null) {
                    // lock ?
                    if (actionInfo.message != null && !actionInfo.message.isEmpty()) user.sendSystemMessage(actionInfo.message);
                    user.postTransferField(fieldID, portal, true);
                }
            } else {
                for (User user : getField().getUsers()) {
                    if (actionInfo.message != null && !actionInfo.message.isEmpty()) user.sendSystemMessage(actionInfo.message);
                    user.postTransferField(fieldID, portal, true);
                }
            }
        } else if (type == ActionType.CreateMob) {
            List<Integer> numArgs = actionInfo.args;
            if (numArgs == null || numArgs.size() < 3) {
                return;
            }
            int templateID = numArgs.get(0);
            int summonType = numArgs.get(1);
            int mobCount = numArgs.get(2);
            int mobType = 0;
            if (numArgs.size() >= 4) {
                mobType = numArgs.get(3);
            }
            if (numArgs.size() >= 6) {
                Logger.logReport("%d", templateID);
                x1 = numArgs.get(4);
                y1 = numArgs.get(5);
            }
            StaticFoothold foothold = getField().getSpace2D().getFootholdUnderneath(x1, y1);
            if (foothold == null) {
                foothold = getField().getSpace2D().getFootholdClosest(getField(), x1, y1, new Pointer<>(0), new Pointer<>(0), 0);
            }
            if (foothold == null) {
                return;
            }
            if (actionInfo.message != null && !actionInfo.message.isEmpty()) {
                for (User user : getField().getUsers()) {
                    user.sendSystemMessage(actionInfo.message);
                }
            }
            for (int i = 0; i < mobCount; i++) {
                getField().getLifePool().createMob(templateID, null, x1, y1, (short)foothold.getSN(), false, summonType, 0, (byte) 1, mobType, null, false);
            }
        } else if (type == ActionType.CreateReward) {
            List<Reward> rewards = Reward.create(template.getRewardInfos(), getField().getFieldID() / 10000000 == 19, 1, 1, false);
            int rewardCount = rewards.size();
            if (rewardCount <= 0) {
                rewards.clear();
                return;
            }
            int x2 = 20 * dropIDx;
            int x3 = 20 * (rewardCount / 2);
            int x4 = 0;
            int rewardDelay = 0;
            for (Reward reward : rewards) {
                int newX2 = x1 + x2 + x4 - x3;
                getField().getDropPool().create(reward, ownerID, templateID, x1, y1, newX2, y1, rewardDelay, false, 0);
                x4 += 20;
                rewardDelay += 200;
            }
            rewards.clear();
        } else if (type == ActionType.CreateNpc) {
            List<Integer> numArgs = actionInfo.args;
            if (numArgs == null || numArgs.size() < 3) {
                return;
            }
            int npcTemplateID = numArgs.get(0);
            int x = numArgs.get(1);
            int y = numArgs.get(2);
            this.npc = getField().getLifePool().createNpc(null, templateID, x, y);

            if (actionInfo.message != null && !actionInfo.message.isEmpty()) {
                for (User user : getField().getUsers()) {
                    user.sendSystemMessage(actionInfo.message);
                }
            }
        } else if (type == ActionType.TrembleEffect) {
            List<Integer> numArgs = actionInfo.args;
            if (numArgs == null || numArgs.size() < 2) {
                return;
            }
            getField().effectTremble(numArgs.get(0) != 0 ? 1 : 0, numArgs.get(1));
        } else if (type == ActionType.OpenScript) {
            List<String> strArgs = actionInfo.strArgs;
            if (strArgs == null || strArgs.isEmpty()) {
                return;
            }
            String script = strArgs.get(0);
            if (script == null || script.isEmpty()) {
                return;
            }
            for (User user : getField().getUsers()) {
                ScriptVM scriptVM = new ScriptVM();
                if (scriptVM.setScript(user, "field/set/" + script, this)) {
                    scriptVM.run(user);
                }
                if (actionInfo.message != null && !actionInfo.message.isEmpty()) {
                    user.sendSystemMessage(actionInfo.message);
                }
            }
        }
    }

    public void doActionByUpdateEvent() {
        int state = this.state;

        ReactorTemplate.StateInfo stateInfo = template.getStateInfo(state);
        if (stateInfo == null) {
            return;
        }

        int eventIDx = 0;
        for (ReactorTemplate.ReactorEventInfo eventInfo : stateInfo.reactorEventInfos) {
            if (eventInfo.type != 100) {
                continue;
            }
            if (eventInfo.args.size() < 2) {
                continue;
            }
            List<Integer> items = new ArrayList<>();
            List<Integer> itemCounts = new ArrayList<>();
            for (int i = 0; i < eventInfo.args.size() >> 1; i++) {
                items.add(i, eventInfo.args.get(2 * i));
                itemCounts.add(i, eventInfo.args.get(2 * i + 1));
            }
            Rect dropRect = new Rect(eventInfo.checkArea.left, eventInfo.checkArea.top, eventInfo.checkArea.right, eventInfo.checkArea.bottom);
            dropRect.offsetRect(this.pos.getX(), this.pos.getY());
            List<Drop> drops = new ArrayList<>();
            getField().getDropPool().findDropInRect(dropRect, drops, 3000);
            if (drops == null || drops.isEmpty() || items.isEmpty() || itemCounts.isEmpty()) {
                continue;
            }
            Drop toRemove = null;
            for (Drop drop : drops) {
                if (!drop.isMoney() && drop.getItem() != null) {
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i) == drop.getItem().getItemID() && itemCounts.get(i) <= drop.getItem().getItemNumber()) {
                            toRemove = drop;
                            break;
                        }
                    }
                }
            }
            if (toRemove != null) {
                Logger.logReport("Removed [%d] Setting [%d] to state [%d]", toRemove.getItem().getItemID(), getTemplateID(), eventIDx);
                getField().getDropPool().remove(toRemove.getDropID(), 0);
                this.ownerID = 0;
                this.ownPartyID = 0;
                this.ownType = 2;
                setState(eventIDx, 0);
            }
            eventIDx++;
        }
    }

    public void findAvailableAction() {
        long cur = System.currentTimeMillis();
        int dropIDx = 0;
        List<ReactorTemplate.ActionInfo> actionInfos = this.template.getActionInfos();

        for (int i = 0; i < actionInfos.size(); i++) {
            ReactorTemplate.ActionInfo actionInfo = actionInfos.get(i);

            int state = actionInfo.state;
            if (state == -1 || state != this.state) {
                continue;
            }
            if (actionInfo.type == 2) {
                int delay = (int) (this.stateEnd - cur);
                doAction(actionInfo, delay, dropIDx);
                dropIDx++;
            } else if (actionInfo.type == 11) {
                // CField_MonsterCarnival::OnGuardianDestroyed(CField_MonsterCarnival *this, unsigned int dwTemplateID, ZXString<char> sName)
            } else {
                EventManager2.lock.lock();
                try {
                    int eventSN = EventManager2.setTime(this, this.stateEnd);
                    EventInfo eventInfo = new EventInfo();
                    eventInfo.setEventSN(eventSN);
                    eventInfo.getArgs().clear();
                    Logger.logReport("Adding Action Info ");
                    eventInfo.getArgs().add(0, this.templateID);
                    eventInfo.getArgs().add(1, i);
                    eventInfo.getArgs().add(2, this.pos.x);
                    eventInfo.getArgs().add(3, this.pos.y);
                    //             <int name="0" value="8800009"/>
                    //            <int name="1" value="-2"/>
                    //            <int name="2" value="1"/>
                    //            <int name="3" value="1"/>
                    //            <int name="4" value="-10"/>
                    //            <int name="5" value="-204"/>
                    this.getEventInfos().put(eventSN, eventInfo);
                } finally {
                    EventManager2.lock.unlock();
                }
            }
        }
        getField().checkReactorAction(this.reactorGen.getName(), this.stateEnd);
    }

    public ReactorTemplate.StateInfo getCurStateInfo() {
        return template.getStateInfo(this.state);
    }

    public int getHitDelay(int eventIDx) {
        ReactorTemplate.StateInfo stateInfo = getCurStateInfo();
        if (stateInfo == null) {
            return 0;
        }

        int hitDelay = 0;
        if (eventIDx >= 0 && eventIDx < stateInfo.reactorEventInfos.size()
                && (hitDelay = stateInfo.reactorEventInfos.get(eventIDx).hitDelay) < 0
                && (hitDelay = stateInfo.hitDelay) < 0
                && (hitDelay = template.getHitDelay()) < 0) {
            return 0;
        }
        return hitDelay;
    }

    public void init(ReactorGen reactorGen) {
        this.reactorGen = reactorGen;
        if (reactorGen != null) {
            if (reactorGen.getRegenInterval() != 0) {
                reactorGen.setReactorCount(reactorGen.getReactorCount() + 1);
            }
        }
    }

    public void removeNpc() {
        if (this.npc != null) {
            getField().getLifePool().removeNpc(this.npc);
            this.npc = null;
        }
    }

    public void setRemoved() {
        GameObjectBase.unregisterGameObject(this);
        if (reactorGen != null) {
            if (reactorGen.getRegenInterval() == 0) {
                return;
            }
            reactorGen.setReactorCount(reactorGen.getReactorCount() - 1);
            if (reactorGen.getReactorCount() == 0) {
                int regenInterval = reactorGen.getRegenInterval();

                int rand = (regenInterval / 10) + Math.abs(Rand32.genRandom().intValue()) % (6 * regenInterval / 10);

                reactorGen.setRegenAfter(rand  + System.currentTimeMillis());
            }
        }
    }

    public void setState(int eventIDx, int actionDelay) {
        long cur = System.currentTimeMillis();
        int hitDelay = getHitDelay(eventIDx);
        this.stateStart = cur;
        this.stateEnd = actionDelay + cur + hitDelay + template.getMoveDelay();

        ReactorTemplate.StateInfo stateInfo = template.getStateInfo(this.state);
        if (stateInfo != null && eventIDx >= 0 && stateInfo.reactorEventInfos != null && eventIDx < stateInfo.reactorEventInfos.size()) {
            this.state = stateInfo.reactorEventInfos.get(eventIDx).stateToBe;
        } else {
            this.state = (this.state + 1) % this.template.getStateCount();
        }
        Logger.logReport("Finding available action | State [%d]", this.state);
        findAvailableAction();

        stateInfo = template.getStateInfo(this.state);// new state
        this.timeOut = stateInfo.timeOut;

        if ((stateInfo.reactorEventInfos == null || stateInfo.reactorEventInfos.isEmpty()) &&
            (getField().getParentFieldSet() == null || template.isRemoveInFieldSet())) {
            getField().getReactorPool().removeReactor(this);
        } else {
            getField().splitSendPacket(getSplit(), makeStateChangePacket(actionDelay, eventIDx), null);
        }
    }

    public void updateOwnerInfo() {
        long cur = System.currentTimeMillis();
        if ((cur - lastHit) > 15000) {
            if (getTemplateID() == 2709000 && getField().getUsers().size() > 0) Logger.logReport("[%d | %s] Reactor State [%d]", getTemplateID(), reactorGen.getName(), getState());
            if (lastHit != 0) {
                this.ownerID = 0;
                this.ownPartyID = 0;
            }
            lastHit = System.currentTimeMillis();
        }
        int timeOut = this.timeOut;
        if (timeOut > 0 && (cur - stateStart) > timeOut) {
            this.timeOut = 0;
            ReactorTemplate.StateInfo stateInfo = template.getStateInfo(this.state);
            if (stateInfo == null) {
                return;
            }
            int index = 0;
            for (ReactorTemplate.ReactorEventInfo eventInfo : stateInfo.reactorEventInfos) {
                if (eventInfo.type == 101) {
                    setState(index, 0);
                    break;
                }
                index++;
            }
        }
    }

    public void onHit(User user, InPacket packet) {
        if ((this.stateEnd - System.currentTimeMillis()) > 0) {
            return;
        }

        // Find Hit Reactor = 0
        // Find Skill Reactor = 1
        boolean skill = packet.decodeInt() != 0;

        int option = packet.decodeInt();
        boolean left = (option & 1) != 0;

        int actionDelay = packet.decodeShort();
        int skillID = packet.decodeInt();// skill id !=0 only when bool skill = 1

        ReactorTemplate.StateInfo stateInfo = getCurStateInfo();
        if (stateInfo == null || stateInfo.reactorEventInfos == null || stateInfo.reactorEventInfos.isEmpty()) {
            return;
        }

        Pointer<Integer> y = new Pointer<>(-1);
        int eventIDx = -1;
        for (int i = 0; i < stateInfo.reactorEventInfos.size(); i++) {
            ReactorTemplate.ReactorEventInfo eventInfo = stateInfo.reactorEventInfos.get(i);

            int hitPriorityLevel = ReactorTemplate.getHitTypePriorityLevel(option, eventInfo.type);
            if (hitPriorityLevel != -1 && hitPriorityLevel < y.get() || y.get() == -1) {
                y.set(hitPriorityLevel);
                eventIDx = i;
            }
            if (hitPriorityLevel == 0) {
                break;
            }
        }
        if (y.get() == -1) {
            return;
        }
        if (template.isMove()) {
            StaticFoothold staticFH = getField().getSpace2D().getFootholdUnderneath(pos.x, pos.y, y);

            int dirX = left ? -1 : 1;
            int tempX = pos.x + dirX * template.getMoveOnce();

            int newX = 0;
            while (staticFH != null) {
                int loopX = left ? staticFH.getX1() :  staticFH.getX2();
                double vy = staticFH.getUvy();
                if (vy != 0.0 || staticFH.getY1() != y.get()) {
                    break;
                }
                if (dirX * (loopX - tempX) >= 0) {
                    newX = tempX;
                    break;
                }
                tempX = loopX;

                // Not sure about this :/
                if (left) {
                    staticFH = getField().getSpace2D().getFoothold(staticFH.getPrevSN());
                } else {
                    staticFH = getField().getSpace2D().getFoothold(staticFH.getNextSN());
                }
            }
            if (getField().getSpace2D().getFootholdUnderneath(newX, y.get(), y) != null) {
                pos.x = newX;
                pos.y = y.get();
            }
        }
        if (this.hitCount == 0 && this.ownerID == 0) {
            int characterID = user.getCharacterID();
            this.ownerID = characterID;

            int partyID = PartyMan.getInstance().charIdToPartyID(characterID);
            this.ownPartyID = partyID != 0 ? partyID : 0;
            this.ownType = partyID != 0 ? 1 : 0;
        }
        if (template.getReqHitCount() > 0) {
            this.hitCount++;
        }
        if (this.hitCount >= template.getReqHitCount()) {
            setState(eventIDx, actionDelay);
            this.hitCount = 0;
            this.lastHitCharacterID = user.getCharacterID();
        }
        this.lastHit = System.currentTimeMillis();

        if (getField().getFieldID() == 990000300) {
            getField().getReactorPool().setReactorTotalHit(getField().getReactorPool().getReactorTotalHit() + 1);
            if (!getField().getReactorPool().isReactorHitEnable() || getField().getReactorPool().getReactorTotalHit() > 10) {
                return;
            }
            String var = getField().getParentFieldSet().getVariable("statueAnswer");
            var = var.substring(0, reactorGen.getName().length() - 1);
            var += (char)(0x30 + getField().getReactorPool().getReactorTotalHit());
            getField().getParentFieldSet().setVariable("statueAnswer", var);
        }
    }

    @Override
    public void onTime(int eventSN) {
        EventManager2.lock.lock();
        try {
            EventInfo eventInfo = getEventInfos().getOrDefault(eventSN, null);
            if (eventInfo == null) {
                return;
            }
            ReactorTemplate.ActionInfo actionInfo = ReactorTemplate.getActionInfo(eventInfo.getArgs().get(0), eventInfo.getArgs().get(1));
            //                    eventInfo.getArgs().add(2, this.pos.x);
            //                    eventInfo.getArgs().add(3, this.pos.y);
            if (actionInfo != null) {
                doAction(actionInfo, 0, 0);
            }
            getEventInfos().remove(eventSN);

        } finally {
            EventManager2.lock.unlock();
        }
    }

    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Reactor;
    }

    @Override
    public int getTemplateID() { return templateID; }

    @Override
    public OutPacket makeEnterFieldPacket() {
        OutPacket packet = new OutPacket(LoopbackPacket.ReactorEnterField);
        packet.encodeInt(getGameObjectID());
        packet.encodeInt(this.templateID);
        packet.encodeByte(this.state);
        packet.encodeShort(this.pos.x);
        packet.encodeShort(this.pos.y);
        packet.encodeBool(this.flip);
        packet.encodeString(this.reactorGen.getName());
        return packet;
    }

    @Override
    public OutPacket makeLeaveFieldPacket() {
        OutPacket packet = new OutPacket(LoopbackPacket.ReactorLeaveField);
        packet.encodeInt(getGameObjectID());
        packet.encodeByte(this.state);
        packet.encodeShort(this.pos.x);
        packet.encodeShort(this.pos.y);
        return packet;
    }

    public OutPacket makeStateChangePacket(int actionDelay, int properEventIDx) {
        OutPacket packet = new OutPacket(LoopbackPacket.ReactorChangeState);
        packet.encodeInt(getGameObjectID());
        packet.encodeByte(this.state);
        packet.encodeShort(this.pos.x);
        packet.encodeShort(this.pos.y);
        packet.encodeShort(actionDelay);
        packet.encodeByte(properEventIDx);
        packet.encodeByte((int) ((this.stateEnd - System.currentTimeMillis() + 99) / 100));
        return packet;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public Point getPos() {
        return pos;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOldState() {
        return oldState;
    }

    public void setOldState(int oldState) {
        this.oldState = oldState;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public long getStateStart() {
        return stateStart;
    }

    public void setStateStart(long stateStart) {
        this.stateStart = stateStart;
    }

    public long getStateEnd() {
        return stateEnd;
    }

    public void setStateEnd(long stateEnd) {
        this.stateEnd = stateEnd;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getOwnPartyID() {
        return ownPartyID;
    }

    public void setOwnPartyID(int ownPartyID) {
        this.ownPartyID = ownPartyID;
    }

    public int getOwnType() {
        return ownType;
    }

    public void setOwnType(int ownType) {
        this.ownType = ownType;
    }

    public boolean isFlip() {
        return flip;
    }

    public long getLastHit() {
        return lastHit;
    }

    public void setLastHit(long lastHit) {
        this.lastHit = lastHit;
    }

    public int getLastHitCharacterID() {
        return lastHitCharacterID;
    }

    public void setLastHitCharacterID(int lastHitCharacterID) {
        this.lastHitCharacterID = lastHitCharacterID;
    }

    public Npc getNpc() {
        return npc;
    }

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public ReactorGen getReactorGen() {
        return reactorGen;
    }

    public ReactorTemplate getTemplate() {
        return template;
    }
}
