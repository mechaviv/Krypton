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
import game.user.stat.ts.EnergyChargeStat;
import game.user.stat.ts.TemporaryStatBase;
import game.user.stat.ts.TwoStateTemporaryStat;
import network.packet.InPacket;
import org.python.jline.internal.Log;
import util.Logger;
import util.Rand32;
import util.SystemTime;
import util.Utilities;
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
        int val = 60372986;// FA 37 99 03
        int v = -115 & 0xFF;
        Logger.logReport("%d", v);
        Logger.logReport("%d", v & 0xFF);
        int val1 =  (byte) val;
        int val2 = (val >> 8) & 0xFF;
        int val3 = (byte) (-56 & 0xFF);
        Logger.logReport("0x%X", val & 0xFF);
        Logger.logReport("0x%X", -56);
        Logger.logReport("0x%X", (byte) val3);
        if (val3 == 0xC8) Logger.logReport("ss");
    }
}
