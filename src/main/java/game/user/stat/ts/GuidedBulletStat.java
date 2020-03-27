package game.user.stat.ts;

import network.packet.OutPacket;

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
}
