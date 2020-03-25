package game.user.quest;

import common.JobAccessor;
import common.JobCategory;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterData;
import game.GameApp;
import game.script.ScriptVM;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import game.user.quest.info.*;
import game.user.quest.info.act.ActItem;
import game.user.quest.info.act.ActSkill;
import game.user.skill.SkillInfo;
import jnr.ffi.annotations.In;
import org.python.jline.internal.Log;
import util.FileTime;
import util.Logger;
import util.Pointer;
import util.SystemTime;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.io.File;
import java.util.*;

/**
 * Created by MechAviv on 1/20/2020.
 */
public class QuestMan {
    private static final QuestMan instance = new QuestMan();
    private static int[] ENABLED_QUESTS = {10619, 28455};
    public static final int
            SUCCESS = 0x0,
            FAIL_NPC = 0x1,
            FAIL_ITEM = 0x2,
            FAIL_PREQUEST = 0x3,
            FAIL_INFO = 0x4,
            FAIL_OTHERINFO = 0x5,
            FAIL_MOB = 0x6,
            FAIL_PROTECTEDITEM = 0x7,
            FAIL_PETNOEXIST = 0x8,
            FAIL_PETCONDITION = 0x9,
            FAIL_MESO = 0xA,
            FAIL_TIME = 0xB,
            FAIL_UNKNOWN = 0xC,
            FAIL_MORPH = 0xD,
            FAIL_BUFF = 0xE,
            FAIL_EXCEPTBUFF = 0xF,
            FAIL_LEVEL = 0x10,
            FAIL_TIMEKEEP = 0x11,
            FAIL_DAYOFWEEK = 0x12;

    private static final boolean LOG_UNHANDLED_QUESTS = false;
    private final Map<String, List<Integer>> seriesQuest;
    private final Map<Integer, String> seriesQuestName;
    private final Map<Integer, String> questName;
    private final int worldID;
    private final Map<Integer, QuestDemand> startDemand;
    private final Map<Integer, QuestDemand> completeDemand;
    private final List<ModQuestTime> modifiedQuestTime;
    private final Map<Integer, List<Integer>> mobQuest;
    private final Map<Integer, List<Integer>> itemQuest;
    private final Map<Integer, QuestAct> startAct;
    private final Map<Integer, QuestAct> completeAct;
    private final Map<String, InitialQuizInfo> initialQuiz;
    private final Map<Integer, Boolean> autoStartQuest;
    private final Map<Integer, Boolean> autoCompleteQuest;
    private final Map<Integer, List<Integer>> equipOnAutoQuestStart;
    private final Map<Integer, List<Integer>> fieldOnAutoQuestStart;
    private final Map<Integer, Boolean> isEquipAutoQuestStart;
    private final Map<Integer, Boolean> isFieldAutoQuestStart;
    private final List<Integer> normalAutoStartQuest;
    private final Map<Integer, Boolean> selectedMobQuest;

    public QuestMan() {
        this.seriesQuest = new HashMap<>();
        this.seriesQuestName = new HashMap<>();
        this.questName = new HashMap<>();
        this.startDemand = new HashMap<>();
        this.completeDemand = new HashMap<>();
        this.modifiedQuestTime = new ArrayList<>();
        this.mobQuest = new HashMap<>();
        this.itemQuest = new HashMap<>();
        this.startAct = new HashMap<>();
        this.completeAct = new HashMap<>();
        this.initialQuiz = new HashMap<>();
        this.autoStartQuest = new HashMap<>();
        this.autoCompleteQuest = new HashMap<>();
        this.equipOnAutoQuestStart = new HashMap<>();
        this.fieldOnAutoQuestStart = new HashMap<>();
        this.isEquipAutoQuestStart = new HashMap<>();
        this.isFieldAutoQuestStart = new HashMap<>();
        this.normalAutoStartQuest = new ArrayList<>();
        this.selectedMobQuest = new HashMap<>();
        this.worldID = GameApp.getInstance().getWorldID();
    }

    public static QuestMan getInstance() {
        return instance;
    }

    public int checkCompleteDemand(int questID, int npcTemplateID, CharacterData cd) {
        if (questID <= 0) {
            Logger.logReport("Fail 1");
            return FAIL_UNKNOWN;
        }
        QuestDemand demand = completeDemand.getOrDefault(questID, null);
        if (demand == null) {
            Logger.logReport("Fail 2");

            return FAIL_UNKNOWN;
        }
        if (!cd.getQuestRecord().containsKey(questID)) {
            Logger.logReport("Fail 3 %d ", questID);

            return FAIL_UNKNOWN;
        }
        String info = cd.getQuestRecord().getOrDefault(questID, null);
        int svrNpcTemplateID = demand.getNpcTemplateID();
        if (svrNpcTemplateID != 0 && svrNpcTemplateID != npcTemplateID) {
            return FAIL_NPC;
        }
        for (QuestItemInfo itemInfo : demand.getDemandItem()) {
            int itemID = itemInfo.getItemID();
            byte ti = (byte) (itemID / 1000000);
            int invCount = 0;
            int eqpCount = 0;
            if (cd.getItemSlotCount(ti) >= 1) {
                for (int i = 1; i <= cd.getItemSlotCount(ti); i++) {
                    ItemSlotBase item = cd.getItem(ti, i);
                    if (item != null && item.getItemID() == itemID) {
                        if (item.getType() == ItemType.Equip) {
                            eqpCount++;
                        } else {
                            invCount += item.getItemNumber();
                        }
                    }
                }
            }
            if (ItemInfo.getEquipItem(itemID) != null) {
                Pointer<Integer> bodyPart = new Pointer<>(0);
                ItemAccessor.getBodyPartFromItem(itemID, cd.getCharacterStat().getGender(), bodyPart, false);
                ItemSlotBase item = cd.getItem(ti, -bodyPart.get());
                if (item != null && item.getItemID() == itemID) {
                    if (item.getType() == ItemType.Equip) {
                        eqpCount++;
                    } else {
                        invCount += item.getItemNumber();
                    }
                }
            }
            int count = itemInfo.getCount();
            if (count > 0) {
                if (invCount < count) {
                    if (invCount + eqpCount >= count) {
                        return FAIL_PROTECTEDITEM;
                    }
                    return FAIL_ITEM;
                }
            }
            if (count < 0 && invCount + count > 0) {
                if (invCount + eqpCount + count <= 0) {
                    return FAIL_PROTECTEDITEM;
                }
                return FAIL_ITEM;
            }
            if (count == 0) {
                if (invCount != 0) {
                    return FAIL_ITEM;
                }
                if (eqpCount != 0) {
                    return FAIL_PROTECTEDITEM;
                }
            }
        }
        for (QuestRecord record : demand.getPrecedeQuest()) {
            int key = record.getQuestID();
            int state = record.getState();
            if (state == 0) {
                if (cd.getQuestRecord().containsKey(key) || cd.getQuestComplete().containsKey(key)) {
                    return FAIL_PREQUEST;
                }
            } else if (state == 1 && !cd.getQuestRecord().containsKey(key)) {
                return FAIL_PREQUEST;
            } else if (state == 2 && !cd.getQuestComplete().containsKey(key)) {
                return FAIL_PREQUEST;
            }
        }

        // todo quest info
        List<QuestMobInfo> mobInfos = demand.getDemandMob();
        if (mobInfos != null && mobInfos.size() != 0) {
            int infoLength = 0;
            int mobInfoCount = 0;
            if (info != null) {
                infoLength = info.length();
            }
            if (mobInfos != null) {
                mobInfoCount = mobInfos.size();
            }
            if (infoLength % 3 != 0 || infoLength / 3 < mobInfoCount) {
                Logger.logReport("info length = [%d] | mob count [%d]", infoLength, mobInfoCount);
                return FAIL_MOB;
            }
            int index = 0;
            for (QuestMobInfo mobInfo : mobInfos) {
                int killCount = Integer.parseInt(info.substring(index, index + 3));
                if (killCount < mobInfo.getCount()) {
                    return FAIL_MOB;
                }
                index += 3;
            }
        }
        return SUCCESS;
    }

    public boolean checkStartDemand(int questID, int npcTemplateID, CharacterData cd, boolean tamingMob, int tamingMobLevel, int curFieldID) {
        if (questID <= 0) {
            return false;
        }
        QuestDemand demand = startDemand.getOrDefault(questID, null);
        if (demand == null ||
                cd.getQuestRecord().containsKey(questID) ||
                demand.getWorldMax() >= 0 && demand.getWorldMax() < worldID ||
                demand.getWorldMin() >= 0 && demand.getWorldMin() > worldID ||
                demand.getNpcTemplateID() != 0 && demand.getNpcTemplateID() != npcTemplateID) {
            return false;
        }
        FileTime curFt = FileTime.systemTimeToFileTime();
        FileTime questEndFt = cd.getQuestComplete().getOrDefault(questID, null);
        if (questEndFt != null) {
            if (demand.getRepeatInterval() < 0) {
                if (!demand.isRepeatDayByDay()) {
                    return false;
                }
                SystemTime stEnd = questEndFt.fileTimeToSystemTime();
                SystemTime stCur = curFt.fileTimeToSystemTime();
                if ( stCur.getYear() < stEnd.getYear() || stCur.getYear() == stEnd.getYear() && (stCur.getMonth() < stEnd.getMonth() || stCur.getMonth() == stEnd.getMonth() && stCur.getDay() <= stEnd.getDay()) ) {
                    return false;
                }
            }
            questEndFt.add(FileTime.FILETIME_MINUTE, demand.getRepeatInterval());
            if (FileTime.compareFileTime(questEndFt, curFt) > 0) {
                return false;
            }
        }
        if (demand.getLevelMax() != 0 && demand.getLevelMax() < cd.getCharacterStat().getLevel()) {
            return false;
        }
        if (demand.getLevelMin() != 0 && demand.getLevelMin() > cd.getCharacterStat().getLevel()) {
            return false;
        }
        if (demand.getPop() != 0 && demand.getPop() > cd.getCharacterStat().getPOP()) {
            return false;
        }

        FileTime startFt = demand.getStart();
        FileTime endFt = demand.getEnd();
        for (ModQuestTime mod : modifiedQuestTime) {
            if (mod.getQuestID() == questID) {
                startFt = mod.getStart();
                endFt = mod.getEnd();
                break;
            }
        }
        if (FileTime.compareFileTime(startFt, curFt) > 0 || FileTime.compareFileTime(endFt, curFt) < 0) {
            return false;
        }
        if (demand.getJobs().size() > 0) {
            boolean acceptableJob = false;
            for (Short job : demand.getJobs()) {
                int userJob = cd.getCharacterStat().getJob();
                if (JobAccessor.getJobCategory(userJob) == JobCategory.ADMIN || job == userJob) {
                    acceptableJob = true;
                    break;
                }
            }
            if (!acceptableJob) {
                return false;
            }
        }
        for (QuestItemInfo itemInfo : demand.getDemandItem()) {
            int itemID = itemInfo.getItemID();
            byte ti = (byte) (itemID / 1000000);
            int invCount = 0;
            int eqpCount = 0;
            if (cd.getItemSlotCount(ti) >= 1) {
                for (int i = 1; i <= cd.getItemSlotCount(ti); i++) {
                    ItemSlotBase item = cd.getItem(ti, i);
                    if (item != null && item.getItemID() == itemID) {
                        invCount++;
                    }
                }
            }
            if (ItemInfo.getEquipItem(itemID) != null) {
                Pointer<Integer> bodyPart = new Pointer<>(0);
                ItemAccessor.getBodyPartFromItem(itemID, cd.getCharacterStat().getGender(), bodyPart, false);
                ItemSlotBase item = cd.getItem(ti, -bodyPart.get());
                if (item != null && item.getItemID() == itemID) {
                    invCount++;
                }
            }
            int count = itemInfo.getCount();
            if (count > 0) {
                if (invCount < count) {
                    return false;
                }
            }
            if (count < 0 && invCount + count > 0 || count == 0 && invCount != 0) {
                return false;
            }
        }
        boolean found = false;
        if (demand.getEquipAllNeed().size() == 0) found = true;
        for (Integer need : demand.getEquipAllNeed()) {
            for (ItemSlotBase item : cd.getEquipped()) {
                if (item.getItemID() == need || found) {
                    found = true;
                    break;
                }
            }
            for (ItemSlotBase item : cd.getEquipped2()) {
                if (item.getItemID() == need || found) {
                    found = true;
                    break;
                }
            }
            for (ItemSlotBase item : cd.getDragonEquipped()) {
                if (item.getItemID() == need || found) {
                    found = true;
                    break;
                }
            }
            for (ItemSlotBase item : cd.getMechanicEquipped()) {
                if (item.getItemID() == need || found) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            return false;
        }
        // demand pet goes here
        for (QuestSkillInfo skill : demand.getDemandSkill()) {
            int skillID = skill.getSkillID();
            if (!SkillInfo.getInstance().isSkillVisible(cd, skillID, null)) {
                return false;
            }
            if (skill.getAcquire()) {
                int level = skill.getLevel();
                if (level > 0 || SkillInfo.getInstance().getSkillLevel(cd, skillID, null) < level) {
                    return false;
                }
            }
        }
        // todo more
        return true;
    }

    public QuestAct getCompleteAct(int questID) {
        return completeAct.getOrDefault(questID, null);
    }

    public QuestDemand getCompleteDemand(int questID) {
        return completeDemand.getOrDefault(questID, null);
    }

    public String getCompleteScriptName(int questID) {
        QuestDemand demand = completeDemand.getOrDefault(questID, null);
        if (demand == null) {
            return null;
        }
        if (isCompleteScriptLinkedQuest(questID)) {
            return demand.getEndScript();
        }
        return null;
    }

    public String getInitialQuizInfo(String key) {
        return null;
    }

    public List<Integer> getQuestsByItem(int itemID) {
        return itemQuest.getOrDefault(itemID, null);
    }

    public List<Integer> getQuestByMob(int mobID) {
        return mobQuest.getOrDefault(mobID, null);
    }

    public String getQuestName(int questID) {
        return questName.getOrDefault(questID, null);
    }

    public List<Integer> getSeriesQuest(int questID) {
        return seriesQuest.getOrDefault(questID, null);
    }

    public String getSeriesQuestName(int questID) {
        return seriesQuestName.getOrDefault(questID, null);
    }

    public QuestAct getStartAct(int questID) {
        return startAct.getOrDefault(questID, null);
    }

    public QuestDemand getStartDemand(int questID) {
        return startDemand.getOrDefault(questID, null);
    }

    public String getStartScriptName(int questID) {
        QuestDemand demand = startDemand.getOrDefault(questID, null);
        if (demand != null) {
            if (isStartScriptLinkedQuest(questID)) {
                return demand.getStartScript();
            }
        }
        return null;
    }

    public boolean isAutoCompleteQuest(int questID) {
        return autoCompleteQuest.getOrDefault(questID, false);
    }

    public boolean isAutoStartQuest(int questID) {
        return autoStartQuest.getOrDefault(questID, false);
    }

    public boolean isCompleteScriptLinkedQuest(int questID) {
        if (questID <= 0) {
            return false;
        }
        QuestDemand demand = completeDemand.getOrDefault(questID, null);
        if (demand == null) {
            return false;
        }
        if (demand.getEndScript() == null || demand.getEndScript().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isEquipOnAutoStartQuest(int itemID) {
        return isEquipAutoQuestStart.getOrDefault(itemID, false);
    }

    public boolean isFieldOnAutoStartQuest(int itemID) {
        return isFieldAutoQuestStart.getOrDefault(itemID, false);
    }

    public boolean isStartScriptLinkedQuest(int questID) {
        if (questID <= 0) {
            return false;
        }
        QuestDemand demand = startDemand.getOrDefault(questID, null);
        if (demand == null) {
            return false;
        }
        if (demand.getStartScript() == null || demand.getStartScript().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean loadAct() {
        WzPackage questPackage = new WzFileSystem().init("Quest").getPackage();

        WzProperty act = questPackage.getItem("Act.img");
        if (act != null) {
            for (WzProperty quest : act.getChildNodes()) {
                int questID = Integer.parseInt(quest.getNodeName());
                WzProperty copy = quest;
                if (!registerAct(questID, copy)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean loadDemand() {
        WzPackage questPackage = new WzFileSystem().init("Quest").getPackage();

        WzProperty checkProperty = questPackage.getItem("Check.img");
        if (checkProperty != null) {
            for (WzProperty quest : checkProperty.getChildNodes()) {
                int questID = Integer.parseInt(quest.getNodeName());
                if (!registerDemand(questID, quest)) {
                    return false;
                }
            }
        }
        loadQuestInfo(questPackage.getItem("QuestInfo.img"));
        return true;
    }

    public boolean loadInitialQuiz() {
        return true;
    }

    public void loadQuestInfo(WzProperty prop) {
        Map<Integer, Integer> seriesQuestOrder = new HashMap<>();
        if (prop != null) {
            for (WzProperty quest : prop.getChildNodes()) {
                int questID = Integer.parseInt(quest.getNodeName());

                String questName = WzUtil.getString(quest.getNode("name"), null);
                if (questName != null) {
                    this.questName.put(questID, questName);
                }

                boolean autoStart = WzUtil.getInt32(quest.getNode("autoStart"), 0) != 0;
                if (autoStart) {
                    autoStartQuest.put(questID, true);
                }

                boolean autoComplete = WzUtil.getInt32(quest.getNode("autoPreComplete"), 0) != 0;
                if (autoComplete) {
                    autoCompleteQuest.put(questID, true);
                }

                boolean selectedMob = WzUtil.getInt32(quest.getNode("selectedMob"), 0) != 0;
                if (selectedMob) {
                    selectedMobQuest.put(questID, true);
                }

                String parent = WzUtil.getString(quest.getNode("parent"), null);
                if (parent != null && !parent.isEmpty()) {
                    int order = WzUtil.getInt32(quest.getNode("order"), 0);
                    if (order != 0) {
                        seriesQuestOrder.put(questID, order);

                        List<Integer> quests = seriesQuest.getOrDefault(parent, new ArrayList<>());
                        quests.add(questID);
                        seriesQuestOrder.put(questID, order);
                        seriesQuest.put(parent, quests);
                        seriesQuestName.put(questID, parent);
                    }
                }
            }
            for (Map.Entry<String, List<Integer>> series : seriesQuest.entrySet()) {
                String key = series.getKey();
                List<Integer> quests = series.getValue();
                Collections.sort(quests, (a, b) -> seriesQuestOrder.getOrDefault(a.intValue(), 0) > seriesQuestOrder.getOrDefault(b.intValue(), 0) ? 1 : -1);
                seriesQuest.put(key, quests);
            }
        }
        loadModifiedQuests();
    }

    public boolean registerDemand(int questID, WzProperty prop) {
        for (int i = 0; i < 2; i++) {
            WzProperty demandData = prop.getNode("" + i);
            if (demandData == null) {
                return false;
            }
            QuestDemand demand = new QuestDemand();
            demand.setWorldMin(Integer.parseInt(WzUtil.getString(demandData.getNode("worldmin"), "-1")));
            demand.setWorldMax(Integer.parseInt(WzUtil.getString(demandData.getNode("worldmax"), "-1")));
            demand.setTamingMobLevelMin(WzUtil.getInt32(demandData.getNode("tamingmoblevelmin"), 0));
            demand.setTamingMobLevelMax(WzUtil.getInt32(demandData.getNode("tamingmoblevelmax"), 30));
            demand.setPetTamenessMin(WzUtil.getInt32(demandData.getNode("pettamenessmin"), 0));
            demand.setPetTamenessMax(WzUtil.getInt32(demandData.getNode("pettamenessmax"), 30000));
            demand.setNpcTemplateID(WzUtil.getInt32(demandData.getNode("npc"), 0));
            demand.setStartScript(WzUtil.getString(demandData.getNode("startscript"), ""));
            demand.setEndScript(WzUtil.getString(demandData.getNode("endscript"), ""));
            String script = demand.getStartScript();
            if (LOG_UNHANDLED_QUESTS && script != null && !script.isEmpty()) {
                File file = new File(ScriptVM.PATH + script + ".py");
                if (!file.exists()) {
                    Logger.logError("QuestID [%d] StartScript [%s] not found !", questID, script);
                    // return false;
                }
            }
            script = demand.getEndScript();
            if (LOG_UNHANDLED_QUESTS && script != null && !script.isEmpty()) {
                File file = new File(ScriptVM.PATH + script + ".py");
                if (!file.exists()) {
                    Logger.logError("QuestID [%d] EndScript [%s] not found !", questID, script);
                    // return false;
                }
            }
            demand.setLevelMin(WzUtil.getInt32(demandData.getNode("lvmin"), 0));
            demand.setLevelMax(WzUtil.getInt32(demandData.getNode("lvmax"), 0));
            demand.setPop(WzUtil.getInt32(demandData.getNode("pop"), 0));
            demand.setRepeatInterval(WzUtil.getInt32(demandData.getNode("interval"), -1));
            demand.setStart(FileTime.getStringToFileTime(WzUtil.getString(demandData.getNode("start"), null), true));
            demand.setEnd(FileTime.getStringToFileTime(WzUtil.getString(demandData.getNode("end"), null), false));
            demand.setRepeatDayByDay(WzUtil.getInt32(demandData.getNode("dayByDay"), 0) != 0);
            // todo handle info loading
            boolean normalAutoStart = WzUtil.getInt32(demandData.getNode("normalAutoStart"), 0) != 0;
            if (!normalAutoStart) {
                WzProperty eqAllNeed = demandData.getNode("equipAllNeed");
                if (eqAllNeed != null) {
                    for (WzProperty eq : eqAllNeed.getChildNodes()) {
                        int key = WzUtil.getInt32(eq, -1);
                        if (key <= 0) {
                            break;
                        }
                        demand.getEquipAllNeed().add(key);
                        List<Integer> quests = equipOnAutoQuestStart.getOrDefault(key, new ArrayList<>());
                        quests.add(questID);
                        equipOnAutoQuestStart.put(key, quests);
                    }
                }
                WzProperty eqSelectNeed = demandData.getNode("equipSelectNeed");
                if (eqSelectNeed != null) {
                    for (WzProperty eq : eqSelectNeed.getChildNodes()) {
                        int key = WzUtil.getInt32(eq, -1);
                        if (key <= 0) {
                            break;
                        }
                        demand.getEquipSelectNeed().add(key);
                        List<Integer> quests = equipOnAutoQuestStart.getOrDefault(key, new ArrayList<>());
                        quests.add(questID);
                        equipOnAutoQuestStart.put(key, quests);
                    }
                }
                WzProperty fieldEnter = demandData.getNode("fieldEnter");
                if (fieldEnter != null) {
                    for (WzProperty field : fieldEnter.getChildNodes()) {
                        int key = WzUtil.getInt32(field, -1);
                        if (key <= 0) {
                            break;
                        }
                        demand.getFieldEnter().add(key);
                        List<Integer> quests = fieldOnAutoQuestStart.getOrDefault(key, new ArrayList<>());
                        quests.add(questID);
                        fieldOnAutoQuestStart.put(key, quests);
                        isFieldAutoQuestStart.put(key, true);
                    }
                }
            } else {
                normalAutoStartQuest.add(questID);
            }
            WzProperty jobProperty = demandData.getNode("job");
            if (jobProperty != null) {
                for (WzProperty job : jobProperty.getChildNodes()) {
                    short key = (short) WzUtil.getInt32(job, -1);
                    if (key <= 0) {
                        break;
                    }
                    demand.getJobs().add(key);
                }
            }
            WzProperty precedeQuestProperty = demandData.getNode("quest");
            if (precedeQuestProperty != null) {
                for (WzProperty quest : precedeQuestProperty.getChildNodes()) {
                    QuestRecord record = new QuestRecord();
                    record.setQuestID(WzUtil.getInt32(quest.getNode("id"), 0));
                    record.setState(WzUtil.getInt32(quest.getNode("state"), 0));
                    demand.getPrecedeQuest().add(record);
                }
            }
            WzProperty itemProperty = demandData.getNode("item");
            if (itemProperty != null) {
                for (WzProperty item : itemProperty.getChildNodes()) {
                    QuestItemInfo itemInfo = new QuestItemInfo();
                    itemInfo.setItemID(WzUtil.getInt32(item.getNode("id"), 0));
                    itemInfo.setCount(WzUtil.getInt32(item.getNode("count"), 0));
                    demand.getDemandItem().add(itemInfo);
                    if (i == 1 && ItemInfo.isQuestItem(itemInfo.getItemID())) {
                        List<Integer> quests = itemQuest.getOrDefault(itemInfo.getItemID(), new ArrayList<>());
                        if (quests.contains(questID)) {
                            Logger.logError("Duplicate Quest Item in Complete Quest Demand :  QuestID = %d, ItemID = %d", questID, itemInfo.getItemID());
                            return false;
                        }
                        quests.add(questID);
                        itemQuest.put(itemInfo.getItemID(), quests);
                    }
                }
            }
            WzProperty petProperty = demandData.getNode("pet");
            if (petProperty != null) {
                for (WzProperty pet : petProperty.getChildNodes()) {
                    int petID = WzUtil.getInt32(pet.getNode("id"), -1);
                    if (petID <= 0) {
                        break;
                    }
                    demand.getDemandPet().put(petID, true);
                }
            }
            WzProperty skillProperty = demandData.getNode("skill");
            if (skillProperty != null) {
                for (WzProperty skill : skillProperty.getChildNodes()) {
                    QuestSkillInfo skillInfo = new QuestSkillInfo();
                    skillInfo.setSkillID(WzUtil.getInt32(skill.getNode("id"), 0));
                    skillInfo.setLevel(WzUtil.getInt32(skill.getNode("level"), 0));
                    skillInfo.setAcquire(WzUtil.getInt32(skill.getNode("acquire"), 0) != 0);
                    demand.getDemandSkill().add(skillInfo);
                }
            }
            WzProperty mobProperty = demandData.getNode("mob");
            if (mobProperty != null) {
                for (WzProperty mob : mobProperty.getChildNodes()) {
                    QuestMobInfo mobInfo = new QuestMobInfo();
                    mobInfo.setMobID(WzUtil.getInt32(mob.getNode("id"), 0));
                    mobInfo.setCount(WzUtil.getInt32(mob.getNode("count"), 0));
                    demand.getDemandMob().add(mobInfo);

                    List<Integer> quests = mobQuest.getOrDefault(mobInfo.getMobID(), new ArrayList<>());
                    quests.add(questID);
                    mobQuest.put(mobInfo.getMobID(), quests);
                }
            }
            if (i == 0) {
                if (startDemand.getOrDefault(questID, null) != null) {
                    return false;// already exists :O
                }
                startDemand.put(questID, demand);
            } else {
                if (completeDemand.getOrDefault(questID, null) != null) {
                    return false;
                }
                completeDemand.put(questID, demand);
            }
        }
        return true;
    }

    public boolean registerAct(int questID, WzProperty prop) {
        for (int i = 0; i < 2; i++) {
            WzProperty actData = prop.getNode("" + i);
            if (actData == null) {
                return false;
            }
            QuestAct act = new QuestAct();
            act.setIncExp(WzUtil.getInt32(actData.getNode("exp"), 0));
            act.setIncMoney(WzUtil.getInt32(actData.getNode("money"), 0));
            act.setIncPop(WzUtil.getInt32(actData.getNode("pop"), 0));
            act.setIncPetTameness(WzUtil.getInt32(actData.getNode("pettameness"), 0));
            act.setNextQuest(WzUtil.getInt32(actData.getNode("nextQuest"), 0));
            act.setInfo(WzUtil.getString(actData.getNode("info"), ""));
            act.setPetSpeed(WzUtil.getInt32(actData.getNode("petspeed"), 0) != 0);
            act.setBuffItemID(WzUtil.getInt32(actData.getNode("buffItemID"), 0));
            act.setMsg(WzUtil.getString(actData.getNode("message"), null));
            act.setNpcAction(WzUtil.getString(actData.getNode("npcAct"), null));

            WzProperty mapProp = actData.getNode("map");
            if (mapProp != null) {
                for (WzProperty field : mapProp.getChildNodes()) {
                    int key = WzUtil.getInt32(field, -1);
                    if (key <= 0) {
                        break;
                    }
                    act.getMaps().add(key);
                }
            }

            WzProperty itemProp = actData.getNode("item");
            if (itemProp != null) {
                for (WzProperty item : itemProp.getChildNodes()) {
                    int itemID = WzUtil.getInt32(item.getNode("id"), 0);

                    boolean nullItem = ItemInfo.getItemSlot(itemID, ItemVariationOption.None) == null;
                    if (nullItem) {
                        Logger.logError("Inexistent item in Quest ActItem - Item : %d, Quest : %d", itemID, questID);
                        continue;
                        //return false;
                    }
                    if (ItemInfo.isCashItem(itemID)) {
                        Logger.logError("CashItem is in a Quest ActItem - Item : %d, Quest : %d", itemID, questID);
                        return false;
                    }
                    ActItem actItem = new ActItem();
                    QuestItemInfo info = new QuestItemInfo();
                    info.setItemID(itemID);
                    info.setCount(WzUtil.getInt32(item.getNode("count"), 0));
                    actItem.setInfo(info);

                    QuestItemOption option = new QuestItemOption();
                    option.setNamed(WzUtil.getInt32(item.getNode("name"), 0) != 0);
                    option.setPeriod(WzUtil.getInt32(item.getNode("period"), 0));
                    option.setJobFlag(WzUtil.getInt32(item.getNode("job"), 0x1F));
                    option.setGender(WzUtil.getInt32(item.getNode("gender"), 2));
                    option.setProbRate(WzUtil.getInt32(item.getNode("prop"), 0));
                    option.setVariation(WzUtil.getInt32(item.getNode("var"), 0));
                    option.setDateExpire(FileTime.getStringToFileTime(WzUtil.getString(item.getNode("dateExpire"), null), false));
                    actItem.setOption(option);

                    act.getActItem().add(actItem);
                }
            }
            WzProperty skillProp = actData.getNode("skill");
            if (skillProp != null) {
                for (WzProperty skill : skillProp.getChildNodes()) {
                    ActSkill actSkill = new ActSkill();
                    actSkill.setSkillID(WzUtil.getInt32(skill.getNode("id"), 0));
                    actSkill.setSkillLevel(WzUtil.getInt32(skill.getNode("skillLevel"), 0));
                    actSkill.setMasterLevel(WzUtil.getInt32(skill.getNode("masterLevel"), 0));
                    actSkill.setOnlyMasterLevel(WzUtil.getInt32(skill.getNode("onlyMasterLevel"), 0) != 0);

                    WzProperty jobProp = skill.getNode("job");
                    if (jobProp != null) {
                        for (WzProperty job : jobProp.getChildNodes()) {
                            actSkill.getJobs().add(WzUtil.getInt32(job, 0));
                        }
                    }
                    act.getActSkill().add(actSkill);
                }
            }
            if (i == 0) {
                if (startAct.getOrDefault(questID, null) != null) {
                    return false;// already exists :O
                }
                startAct.put(questID, act);
            } else {
                if (completeAct.getOrDefault(questID, null) != null) {
                    return false;
                }
                completeAct.put(questID, act);
            }
        }
        return true;
    }

    public void loadModifiedQuests() {
        for (int quest : ENABLED_QUESTS) {
            setQuestTime(quest, FileTime.START, FileTime.END);
        }
    }

    public List<ModQuestTime> getModifiedQuestTime() {
        return modifiedQuestTime;
    }

    public void setQuestTime(int questID, FileTime start, FileTime end) {
        ModQuestTime temp = new ModQuestTime();
        temp.setQuestID(questID);
        temp.setStart(start);
        temp.setEnd(end);
        for (ModQuestTime mod : modifiedQuestTime) {
            if (mod.getQuestID() == questID) {
                mod.setStart(start);
                mod.setEnd(end);
                return;
            }
        }
        modifiedQuestTime.add(temp);
    }
}
