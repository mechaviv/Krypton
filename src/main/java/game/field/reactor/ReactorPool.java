package game.field.reactor;

import game.field.Field;
import game.field.FieldSplit;
import game.user.User;
import network.packet.ClientPacket;
import network.packet.InPacket;
import util.Logger;
import util.Rand32;
import util.Utilities;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by MechAviv on 3/19/2020.
 */
public class ReactorPool {
    private final Field field;
    private boolean shuffleOnReset;
    private String shuffleName;
    private Map<Integer, Reactor> reactors;
    private Map<String, Integer> reactorNames;
    private List<ReactorGen> reactorGens;
    private long lastCreateReactorTime;
    private boolean reactorHitEnable;
    private int reactorTotalHit;

    public ReactorPool(Field field) {
        this.field = field;
        this.reactors = new ConcurrentHashMap<>();
        this.reactorNames = new ConcurrentHashMap<>();
        this.reactorGens = new ArrayList<>();
    }

    public void createReactor(ReactorGen prg) {
        ReactorTemplate template = ReactorTemplate.getReactorTemplate(prg.getTemplateID());
        if (template == null) {
            return;
        }
        Reactor reactor = new Reactor(template, getField());
        if (template.isMove() && getField().getSpace2D().getFootholdUnderneath(prg.getX(), prg.getY().get() - 10, prg.getY()) == null) {
            return;
        }
        reactor.setTemplateID(prg.getTemplateID());
        reactor.setPos(new Point(prg.getX(), prg.getY().get()));
        reactor.setFlip(prg.isFlip());
        reactor.setHitCount(0);
        reactor.setState(0);
        reactor.setOldState(0);
        reactor.setStateEnd(System.currentTimeMillis());
        reactor.init(prg);
        if (getField().splitRegisterFieldObj(reactor.getPos().x, reactor.getPos().y, 9, reactor)) {
            reactors.put(reactor.getGameObjectID(), reactor);
            if (prg.getName() != null && !prg.getName().isEmpty()) {
                reactorNames.put(prg.getName(), reactor.getGameObjectID());
            }
        }
    }

    public int getState(String name) {
        int objID = reactorNames.getOrDefault(name, -1);
        if (objID == -1) {
            return -1;
        }
        Reactor reactor = reactors.getOrDefault(objID, null);
        if (reactor == null) {
            return -1;
        }
        return reactor.getState();
    }

    public void init(Field field, WzProperty mapData, WzProperty info) {
        if (mapData == null) {
            return;
        }
        if (info != null) {
            this.shuffleOnReset = WzUtil.getBoolean(info.getNode("reactorShuffle"), false);
            this.shuffleName = WzUtil.getString(info.getNode("reactorShuffleName"), null);
        }

        long cur = System.currentTimeMillis();
        WzProperty reactorData = mapData.getNode("reactor");
        if (reactorData != null) {
            for (WzProperty reactor : reactorData.getChildNodes()) {
                String id = WzUtil.getString(reactor.getNode("id"), "");
                if (id.isEmpty()) continue;

                ReactorGen reactorGen = new ReactorGen();
                reactorGen.setTemplateID(Integer.parseInt(id));
                reactorGen.setName(WzUtil.getString(reactor.getNode("name"), ""));
                ReactorTemplate template = ReactorTemplate.getReactorTemplate(reactorGen.getTemplateID());
                if (template == null) {
                    Logger.logError("Invalid Reactor template ID(%d) on map(%d)", reactorGen.getTemplateID(), getField().getFieldID());
                    continue;
                }
                reactorGen.setX(WzUtil.getShort(reactor.getNode("x"), 0));
                reactorGen.setY(WzUtil.getShort(reactor.getNode("y"), 0));

                int interval = 1000 * WzUtil.getInt32(reactor.getNode("reactorTime"), 0);
                reactorGen.setRegenInterval(interval);

                long regenAfter = 0;
                if (interval > 0) {
                    regenAfter = cur + (interval / 10) + Math.abs(Rand32.genRandom().intValue()) % (6 * interval / 10);
                }
                reactorGen.setRegenAfter(regenAfter);
                reactorGen.setReactorCount(0);
                reactorGen.setFlip(WzUtil.getBoolean(reactor.getNode("f"), false));

                reactorGens.add(reactorGen);
            }
        }
        tryCreateCreator(true);
        this.reactorTotalHit = 0;
        this.reactorHitEnable = false;
    }

    public void onHit(User user, InPacket packet) {
        Reactor reactor = reactors.getOrDefault(packet.decodeInt(), null);
        if (reactor != null) {
            reactor.onHit(user, packet);
        }
    }

    public void onTouch(User user, InPacket packet) {
        //?
    }

    public void onPacket(User user, int type, InPacket packet) {
        if (type == ClientPacket.ReactorHit) {
            onHit(user, packet);
        } else if (type == ClientPacket.ReactorTouch) {
            onTouch(user, packet);
        }
    }

    public void removeReactor(Reactor reactor) {
        getField().splitUnregisterFieldObj(9, reactor);
        reactor.setRemoved();
        reactors.remove(reactor.getGameObjectID());
        if (reactor.getReactorGen().getName() != null && !reactor.getReactorGen().getName().isEmpty()) {
            reactorNames.remove(reactor.getReactorGen().getName());
        }
    }

    public void reset(boolean shuffle) {
        List<Point> positions = new ArrayList<>();
        List<Reactor> shuffledReactors = new ArrayList<>();

        for (Reactor reactor : reactors.values()) {
            reactor.setOldState(0);
            reactor.setStateEnd(System.currentTimeMillis());
            setState(reactor.getGameObjectID(), 0);
            reactor.removeNpc();
            if (shuffleOnReset || shuffle) {
                if (shuffleName == null || shuffleName.isEmpty() || shuffleName.equals(reactor.getReactorGen().getName())) {
                    positions.add(new Point(reactor.getPos().x, reactor.getPos().y));
                    shuffledReactors.add(reactor);
                }
            }
        }

        List<Integer> shuffles = new ArrayList<>();
        Utilities.getRandomUniqueArray(shuffles, 0, positions.size(), positions.size());

        if (shuffleOnReset || shuffle) {
            int shuffleIndex = 0;
            for (Reactor reactor : shuffledReactors) {
                int rand = shuffles.get(shuffleIndex);
                shuffleIndex++;

                Point newPos = new Point(positions.get(rand).x, positions.get(rand).y);
                reactor.setPos(newPos);

                FieldSplit split = getField().splitFromPoint(newPos.x, newPos.y);
                getField().splitMigrateFieldObj(split, 9, reactor);
            }
        }
        this.reactorHitEnable = false;
        shuffles.clear();
        positions.clear();
        shuffledReactors.clear();// may remove reactor pointer ?
    }

    public boolean setState(String name, int state) {
        int objID = reactorNames.getOrDefault(name, -1);
        if (objID == -1) {
            return false;
        }
        Reactor reactor = reactors.getOrDefault(objID, null);
        if (reactor == null) {
            return false;
        }
        reactor.setState(state);
        reactor.findAvailableAction();

        ReactorTemplate.StateInfo stateInfo = reactor.getCurStateInfo();
        if ((stateInfo.reactorEventInfos == null || stateInfo.reactorEventInfos.isEmpty()) &&
                (getField().getParentFieldSet() == null || reactor.getTemplate().isRemoveInFieldSet())) {
            removeReactor(reactor);
        } else {
            getField().splitSendPacket(reactor.getSplit(), reactor.makeStateChangePacket(0, -1), null);
        }
        return true;
    }

    public void setState(int id, int state) {
        Reactor reactor = reactors.getOrDefault(id, null);
        if (reactor == null) {
            return;
        }
        reactor.setState(state);
        reactor.findAvailableAction();

        ReactorTemplate.StateInfo stateInfo = reactor.getCurStateInfo();
        if ((stateInfo.reactorEventInfos == null || stateInfo.reactorEventInfos.isEmpty()) &&
                (getField().getParentFieldSet() == null || reactor.getTemplate().isRemoveInFieldSet())) {
                removeReactor(reactor);
        } else {
            getField().splitSendPacket(reactor.getSplit(), reactor.makeStateChangePacket(0, -1), null);
        }
    }

    public void tryCreateCreator(boolean reset) {
        if (reactorGens == null || reactorGens.isEmpty()) {
            return;
        }
        long cur = System.currentTimeMillis();
        if (!reset && (cur - lastCreateReactorTime) < 7000) {
            return;
        }
        List<ReactorGen> toCreate = new ArrayList<>();
        for (ReactorGen reactorGen : reactorGens) {
            if (reactorGen.getRegenInterval() <= 0) {
                if (reset) {
                    toCreate.add(reactorGen);
                }
                continue;
            }
            if (reactorGen.getReactorCount() == 0 && (cur - reactorGen.getRegenAfter()) >= 0) {
                toCreate.add(reactorGen);
            }
        }

        for (ReactorGen create : toCreate) {
            createReactor(create);
        }
        this.lastCreateReactorTime = cur;
        toCreate.clear();
    }

    public void update(long cur) {
        for (Reactor reactor : reactors.values()) {
            reactor.updateOwnerInfo();
            reactor.doActionByUpdateEvent();
        }
        tryCreateCreator(false);
    }

    public Field getField() {
        return field;
    }

    public boolean isShuffleOnReset() {
        return shuffleOnReset;
    }

    public void setShuffleOnReset(boolean shuffleOnReset) {
        this.shuffleOnReset = shuffleOnReset;
    }

    public String getShuffleName() {
        return shuffleName;
    }

    public void setShuffleName(String shuffleName) {
        this.shuffleName = shuffleName;
    }

    public Map<Integer, Reactor> getReactors() {
        return reactors;
    }

    public void setReactors(Map<Integer, Reactor> reactors) {
        this.reactors = reactors;
    }

    public Map<String, Integer> getReactorNames() {
        return reactorNames;
    }

    public void setReactorNames(Map<String, Integer> reactorNames) {
        this.reactorNames = reactorNames;
    }

    public List<ReactorGen> getReactorGens() {
        return reactorGens;
    }

    public void setReactorGens(List<ReactorGen> reactorGens) {
        this.reactorGens = reactorGens;
    }

    public long getLastCreateReactorTime() {
        return lastCreateReactorTime;
    }

    public void setLastCreateReactorTime(long lastCreateReactorTime) {
        this.lastCreateReactorTime = lastCreateReactorTime;
    }

    public boolean isReactorHitEnable() {
        return reactorHitEnable;
    }

    public void setReactorHitEnable(boolean reactorHitEnable) {
        this.reactorHitEnable = reactorHitEnable;
    }

    public int getReactorTotalHit() {
        return reactorTotalHit;
    }

    public void setReactorTotalHit(int reactorTotalHit) {
        this.reactorTotalHit = reactorTotalHit;
    }
}
