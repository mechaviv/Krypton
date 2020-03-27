package game.user.stat.psd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class PassiveSkillData {
    private int MHPr;
    private int MMPr;
    private int Cr;
    private int CDMin;
    private int ACCr;
    private int EVAr;
    private int Ar;
    private int Er;
    private int PDDr;
    private int MDDr;
    private int PDr;
    private int MDr;
    private int DIPr;
    private int PDamr;
    private int MDamr;
    private int PADr;
    private int MADr;
    private int EXPr;
    private int IMPr;
    private int ASRr;
    private int TERr;
    private int MESOr;
    private int PADx;
    private int MADx;
    private int IMDr;
    private int PsdJump;
    private int PsdSpeed;
    private int OCr;
    private int DCr;
    private final Map<Integer, AdditionPsd> additionPsd;

    public PassiveSkillData() {
        this.additionPsd = new HashMap<>();
    }

    public void clear() {
        this.MHPr = 0;
        this.MMPr = 0;
        this.Cr = 0;
        this.CDMin = 0;
        this.ACCr = 0;
        this.EVAr = 0;
        this.Ar = 0;
        this.Er = 0;
        this.PDDr = 0;
        this.MDDr = 0;
        this.PDr = 0;
        this.MDr = 0;
        this.DIPr = 0;
        this.PDamr = 0;
        this.MDamr = 0;
        this.PADr = 0;
        this.MADr = 0;
        this.EXPr = 0;
        this.IMPr = 0;
        this.ASRr = 0;
        this.TERr = 0;
        this.MESOr = 0;
        this.PADx = 0;
        this.MADx = 0;
        this.IMDr = 0;
        this.PsdJump = 0;
        this.PsdSpeed = 0;
        this.OCr = 0;
        this.DCr = 0;
        this.additionPsd.clear();
    }

    public int getMHPr() {
        return MHPr;
    }

    public void setMHPr(int MHPr) {
        this.MHPr = MHPr;
    }

    public int getMMPr() {
        return MMPr;
    }

    public void setMMPr(int MMPr) {
        this.MMPr = MMPr;
    }

    public int getCr() {
        return Cr;
    }

    public void setCr(int cr) {
        Cr = cr;
    }

    public int getCDMin() {
        return CDMin;
    }

    public void setCDMin(int CDMin) {
        this.CDMin = CDMin;
    }

    public int getACCr() {
        return ACCr;
    }

    public void setACCr(int ACCr) {
        this.ACCr = ACCr;
    }

    public int getEVAr() {
        return EVAr;
    }

    public void setEVAr(int EVAr) {
        this.EVAr = EVAr;
    }

    public int getAr() {
        return Ar;
    }

    public void setAr(int ar) {
        Ar = ar;
    }

    public int getEr() {
        return Er;
    }

    public void setEr(int er) {
        Er = er;
    }

    public int getPDDr() {
        return PDDr;
    }

    public void setPDDr(int PDDr) {
        this.PDDr = PDDr;
    }

    public int getMDDr() {
        return MDDr;
    }

    public void setMDDr(int MDDr) {
        this.MDDr = MDDr;
    }

    public int getPDr() {
        return PDr;
    }

    public void setPDr(int PDr) {
        this.PDr = PDr;
    }

    public int getMDr() {
        return MDr;
    }

    public void setMDr(int MDr) {
        this.MDr = MDr;
    }

    public int getDIPr() {
        return DIPr;
    }

    public void setDIPr(int DIPr) {
        this.DIPr = DIPr;
    }

    public int getPDamR() {
        return PDamr;
    }

    public void setPDamR(int PDamr) {
        this.PDamr = PDamr;
    }

    public int getMDamR() {
        return MDamr;
    }

    public void setMDamR(int MDamr) {
        this.MDamr = MDamr;
    }

    public int getPADr() {
        return PADr;
    }

    public void setPADr(int PADr) {
        this.PADr = PADr;
    }

    public int getMADr() {
        return MADr;
    }

    public void setMADr(int MADr) {
        this.MADr = MADr;
    }

    public int getEXPr() {
        return EXPr;
    }

    public void setEXPr(int EXPr) {
        this.EXPr = EXPr;
    }

    public int getIMPr() {
        return IMPr;
    }

    public void setIMPr(int IMPr) {
        this.IMPr = IMPr;
    }

    public int getASRr() {
        return ASRr;
    }

    public void setASRr(int ASRr) {
        this.ASRr = ASRr;
    }

    public int getTERr() {
        return TERr;
    }

    public void setTERr(int TERr) {
        this.TERr = TERr;
    }

    public int getMESOr() {
        return MESOr;
    }

    public void setMESOr(int MESOr) {
        this.MESOr = MESOr;
    }

    public int getPADx() {
        return PADx;
    }

    public void setPADx(int PADx) {
        this.PADx = PADx;
    }

    public int getMADx() {
        return MADx;
    }

    public void setMADx(int MADx) {
        this.MADx = MADx;
    }

    public int getIMDr() {
        return IMDr;
    }

    public void setIMDr(int IMDr) {
        this.IMDr = IMDr;
    }

    public int getPsdJump() {
        return PsdJump;
    }

    public void setPsdJump(int psdJump) {
        PsdJump = psdJump;
    }

    public int getPsdSpeed() {
        return PsdSpeed;
    }

    public void setPsdSpeed(int psdSpeed) {
        PsdSpeed = psdSpeed;
    }

    public int getOCr() {
        return OCr;
    }

    public void setOCr(int OCr) {
        this.OCr = OCr;
    }

    public int getDCr() {
        return DCr;
    }

    public void setDCr(int DCr) {
        this.DCr = DCr;
    }

    public Map<Integer, AdditionPsd> getAdditionPsd() {
        return additionPsd;
    }
}
