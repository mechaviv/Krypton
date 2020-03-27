package network.security;

/**
 * Created by MechAviv on 3/27/2020.
 */
public class SocketKey {
    private int seqSnd;
    private int seqRcv;

    public SocketKey(int seqSnd, int seqRcv) {
        this.seqSnd = seqSnd;
        this.seqRcv = seqRcv;
    }

    public void updateSend() {
        this.seqSnd = IGCipher.innoHash(seqSnd, 4, 0);
    }

    public void updateRecv() {
        this.seqRcv = IGCipher.innoHash(seqRcv, 4, 0);
    }

    public int getSeqSnd() {
        return seqSnd;
    }

    public int getSeqRcv() {
        return seqRcv;
    }
}
