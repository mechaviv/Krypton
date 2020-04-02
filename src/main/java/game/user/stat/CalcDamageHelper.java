package game.user.stat;

import game.field.life.mob.AttackElem;
import game.user.skill.Skills;
import game.user.skill.entries.SkillEntry;
import util.Logger;

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
        Logger.logReport("CalcDamage__AdjustRandomDamage: Damage [%s] | Rand [0x%X] | k [%s] | Mastery [%d]", damage, rand, k, mastery);
        double m = Math.min((mastery / 100.0 + k), 0.95);
        return get_rand(rand, damage, m * damage + 0.5);
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
}
