package game.field.reactor;

import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import util.Logger;
import util.Rect;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 3/19/2020.
 */
public class ReactorTemplate {
    private static final Map<Integer, ReactorTemplate> templates = new HashMap<>();
    private static boolean LOG_INEXISTS_SCRIPTS = true;
    private final List<StateInfo> stateInfos;
    private final List<List<ReactorEventInfo>> eventInfos;
    private int templateID;
    private int stateCount;
    private int hitDelay;
    private boolean move;
    private int moveOnce;
    private int moveDelay;
    private int reqHitCount;
    private boolean removeInFieldSet;
    private List<RewardInfo> rewardInfos;
    private List<ActionInfo> actionInfos;

    public ReactorTemplate() {
        this.rewardInfos = new ArrayList<>();
        this.stateInfos = new ArrayList<>();
        this.eventInfos = new ArrayList<>();
        this.actionInfos = new ArrayList<>();
    }

    public static void load() {
        WzPackage reactorDir = new WzFileSystem().init("Reactor").getPackage();
        WzProperty rewardMain = new WzFileSystem().init("Data").getPackage().getItem("Reward.img");
        WzProperty actionMain = new WzFileSystem().init("Data").getPackage().getItem("ReactorAction.img");
        if (reactorDir != null) {
            for (WzProperty reactorData : reactorDir.getEntries().values()) {
                int templateID = Integer.parseInt(reactorData.getNodeName().replaceAll(".img", ""));
                WzProperty rewardData = rewardMain.getNode(String.format("r%07d", templateID));
                if (!registerReactor(templateID, reactorData, rewardData, actionMain)) {
                    Logger.logError("Failed in parsing Reactor file[%d]", templateID);
                    if (reactorData == null) {
                        Logger.logError("Reward for Reactor [%d] doesn't exist", templateID);
                    }
                }
            }
            reactorDir.release();
            rewardMain.release();
            actionMain.release();
        }
        reactorDir = null;
        rewardMain = null;
        actionMain = null;
    }

    public static void unload() {
        templates.clear();
    }

    private static boolean registerReactor(int templateID, WzProperty prop, WzProperty reward, WzProperty actionData) {
        if (templates.containsKey(templateID)) {
            return false;
        }
        ReactorTemplate template = new ReactorTemplate();

        WzProperty info = prop.getNode("info");
        if (info == null) {
            return false;
        }
        template.move = WzUtil.getInt32(info.getNode("move"), 0) != 0;
        if (template.move) {
            template.moveOnce = WzUtil.getInt32(info.getNode("moveOnce"), 0);
            template.moveDelay = WzUtil.getInt32(info.getNode("moveDelay"), 0);
        } else {
            template.moveOnce = 0;
            template.moveDelay = 0;
        }
        template.reqHitCount = WzUtil.getInt32(info.getNode("hitCount"), 0);
        template.hitDelay = getSumDelay(prop);
        template.removeInFieldSet = WzUtil.getInt32(info.getNode("removeInFieldSet"), 0) != 0;

        String actionPath = WzUtil.getString(prop.getNode("action"), null);
        if (actionPath != null) {
            List<ActionInfo> actionInfos = new ArrayList<>();
            // after handle all events I should return false if load fails
            loadAction(actionData, actionPath, actionInfos);
            template.actionInfos = actionInfos;
        }
        WzProperty linkProp = prop;
        String link = WzUtil.getString(info.getNode("link"), null);
        if (link != null) {
            linkProp = new WzFileSystem().init("Reactor").getPackage().getItem(link + ".img");
            if (linkProp == null) {
                Logger.logReport("Link [%s.img] is null");
                return false;
            }
        }
        int stateCount = 0;
        for (stateCount = 0; ; stateCount++) {
            WzProperty data = linkProp.getNode("" + stateCount);
            if (data == null) {
                break;
            }
            StateInfo stateInfo = new StateInfo();
            stateInfo.hitDelay = getSumDelay(data);
            stateInfo.timeOut = 0;

            WzProperty eventData = data.getNode("event");
            if (eventData != null) {
                stateInfo.timeOut = WzUtil.getInt32(eventData.getNode("timeOut"), 0);
                List<ReactorEventInfo> eventInfos = new ArrayList<>();
                if (!loadEvent(eventData, eventInfos)) {
                    return false;
                }
                stateInfo.reactorEventInfos = eventInfos;
            }
            template.getStateInfos().add(stateInfo);
        }

        template.stateCount = stateCount;
        if (reward != null) {
            List<RewardInfo> rewardInfos = new ArrayList<>();
            if (Reward.loadReward(reward, rewardInfos) != 0) {
                return false;
            }
            template.rewardInfos = rewardInfos;
        }
        templates.put(templateID, template);
        return true;
    }

    private static boolean loadAction(WzProperty prop, String actionPath, List<ActionInfo> actionInfos) {
        WzProperty actionData = prop.getNode(actionPath);
        if (actionData == null) {
            // when finish handle all actions I should return false here
            if (LOG_INEXISTS_SCRIPTS)
                //Logger.logError("Inexistent reactor action script '%s'", actionPath);
                return true;
        }
        for (int i = 0; ; i++) {
            WzProperty data = actionData.getNode("" + i);
            if (data == null) {
                break;
            }
            ActionInfo actionInfo = new ActionInfo();
            actionInfo.state = WzUtil.getInt32(data.getNode("state"), -1);
            actionInfo.type = WzUtil.getInt32(data.getNode("type"), -1);
            actionInfo.message = WzUtil.getString(data.getNode("message"), null);
            if (actionInfo.state < 0 || actionInfo.type < 0) {
                Logger.logError("[Reactor Action Info] Invalid state[%d] or type[%d] Script:'%s'", actionInfo.state, actionInfo.type, actionPath);
                return false;
            }
            for (int j = 0; ; j++) {
                WzProperty argData = data.getNode("" + j);
                if (argData == null) {
                    break;
                }
                int intVal = WzUtil.getInt32(argData, -1);
                if (intVal != -1) {
                    actionInfo.args.add(intVal);
                } else {
                    String strVal = WzUtil.getString(argData, null);
                    if (strVal != null) {
                        actionInfo.strArgs.add(strVal);
                    }
                }
            }
            actionInfos.add(actionInfo);
        }
        return true;
    }

    private static boolean loadEvent(WzProperty prop, List<ReactorEventInfo> eventInfos) {
        if (prop == null) {
            return false;
        }
        for (int i = 0; ; i++) {
            WzProperty data = prop.getNode("" + i);
            if (data == null) {
                break;
            }
            ReactorEventInfo reactorEventInfo = new ReactorEventInfo();
            reactorEventInfo.type = WzUtil.getInt32(data.getNode("type"), -1);
            reactorEventInfo.stateToBe = WzUtil.getInt32(data.getNode("state"), -1);
            reactorEventInfo.hitDelay = getSumDelay(data);
            Point lt = WzUtil.getPoint(data.getNode("lt"), null);
            Point rb = WzUtil.getPoint(data.getNode("rb"), null);
            if (lt != null || rb != null) {
                reactorEventInfo.checkArea = new Rect(lt.x, lt.y, rb.x, rb.y);
            } else {
                reactorEventInfo.checkArea = new Rect();
            }
            for (int j = 0; ; j++) {
                WzProperty argData = data.getNode("" + j);
                if (argData == null) {
                    break;
                }
                int val = WzUtil.getInt32(argData, 0x7FFFFFFF);
                if (val == 0x7FFFFFFF) {
                    break;
                }
                reactorEventInfo.args.add(val);
            }
            eventInfos.add(reactorEventInfo);
        }
        return true;
    }

    private static int getSumDelay(WzProperty prop) {
        if (prop == null) {
            return -1;
        }
        int delay = 0;
        // should I redirect it to hit (0/hit/delay)?
        for (int i = 0; ; i++) {
            WzProperty data = prop.getNode("" + i);
            if (data == null) {
                break;
            }
            delay += WzUtil.getInt32(data.getNode("delay"), 120);
        }
        return delay;
    }

    public static ReactorTemplate getReactorTemplate(int templateID) {
        return templates.getOrDefault(templateID, null);
    }

    public static ActionInfo getActionInfo(int templateID, int idX) {
        ReactorTemplate template = getReactorTemplate(templateID);
        if (template == null || idX < 0 || idX >= template.getActionInfos().size()) {
            return null;
        }
        return template.getActionInfos().get(idX);
    }

    public static int getHitTypePriorityLevel(int option, int type) {
        int opt1 = (option & 1) != 0 ? 1 : 0;
        int opt2 = (option & 1) == 0 ? 1 : 0;
        if ((option & 2) != 0) {
            switch (type) {
                case 0:
                    return 1;
                case 1:
                    return -opt1;
                case 2:
                    return opt1 - 1;
            }
        } else {
            switch (type) {
                case 0:
                    return 2;
                case 1:
                    return 2 * opt2 - 1;
                case 2:
                    return 2 * opt1 - 1;
                case 3:
                    return -opt1;
                case 4:
                    return opt1 - 1;
            }
        }
        return -1;
    }

    public StateInfo getStateInfo(int idX) {
        if (idX < 0 || idX >= this.stateCount) {
            return null;
        }
        return stateInfos.get(idX);
    }

    public int getTemplateID() {
        return templateID;
    }

    public int getStateCount() {
        return stateCount;
    }

    public int getHitDelay() {
        return hitDelay;
    }

    public boolean isMove() {
        return move;
    }

    public int getMoveOnce() {
        return moveOnce;
    }

    public int getMoveDelay() {
        return moveDelay;
    }

    public int getReqHitCount() {
        return reqHitCount;
    }

    public boolean isRemoveInFieldSet() {
        return removeInFieldSet;
    }

    public List<RewardInfo> getRewardInfos() {
        return rewardInfos;
    }

    public List<StateInfo> getStateInfos() {
        return stateInfos;
    }

    public List<List<ReactorEventInfo>> getEventInfos() {
        return eventInfos;
    }

    public List<ActionInfo> getActionInfos() {
        return actionInfos;
    }

    public static class ReactorEventInfo {
        public int type;
        public int hitDelay;
        public int stateToBe;
        public Rect checkArea = new Rect();
        public List<Integer> args = new ArrayList<>();
    }

    public static class ActionInfo {
        public int state;
        public int type;
        public int prob;
        public int period;
        public String dateExpire;
        public List<Integer> args = new ArrayList<>();
        public List<String> strArgs = new ArrayList<>();
        public String message;
    }

    public static class StateInfo {
        public int hitDelay;
        public int timeOut;
        public List<ReactorEventInfo> reactorEventInfos = new ArrayList<>();
    }
}
