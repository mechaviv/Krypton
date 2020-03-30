package common.user;

import network.packet.OutPacket;
import util.Rand32;

/**
 * Created by MechAviv on 3/30/2020.
 */
public class WildHunterInfo {
    private static final int[] RIDING_WILD_HUNTER_JAGUAR_190 = {0, 1932015, 1932030, 1932031, 1932032, 1932033, 1932036};

    private byte ridingType;
    private byte idX;
    private final int[] capturedMobs;

    public WildHunterInfo() {
        this.capturedMobs = new int[5];
    }

    public void encode(OutPacket packet) {
        packet.encodeByte(idX + 10 * ridingType);
        for (int i = 0; i < capturedMobs.length; i++) {
            packet.encodeInt(capturedMobs[i]);
        }
    }

    public int getRandomCapturedMob() {
        int[] randomMobs = new int[5];

        int randCounter = 0;
        for (int i = 0; i < capturedMobs.length; i++) {
            if (capturedMobs[i] != 0) {
                randomMobs[randCounter++] = capturedMobs[i];
            }
        }
        return randCounter != 0 ? randomMobs[Math.abs(Rand32.genRandom().intValue()) % randCounter] : 0;
    }

    public int getRidingItem() {
        if (ridingType <= 0) {
            return RIDING_WILD_HUNTER_JAGUAR_190[0];
        }
        return RIDING_WILD_HUNTER_JAGUAR_190[Math.min(ridingType, 6)];
    }

    public byte getRidingType() {
        return ridingType;
    }

    public void setRidingType(byte ridingType) {
        this.ridingType = ridingType;
    }

    public int[] getCapturedMobs() {
        return capturedMobs;
    }

    public byte getIdX() {
        return idX;
    }

    public void setIdX(byte idX) {
        this.idX = idX;
    }
}
