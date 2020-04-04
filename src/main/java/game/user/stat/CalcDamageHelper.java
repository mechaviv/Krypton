package game.user.stat;

import common.user.CharacterData;
import game.field.life.mob.AttackElem;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillInfo;
import game.user.skill.Skills;
import game.user.skill.data.SkillLevelData;
import game.user.skill.entries.SkillEntry;
import org.python.jline.internal.Log;
import util.Logger;
import util.Pointer;

import java.util.List;

/**
 * Created by MechAviv on 4/1/2020.
 */
public class CalcDamageHelper {
    public static double get_rand(int rand, double f0, double f1) {
        double realF1 = f1;
        double realF0 = f0;
        if (f0 > f1) {
            realF1 = f0;
            realF0 = f1;
        }
        double result;
        if (realF1 != realF0) {
            result =  realF0 + (double) (Integer.toUnsignedLong(rand) % 10000000) * (realF1 - realF0) / 9999999.0;
        } else {
            result = realF0;
        }
        Logger.logReport("get_rand(0x%X, %s, %s) = %s", rand, f0, f1, result);
        return result;
    }

    public static double get_rand_old(int rand, double f0, double f1) {
        double realF1 = f1;
        double realF0 = f0;
        if (f0 > f1) {
            realF1 = f0;
            realF0 = f1;
            return realF0 + (double) (Integer.toUnsignedLong(rand) % 10000000) * (realF1 - realF0) / 9999999.0;
        }
        if (f1 != f0) {
            return realF0 + (double) (Integer.toUnsignedLong(rand) % 10000000) * (realF1 - realF0) / 9999999.0;
        }
        return f0;
    }

    public static int calcACCr(int acc, int mobEVA, int attackLevel, int targetLevel, int ar) {
        int sqrtACC = (int) Math.sqrt(acc);
        int sqrtMobEVA = (int) Math.sqrt(mobEVA);
        int result = sqrtACC - sqrtMobEVA + 100 + ar * (sqrtACC - sqrtMobEVA + 100) / 100;
        result = Math.min(result, 100);
        if (targetLevel > attackLevel) {
            double range = Math.min(5 * (targetLevel - attackLevel), result);
            result -= range;
        }
        return result;
    }

    public static int calcBaseDamage(int p1, int p2, int p3, int ad, double k) {
        return (int) ((double) (p3 + p2 + 4 * p1) / 100.0 * ((double) ad * k) + 0.5);
    }

    public static double adjustRandomDamage(double damage, int rand, double k, int mastery) {
        double m = Math.min((mastery / 100.0 + k), 0.95);
        return get_rand(rand, damage, (int) (m * damage + 0.5));
        // Cli = CalcDamage__AdjustRandomDamage: Result DMG [14.571638] | Damage [17.000000] | Rand [0xD9B0DEF] | k [0.200000] | Mastery [0]
        // Svr = CalcDamage__AdjustRandomDamage: Result DMG [5.9431351] | Damage [6.800000000000001] | Rand [0xD9B0DEF] | k [0.2] | Mastery [0]
    }

    public static double getDamageAdjustedByElemAttr(double damage, SkillEntry skill, List<Integer> damagedElemAttr, int slv, double adjustByBuff, double boost) {
        if (skill == null || slv == 0) {
            return damage;
        }
        int skillID = skill.getSkillID();
        if (skillID == Skills.Mage1.MAGIC_COMPOSITION) {
            return getDamageAdjustedByElemAttr(damage * 0.5, damagedElemAttr.get(AttackElem.Fire), 1.0, 0.0) + getDamageAdjustedByElemAttr(damage * 0.5, damagedElemAttr.get(AttackElem.Poison), 1.0, boost);
        } else if (skillID == Skills.Mage2.MAGIC_COMPOSITION) {
            return getDamageAdjustedByElemAttr(damage * 0.5, damagedElemAttr.get(AttackElem.Ice), 1.0, 0.0) + getDamageAdjustedByElemAttr(damage * 0.5, damagedElemAttr.get(AttackElem.Light), 1.0, boost);
        } else if (skillID == Skills.Ranger.FIRE_SHOT || skillID == Skills.Sniper.ICE_SHOT) {
            return getDamageAdjustedByElemAttr(damage, damagedElemAttr.get(skill.getAttackElemAttr()), skill.getLevelData(slv).X / 100.0, boost);
        }
        return getDamageAdjustedByElemAttr(damage, damagedElemAttr.get(skill.getAttackElemAttr()), adjustByBuff, boost);
    }

    public static double getDamageAdjustedByElemAttr(double damage, int attr, double adjust, double boost) {
        switch (attr) {
            case AttackElem.Ice:
                return (1.0 - adjust) * damage;
            case AttackElem.Fire:
                return (1.0 - (adjust * 0.5 + boost)) * damage;
            case AttackElem.Light:
                double result = (adjust * 0.5 + boost + 1.0) * damage;
                return Math.max(Math.min(result, Integer.MAX_VALUE), damage);
        }
        return damage;
    }

    public static double getDamageAdjustedByChargedElemAttr(double damage, List<Integer> damagedElemAttr, SecondaryStat ss, CharacterData cd) {
        if (ss.getStatOption(CharacterTemporaryStat.WeaponCharge) == 0) {
            return damage;
        }
        int reason = ss.getStatReason(CharacterTemporaryStat.WeaponCharge);
        int element = SkillAccessor.getElementByChargedSkillID(reason);
        if (element == AttackElem.Physical) {
            return damage;
        }
        Pointer<SkillEntry> skill = new Pointer<>();
        int slv = SkillInfo.getInstance().getSkillLevel(cd, reason, skill);
        if (slv <= 0) {
            return damage;
        }
        SkillLevelData sd = skill.get().getLevelData(slv);
        return getDamageAdjustedByElemAttr((sd.Damage / 100) * damage, damagedElemAttr.get(element), sd.Z / 100, 0.0);
    }

    public static double getDamageAdjustedByAssistChargedElemAttr(double damage, List<Integer> damagedElemAttr, SecondaryStat ss, CharacterData cd) {
        if (ss.getStatOption(CharacterTemporaryStat.AssistCharge) == 0) {
            return 0.0;
        }
        int reason = ss.getStatReason(CharacterTemporaryStat.AssistCharge);
        int element = SkillAccessor.getElementByChargedSkillID(reason);
        if (element == AttackElem.Physical) {
            return 0.0;
        }
        Pointer<SkillEntry> skill = new Pointer<>();
        int slv = SkillInfo.getInstance().getSkillLevel(cd, reason, skill);
        if (slv <= 0) {
            return damage;
        }
        SkillLevelData sd = skill.get().getLevelData(slv);
        return getDamageAdjustedByElemAttr(((sd.Damage / 100) - 1.0) * damage * 0.5, damagedElemAttr.get(element), sd.Z / 100, 0.0);
    }
}
