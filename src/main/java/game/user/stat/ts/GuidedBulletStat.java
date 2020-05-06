package game.user.stat.ts;

import common.user.CharacterData;
import game.user.skill.SkillInfo;
import game.user.skill.Skills;
import game.user.skill.entries.SkillEntry;
import game.user.stat.SecondaryStat;
import network.packet.OutPacket;
import util.Pointer;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class GuidedBulletStat extends TwoStateTemporaryStat {
    private int mobID;

    public GuidedBulletStat() {
        super();
        this.mobID = 0;
    }

    public int getMobID() {
        getLock().lock();
        try {
            return mobID;
        } finally {
            getLock().unlock();
        }
    }

    public void setMobID(int mobID) {
        getLock().lock();
        try {
            this.mobID = mobID;
        } finally {
            getLock().unlock();
        }
    }

    @Override
    public void encodeForClient(OutPacket packet) {
        super.encodeForClient(packet);
        packet.encodeInt(getMobID());
    }

    public static double applyGuidedBulletDamage(CharacterData cd, SecondaryStat ss, int mobID, double damage) {
        Pointer<SkillEntry> advGuidedBullet = new Pointer<>();
        int slv = SkillInfo.getInstance().getSkillLevel(cd, Skills.Captain.ADVANCED_HOMING, advGuidedBullet);
        if (slv <= 0) {
            return damage;
        }
        GuidedBulletStat ts = (GuidedBulletStat) ss.temporaryStats[TSIndex.GUIDED_BULLET];
        if (ts == null) {
            return damage;
        }
        if (ts.getMobID() == mobID) {
            damage *= (double) (advGuidedBullet.get().getLevelData(slv).X + 100) / 100.0;
        }
        return damage;
    }
}
