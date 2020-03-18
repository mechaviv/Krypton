package game.user.skill.data;

import util.FileTime;
import util.Logger;
import util.Rect;
import util.Utilities;
import util.wz.WzFileSystem;
import util.wz.WzProperty;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class SkillLevelData {
    public String help;
    public int HP;
    public int MP;
    public int PAD;
    public int PDD;
    public int MAD;
    public int MDD;
    public int ACC;
    public int EVA;
    public int Craft;
    public int Speed;
    public int Jump;
    public int Morph;
    public int HPCon;
    public int MPCon;
    public int MoneyCon;
    public int ItemCon;
    public int ItemConNo;
    public int Damage;
    public int FixDamage;
    public int SelfDestruction;
    public int Time;
    public int SubTime;
    public int Prop;
    public int SubProp;
    public int AttackCount;
    public int BulletCount;
    public int BulletConsume;
    public int Mastery;
    public int MobCount;
    public int X;
    public int Y;
    public int Z;
    public int Action;
    public int EMHP;
    public int EMMP;
    public int EPAD;
    public int EPDD;
    public int EMDD;
    public Rect affectedArea;
    public int Range;
    public int Cooltime;
    public int MHPr;
    public int MMPr;
    public int Cr;
    public int CDMin;
    public int CDMax;
    public int ACCr;
    public int EVAr;
    public int Ar;
    public int Er;
    public int PDDr;
    public int MDDr;
    public int PDr;
    public int MDr;
    public int DIPr;
    public int PDamr;
    public int MDamr;
    public int PADr;
    public int MADr;
    public int EXPr;
    public int Dot;
    public short DotInterval;
    public short DotTime;
    public int IMPr;
    public int ASRr;
    public int TERr;
    public int MESOr;
    public int PADx;
    public int MADx;
    public int IMDr;
    public int PsdJump;
    public int PsdSpeed;
    public int OCr;
    public int DCr;
    public int ReqGL;
    public int Price;
    public int CRC;
    public int S;
    public int U;
    public int V;
    public int W;
    public float T;
    FileTime dateExpire;
    public boolean loaded;
    //int bCalcCRC;

    public SkillLevelData() {
        loaded = false;
        affectedArea = new Rect();
        dateExpire = FileTime.DATE_2079;
    }

    public void loadLevelData(int skillID, WzProperty levelData, SkillLevelDataCommon levelCommon, int level, WzProperty strSR) {
        String strID = String.format("%07d", skillID);
        if (strSR == null) {
            strSR = new WzFileSystem().init("String").getPackage().getItem("Skill.img");
            if (strSR == null) {
                Logger.logError("String property is null");
            }
        }
        // calc helper
        // calc action
        this.HP = getParsedCommonData(levelCommon.HP, skillID, level);
        this.MP = getParsedCommonData(levelCommon.MP, skillID, level);
        this.PAD = getParsedCommonData(levelCommon.PAD, skillID, level);
        this.PDD = getParsedCommonData(levelCommon.PDD, skillID, level);
        this.MAD = getParsedCommonData(levelCommon.MAD, skillID, level);
        this.MDD = getParsedCommonData(levelCommon.MDD, skillID, level);
        this.EMHP = getParsedCommonData(levelCommon.EMHP, skillID, level);
        this.EMMP = getParsedCommonData(levelCommon.EMMP, skillID, level);
        this.EPAD = getParsedCommonData(levelCommon.EPAD, skillID, level);
        this.EPDD = getParsedCommonData(levelCommon.EPDD, skillID, level);
        this.EMDD = getParsedCommonData(levelCommon.EMDD, skillID, level);
        this.ACC = getParsedCommonData(levelCommon.ACC, skillID, level);
        this.EVA = getParsedCommonData(levelCommon.EVA, skillID, level);
        this.Craft = getParsedCommonData(levelCommon.Craft, skillID, level);
        this.Speed = getParsedCommonData(levelCommon.Speed, skillID, level);
        this.Jump = getParsedCommonData(levelCommon.Jump, skillID, level);
        this.Morph = getParsedCommonData(levelCommon.Morph, skillID, level);
        this.HPCon = getParsedCommonData(levelCommon.HPCon, skillID, level);
        this.MPCon = getParsedCommonData(levelCommon.MPCon, skillID, level);
        this.MoneyCon = getParsedCommonData(levelCommon.MoneyCon, skillID, level);
        this.ItemCon = getParsedCommonData(levelCommon.ItemCon, skillID, level);
        this.ItemConNo = getParsedCommonData(levelCommon.ItemConNo, skillID, level);
        this.Damage = getParsedCommonData(levelCommon.Damage, skillID, level);
        this.FixDamage = getParsedCommonData(levelCommon.FixDamage, skillID, level);
        this.SelfDestruction = getParsedCommonData(levelCommon.SelfDestruction, skillID, level);
        this.Time = getParsedCommonData(levelCommon.Time, skillID, level);
        this.SubTime = getParsedCommonData(levelCommon.SubTime, skillID, level);
        this.Prop = getParsedCommonData(levelCommon.Prop, skillID, level);
        this.SubProp = getParsedCommonData(levelCommon.SubProp, skillID, level);
        this.Range = getParsedCommonData(levelCommon.Range, skillID, level);
        this.MobCount = getParsedCommonData(levelCommon.MobCount, skillID, level);
        this.AttackCount = getParsedCommonData(levelCommon.AttackCount, skillID, level);
        this.BulletCount = getParsedCommonData(levelCommon.BulletCount, skillID, level);
        this.BulletConsume = getParsedCommonData(levelCommon.BulletConsume, skillID, level);

        this.Mastery = getParsedCommonData(levelCommon.Mastery, skillID, level);// MIN 0 MAX 100
        if (this.Mastery <= 0) this.Mastery = 0;
        if (this.Mastery >= 100) this.Mastery = 100;

        this.X = getParsedCommonData(levelCommon.X, skillID, level);
        this.Y = getParsedCommonData(levelCommon.Y, skillID, level);
        this.Z = getParsedCommonData(levelCommon.Z, skillID, level);
        this.Cooltime = getParsedCommonData(levelCommon.Cooltime, skillID, level);
        this.Action = getParsedCommonData(levelCommon.Action, skillID, level);
        this.MHPr = getParsedCommonData(levelCommon.MHPr, skillID, level);
        this.MMPr = getParsedCommonData(levelCommon.MMPr, skillID, level);
        this.Cr = getParsedCommonData(levelCommon.Cr, skillID, level);
        this.CDMin = getParsedCommonData(levelCommon.CDMin, skillID, level);
        this.CDMax = getParsedCommonData(levelCommon.CDMax, skillID, level);
        this.ACCr = getParsedCommonData(levelCommon.ACCr, skillID, level);
        this.EVAr = getParsedCommonData(levelCommon.EVAr, skillID, level);
        this.Er = getParsedCommonData(levelCommon.Er, skillID, level);
        this.PDDr = getParsedCommonData(levelCommon.PDDr, skillID, level);
        this.MDDr = getParsedCommonData(levelCommon.MDDr, skillID, level);
        this.PDr = getParsedCommonData(levelCommon.PDr, skillID, level);
        this.MDr = getParsedCommonData(levelCommon.MDr, skillID, level);
        this.DIPr = getParsedCommonData(levelCommon.DIPr, skillID, level);
        this.PDamr = getParsedCommonData(levelCommon.PDamr, skillID, level);
        this.MDamr = getParsedCommonData(levelCommon.MDamr, skillID, level);
        this.PADr = getParsedCommonData(levelCommon.PADr, skillID, level);
        this.MADr = getParsedCommonData(levelCommon.MADr, skillID, level);
        this.EXPr = getParsedCommonData(levelCommon.EXPr, skillID, level);
        this.Dot = getParsedCommonData(levelCommon.Dot, skillID, level);
        this.DotInterval = (short) getParsedCommonData(levelCommon.DotInterval, skillID, level);
        this.DotTime = (short) getParsedCommonData(levelCommon.DotTime, skillID, level);
        this.IMPr = getParsedCommonData(levelCommon.IMPr, skillID, level);
        this.ASRr = getParsedCommonData(levelCommon.ASRr, skillID, level);
        this.TERr = getParsedCommonData(levelCommon.TERr, skillID, level);
        this.MESOr = getParsedCommonData(levelCommon.MESOr, skillID, level);
        this.PADx = getParsedCommonData(levelCommon.PADx, skillID, level);
        this.MADx = getParsedCommonData(levelCommon.MADx, skillID, level);
        this.IMDr = getParsedCommonData(levelCommon.IMDr, skillID, level);
        this.PsdJump = getParsedCommonData(levelCommon.PsdJump, skillID, level);
        this.PsdSpeed = getParsedCommonData(levelCommon.PsdSpeed, skillID, level);
        this.OCr = getParsedCommonData(levelCommon.OCr, skillID, level);
        this.DCr = getParsedCommonData(levelCommon.DCr, skillID, level);
        this.ReqGL = getParsedCommonData(levelCommon.ReqGL, skillID, level);
        this.Price = getParsedCommonData(levelCommon.Price, skillID, level);
        this.affectedArea = levelCommon.affectedArea;
        this.S = getParsedCommonData(levelCommon.S, skillID, level);
        this.U = getParsedCommonData(levelCommon.U, skillID, level);
        this.V = getParsedCommonData(levelCommon.V, skillID, level);
        this.W = getParsedCommonData(levelCommon.W, skillID, level);
        this.T = getParsedCommonDataFloat(levelCommon.T, skillID, level);
        this.loaded = true;
    }

    public void loadLevelDataByCommon(int skillID, SkillLevelDataCommon levelCommon, int level, WzProperty strSR) {
        String strID = String.format("%07d", skillID);
        if (strSR == null) {
            strSR = new WzFileSystem().init("String").getPackage().getItem("Skill.img");
            if (strSR == null) {
                Logger.logError("String property is null");
            }
        }
        this.HP = getParsedCommonData(levelCommon.HP, skillID, level);
        this.MP = getParsedCommonData(levelCommon.MP, skillID, level);
        this.PAD = getParsedCommonData(levelCommon.PAD, skillID, level);
        this.PDD = getParsedCommonData(levelCommon.PDD, skillID, level);
        this.MAD = getParsedCommonData(levelCommon.MAD, skillID, level);
        this.MDD = getParsedCommonData(levelCommon.MDD, skillID, level);
        this.EMHP = getParsedCommonData(levelCommon.EMHP, skillID, level);
        this.EMMP = getParsedCommonData(levelCommon.EMMP, skillID, level);
        this.EPAD = getParsedCommonData(levelCommon.EPAD, skillID, level);
        this.EPDD = getParsedCommonData(levelCommon.EPDD, skillID, level);
        this.EMDD = getParsedCommonData(levelCommon.EMDD, skillID, level);
        this.ACC = getParsedCommonData(levelCommon.ACC, skillID, level);
        this.EVA = getParsedCommonData(levelCommon.EVA, skillID, level);
        this.Craft = getParsedCommonData(levelCommon.Craft, skillID, level);
        this.Speed = getParsedCommonData(levelCommon.Speed, skillID, level);
        this.Jump = getParsedCommonData(levelCommon.Jump, skillID, level);
        this.Morph = getParsedCommonData(levelCommon.Morph, skillID, level);
        this.HPCon = getParsedCommonData(levelCommon.HPCon, skillID, level);
        this.MPCon = getParsedCommonData(levelCommon.MPCon, skillID, level);
        this.MoneyCon = getParsedCommonData(levelCommon.MoneyCon, skillID, level);
        this.ItemCon = getParsedCommonData(levelCommon.ItemCon, skillID, level);
        this.ItemConNo = getParsedCommonData(levelCommon.ItemConNo, skillID, level);
        this.Damage = getParsedCommonData(levelCommon.Damage, skillID, level);
        this.FixDamage = getParsedCommonData(levelCommon.FixDamage, skillID, level);
        this.SelfDestruction = getParsedCommonData(levelCommon.SelfDestruction, skillID, level);
        this.Time = getParsedCommonData(levelCommon.Time, skillID, level);
        this.SubTime = getParsedCommonData(levelCommon.SubTime, skillID, level);
        this.Prop = getParsedCommonData(levelCommon.Prop, skillID, level);
        this.SubProp = getParsedCommonData(levelCommon.SubProp, skillID, level);
        this.Range = getParsedCommonData(levelCommon.Range, skillID, level);
        this.MobCount = getParsedCommonData(levelCommon.MobCount, skillID, level);
        this.AttackCount = getParsedCommonData(levelCommon.AttackCount, skillID, level);
        this.BulletCount = getParsedCommonData(levelCommon.BulletCount, skillID, level);
        this.BulletConsume = getParsedCommonData(levelCommon.BulletConsume, skillID, level);

        this.Mastery = getParsedCommonData(levelCommon.Mastery, skillID, level);// MIN 0 MAX 100
        if (this.Mastery <= 0) this.Mastery = 0;
        if (this.Mastery >= 100) this.Mastery = 100;

        this.X = getParsedCommonData(levelCommon.X, skillID, level);
        this.Y = getParsedCommonData(levelCommon.Y, skillID, level);
        this.Z = getParsedCommonData(levelCommon.Z, skillID, level);
        this.Cooltime = getParsedCommonData(levelCommon.Cooltime, skillID, level);
        this.Action = getParsedCommonData(levelCommon.Action, skillID, level);
        this.MHPr = getParsedCommonData(levelCommon.MHPr, skillID, level);
        this.MMPr = getParsedCommonData(levelCommon.MMPr, skillID, level);
        this.Cr = getParsedCommonData(levelCommon.Cr, skillID, level);
        this.CDMin = getParsedCommonData(levelCommon.CDMin, skillID, level);
        this.CDMax = getParsedCommonData(levelCommon.CDMax, skillID, level);
        this.ACCr = getParsedCommonData(levelCommon.ACCr, skillID, level);
        this.EVAr = getParsedCommonData(levelCommon.EVAr, skillID, level);
        this.Er = getParsedCommonData(levelCommon.Er, skillID, level);
        this.PDDr = getParsedCommonData(levelCommon.PDDr, skillID, level);
        this.MDDr = getParsedCommonData(levelCommon.MDDr, skillID, level);
        this.PDr = getParsedCommonData(levelCommon.PDr, skillID, level);
        this.MDr = getParsedCommonData(levelCommon.MDr, skillID, level);
        this.DIPr = getParsedCommonData(levelCommon.DIPr, skillID, level);
        this.PDamr = getParsedCommonData(levelCommon.PDamr, skillID, level);
        this.MDamr = getParsedCommonData(levelCommon.MDamr, skillID, level);
        this.PADr = getParsedCommonData(levelCommon.PADr, skillID, level);
        this.MADr = getParsedCommonData(levelCommon.MADr, skillID, level);
        this.EXPr = getParsedCommonData(levelCommon.EXPr, skillID, level);
        this.Dot = getParsedCommonData(levelCommon.Dot, skillID, level);
        this.DotInterval = (short) getParsedCommonData(levelCommon.DotInterval, skillID, level);
        this.DotTime = (short) getParsedCommonData(levelCommon.DotTime, skillID, level);
        this.IMPr = getParsedCommonData(levelCommon.IMPr, skillID, level);
        this.ASRr = getParsedCommonData(levelCommon.ASRr, skillID, level);
        this.TERr = getParsedCommonData(levelCommon.TERr, skillID, level);
        this.MESOr = getParsedCommonData(levelCommon.MESOr, skillID, level);
        this.PADx = getParsedCommonData(levelCommon.PADx, skillID, level);
        this.MADx = getParsedCommonData(levelCommon.MADx, skillID, level);
        this.IMDr = getParsedCommonData(levelCommon.IMDr, skillID, level);
        this.PsdJump = getParsedCommonData(levelCommon.PsdJump, skillID, level);
        this.PsdSpeed = getParsedCommonData(levelCommon.PsdSpeed, skillID, level);
        this.OCr = getParsedCommonData(levelCommon.OCr, skillID, level);
        this.DCr = getParsedCommonData(levelCommon.DCr, skillID, level);
        this.ReqGL = getParsedCommonData(levelCommon.ReqGL, skillID, level);
        this.Price = getParsedCommonData(levelCommon.Price, skillID, level);
        this.affectedArea = levelCommon.affectedArea;
        this.S = getParsedCommonData(levelCommon.S, skillID, level);
        this.U = getParsedCommonData(levelCommon.U, skillID, level);
        this.V = getParsedCommonData(levelCommon.V, skillID, level);
        this.W = getParsedCommonData(levelCommon.W, skillID, level);
        this.T = getParsedCommonDataFloat(levelCommon.T, skillID, level);
        // help string calc
        // crc calc
        this.loaded = true;
    }

    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    public static int getParsedCommonData(String text, int skillID, int level) {
        int result = 0;
        if (text == null || text.isEmpty() || level == 0) {
            return 0;
        }
        // Sometimes newlines get taken, just remove those
        text = text.replace("\n", "").replace("\r", "");
        text = text.replace("\\n", "").replace("\\r", ""); // unluko
        String original = text;
        if(Utilities.isNumber(text)) {
            result = Integer.parseInt(text);
        } else {
            try {
                text = text.replace("u", "Math.ceil");
                text = text.replace("d", "Math.floor");
                String toReplace = text.contains("y") ? "y"
                        : text.contains("X") ? "X"
                        : "x";
                Object res = engine.eval(text.replace(toReplace, level + ""));
                if(res instanceof Integer) {
                    result = (Integer) res;
                } else if(res instanceof Double) {
                    result = ((Double) res).intValue();
                }
            } catch (ScriptException e) {
                Logger.logError("Error when parsing: skill %d, level %d, tried to eval %s.", skillID, level, original);
                e.printStackTrace();
            }
        }
        return result;
    }

    public static float getParsedCommonDataFloat(String text, int skillID, int level) {
        float result = 0.0f;
        if (text == null || text.isEmpty() || level == 0) {
            return 0.0f;
        }
        // Sometimes newlines get taken, just remove those
        text = text.replace("\n", "").replace("\r", "");
        text = text.replace("\\n", "").replace("\\r", ""); // unluko
        String original = text;
        if(Utilities.isNumber(text)) {
            result = Float.parseFloat(text);
        } else {
            try {
                text = text.replace("u", "Math.ceil");
                text = text.replace("d", "Math.floor");
                String toReplace = text.contains("y") ? "y"
                        : text.contains("X") ? "X"
                        : "x";
                Object res = engine.eval(text.replace(toReplace, level + ""));
                if(res instanceof Integer) {
                    result = (Integer) res;
                } else if(res instanceof Double) {
                    result = ((Double) res).floatValue();
                }
            } catch (ScriptException e) {
                Logger.logError("Error when parsing: skill %d, level %d, tried to eval %s.", skillID, level, original);
                e.printStackTrace();
            }
        }
        return result;
    }
}
