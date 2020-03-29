package game.field.affectedarea;

import game.field.Field;
import game.field.FieldObj;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Rect;

/**
 * Created by MechAviv on 3/29/2020.
 */
public class AffectedArea extends FieldObj {
    public static final int
            MOB_SKILL       = 0,
            USER_SKILL      = 1,
            SMOKE           = 2,
            BUFF            = 3,// area buff item
            BLESSED_MIST    = 4;

    private int id;
    private int type;
    private int ownerID;
    private int skillID;
    private int slv;
    private long start;
    private long end;
    private Rect affectedArea;
    private int phase;
    private int elemAttr;

    public AffectedArea(Field field) {
        super(field);
        this.id = AffectedAreaPool.affectedAreaIdCounter.incrementAndGet();
        this.affectedArea = new Rect();
    }

    public boolean isMobSkill() {
        return type == MOB_SKILL;
    }

    public boolean isUserSkill() {
        return type == USER_SKILL;
    }

    public boolean isSmoke() {
        return type == SMOKE;
    }

    public boolean isBuff() {
        return type == BUFF;
    }

    public boolean isBlessedMist() {
        return type == BLESSED_MIST;
    }

    @Override
    public OutPacket makeEnterFieldPacket() {
        long time = System.currentTimeMillis();
        OutPacket packet = new OutPacket(LoopbackPacket.AffectedAreaCreated);
        packet.encodeInt(id);
        packet.encodeInt(type);
        packet.encodeInt(ownerID);
        packet.encodeInt(skillID);
        packet.encodeByte(slv);
        packet.encodeShort((int) Math.max(((start - time) / 100), 0));
        affectedArea.encode(packet);
        packet.encodeInt(elemAttr);
        packet.encodeInt(phase);
        return packet;
    }

    @Override
    public OutPacket makeLeaveFieldPacket() {
        OutPacket packet = new OutPacket(LoopbackPacket.AffectedAreaRemoved);
        packet.encodeInt(id);
        return packet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getSlv() {
        return slv;
    }

    public void setSlv(int slv) {
        this.slv = slv;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Rect getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(Rect affectedArea) {
        this.affectedArea = affectedArea;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getElemAttr() {
        return elemAttr;
    }

    public void setElemAttr(int elemAttr) {
        this.elemAttr = elemAttr;
    }
}
