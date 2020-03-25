package game.user;

import game.user.func.FunckeyMapped;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 * Created by MechAviv on 3/25/2020.
 */
public class FuncKeyMappedMan {
    public static OutPacket onInit(boolean funcKeyMappedInitEmpty, FunckeyMapped[] funcKeyMapped) {
        OutPacket packet = new OutPacket(LoopbackPacket.FuncKeyMappedInit);
        packet.encodeBool(funcKeyMappedInitEmpty);
        if (!funcKeyMappedInitEmpty) {
            for (int i = 0; i < 89; i++) {
                packet.encodeByte(funcKeyMapped[i].getType());
                packet.encodeInt(funcKeyMapped[i].getID());
            }
        }
        return packet;
    }

    public static OutPacket onPetConsumeItemInit(int petConsumeItemID) {
        OutPacket packet = new OutPacket(LoopbackPacket.PetConsumeItemInit);
        packet.encodeInt(petConsumeItemID);
        return packet;
    }

    public static OutPacket onPetConsumeMPItemInit(int petConsumeItemID) {
        OutPacket packet = new OutPacket(LoopbackPacket.PetConsumeMPItemInit);
        packet.encodeInt(petConsumeItemID);
        return packet;
    }
}
