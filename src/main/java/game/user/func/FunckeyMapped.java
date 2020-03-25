package game.user.func;

import java.util.Arrays;

/**
 * Created by MechAviv on 3/25/2020.
 */
public class FunckeyMapped {
    private static final FunckeyMapped[] DEFAULT_FKM = new FunckeyMapped[89];
    static {
        DEFAULT_FKM[0] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[1] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[2] = new FunckeyMapped(4, 0xA);
        DEFAULT_FKM[3] = new FunckeyMapped(4, 0xC);
        DEFAULT_FKM[4] = new FunckeyMapped(4, 0xD);
        DEFAULT_FKM[5] = new FunckeyMapped(4, 0x12);
        DEFAULT_FKM[6] = new FunckeyMapped(4, 0x18);
        DEFAULT_FKM[7] = new FunckeyMapped(4, 0x15);
        DEFAULT_FKM[8] = new FunckeyMapped(4, 0x1D);
        DEFAULT_FKM[9] = new FunckeyMapped(0, 0);// 1
        DEFAULT_FKM[10] = new FunckeyMapped(0, 0);// 2
        DEFAULT_FKM[11] = new FunckeyMapped(0, 0);// 3
        DEFAULT_FKM[12] = new FunckeyMapped(0, 0);// 4
        DEFAULT_FKM[13] = new FunckeyMapped(0, 0);// 5
        DEFAULT_FKM[14] = new FunckeyMapped(0, 0);// 6
        DEFAULT_FKM[15] = new FunckeyMapped(0, 0);// 7
        DEFAULT_FKM[16] = new FunckeyMapped(4, 8);
        DEFAULT_FKM[17] = new FunckeyMapped(4, 5);
        DEFAULT_FKM[18] = new FunckeyMapped(4, 0);
        DEFAULT_FKM[19] = new FunckeyMapped(4, 4);
        DEFAULT_FKM[20] = new FunckeyMapped(4, 0x1C);
        DEFAULT_FKM[21] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[22] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[23] = new FunckeyMapped(4, 1);
        DEFAULT_FKM[24] = new FunckeyMapped(4, 0x19);
        DEFAULT_FKM[25] = new FunckeyMapped(4, 0x13);
        DEFAULT_FKM[26] = new FunckeyMapped(4, 0xE);
        DEFAULT_FKM[27] = new FunckeyMapped(4, 0xF);
        DEFAULT_FKM[28] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[29] = new FunckeyMapped(5, 0x34);
        DEFAULT_FKM[30] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[31] = new FunckeyMapped(4, 2);
        DEFAULT_FKM[32] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[33] = new FunckeyMapped(4, 0x1A);
        DEFAULT_FKM[34] = new FunckeyMapped(4, 0x11);
        DEFAULT_FKM[35] = new FunckeyMapped(4, 0xB);
        DEFAULT_FKM[36] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[37] = new FunckeyMapped(4, 3);
        DEFAULT_FKM[38] = new FunckeyMapped(4, 0x14);
        DEFAULT_FKM[39] = new FunckeyMapped(0, 0x1B);
        DEFAULT_FKM[40] = new FunckeyMapped(0, 0x10);
        DEFAULT_FKM[41] = new FunckeyMapped(0, 0x17);
        DEFAULT_FKM[42] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[43] = new FunckeyMapped(4, 9);
        DEFAULT_FKM[44] = new FunckeyMapped(5, 0x32);
        DEFAULT_FKM[45] = new FunckeyMapped(5, 0x33);
        DEFAULT_FKM[46] = new FunckeyMapped(4, 6);
        DEFAULT_FKM[47] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[48] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[49] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[50] = new FunckeyMapped(4, 7);
        DEFAULT_FKM[51] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[52] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[53] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[54] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[55] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[56] = new FunckeyMapped(5, 0x35);
        DEFAULT_FKM[57] = new FunckeyMapped(5, 0x36);
        DEFAULT_FKM[58] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[59] = new FunckeyMapped(6, 0x64);
        DEFAULT_FKM[60] = new FunckeyMapped(6, 0x65);
        DEFAULT_FKM[61] = new FunckeyMapped(6, 0x66);
        DEFAULT_FKM[62] = new FunckeyMapped(6, 0x67);
        DEFAULT_FKM[63] = new FunckeyMapped(6, 0x68);
        DEFAULT_FKM[64] = new FunckeyMapped(6, 0x69);
        DEFAULT_FKM[65] = new FunckeyMapped(6, 0x6A);
        DEFAULT_FKM[66] = new FunckeyMapped(0, 0);// 1
        DEFAULT_FKM[67] = new FunckeyMapped(0, 0);// 2
        DEFAULT_FKM[68] = new FunckeyMapped(0, 0);// 3
        DEFAULT_FKM[69] = new FunckeyMapped(0, 0);// 4
        DEFAULT_FKM[70] = new FunckeyMapped(0, 0);// 5
        DEFAULT_FKM[71] = new FunckeyMapped(0, 0);// 6
        DEFAULT_FKM[72] = new FunckeyMapped(0, 0);// 7
        DEFAULT_FKM[73] = new FunckeyMapped(0, 0);// 8
        DEFAULT_FKM[74] = new FunckeyMapped(0, 0);// 9
        DEFAULT_FKM[75] = new FunckeyMapped(0, 0);// 10
        DEFAULT_FKM[76] = new FunckeyMapped(0, 0);// 11
        DEFAULT_FKM[77] = new FunckeyMapped(0, 0);// 12
        DEFAULT_FKM[78] = new FunckeyMapped(0, 0);// 13
        DEFAULT_FKM[79] = new FunckeyMapped(0, 0);// 14
        DEFAULT_FKM[80] = new FunckeyMapped(0, 0);// 15
        DEFAULT_FKM[81] = new FunckeyMapped(0, 0);// 16
        DEFAULT_FKM[82] = new FunckeyMapped(0, 0);// 17
        DEFAULT_FKM[83] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[84] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[85] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[86] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[87] = new FunckeyMapped(0, 0);
        DEFAULT_FKM[88] = new FunckeyMapped(0, 0);
    }

    public static FunckeyMapped[] getDefault() {
        return Arrays.copyOf(DEFAULT_FKM, 89);
    }

    private int type;
    private int ID;

    public FunckeyMapped(int type, int ID) {
        this.type = type;
        this.ID = ID;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
