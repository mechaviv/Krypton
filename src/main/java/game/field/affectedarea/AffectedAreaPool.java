package game.field.affectedarea;

import game.field.Field;
import game.field.FieldSplit;
import game.user.skill.Skills;
import util.Rect;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by MechAviv on 3/29/2020.
 */
public class AffectedAreaPool {
    private final Field field;
    private final Map<Integer, AffectedArea> affectedAreas;
    public final static AtomicInteger affectedAreaIdCounter = new AtomicInteger(0);

    public AffectedAreaPool(Field field) {
        this.field = field;
        this.affectedAreas = new HashMap<>();
    }

    public void update(long time) {
        for (Iterator<Map.Entry<Integer, AffectedArea> > it = affectedAreas.entrySet().iterator(); it.hasNext();) {
            AffectedArea affectedArea = it.next().getValue();
            if (time - affectedArea.getEnd() >= 0) {
                it.remove();
                field.splitUnregisterFieldObj(FieldSplit.AffectedArea, affectedArea);
            }
        }
    }

    public boolean insertAffectedArea(boolean mobSkill, int ownerID, int skillID, int slv, long start, long end, Point pt, Rect rect) {
        AffectedArea affectedArea = new AffectedArea(field);
        int type = mobSkill ? AffectedArea.MOB_SKILL : AffectedArea.USER_SKILL;
        if (skillID == Skills.Shadower.SMOKE_SHELL) {
            type = AffectedArea.SMOKE;
        }
        affectedArea.setType(type);

        affectedArea.setOwnerID(ownerID);
        affectedArea.setSkillID(skillID);
        affectedArea.setSlv(slv);
        affectedArea.setStart(start);
        affectedArea.setEnd(end);
        affectedArea.setAffectedArea(new Rect(rect.left, rect.top, rect.right, rect.bottom));
        affectedAreas.put(affectedArea.getId(), affectedArea);

        return field.splitRegisterFieldObj(pt.x, pt.y, FieldSplit.AffectedArea, affectedArea);
    }

    public AffectedArea getAffectedAreaByPoint(Point pt) {
        long cur = System.currentTimeMillis();
        for (AffectedArea affectedArea : affectedAreas.values()) {
            boolean expired = cur - affectedArea.getStart() < 0;
            if (!expired && !affectedArea.isMobSkill() && affectedArea.getAffectedArea().ptInRect(pt)) {
                return affectedArea;
            }
        }
        return null;
    }
}
