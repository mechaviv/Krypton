package game.user.stat.ts;

import network.packet.OutPacket;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class PartyBoosterStat  extends TwoStateTemporaryStat {
    private long currentTime;
    private int expireTerm;

    public PartyBoosterStat() {
        super();
        this.currentTime = 0;
        this.expireTerm = 0;
    }

    public int getExpireTerm() {
        return 1000 * expireTerm;
    }

    public void setExpireTerm(int expireTerm) { this.expireTerm = expireTerm; }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public boolean isExpiredAt(long cur) {
        return getExpireTerm() < cur - currentTime;
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
        encodeTime(packet, currentTime);
        packet.encodeShort(expireTerm);
    }
}
