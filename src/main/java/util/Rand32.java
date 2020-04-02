package util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 3/12/2020.
 */
public class Rand32 {
    private static final Rand32 g_rand = new Rand32();
    public static Rand32 getInstance() { return g_rand; }

    private int s1;
    private int s2;
    private int s3;
    private int past_s1;
    private int past_s2;
    private int past_s3;
    private final ReentrantLock lock;

    public Rand32() {
        int time = Utilities.timeGetTime();
        int randNum = 214013 * (214013 * (214013 * time + 2531011) + 2531011) + 2531011;
        this.lock = new ReentrantLock();

        this.lock.lock();
        try {
            this.s1 = randNum | 0x100000;
            this.past_s1 = this.s1;
            this.s2 = randNum | 0x1000;
            this.past_s2 = this.s2;
            this.s3 = randNum | 0x10;
            this.past_s3 = this.s3;
            //System.out.println(String.format("S1 = [%d] | S2 [%d] | S3 [%d]", s1, s2, s3));
            //System.out.println(String.format("PS1 = [%d] | PS2 [%d] | PS3 [%d]", past_s1, past_s2, past_s3));
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Generates a new random within a specified range (R)
     * and beginning at a specified start (N).
     *
     * @param range The maximum range of the random
     * @param start The minimum random
     * @return A new random
     */
    public static final int getRand(int range, int start) {
        if (range != 0)
            return getInstance().random() % range + start;
        return getInstance().random();
    }

    /**
     * A shortcut to generating a random versus:
     * g_rand->Random()
     * or: GetInstance()->Random()
     * or: new Rand32().Random()
     *
     * @return A new pseudorandom number
     */
    public static final int genRandom() {
        return Math.abs(getInstance().random());
    }

    public float randomFloat() {
        int uBits = ((random() & 0x007FFFFF) | 0x3F800000);

        return Float.intBitsToFloat(uBits) - 1.0f;
    }

    public void setSeed(int s1, int s2, int s3) {
        lock.lock();
        try {
            this.s1 = s1;
            this.past_s1 = s1;
            this.s2 = s2;
            this.past_s2 = s2;
            this.s3 = s3;
            this.past_s3 = s3;
        } finally {
            lock.unlock();
        }
    }

    public void seed(int s1, int s2, int s3) {
        lock.lock();
        try {
            this.s1 = s1 | 0x100000;
            this.past_s1 = this.s1;
            this.s2 = s2 | 0x1000;
            this.past_s2 = this.s2;
            this.s3 = s3 | 0x10;
            this.past_s3 = this.s3;
        } finally {
            lock.unlock();
        }
    }

    public int random() {
        int result = 0;
        lock.lock();
        try {
            s1 = ((((s1 >> 6) & 0x3FFFFFF) ^ (s1 << 12)) & 0x1FFF) ^ ((s1 >> 19) & 0x1FFF) ^ (s1 << 12);
            s2 = ((((s2 >> 23) & 0x1FF) ^ (s2 << 4)) & 0x7F) ^ ((s2 >> 25) & 0x7F) ^ (s2 << 4);
            s3 = ((((s3 << 17) ^ ((s3 >> 8) & 0xFFFFFF)) & 0x1FFFFF) ^ (s3 << 17)) ^ ((s3 >> 11) & 0x1FFFFF);

            result = s1 ^ s2 ^ s3;

        } finally {
            lock.unlock();
        }
        return result;
    }

    public int getS1() {
        return s1;
    }

    public int getS2() {
        return s2;
    }

    public int getS3() {
        return s3;
    }

    public int getPastRand() {
        int result = 0;
        lock.lock();
        try {
            result = 16 * (((past_s3 & 0xFFFFFFF0) << 13) ^ (past_s2 ^ ((past_s1 & 0xFFFFFFFE) << 8)) & 0xFFFFFFF8) ^ ((past_s1 & 0x7FFC0 ^ ((past_s3 & 0x1FFFFF00 ^ ((past_s3 ^ ((past_s1 ^ (((past_s2 >> 2) ^ past_s2 & 0x3F800000) >> 4)) >> 8)) >> 3)) >> 2)) >> 6);
        } finally {
            lock.unlock();
        }
        return result;
    }

    public int getPast_s1() {
        return past_s1;
    }

    public int getPast_s2() {
        return past_s2;
    }

    public int getPast_s3() {
        return past_s3;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}

