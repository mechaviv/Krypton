package game.user.stat.ts;

import network.packet.OutPacket;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class DashTemporaryStat extends TwoStateTemporaryStat {
    public int expireTerm;

    public DashTemporaryStat() {
        super();
        this.expireTerm = 0;
    }

    public int getExpireTerm() {
        return 1000 * expireTerm;
    }

    public void setExpireTerm(int expireTerm) { this.expireTerm = expireTerm; }

    @Override
    public boolean isExpiredAt(long cur) {
        return getExpireTerm() < cur - getLastUpdated();
    }

    @Override
    public boolean isActivated(long time) {
        return getValue() != 0;
    }

    @Override
    public int getMaxValue(){
        return 0;
    }

    @Override
    public void encodeForClient(OutPacket packet) {
        super.encodeForClient(packet);
        packet.encodeShort(expireTerm);
    }
}
