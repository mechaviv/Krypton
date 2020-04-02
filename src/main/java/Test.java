import common.JobAccessor;
import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import game.field.drop.RewardType;
import game.field.life.mob.BossIDs;
import game.field.life.mob.MobTemplate;
import game.field.reactor.ReactorTemplate;
import game.script.ScriptVM;
import game.user.func.FunckeyMapped;
import game.user.item.ItemInfo;
import game.user.item.ItemOptionInfo;
import game.user.quest.QuestMan;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillInfo;
import game.user.skill.Skills;
import game.user.skill.entries.MobSkillEntry;
import game.user.stat.CalcDamageHelper;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.ts.EnergyChargeStat;
import game.user.stat.ts.TemporaryStatBase;
import game.user.stat.ts.TwoStateTemporaryStat;
import network.packet.InPacket;
import org.python.jline.internal.Log;
import util.*;
import util.wz.WzFileSystem;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MechAviv on 3/18/2020.
 */
public class Test {
    public static void main(String[] args) {
        // Crit Damage = [38.546316854631684] | Real Damage [148.8256451785645]
        // svr 206 crit | should be 205
        double dmg = 148.8256451785645;
        int critDamage = (int) 38.546316854631684;
        dmg += dmg * critDamage / 100.0;
        System.out.println(dmg);// 105
    }

    public static double adjustRandomDamage(double damage, int rand, double k, int mastery) {
        double prop = (double)mastery / 100.0 + k;
        if (prop >= 0.95) {
            prop = 0.95;
        }
        double p = prop * damage + 0.5;
        return get_rand(rand, damage, p);
    }

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
}
