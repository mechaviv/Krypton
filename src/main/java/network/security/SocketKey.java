package network.security;

import util.Logger;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 3/27/2020.
 */
public class SocketKey {
    private int seqSnd;
    private int seqRcv;
    private final ReentrantLock lock;

    public SocketKey(int seqSnd, int seqRcv) {
        this.seqSnd = seqSnd;
        this.seqRcv = seqRcv;
        this.lock = new ReentrantLock();
    }

    public void updateSend() {
        lock.lock();
        try {
            this.seqSnd = IGCipher.innoHash(seqSnd, 4, 0);
        } finally {
            lock.unlock();
        }
    }

    public void updateRecv() {
        lock.lock();
        try {
            this.seqRcv = IGCipher.innoHash(seqRcv, 4, 0);
        } finally {
            lock.unlock();
        }
    }

    public int getSeqSnd() {
        lock.lock();
        try {
            return seqSnd;
        } finally {
            lock.unlock();
        }
    }

    public int getSeqRcv() {
        lock.lock();
        try {
            return seqRcv;
        } finally {
            lock.unlock();
        }
    }
}
