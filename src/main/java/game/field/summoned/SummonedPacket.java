package game.field.summoned;

import game.field.MovePath;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 * Created by MechAviv on 3/28/2020.
 */
public class SummonedPacket {
    public static OutPacket onMove(int characterID, int summonedID, MovePath mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.SummonedMove);
        packet.encodeInt(characterID);
        packet.encodeInt(summonedID);
        mp.encode(packet);
        return packet;
    }

    public static OutPacket onAttack(int characterID, int summonedID, int attackIDx, int damage, int templateID, int left) {
        OutPacket packet = new OutPacket(LoopbackPacket.SummonedAttack);
        packet.encodeInt(characterID);
        packet.encodeInt(summonedID);
        packet.encodeByte(attackIDx);
        packet.encodeInt(damage);
        if (attackIDx > -2) {
            packet.encodeInt(templateID);
            packet.encodeByte(left);
        }
        return packet;
    }

    public static OutPacket onSkill(int characterID, int summonedID, int action) {
        OutPacket packet = new OutPacket(LoopbackPacket.SummonedSkill);
        packet.encodeInt(characterID);
        packet.encodeInt(summonedID);
        packet.encodeByte(action);
        return packet;
    }

    public static OutPacket onHit(int characterID, int summonedID, int attackIDx, int damage, int templateID, int left) {
        OutPacket packet = new OutPacket(LoopbackPacket.SummonedHit);
        packet.encodeInt(characterID);
        packet.encodeInt(summonedID);
        packet.encodeByte(attackIDx);
        packet.encodeInt(damage);
        if (attackIDx > -2) {
            packet.encodeInt(templateID);
            packet.encodeByte(left);
        }
        return packet;
    }
}
