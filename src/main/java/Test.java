import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import game.field.drop.RewardType;
import game.field.life.mob.BossIDs;
import game.field.life.mob.MobTemplate;
import game.field.reactor.ReactorTemplate;
import game.script.ScriptVM;
import game.user.item.ItemInfo;
import game.user.quest.QuestMan;
import game.user.skill.SkillAccessor;
import org.python.jline.internal.Log;
import util.Logger;
import util.Rand32;
import util.SystemTime;
import util.wz.WzFileSystem;
import util.wz.WzProperty;
import util.wz.WzUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MechAviv on 3/18/2020.
 */
public class Test {
    public static void main(String[] args) {
        for (int i = 8820000; i <= 8820027; i++) {
            Logger.logReport("%d - ", i);
        }
    }
}
