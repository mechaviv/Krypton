package game.field.life.mob;
import game.user.stat.Flag;
import network.packet.OutPacket;
import util.Logger;

/**
 * Created by MechAviv on 3/27/2020.
 */
public class MobStatHelper {
    private static void encodeSingleStat(MobStat ms, OutPacket packet, long cur, Flag flag, int mts) {
        if (flag.operatorAND(MobStats.getMask(mts)).isSet() && ms.getStatOption(mts) != 0) {
            MobStatOption opt = ms.getStat(mts);
            packet.encodeShort(opt.getOption());
            packet.encodeInt(opt.getReason());
            packet.encodeShort((int) ((opt.getDuration() - cur) / 500));
        }
    }

    public static void encodeTemporaryStats(MobStat ms, OutPacket packet, long cur, Flag toSend) {
        packet.encodeBuffer(toSend.toByteArray());
        encodeSingleStat(ms, packet, cur, toSend, MobStats.PAD);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.PDD);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MAD);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MDD);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.ACC);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.EVA);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Speed);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Stun);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Freeze);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Poison);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Seal);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Darkness);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.PowerUp);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MagicUp);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.PGuardUp);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MGuardUp);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.PImmune);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MImmune);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Doom);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Web);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.HardSkin);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Ambush);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Venom);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Blind);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.SealSkill);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Dazzle);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.PCounter);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MCounter);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.RiseByToss);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.BodyPressure);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Weakness);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.TimeBomb);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.Showdown);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.MagicCrash);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.DamagedElemAttr);
        encodeSingleStat(ms, packet, cur, toSend, MobStats.HealByDamage);
    }
}
