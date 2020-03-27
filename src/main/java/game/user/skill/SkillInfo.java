package game.user.skill;

import common.JobAccessor;
import common.Request;
import common.item.ItemAccessor;
import common.user.CharacterData;
import common.user.CharacterStat;
import game.field.life.mob.AttackElem;
import game.user.User;
import game.user.item.BundleItem;
import game.user.item.ItemInfo;
import game.user.skill.data.*;
import game.user.skill.entries.*;
import game.user.stat.SecondaryStat;
import game.user.stat.psd.AdditionPsd;
import util.Logger;
import util.Pointer;
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

/**
 * Created by MechAviv on 1/30/2020.
 */
public class SkillInfo {
    private static final SkillInfo instance = new SkillInfo();

    private Map<Integer, SkillEntry> skills;
    private Map<Integer, SkillRoot> skillRoots;
    private Map<Integer, MobSkillEntry> mobSkills;
    private Map<Integer, MCSkillEntry> mcSkills;
    private List<Integer> mcRandomSkills;
    private Map<Integer, MCGuardianEntry> mcGaurdians;
    private Map<Integer, BFSkillEntry> bfSkills;
    private Map<Integer, ItemSkillEntry> itemSkills;
    private Map<Integer, ItemOptionSkillEntry> itemOptionSkills;

    public SkillInfo() {
        this.skills = new HashMap<>();
        this.skillRoots = new HashMap<>();
        this.mobSkills = new HashMap<>();
        this.mcSkills = new HashMap<>();
        this.mcGaurdians = new HashMap<>();
        this.mcRandomSkills = new ArrayList<>();
        this.bfSkills = new HashMap<>();
        this.itemSkills = new HashMap<>();
        this.itemOptionSkills = new HashMap<>();
    }

    public static SkillInfo getInstance() {
        return instance;
    }

    public ItemOptionSkillEntry getItemOptionSkill(int skillID) {
        return itemOptionSkills.getOrDefault(skillID, null);
    }

    public ItemSkillEntry getItemSkill(int skillID) {
        return itemSkills.getOrDefault(skillID, null);
    }

    public MCGuardianEntry getMCGuardian(int skillID) {
        return mcGaurdians.getOrDefault(skillID, null);
    }

    public MCSkillEntry getMCSkill(int skillID) {
        return mcSkills.getOrDefault(skillID, null);
    }

    public MobSkillEntry getMobSkill(int skillID) {
        return mobSkills.getOrDefault(skillID, null);
    }

    public int getMobTossSkillID(CharacterData cd) {
        return 0;
    }

    public int getPureSkillLevel(CharacterData cd, int skillID, Pointer<SkillEntry> skillEntry) {
        return 0;
    }

    public int getShootSkillRange(CharacterData cd, int skillID, int wt) {
        return 0;
    }

    public SkillEntry getSkill(int skillID) {
        return skills.getOrDefault(skillID, null);
    }

    public int getBundleItemMaxPerSlot(int itemID, CharacterData cd) {
        int maxPerSlot = 0;
        BundleItem info = ItemInfo.getBundleItem(itemID);
        if (info != null) {
            maxPerSlot = info.getSlotMax();
            if (cd != null && ItemAccessor.isRechargeableItem(itemID)) {
                Pointer<SkillEntry> skill = new Pointer<>();
                int slv = getSkillLevel(cd, Skills.Assassin.JavelinMastery, skill);
                if (slv > 0) {
                    maxPerSlot += skill.get().getLevelData(slv).Y;
                }
            }
        }
        return maxPerSlot;
    }
    public int getSkillLevel(CharacterData cd, SecondaryStat ss, int skillID, Pointer<SkillEntry> skillEntry) {
        if (skillID > Skills.Citizen.VISITOR_MORPH_SKILL_SKILL || skillID < Skills.Citizen.VISITOR_MORPH_SKILL_NORMAL && (skillID < Skills.EvanJr.VISITOR_MORPH_SKILL_NORMAL || skillID > Skills.EvanJr.VISITOR_MORPH_SKILL_SKILL)) {
            return getSkillLevel(cd, skillID, skillEntry);
        } else if (skillID < Skills.Legend.VISITOR_MORPH_SKILL_NORMAL
                && (skillID > Skills.Noblesse.VISITOR_MORPH_SKILL_SKILL || skillID < Skills.Noblesse.VISITOR_MORPH_SKILL_NORMAL
                && (skillID < Skills.Beginner.VISITOR_MORPH_SKILL_NORMAL || skillID > Skills.Beginner.VISITOR_MORPH_SKILL_SKILL))) {
            return getSkillLevel(cd, skillID, skillEntry);
        }
        if (skillEntry != null) {
            skillEntry.set(getSkill(skillID));
        }
        int ssMorph = 0;
        switch (ssMorph) {
            case 65:
                return 2;
            case 66:
                return 3;
            case 67:
                return 4;
        }
        return 1;
    }

    public int getSkillLevel(CharacterData cd, int skillID) {
        Integer level = cd.getSkillRecord().get(skillID);
        if (level == null) {
            return 0;
        }
        return level;
    }

    public int getSkillLevel(CharacterData cd, int skillID, Pointer<SkillEntry> skillEntry) {
        switch (skillID) {
            case Skills.Beginner.BAMBOO:
            case Skills.Beginner.INVINCIBLE:
            case Skills.Beginner.BERSERK:
            case Skills.Beginner.MASSACRE:
            case Skills.Beginner.VISITOR_MORPH_SKILL_NORMAL:
            case Skills.Beginner.VISITOR_MORPH_SKILL_SKILL:
            case Skills.Noblesse.BAMBOO:
            case Skills.Noblesse.INVINCIBLE:
            case Skills.Noblesse.BERSERK:
            case Skills.Noblesse.MASSACRE:
            case Skills.Noblesse.VISITOR_MORPH_SKILL_NORMAL:
            case Skills.Noblesse.VISITOR_MORPH_SKILL_SKILL:
            case Skills.Legend.BAMBOO:
            case Skills.Legend.INVINCIBLE:
            case Skills.Legend.BERSERK:
            case Skills.Legend.MASSACRE:
            case Skills.Legend.VISITOR_MORPH_SKILL_NORMAL:
            case Skills.Legend.VISITOR_MORPH_SKILL_SKILL:
            case Skills.EvanJr.BAMBOO:
            case Skills.EvanJr.INVINCIBLE:
            case Skills.EvanJr.BERSERK:
            case Skills.EvanJr.MASSACRE:
            case Skills.EvanJr.VISITOR_MORPH_SKILL_NORMAL:
            case Skills.EvanJr.VISITOR_MORPH_SKILL_SKILL:
            case Skills.Citizen.BAMBOO:
            case Skills.Citizen.INVINCIBLE:
            case Skills.Citizen.BERSERK:
            case Skills.Citizen.MASSACRE:
            case Skills.Citizen.VISITOR_MORPH_SKILL_NORMAL:
            case Skills.Citizen.VISITOR_MORPH_SKILL_SKILL:
                if (skillEntry != null) {
                    skillEntry.set(getSkill(skillID));
                }
                return 1;
            case Skills.Dual3.HUSTLE_RUSH:
                skillID = Skills.Dual3.HUSTLE_DASH;
                break;
            case Skills.WildHunter.MINE_DUMMY_SUMMONED:
                skillID = Skills.WildHunter.MINE;
                break;
            case Skills.WildHunter.SWALLOW_DUMMY_ATTACK:
            case Skills.WildHunter.SWALLOW_DUMMY_BUFF:
                skillID = Skills.WildHunter.SWALLOW;
                break;
            case Skills.Mechanic.FLAMETHROWER_UP:
                skillID = Skills.Mechanic.FLAMETHROWER;
                break;
            case Skills.Mechanic.GATLING_UP:
                skillID = Skills.Mechanic.GATLING;
                break;
            case Skills.Mechanic.ROBOROBO_DUMMY:
                skillID = Skills.Mechanic.ROBOROBO;
                break;
            case Skills.Mechanic.SIEGE2_SPECIAL:
                skillID = Skills.Mechanic.SIEGE1;
                break;
        }

        SkillEntry skill = getSkill(skillID);
        if (skillEntry != null) {
            skillEntry.set(skill);
        }
        if (skill == null) {
            return 0;
        }
        int slv = cd.getSkillRecord().getOrDefault(skillID, 0);
        if (slv == 0) {
            return 0;
        }
        // slv + skill bonus
        int maxLevel = skill.getMaxLevel();
        if (skill.isCombatOrders()) {
            maxLevel += cd.getCombatOrders();
        }
        if (slv < maxLevel) {
            maxLevel = slv;
        }
        return maxLevel <= 0 ? 0 : maxLevel;
    }

    public SkillRoot getSkillRoot(int skillRootID) {
        return skillRoots.getOrDefault(skillRootID, null);
    }

    public SkillRoot getSkillRootVisible(int skillRootID, CharacterData cd) {
        SkillRoot root = getSkillRoot(skillRootID);

        SkillRoot skillRootVisible = new SkillRoot();
        skillRootVisible.setBookName(root.getBookName());

        skillRootVisible.getSkills().clear();
        for (SkillEntry skill : root.getSkills()) {
            if (isSkillVisible(cd, skill.getSkillID(), null)) {
                skillRootVisible.getSkills().add(skill);
            }
        }
        return skillRootVisible;
    }

    public boolean isMobChaseAttack(SkillEntry skill) {
        if (skill != null) {
            int skillID = skill.getSkillID();
            if (skillID == Skills.Rogue.Disorder || skillID == Skills.Captain.MIND_CONTROL || skillID == Skills.NightWalker.DISORDER) {
                return false;
            }
        }
        return true;
    }

    public boolean isSkillVisible(CharacterData cd, int skillID, SkillEntry skillEntry) {
        if (skillEntry == null) {
            skillEntry = getSkill(skillID);
        }
        if (skillEntry != null) {
            if (skillID != Skills.Dual3.HUSTLE_RUSH && skillID != Skills.Beginner.DISABLE_EVENT_RIDING_DASH && skillID != Skills.Noblesse.DISABLE_EVENT_RIDING_DASH) {
                if (!JobAccessor.isDualJobBorn(cd.getCharacterStat().getJob(), cd.getCharacterStat().getSubJob()) || skillID != Skills.Rogue.KeenEyes && skillID != Skills.Rogue.LuckySeven) {
                    int slv = cd.getSkillRecord().getOrDefault(skillID, 0);
                    if ((cd.getSkillRecord().containsKey(skillID) || !skillEntry.isInvisible()) && (!skillEntry.isTimeLimited() || slv != 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean adjustConsumeForActiveSkill(User user, int skillID, byte slv) {
        if (slv <= 0) {
            return false;
        }
        if ((skillID / 1000 % 10) == 0) {
            return true;
        }
        Pointer<SkillEntry> curSkillEntry = new Pointer<>();
        if (getSkillLevel(user.getCharacter(), skillID, curSkillEntry) < slv) {
            return false;
        }
        SkillEntry skillEntry = curSkillEntry.get();
        SkillLevelData level = skillEntry.getLevelData(slv);
        int hp = level.HPCon;
        int mp = level.MPCon;
        if (user.lock()) {
            try {
                int flag = 0;
                if (hp > 0) {
                    flag = CharacterStat.CharacterStatType.HP;
                }
                if (mp > 0) {
                    flag |= CharacterStat.CharacterStatType.MP;
                }
                if (user.getHP() == 0 || hp > 0 && hp >= user.getHP() || mp > 0 && mp > user.getCharacter().getCharacterStat().getMP()) {
                    if (flag != 0)
                        user.sendCharacterStat(Request.None, flag);
                    return false;
                }
                if (hp > 0) {
                    user.incHP(-hp, true);
                }
                if (mp > 0) {
                    user.incMP(-mp, true);
                }
                if (flag != 0) {
                    user.sendCharacterStat(Request.None, flag);
                }
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }

    public boolean iterateSkillInfo() {
        WzProperty stringProp = new WzFileSystem().init("String").getPackage().getItem("Skill.img");
        if (stringProp == null) {
            Logger.logError("Null String properties.");
            return false;
        }
        WzPackage skillDir = new WzFileSystem().init("Skill").getPackage();
        if (skillDir != null) {
            for (WzProperty root : skillDir.getEntries().values()) {
                String rootName = root.getNodeName();
                if (rootName.contains("Mob") || rootName.contains("MC") || rootName.contains("Ite") || rootName.contains("Att") || rootName.contains("BF")) {
                    continue;
                }
                int skillRootID = Integer.parseInt(rootName.replaceAll(".img", ""));
                if (!loadSkillRoot(skillRootID, root, stringProp)) {
                    skillDir.release();
                    skillDir = null;
                    Logger.logError("Unable to load skill root.");
                    return false;
                }
            }
            skillDir.release();
            stringProp.release();
        }
        skillDir = null;
        stringProp = null;
        return true;
    }

    // CSkillInfo::LoadCharLevelData (needed ?)

    public void loadFinalAttack(List<List<Integer>> finalAttacks, WzProperty p1) {
        if (p1 == null) {
            return;
        }
    }

    public boolean loadItemOptionSkill() {
        return true;
    }

    public boolean loadItemOptionSkillLevelData(int skillID, ItemOptionSkillLevelData[] levelData, WzProperty p) {
        return true;
    }

    public boolean loadItemSkill() {
        return true;
    }

    public boolean loadItemSkillLevelData(int skillID, ItemSkillLevelData[] levelData, WzProperty p) {
        return true;
    }

    public boolean loadLevelDataCommon(int skillID, WzProperty common, SkillLevelDataCommon commonData, Pointer<Integer> maxLevel) {
        if (common == null) {
            return false;
        }
        maxLevel.set(WzUtil.getInt32(common.getNode("maxLevel"), 0));
        commonData.HP = WzUtil.getString(common.getNode("hp"), null);
        commonData.MP = WzUtil.getString(common.getNode("mp"), null);
        commonData.PAD = WzUtil.getString(common.getNode("pad"), null);
        commonData.PDD = WzUtil.getString(common.getNode("pdd"), null);
        commonData.MAD = WzUtil.getString(common.getNode("mad"), null);
        commonData.MDD = WzUtil.getString(common.getNode("mdd"), null);
        commonData.EMHP = WzUtil.getString(common.getNode("emhp"), null);
        commonData.EMMP = WzUtil.getString(common.getNode("emmp"), null);
        commonData.EPAD = WzUtil.getString(common.getNode("epad"), null);
        commonData.EPDD = WzUtil.getString(common.getNode("epdd"), null);
        commonData.EMDD = WzUtil.getString(common.getNode("emdd"), null);
        commonData.ACC = WzUtil.getString(common.getNode("acc"), null);
        commonData.EVA = WzUtil.getString(common.getNode("eva"), null);
        commonData.Speed = WzUtil.getString(common.getNode("speed"), null);
        commonData.Jump = WzUtil.getString(common.getNode("jump"), null);
        commonData.Morph = WzUtil.getString(common.getNode("morph"), null);
        commonData.HPCon = WzUtil.getString(common.getNode("hpCon"), null);
        commonData.MPCon = WzUtil.getString(common.getNode("mpCon"), null);
        commonData.MoneyCon = WzUtil.getString(common.getNode("moneyCon"), null);
        commonData.ItemCon = WzUtil.getString(common.getNode("itemCon"), null);
        commonData.ItemConNo = WzUtil.getString(common.getNode("itemConNo"), null);
        commonData.Damage = WzUtil.getString(common.getNode("damage"), null);
        commonData.FixDamage = WzUtil.getString(common.getNode("fixdamage"), null);
        commonData.SelfDestruction = WzUtil.getString(common.getNode("selfDestruction"), null);
        commonData.Time = WzUtil.getString(common.getNode("time"), null);
        commonData.SubTime = WzUtil.getString(common.getNode("subTime"), null);
        commonData.Prop = WzUtil.getString(common.getNode("prop"), null);
        commonData.SubProp = WzUtil.getString(common.getNode("subProp"), null);
        commonData.Range = WzUtil.getString(common.getNode("range"), null);
        commonData.MobCount = WzUtil.getString(common.getNode("mobCount"), null);
        commonData.AttackCount = WzUtil.getString(common.getNode("attackCount"), null);
        commonData.BulletCount = WzUtil.getString(common.getNode("bulletCount"), null);
        commonData.BulletConsume = WzUtil.getString(common.getNode("bulletConsume"), null);
        commonData.Mastery = WzUtil.getString(common.getNode("mastery"), null);
        commonData.X = WzUtil.getString(common.getNode("x"), null);
        commonData.Y = WzUtil.getString(common.getNode("y"), null);
        commonData.Z = WzUtil.getString(common.getNode("z"), null);
        commonData.Cooltime = WzUtil.getString(common.getNode("cooltime"), null);
        commonData.Action = WzUtil.getString(common.getNode("action"), null);
        commonData.MHPr = WzUtil.getString(common.getNode("mhpR"), null);
        commonData.MMPr = WzUtil.getString(common.getNode("mmpR"), null);
        commonData.Cr = WzUtil.getString(common.getNode("cr"), null);
        commonData.CDMin = WzUtil.getString(common.getNode("criticaldamageMin"), null);
        commonData.CDMax = WzUtil.getString(common.getNode("criticaldamageMax"), null);
        commonData.ACCr = WzUtil.getString(common.getNode("accR"), null);
        commonData.EVAr = WzUtil.getString(common.getNode("evaR"), null);
        commonData.Ar = WzUtil.getString(common.getNode("ar"), null);
        commonData.Er = WzUtil.getString(common.getNode("er"), null);
        commonData.PDDr = WzUtil.getString(common.getNode("pddR"), null);
        commonData.MDDr = WzUtil.getString(common.getNode("mddR"), null);
        commonData.PDr = WzUtil.getString(common.getNode("pdr"), null);
        commonData.MDr = WzUtil.getString(common.getNode("mdr"), null);
        commonData.DIPr = WzUtil.getString(common.getNode("damR"), null);
        commonData.PDamr = WzUtil.getString(common.getNode("pdR"), null);
        commonData.MDamr = WzUtil.getString(common.getNode("mdR"), null);
        commonData.PADr = WzUtil.getString(common.getNode("padR"), null);
        commonData.MADr = WzUtil.getString(common.getNode("madR"), null);
        commonData.EXPr = WzUtil.getString(common.getNode("expR"), null);
        commonData.Dot = WzUtil.getString(common.getNode("dot"), null);
        commonData.DotInterval = WzUtil.getString(common.getNode("dotInterval"), null);
        commonData.DotTime = WzUtil.getString(common.getNode("dotTime"), null);
        commonData.IMPr = WzUtil.getString(common.getNode("ignoreMobpdpR"), null);
        commonData.ASRr = WzUtil.getString(common.getNode("asrR"), null);
        commonData.TERr = WzUtil.getString(common.getNode("terR"), null);
        commonData.MESOr = WzUtil.getString(common.getNode("mesoR"), null);
        commonData.PADx = WzUtil.getString(common.getNode("padX"), null);
        commonData.MADx = WzUtil.getString(common.getNode("madX"), null);
        commonData.IMDr = WzUtil.getString(common.getNode("ignoreMobDamR"), null);
        commonData.PsdJump = WzUtil.getString(common.getNode("psdJump"), null);
        commonData.PsdSpeed = WzUtil.getString(common.getNode("psdSpeed"), null);
        commonData.OCr = WzUtil.getString(common.getNode("overChargeR"), null);
        commonData.DCr = WzUtil.getString(common.getNode("disCountR"), null);
        commonData.ReqGL = WzUtil.getString(common.getNode("reqGuildLevel"), null);
        commonData.Price = WzUtil.getString(common.getNode("price"), null);
        commonData.S = WzUtil.getString(common.getNode("s"), null);
        commonData.U = WzUtil.getString(common.getNode("u"), null);
        commonData.V = WzUtil.getString(common.getNode("v"), null);
        commonData.W = WzUtil.getString(common.getNode("w"), null);
        commonData.T = WzUtil.getString(common.getNode("t"), null);
        Point lt = WzUtil.getPoint(common.getNode("lt"), null);
        Point rb = WzUtil.getPoint(common.getNode("rb"), null);
        if (lt != null || rb != null) {
            commonData.affectedArea = new Rect(lt.x, lt.y, rb.x, rb.y);
        }
        return true;
    }

    public boolean loadMCGuardian() {
        return true;
    }

    public boolean loadMCSkill() {
        return true;
    }

    public boolean loadMobSkill() {
        return true;
    }

    public boolean loadMobSkillLevelData(int skillID, MobSkillLevelData[] levelData, WzProperty p) {
        return true;
    }

    public void loadReqSkill(List<SkillRecord> records, WzProperty p) {
        if (p == null) {
            return;
        }
        for (WzProperty skill : p.getChildNodes()) {
            int skillid = Integer.parseInt(skill.getNodeName());
            int level = WzUtil.getInt32(skill, 0);
            if (level <= 0)
                continue;
            records.add(new SkillRecord(skillid, level));
        }
    }

    public SkillEntry loadSkill(int skillID, WzProperty skill, WzProperty strSR) {
        String strID = String.format("%07d", skillID);

        SkillEntry entry = new SkillEntry();
        entry.setSkillID(skillID);

        WzProperty skillStrProp = strSR.getNode(strID);
        if (skillStrProp != null) {
            entry.setName(WzUtil.getString(skillStrProp.getNode("name"), null));
            entry.setDescription(WzUtil.getString(skillStrProp.getNode("desc"), null));
        }
        entry.setSkillType(WzUtil.getInt32(skill.getNode("skillType"), SkillType.NONE));
        entry.setPsdSkill(WzUtil.getInt32(skill.getNode("psd"), 0));
        entry.setWeapon(WzUtil.getInt32(skill.getNode("weapon"), 0));
        entry.setSubWeapon(WzUtil.getInt32(skill.getNode("subWeapon"), 0));
        entry.setInvisible(WzUtil.getInt32(skill.getNode("invisible"), 0) != 0);
        entry.setUpButtonDisabled(WzUtil.getInt32(skill.getNode("disable"), 0) != 0);
        entry.setDefaultMasterLev(WzUtil.getInt32(skill.getNode("masterLevel"), 0));
        entry.setCombatOrders(WzUtil.getInt32(skill.getNode("combatOrders"), 0) != 0);
        entry.setTimeLimited(WzUtil.getInt32(skill.getNode("timeLimited"), 0) != 0);
        entry.setMobCode(WzUtil.getInt32(skill.getNode("mobCode"), 0));
        // additional psd skill stuff (should read more about it)
        // action & special action codes
        String elemAttr = WzUtil.getString(skill.getNode("elemAttr"), null);
        entry.setAttackElemAttr(AttackElem.getElementAttribute(elemAttr == null || elemAttr.isEmpty() ? '\0' : elemAttr.charAt(0)));
        // prepare stuff
        List<List<Integer>> finalAttacks = entry.getFinalAttacks();
        loadFinalAttack(finalAttacks, skill.getNode("finalAttack"));
        entry.setFinalAttacks(finalAttacks);

        List<SkillRecord> reqs = entry.getReqSkills();
        loadReqSkill(reqs, skill.getNode("req"));
        entry.setReqSkills(reqs);

        //             <imgdir name="psdSkill">
        //                <imgdir name="1311001" />
        //                <imgdir name="1311003" />
        //                <imgdir name="1311005" />
        //                <imgdir name="1311006" />
        //            </imgdir>
        WzProperty psdSkillData = skill.getNode("psdSkill");
        if (psdSkillData != null) {
            for (WzProperty psdSkill : psdSkillData.getChildNodes()) {
                AdditionPsd apsd = new AdditionPsd();
                apsd.setAr(WzUtil.getInt32(psdSkill.getNode("ar"), 0));
                apsd.setCr(WzUtil.getInt32(psdSkill.getNode("cr"), 0));
                apsd.setCDMin(WzUtil.getInt32(psdSkill.getNode("criticaldamageMin"), 0));
                apsd.setDIPr(WzUtil.getInt32(psdSkill.getNode("damR"), 0));
                apsd.setPDamr(WzUtil.getInt32(psdSkill.getNode("pdR"), 0));
                apsd.setMDamr(WzUtil.getInt32(psdSkill.getNode("mdR"), 0));
                apsd.setIMPr(WzUtil.getInt32(psdSkill.getNode("ignoreMobpdpR"), 0));
                entry.getAdditionPsdOffset().put(Integer.valueOf(psdSkill.getNodeName()), apsd);
            }
        }
        SkillLevelDataCommon common = new SkillLevelDataCommon();
        Pointer<Integer> maxLevel = new Pointer<>(0);
        loadLevelDataCommon(skillID, skill.getNode("common"), common, maxLevel);
        entry.setCommon(common);
        entry.setMaxLevel(maxLevel.get());
        // modify desc for max level stuff {not really important}
        entry.setLevelDataProp(skill.getNode("level"));
        return entry;
    }

    public boolean loadSkillRoot(int skillRootID, WzProperty skillRoot, WzProperty strSR) {
        SkillRoot root = new SkillRoot();
        root.setBookName(WzUtil.getString(strSR.getNode(String.format("%03d", skillRootID)).getNode("bookName"), null));
        root.setSkillRootID(skillRootID);

        List<SkillEntry> skillList = new ArrayList<>();
        for (WzProperty skill : skillRoot.getNode("skill").getChildNodes()) {
            int skillID = Integer.parseInt(skill.getNodeName());
            SkillEntry skillEntry = loadSkill(skillID, skill, strSR);
            skillList.add(skillEntry);
            skills.put(skillID, skillEntry);
        }
        root.setSkills(skillList);

        skillRoots.put(skillRootID, root);
        return true;
    }
}
