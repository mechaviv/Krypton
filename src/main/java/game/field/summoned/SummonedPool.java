package game.field.summoned;

import game.field.Field;
import game.field.FieldSplit;
import game.field.StaticFoothold;
import game.field.drop.Drop;
import game.field.life.AssistType;
import game.field.life.MoveAbility;
import game.user.skill.SkillInfo;
import game.user.skill.Skills.*;
import game.user.skill.data.SkillLevelData;
import game.user.skill.entries.SkillEntry;
import util.Logger;
import util.Pointer;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by MechAviv on 3/28/2020.
 */
public class SummonedPool {
    private final Field field;
    private final Map<Integer, Summoned> summoneds;
    public final static AtomicInteger summonedIdCounter = new AtomicInteger(0);

    public SummonedPool(Field field) {
        this.field = field;
        this.summoneds = new HashMap<>();
    }

    public void update(long time) {
        for (Iterator<Map.Entry<Integer, Summoned> > it = summoneds.entrySet().iterator(); it.hasNext();) {
            Summoned summoned = it.next().getValue();
            if (time - summoned.getEnd() >= 0) {
                it.remove();
                for (FieldSplit split : summoned.getSplits()) {
                    field.splitUnregisterFieldObj(split, FieldSplit.Summoned, summoned, summoned.makeLeaveFieldPacket(0));
                }
            }
        }
    }

    public boolean createSummoned(Summoned summoned, Point pt) {
        if (summoned.getMoveAbility() == 0) {
            return false;
        }
        FieldSplit fieldSplit = field.splitFromPoint(pt.x, pt.y);
        if (fieldSplit == null) {
            Logger.logError("Couldn't register summoned [ %d : ( %d, %d ) ]", field.getFieldID(), pt.x, pt.y);
            return false;
        }
        summoned.setField(field);
        summoned.setCurPos(new Point(pt.x, pt.y));
        field.getEncloseSplit(fieldSplit, summoned.getSplits());
        for (FieldSplit split : summoned.getSplits()) {
            field.splitRegisterFieldObj(split, FieldSplit.Summoned, summoned, summoned.makeEnterFieldPacket(0));
        }
        summoneds.put(summoned.getSummonedID(), summoned);
        return true;
    }

    public Summoned createSummoned(int characterID, int skillID, int slv, int charLevel, Point pt, long end, boolean migrate) {
        Pointer<Integer> pcy = new Pointer<>(0);
        StaticFoothold staticFoothold = field.getSpace2D().getFootholdUnderneath(pt.x, pt.y - 5, pcy);
        if (staticFoothold == null) {
            return null;
        }
        Point newPos = new Point(pt.x, pcy.get());
        FieldSplit fieldSplit = field.splitFromPoint(newPos.x, newPos.y);
        if (fieldSplit == null) {
            return null;
        }
        int moveAbility = MoveAbility.Jump;
        int assistType = AssistType.Attack;
        if (skillID == DarkKnight.BEHOLDER || skillID == ArchMage1.IFRIT || skillID == ArchMage2.ELQUINES || skillID == Bishop.BAHAMUT || skillID == SoulMaster.SOUL) {
            moveAbility = MoveAbility.Walk;
            if (skillID == DarkKnight.BEHOLDER)
                assistType = AssistType.Heal;
        } else if (skillID == Priest.SUMMON_DRAGON || skillID == Ranger.SILVER_HAWK || skillID == Bowmaster.PHOENIX || skillID == Sniper.GOLDEN_EAGLE || skillID == CrossbowMaster.FREEZER) {
            moveAbility = MoveAbility.Fly;
        } else if (skillID == Hermit.SHADOW_MIRROR) {
            moveAbility = MoveAbility.Stop;
        } else if (skillID == Ranger.PUPPET || skillID == Sniper.PUPPET) {
            moveAbility = MoveAbility.Stop;
            assistType = AssistType.None;
        }
        Summoned summoned = new Summoned(field);
        summoned.init(characterID, skillID, slv, charLevel, newPos, (short) staticFoothold.getSN(), moveAbility, assistType);
        summoned.setCreateTime(System.currentTimeMillis());

        SkillEntry skill = SkillInfo.getInstance().getSkill(skillID);
        if (skill == null) {
            return null;
        }
        SkillLevelData sd = skill.getLevelData(slv);
        summoned.setEnd(migrate ? end : summoned.getCreateTime() + 1000 * sd.Time);
        summoned.setHp(sd.X);
        summoned.setPAD(sd.EPAD);
        summoned.setMAD(sd.MAD);

        field.getEncloseSplit(fieldSplit, summoned.getSplits());
        for (FieldSplit split : summoned.getSplits()) {
            field.splitRegisterFieldObj(split, FieldSplit.Summoned, summoned, summoned.makeEnterFieldPacket(0));
        }
        summoneds.put(summoned.getSummonedID(), summoned);
        return summoned;
    }

    public Summoned getSummoned(int characterID, int skillID) {
        for (Summoned summoned : summoneds.values()) {
            if (summoned.getCharacterID() == characterID && summoned.getSkillID() == skillID) {
                return summoned;
            }
        }
        return null;
    }

    public Summoned getSummoned(int summonedID) {
        return summoneds.getOrDefault(summonedID, null);
    }

    public void removeSummoned(int characterID, int skillID, int leaveType) {
        for (Iterator<Map.Entry<Integer, Summoned> > it = summoneds.entrySet().iterator(); it.hasNext();) {
            Summoned summoned = it.next().getValue();
            if (summoned.getCharacterID() == characterID && (skillID == 0 || summoned.getSkillID() == skillID)) {
                for (FieldSplit split : summoned.getSplits()) {
                    field.splitUnregisterFieldObj(split, FieldSplit.Summoned, summoned, summoned.makeLeaveFieldPacket(leaveType));
                }
                it.remove();
            }
        }
    }
}
