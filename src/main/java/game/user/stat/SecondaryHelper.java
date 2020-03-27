package game.user.stat;

import game.user.stat.ts.TSIndex;
import network.packet.OutPacket;

import static game.user.stat.CharacterTemporaryStat.*;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class SecondaryHelper {
    private static void encodeForLocalStat(SecondaryStat ss, OutPacket packet, long cur, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            SecondaryStatOption opt = ss.getStat(cts);
            packet.encodeShort(opt.getOption());
            packet.encodeInt(opt.getReason());
            packet.encodeInt((int) (opt.getDuration() - cur));
        }
    }

    private static void addStatToFlag(SecondaryStat ss, Flag flag, Flag toSend, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet() && ss.getStatOption(cts) != 0) {
            toSend.performOR(CharacterTemporaryStat.getMask(cts));
        }
    }

    private static void encodeRemoteByteOption(SecondaryStat ss, OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeByte(ss.getStatOption(cts));
        }
    }

    private static void encodeRemoteShortOption(SecondaryStat ss, OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeShort(ss.getStatOption(cts));
        }
    }

    private static void encodeRemoteIntOption(SecondaryStat ss, OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeInt(ss.getStatOption(cts));
        }
    }

    private static void encodeRemoteReason(SecondaryStat ss, OutPacket packet, Flag flag, int cts) {
        if (flag.operatorAND(CharacterTemporaryStat.getMask(cts)).isSet()) {
            packet.encodeInt(ss.getStatReason(cts));
        }
    }

    public static void encodeForLocal(SecondaryStat ss, OutPacket packet, long cur, Flag toSend) {
        encodeForLocalStat(ss, packet, cur, toSend, PAD);
        encodeForLocalStat(ss, packet, cur, toSend, PDD);
        encodeForLocalStat(ss, packet, cur, toSend, MAD);
        encodeForLocalStat(ss, packet, cur, toSend, MDD);
        encodeForLocalStat(ss, packet, cur, toSend, ACC);
        encodeForLocalStat(ss, packet, cur, toSend, EVA);
        encodeForLocalStat(ss, packet, cur, toSend, Craft);
        encodeForLocalStat(ss, packet, cur, toSend, Speed);
        encodeForLocalStat(ss, packet, cur, toSend, Jump);
        encodeForLocalStat(ss, packet, cur, toSend, EMHP);
        encodeForLocalStat(ss, packet, cur, toSend, EMMP);
        encodeForLocalStat(ss, packet, cur, toSend, EPAD);
        encodeForLocalStat(ss, packet, cur, toSend, EPDD);
        encodeForLocalStat(ss, packet, cur, toSend, EMDD);
        encodeForLocalStat(ss, packet, cur, toSend, MagicGuard);
        encodeForLocalStat(ss, packet, cur, toSend, DarkSight);
        encodeForLocalStat(ss, packet, cur, toSend, Booster);
        encodeForLocalStat(ss, packet, cur, toSend, PowerGuard);
        encodeForLocalStat(ss, packet, cur, toSend, Guard);
        encodeForLocalStat(ss, packet, cur, toSend, SafetyDamage);
        encodeForLocalStat(ss, packet, cur, toSend, SafetyAbsorb);
        encodeForLocalStat(ss, packet, cur, toSend, MaxHP);
        encodeForLocalStat(ss, packet, cur, toSend, MaxMP);
        encodeForLocalStat(ss, packet, cur, toSend, Invincible);
        encodeForLocalStat(ss, packet, cur, toSend, SoulArrow);
        encodeForLocalStat(ss, packet, cur, toSend, Stun);
        encodeForLocalStat(ss, packet, cur, toSend, Poison);
        encodeForLocalStat(ss, packet, cur, toSend, Seal);
        encodeForLocalStat(ss, packet, cur, toSend, Darkness);
        encodeForLocalStat(ss, packet, cur, toSend, ComboCounter);
        encodeForLocalStat(ss, packet, cur, toSend, WeaponCharge);
        encodeForLocalStat(ss, packet, cur, toSend, DragonBlood);
        encodeForLocalStat(ss, packet, cur, toSend, HolySymbol);
        encodeForLocalStat(ss, packet, cur, toSend, MesoUp);
        encodeForLocalStat(ss, packet, cur, toSend, ShadowPartner);
        encodeForLocalStat(ss, packet, cur, toSend, PickPocket);
        encodeForLocalStat(ss, packet, cur, toSend, MesoGuard);
        encodeForLocalStat(ss, packet, cur, toSend, Thaw);
        encodeForLocalStat(ss, packet, cur, toSend, Weakness);
        encodeForLocalStat(ss, packet, cur, toSend, Curse);
        encodeForLocalStat(ss, packet, cur, toSend, Slow);
        encodeForLocalStat(ss, packet, cur, toSend, Morph);
        encodeForLocalStat(ss, packet, cur, toSend, Ghost);
        encodeForLocalStat(ss, packet, cur, toSend, Regen);
        encodeForLocalStat(ss, packet, cur, toSend, BasicStatUp);
        encodeForLocalStat(ss, packet, cur, toSend, Stance);
        encodeForLocalStat(ss, packet, cur, toSend, SharpEyes);
        encodeForLocalStat(ss, packet, cur, toSend, ManaReflection);
        encodeForLocalStat(ss, packet, cur, toSend, Attract);
        encodeForLocalStat(ss, packet, cur, toSend, SpiritJavelin);
        encodeForLocalStat(ss, packet, cur, toSend, Infinity);
        encodeForLocalStat(ss, packet, cur, toSend, Holyshield);
        encodeForLocalStat(ss, packet, cur, toSend, HamString);
        encodeForLocalStat(ss, packet, cur, toSend, Blind);
        encodeForLocalStat(ss, packet, cur, toSend, Concentration);
        encodeForLocalStat(ss, packet, cur, toSend, BanMap);
        encodeForLocalStat(ss, packet, cur, toSend, MaxLevelBuff);
        encodeForLocalStat(ss, packet, cur, toSend, Barrier);
        encodeForLocalStat(ss, packet, cur, toSend, DojangShield);
        encodeForLocalStat(ss, packet, cur, toSend, ReverseInput);
        encodeForLocalStat(ss, packet, cur, toSend, MesoUpByItem);
        encodeForLocalStat(ss, packet, cur, toSend, ItemUpByItem);
        encodeForLocalStat(ss, packet, cur, toSend, RespectPImmune);
        encodeForLocalStat(ss, packet, cur, toSend, RespectMImmune);
        encodeForLocalStat(ss, packet, cur, toSend, DefenseAtt);
        encodeForLocalStat(ss, packet, cur, toSend, DefenseState);
        encodeForLocalStat(ss, packet, cur, toSend, DojangBerserk);
        encodeForLocalStat(ss, packet, cur, toSend, DojangInvincible);
        encodeForLocalStat(ss, packet, cur, toSend, Spark);
        encodeForLocalStat(ss, packet, cur, toSend, SoulMasterFinal);
        encodeForLocalStat(ss, packet, cur, toSend, WindBreakerFinal);
        encodeForLocalStat(ss, packet, cur, toSend, ElementalReset);
        encodeForLocalStat(ss, packet, cur, toSend, WindWalk);
        encodeForLocalStat(ss, packet, cur, toSend, EventRate);
        encodeForLocalStat(ss, packet, cur, toSend, ComboAbilityBuff);
        encodeForLocalStat(ss, packet, cur, toSend, ComboDrain);
        encodeForLocalStat(ss, packet, cur, toSend, ComboBarrier);
        encodeForLocalStat(ss, packet, cur, toSend, BodyPressure);
        encodeForLocalStat(ss, packet, cur, toSend, SmartKnockback);
        encodeForLocalStat(ss, packet, cur, toSend, RepeatEffect);
        encodeForLocalStat(ss, packet, cur, toSend, ExpBuffRate);
        encodeForLocalStat(ss, packet, cur, toSend, IncEffectHPPotion);
        encodeForLocalStat(ss, packet, cur, toSend, IncEffectMPPotion);
        encodeForLocalStat(ss, packet, cur, toSend, StopPortion);
        encodeForLocalStat(ss, packet, cur, toSend, StopMotion);
        encodeForLocalStat(ss, packet, cur, toSend, Fear);
        encodeForLocalStat(ss, packet, cur, toSend, EvanSlow);
        encodeForLocalStat(ss, packet, cur, toSend, MagicShield);
        encodeForLocalStat(ss, packet, cur, toSend, MagicResistance);
        encodeForLocalStat(ss, packet, cur, toSend, SoulStone);
        encodeForLocalStat(ss, packet, cur, toSend, Flying);
        encodeForLocalStat(ss, packet, cur, toSend, Frozen);
        encodeForLocalStat(ss, packet, cur, toSend, AssistCharge);
        encodeForLocalStat(ss, packet, cur, toSend, Enrage);
        encodeForLocalStat(ss, packet, cur, toSend, SuddenDeath);
        encodeForLocalStat(ss, packet, cur, toSend, NotDamaged);
        encodeForLocalStat(ss, packet, cur, toSend, FinalCut);
        encodeForLocalStat(ss, packet, cur, toSend, ThornsEffect);
        encodeForLocalStat(ss, packet, cur, toSend, SwallowAttackDamage);
        encodeForLocalStat(ss, packet, cur, toSend, MorewildDamageUp);
        encodeForLocalStat(ss, packet, cur, toSend, Mine);
        encodeForLocalStat(ss, packet, cur, toSend, Cyclone);
        encodeForLocalStat(ss, packet, cur, toSend, SwallowCritical);
        encodeForLocalStat(ss, packet, cur, toSend, SwallowMaxMP);
        encodeForLocalStat(ss, packet, cur, toSend, SwallowDefence);
        encodeForLocalStat(ss, packet, cur, toSend, SwallowEvasion);
        encodeForLocalStat(ss, packet, cur, toSend, Conversion);
        encodeForLocalStat(ss, packet, cur, toSend, Revive);
        encodeForLocalStat(ss, packet, cur, toSend, Sneak);
        encodeForLocalStat(ss, packet, cur, toSend, Mechanic);
        encodeForLocalStat(ss, packet, cur, toSend, Aura);
        encodeForLocalStat(ss, packet, cur, toSend, DarkAura);
        encodeForLocalStat(ss, packet, cur, toSend, BlueAura);
        encodeForLocalStat(ss, packet, cur, toSend, YellowAura);
        encodeForLocalStat(ss, packet, cur, toSend, SuperBody);
        encodeForLocalStat(ss, packet, cur, toSend, MorewildMaxHP);
        encodeForLocalStat(ss, packet, cur, toSend, Dice);
        encodeForLocalStat(ss, packet, cur, toSend, BlessingArmor);
        encodeForLocalStat(ss, packet, cur, toSend, DamR);
        encodeForLocalStat(ss, packet, cur, toSend, TeleportMasteryOn);
        encodeForLocalStat(ss, packet, cur, toSend, CombatOrders);
        encodeForLocalStat(ss, packet, cur, toSend, Beholder);
        encodeForLocalStat(ss, packet, cur, toSend, SummonBomb);
    }

    public static void encodeForRemote(SecondaryStat ss, OutPacket packet, Flag toSend) {
        encodeRemoteByteOption(ss, packet, toSend, Speed);
        encodeRemoteByteOption(ss, packet, toSend, ComboCounter);
        encodeRemoteReason(ss, packet, toSend, WeaponCharge);

        encodeRemoteReason(ss, packet, toSend, Stun);
        encodeRemoteReason(ss, packet, toSend, Darkness);
        encodeRemoteReason(ss, packet, toSend, Seal);
        encodeRemoteReason(ss, packet, toSend, Weakness);
        encodeRemoteReason(ss, packet, toSend, Curse);
        encodeRemoteShortOption(ss, packet, toSend, Poison);
        encodeRemoteReason(ss, packet, toSend, Poison);
        encodeRemoteReason(ss, packet, toSend, ShadowPartner);
        encodeRemoteShortOption(ss, packet, toSend, Morph);
        encodeRemoteShortOption(ss, packet, toSend, Ghost);
        encodeRemoteReason(ss, packet, toSend, Attract);
        encodeRemoteIntOption(ss, packet, toSend, SpiritJavelin);
        encodeRemoteReason(ss, packet, toSend, BanMap);
        encodeRemoteReason(ss, packet, toSend, Barrier);
        encodeRemoteReason(ss, packet, toSend, DojangShield);
        encodeRemoteReason(ss, packet, toSend, ReverseInput);
        encodeRemoteIntOption(ss, packet, toSend, RespectPImmune);
        encodeRemoteIntOption(ss, packet, toSend, RespectMImmune);
        encodeRemoteIntOption(ss, packet, toSend, DefenseAtt);
        encodeRemoteIntOption(ss, packet, toSend, DefenseState);
        encodeRemoteReason(ss, packet, toSend, RepeatEffect);
        encodeRemoteReason(ss, packet, toSend, StopPortion);
        encodeRemoteReason(ss, packet, toSend, StopMotion);
        encodeRemoteReason(ss, packet, toSend, Fear);
        encodeRemoteIntOption(ss, packet, toSend, MagicShield);
        encodeRemoteReason(ss, packet, toSend, Frozen);
        encodeRemoteReason(ss, packet, toSend, SuddenDeath);
        encodeRemoteReason(ss, packet, toSend, FinalCut);
        encodeRemoteByteOption(ss, packet, toSend, Cyclone);
        encodeRemoteReason(ss, packet, toSend, Mechanic);
        encodeRemoteReason(ss, packet, toSend, DarkAura);
        encodeRemoteReason(ss, packet, toSend, BlueAura);
        encodeRemoteReason(ss, packet, toSend, YellowAura);
    }
    
    public static void addStatToFlag(SecondaryStat ss, Flag flag, Flag toSend) {
        addStatToFlag(ss, flag, toSend, Speed);
        addStatToFlag(ss, flag, toSend, ComboCounter);
        addStatToFlag(ss, flag, toSend, WeaponCharge);
        addStatToFlag(ss, flag, toSend, Stun);
        addStatToFlag(ss, flag, toSend, Darkness);
        addStatToFlag(ss, flag, toSend, Seal);
        addStatToFlag(ss, flag, toSend, Weakness);
        addStatToFlag(ss, flag, toSend, Curse);
        addStatToFlag(ss, flag, toSend, Poison);
        addStatToFlag(ss, flag, toSend, ShadowPartner);
        addStatToFlag(ss, flag, toSend, DarkSight);
        addStatToFlag(ss, flag, toSend, SoulArrow);
        addStatToFlag(ss, flag, toSend, Morph);
        addStatToFlag(ss, flag, toSend, Ghost);
        addStatToFlag(ss, flag, toSend, Attract);
        addStatToFlag(ss, flag, toSend, SpiritJavelin);
        addStatToFlag(ss, flag, toSend, BanMap);
        addStatToFlag(ss, flag, toSend, Barrier);
        addStatToFlag(ss, flag, toSend, DojangShield);
        addStatToFlag(ss, flag, toSend, ReverseInput);
        addStatToFlag(ss, flag, toSend, RespectPImmune);
        addStatToFlag(ss, flag, toSend, RespectMImmune);
        addStatToFlag(ss, flag, toSend, DefenseAtt);
        addStatToFlag(ss, flag, toSend, DefenseState);
        addStatToFlag(ss, flag, toSend, DojangBerserk);
        addStatToFlag(ss, flag, toSend, DojangInvincible);
        addStatToFlag(ss, flag, toSend, WindWalk);
        addStatToFlag(ss, flag, toSend, RepeatEffect);
        addStatToFlag(ss, flag, toSend, StopPortion);
        addStatToFlag(ss, flag, toSend, StopMotion);
        addStatToFlag(ss, flag, toSend, Fear);
        addStatToFlag(ss, flag, toSend, MagicShield);
        addStatToFlag(ss, flag, toSend, Flying);
        addStatToFlag(ss, flag, toSend, Frozen);
        addStatToFlag(ss, flag, toSend, SuddenDeath);
        addStatToFlag(ss, flag, toSend, FinalCut);
        addStatToFlag(ss, flag, toSend, Cyclone);
        addStatToFlag(ss, flag, toSend, Sneak);
        addStatToFlag(ss, flag, toSend, MorewildDamageUp);
        addStatToFlag(ss, flag, toSend, Mechanic);
        addStatToFlag(ss, flag, toSend, DarkAura);
        addStatToFlag(ss, flag, toSend, BlueAura);
        addStatToFlag(ss, flag, toSend, YellowAura);
        addStatToFlag(ss, flag, toSend, BlessingArmor);

        for (int index = 0; index < TSIndex.NO; index++) {
            toSend.performOR(CharacterTemporaryStat.getMask(TSIndex.getCTSByIndex(index)));
        }
    }
}
