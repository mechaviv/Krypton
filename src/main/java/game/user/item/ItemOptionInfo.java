package game.user.item;

import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class ItemOptionInfo {
    private static final Map<Integer, ItemOptionInfo> options = new HashMap<>();

    public int itemOptionID, reqLevel, optionType;
    public List<ItemOptionLevelData> levelData;

    public ItemOptionInfo() {
        this.levelData = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) levelData.add(i, new ItemOptionLevelData());
    }

    public static ItemOptionInfo getItemOption(int optionID) {
        return options.getOrDefault(optionID, null);
    }

    public static boolean iterateItemOptionInfo() {
        WzProperty optionDir = new WzFileSystem().init("Item").getPackage().getItem("ItemOption.img");
        if (optionDir == null) {
            return false;
        }
        for (WzProperty optionData : optionDir.getChildNodes()) {
            ItemOptionInfo optionInfo = new ItemOptionInfo();
            optionInfo.itemOptionID = Integer.parseInt(optionData.getNodeName());
            optionInfo.reqLevel = WzUtil.getInt32(optionData.getNode("info/reqLevel"), 0);
            optionInfo.optionType = WzUtil.getInt32(optionData.getNode("info/optionType"), 0);

            WzProperty lvlData = optionData.getNode("level");
            if (lvlData != null) {
                for (int i = 1; i <= 20; i++) {
                    WzProperty levelDataWZ = lvlData.getNode("" + i);
                    if (levelDataWZ == null) {
                        break;
                    }
                    ItemOptionLevelData levelData = new ItemOptionLevelData();
                    levelData.prob = WzUtil.getInt32(levelDataWZ.getNode("prop"), 0);
                    levelData.time = WzUtil.getInt32(levelDataWZ.getNode("time"), 0);
                    levelData.incSTR = WzUtil.getInt32(levelDataWZ.getNode("incSTR"), 0);
                    levelData.incDEX = WzUtil.getInt32(levelDataWZ.getNode("incDEX"), 0);
                    levelData.incINT = WzUtil.getInt32(levelDataWZ.getNode("incINT"), 0);
                    levelData.incLUK = WzUtil.getInt32(levelDataWZ.getNode("incLUK"), 0);
                    levelData.incHP = WzUtil.getInt32(levelDataWZ.getNode("HP"), 0);
                    levelData.incMP = WzUtil.getInt32(levelDataWZ.getNode("MP"), 0);
                    levelData.incMaxHP = WzUtil.getInt32(levelDataWZ.getNode("incMHP"), 0);
                    levelData.incMaxMP = WzUtil.getInt32(levelDataWZ.getNode("incMMP"), 0);
                    levelData.incACC = WzUtil.getInt32(levelDataWZ.getNode("incACC"), 0);
                    levelData.incEVA = WzUtil.getInt32(levelDataWZ.getNode("incEVA"), 0);
                    levelData.incSpeed = WzUtil.getInt32(levelDataWZ.getNode("incSpeed"), 0);
                    levelData.incJump = WzUtil.getInt32(levelDataWZ.getNode("incJump"), 0);
                    levelData.incPAD = WzUtil.getInt32(levelDataWZ.getNode("incPAD"), 0);
                    levelData.incMAD = WzUtil.getInt32(levelDataWZ.getNode("incMAD"), 0);
                    levelData.incPDD = WzUtil.getInt32(levelDataWZ.getNode("incPDD"), 0);
                    levelData.incMDD = WzUtil.getInt32(levelDataWZ.getNode("incMDD"), 0);
                    levelData.incSTRr = WzUtil.getInt32(levelDataWZ.getNode("incSTRr"), 0);
                    levelData.incDEXr = WzUtil.getInt32(levelDataWZ.getNode("incDEXr"), 0);
                    levelData.incINTr = WzUtil.getInt32(levelDataWZ.getNode("incINTr"), 0);
                    levelData.incLUKr = WzUtil.getInt32(levelDataWZ.getNode("incLUKr"), 0);
                    levelData.incMaxHPr = WzUtil.getInt32(levelDataWZ.getNode("incMHPr"), 0);
                    levelData.incMaxMPr = WzUtil.getInt32(levelDataWZ.getNode("incMMPr"), 0);
                    levelData.incACCr = WzUtil.getInt32(levelDataWZ.getNode("incACCr"), 0);
                    levelData.incEVAr = WzUtil.getInt32(levelDataWZ.getNode("incEVAr"), 0);
                    levelData.incPADr = WzUtil.getInt32(levelDataWZ.getNode("incPADr"), 0);
                    levelData.incMADr = WzUtil.getInt32(levelDataWZ.getNode("incMADr"), 0);
                    levelData.incPDDr = WzUtil.getInt32(levelDataWZ.getNode("incPDDr"), 0);
                    levelData.incMDDr = WzUtil.getInt32(levelDataWZ.getNode("incMDDr"), 0);
                    levelData.incCr = WzUtil.getInt32(levelDataWZ.getNode("incCr"), 0);
                    levelData.incCDr = WzUtil.getInt32(levelDataWZ.getNode("incCDr"), 0);
                    levelData.incMAMr = WzUtil.getInt32(levelDataWZ.getNode("incMAMr"), 0);
                    levelData.incSkill = WzUtil.getInt32(levelDataWZ.getNode("incSkill"), 0);
                    levelData.incAllSkill = WzUtil.getInt32(levelDataWZ.getNode("incAllskill"), 0);
                    levelData.recoveryHP = WzUtil.getInt32(levelDataWZ.getNode("RecoveryHP"), 0);
                    levelData.recoveryMP = WzUtil.getInt32(levelDataWZ.getNode("RecoveryMP"), 0);
                    levelData.recoveryUP = WzUtil.getInt32(levelDataWZ.getNode("RecoveryUP"), 0);
                    levelData.mpConReduce = WzUtil.getInt32(levelDataWZ.getNode("mpconReduce"), 0);
                    levelData.mpConRestore = WzUtil.getInt32(levelDataWZ.getNode("mpRestore"), 0);
                    levelData.ignoreTargetDEF = WzUtil.getInt32(levelDataWZ.getNode("ignoreTargetDEF"), 0);
                    levelData.ignoreDAM = WzUtil.getInt32(levelDataWZ.getNode("ignoreDAM"), 0);
                    levelData.ignoreDAMr = WzUtil.getInt32(levelDataWZ.getNode("ignoreDAMr"), 0);
                    levelData.incDAMr = WzUtil.getInt32(levelDataWZ.getNode("incDAMr"), 0);
                    levelData.damReflect = WzUtil.getInt32(levelDataWZ.getNode("DAMreflect"), 0);
                    levelData.attackType = WzUtil.getInt32(levelDataWZ.getNode("attackType"), 0);
                    levelData.incMesoProb = WzUtil.getInt32(levelDataWZ.getNode("incMesoProp"), 0);
                    levelData.incRewardProb = WzUtil.getInt32(levelDataWZ.getNode("incRewardProp"), 0);
                    levelData.level = WzUtil.getInt32(levelDataWZ.getNode("level"), 0);
                    levelData.boss = WzUtil.getInt32(levelDataWZ.getNode("boss"), 0);

                    String face = WzUtil.getString(levelDataWZ.getNode("face"), "");
                    if (face != null && !face.isEmpty()) {
                        levelData.emotion = true;
                        if (face.equalsIgnoreCase("angry")) {
                            levelData.emoAngry = true;
                        }
                        if (face.equalsIgnoreCase("cheers")) {
                            levelData.emoCheer = true;
                        }
                        if (face.equalsIgnoreCase("love")) {
                            levelData.emoLove = true;
                        }
                        if (face.equalsIgnoreCase("blaze")) {
                            levelData.emoBlaze = true;
                        }
                        if (face.equalsIgnoreCase("glitter")) {
                            levelData.emoGlitter = true;
                        }
                    }
                    optionInfo.levelData.set(i - 1, levelData);
                }
            }
            options.put(optionInfo.itemOptionID, optionInfo);
        }
        return true;
    }
}
