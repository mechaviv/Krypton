package game.user.stat.ts;

import game.user.stat.CharacterTemporaryStat;

/**
 * Created by MechAviv on 3/26/2020.
 */
public class TSIndex {
    public static final int 
            ENERGY_CHARGED  = 0x0,
            DASH_SPEED      = 0x1,
            DASH_JUMP       = 0x2,
            RIDE_VEHICLE    = 0x3,
            PARTY_BOOSTER   = 0x4,
            GUIDED_BULLET   = 0x5,
            UNDEAD          = 0x6,
            NO              = 0x7;

    public static int getCTSByIndex(int index) {
        switch (index) {
            case ENERGY_CHARGED:
                return CharacterTemporaryStat.EnergyCharged;
            case DASH_SPEED:
                return CharacterTemporaryStat.DashSpeed;
            case DASH_JUMP:
                return CharacterTemporaryStat.DashJump;
            case RIDE_VEHICLE:
                return CharacterTemporaryStat.RideVehicle;
            case PARTY_BOOSTER:
                return CharacterTemporaryStat.PartyBooster;
            case GUIDED_BULLET:
                return CharacterTemporaryStat.GuidedBullet;
            case UNDEAD:
                return CharacterTemporaryStat.Undead;
        }
        return -1;
    }

    public static int getIndexByCTS(int cts) {
        switch (cts) {
            case CharacterTemporaryStat.EnergyCharged:
                return ENERGY_CHARGED;
            case CharacterTemporaryStat.DashSpeed:
                return DASH_SPEED;
            case CharacterTemporaryStat.DashJump:
                return DASH_JUMP;
            case CharacterTemporaryStat.RideVehicle:
                return RIDE_VEHICLE;
            case CharacterTemporaryStat.PartyBooster:
                return PARTY_BOOSTER;
            case CharacterTemporaryStat.GuidedBullet:
                return GUIDED_BULLET;
            case CharacterTemporaryStat.Undead:
                return UNDEAD;
        }
        return -1;
    }
}
