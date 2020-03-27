package game.user.stat.ts;

import network.packet.OutPacket;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class TwoStateTemporaryStat extends TemporaryStatBase {
    public TwoStateTemporaryStat() {
        super();
    }

    public boolean isExpiredAt(long cur) {
        return false;
    }

    public boolean isActivated(long time) {
        return getValue() != 0;
    }

    public int getMaxValue(){
        return 0;
    }

    public int getExpireTerm() {
        return 0x7FFFFFFF;
    }

    @Override
    public void encodeForClient(OutPacket packet) {
        super.encodeForClient(packet);
    }
}
