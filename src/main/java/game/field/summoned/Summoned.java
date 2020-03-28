package game.field.summoned;

import common.Request;
import common.user.CharacterStat;
import common.user.UserEffect;
import game.field.Field;
import game.field.FieldObj;
import game.field.life.MoveAbility;
import game.user.User;
import game.user.skill.SkillInfo;
import game.user.skill.Skills;
import game.user.skill.data.SkillLevelData;
import game.user.skill.entries.SkillEntry;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.Flag;
import game.user.stat.SecondaryStatOption;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;

import java.awt.*;

/**
 * Created by MechAviv on 3/28/2020.
 */
public class Summoned extends FieldObj {
    private int summonedID;
    private int characterID;
    private int skillID;
    private int slv;
    private int charLevel;
    private int moveAbility;
    private int assistType;
    private long createTime;
    private long end;
    private int hp;
    private int PAD;
    private int MAD;
    private Point curPos;
    private int moveAction;
    private short footholdSN;

    public Summoned(Field field) {
        super(field);
    }

    public void init(int characterID, int skillID, int slv, int charLevel, Point pt, short footholdSN, int moveAbility, int assistType) {
        this.characterID = characterID;
        this.skillID = skillID;
        this.slv = slv;
        this.charLevel = charLevel;
        this.curPos = pt;
        this.footholdSN = footholdSN;
        this.moveAbility = moveAbility;
        this.assistType = assistType;
        this.moveAction = 4;
    }

    public void onHit(User user, InPacket packet) {
        int attackIDx = packet.decodeByte();
        int damage = packet.decodeInt();

        int templateID = 0, left = 0;
        if (attackIDx != 0xFE) {
            templateID = packet.decodeInt();
            left = packet.decodeByte();
            // rnd gen for mob
        }
        if (getField() != null) {
            getField().splitSendPacket(getSplit(), SummonedPacket.onHit(user.getCharacterID(), getSummonedID(), attackIDx, damage, templateID, left), user);
            this.hp -= damage;
            if (this.hp <= 0) {
                this.hp = 0;
                this.end = System.currentTimeMillis();
                getField().getSummonedPool().removeSummoned(getCharacterID(), getSkillID(), 0);
            }
        }
    }

    public void onSkill(User user, InPacket packet) {
        if (getField() == null) {
            return;
        }
        int buffID = packet.decodeInt();
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        Pointer<SkillEntry> buff = new Pointer<>();

        int slv = SkillInfo.getInstance().getSkillLevel(user.getCharacter(), skillID, skillEntry);
        int buffSlv = SkillInfo.getInstance().getSkillLevel(user.getCharacter(), buffID, buff);
        if (slv <= 0 || buffSlv <= 0) {
            return;
        }
        SkillLevelData sd = buff.get().getLevelData(buffSlv);
        int action = packet.decodeByte();
        Flag set = new Flag(Flag.INT_128);
        long duration = 1000 * sd.Time + System.currentTimeMillis();
        if (buffID == Skills.DarkKnight.BEHOLDERS_HEALING) {
            user.incHP(sd.HP, false);
            user.sendCharacterStat(Request.None, CharacterStat.CharacterStatType.HP);
        } else if (buffID == Skills.DarkKnight.BEHOLDERS_BUFF) {
            int type = packet.decodeByte();
            int reason = 2022125 + type;
            if (type == 0) {
                set.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.PDD, new SecondaryStatOption(sd.EPDD, -reason, duration)));
            } else if (type == 1) {
                set.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.MDD, new SecondaryStatOption(sd.EMDD, -reason, duration)));
            } else if (type == 2) {
                set.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.ACC, new SecondaryStatOption(sd.ACC, -reason, duration)));
            } else if (type == 3) {
                set.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.EVA, new SecondaryStatOption(sd.EVA, -reason, duration)));
            } else if (type == 4) {
                set.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.PAD, new SecondaryStatOption(sd.EPAD, -reason, duration)));
            }
            user.validateStat(true);
            user.sendTemporaryStatSet(set);
        } else {
            user.sendDebugMessage("New Summon Skill [%d]", buffID);
        }
        user.onUserEffect(true, true, UserEffect.SkillAffected, buffID, slv);
        getField().splitSendPacket(getSplit(), SummonedPacket.onSkill(user.getCharacterID(), getSummonedID(), action), user);
    }

    public void onAttack(User user, InPacket packet) {

    }

    public void onMove(User user, InPacket packet) {
        if (getField() == null) {
            return;
        }
        getField().onSummonedMove(user, this, packet);
    }

    public void setMovePosition(int x, int y, int moveAction, short sn) {
        this.curPos = new Point(x, y);
        this.moveAction = moveAction;
        this.footholdSN = this.moveAbility == MoveAbility.Stop ? sn : 0;
    }

    @Override
    public OutPacket makeEnterFieldPacket() {
        return makeEnterFieldPacket(1);
    }

    public OutPacket makeEnterFieldPacket(int enterType) {
        OutPacket packet = new OutPacket(LoopbackPacket.SummonedEnterField);
        packet.encodeInt(characterID);
        packet.encodeInt(summonedID);
        packet.encodeInt(skillID);
        packet.encodeByte(charLevel);
        packet.encodeByte(slv);
        packet.encodeShort(curPos.x);
        packet.encodeShort(curPos.y);
        packet.encodeByte(moveAction);
        packet.encodeShort(footholdSN);
        packet.encodeByte(moveAbility);
        packet.encodeByte(assistType);
        packet.encodeByte(enterType);
        packet.encodeByte(0);// if 1 encode avatar look
        if (skillID == Skills.Mechanic.TESLA_COIL) {
            int teslaCoilState = 0;
            packet.encodeByte(teslaCoilState);
            if (teslaCoilState == 1) {
                for (int i = 0; i < 3; i++) {
                    // pTriangle
                    packet.encodeShort(0);// X
                    packet.encodeShort(0);// Y
                }
            }
        }
        return packet;
    }

    @Override
    public OutPacket makeLeaveFieldPacket() {
        return makeLeaveFieldPacket(1);
    }

    public OutPacket makeLeaveFieldPacket(int leaveType) {
        OutPacket packet = new OutPacket(LoopbackPacket.SummonedLeaveField);
        packet.encodeInt(characterID);
        packet.encodeInt(summonedID);
        packet.encodeByte(leaveType);
        return packet;
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
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

    public int getMoveAbility() {
        return moveAbility;
    }

    public void setMoveAbility(int moveAbility) {
        this.moveAbility = moveAbility;
    }

    public int getAssistType() {
        return assistType;
    }

    public void setAssistType(int assistType) {
        this.assistType = assistType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getPAD() {
        return PAD;
    }

    public void setPAD(int PAD) {
        this.PAD = PAD;
    }

    public int getMAD() {
        return MAD;
    }

    public void setMAD(int MAD) {
        this.MAD = MAD;
    }

    public Point getCurPos() {
        return curPos;
    }

    public void setCurPos(Point curPos) {
        this.curPos = curPos;
    }

    public int getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(int moveAction) {
        this.moveAction = moveAction;
    }

    public short getFootholdSN() {
        return footholdSN;
    }

    public void setFootholdSN(short footholdSN) {
        this.footholdSN = footholdSN;
    }

    public int getSummonedID() {
        return summonedID;
    }

    public void setSummonedID(int summonedID) {
        this.summonedID = summonedID;
    }

    public int getCharLevel() {
        return charLevel;
    }

    public void setCharLevel(int charLevel) {
        this.charLevel = charLevel;
    }
}
