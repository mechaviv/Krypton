package game.user.stat.ts;

import network.packet.OutPacket;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class TemporaryStatBase {
    private int value;
    private int reason;
    private long lastUpdated;
    private final ReentrantLock lock;

    public TemporaryStatBase() {
        this.value = 0;
        this.reason = 0;
        this.lastUpdated = System.currentTimeMillis();
        this.lock = new ReentrantLock();
    }

    public void encodeForClient(OutPacket packet) {
        lock.lock();
        try {
            packet.encodeInt(value);
            packet.encodeInt(reason);
            encodeTime(packet, lastUpdated);
        } finally {
            lock.unlock();
        }
    }

    protected void encodeTime(OutPacket packet, long time) {
        long cur = System.currentTimeMillis();
        packet.encodeBool(time < cur);
        packet.encodeInt((int) (time >= cur ? time - cur : cur - time));
    }

    public void reset() {
        setValue(0);
        setReason(0);
        setLastUpdated(System.currentTimeMillis());
    }

    public int getValue() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    public int getResaon() {
        lock.lock();
        try {
            return reason;
        } finally {
            lock.unlock();
        }
    }

    public void setValue(int value) {
        lock.lock();
        try {
            this.value = value;
        } finally {
            lock.unlock();
        }
    }

    public void setReason(int reason) {
        lock.lock();
        try {
            this.reason = reason;
        } finally {
            lock.unlock();
        }
    }

    public long getLastUpdated() {
        lock.lock();
        try {
            return lastUpdated;
        } finally {
            lock.unlock();
        }
    }

    public void setLastUpdated(long lastUpdated) {
        lock.lock();
        try {
            this.lastUpdated = lastUpdated;
        } finally {
            lock.unlock();
        }
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
