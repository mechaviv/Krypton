package game.field.life.mob;

/**
 * Created by MechAviv on 3/28/2020.
 */
public class MobSkills {
    public static final int
            POWERUP         = 0x64,
            MAGICUP         = 0x65,
            PGUARDUP        = 0x66,
            MGUARDUP        = 0x67,
            HASTE           = 0x68,
            POWERUP_M       = 0x6E,
            MAGICUP_M       = 0x6F,
            PGUARDUP_M      = 0x70,
            MGUARDUP_M      = 0x71,
            HEAL_M          = 0x72,
            HASTE_M         = 0x73,
            SEAL            = 0x78,
            DARKNESS        = 0x79,
            WEAKNESS        = 0x7A,
            STUN            = 0x7B,
            CURSE           = 0x7C,
            POISON          = 0x7D,
            SLOW            = 0x7E,
            DISPEL          = 0x7F,
            ATTRACT         = 0x80,
            BANMAP          = 0x81,
            AREA_FIRE       = 0x82,
            AREA_POISON     = 0x83,
            REVERSE_INPUT   = 0x84,
            UNDEAD          = 0x85,
            STOPPORTION     = 0x86,
            STOPMOTION      = 0x87,
            FEAR            = 0x88,
            FROZEN          = 0x89,
            PHYSICAL_IMMUNE = 0x8C,
            MAGIC_IMMUNE    = 0x8D,
            HARDSKIN        = 0x8E,
            PCOUNTER        = 0x8F,
            MCOUNTER        = 0x90,
            PMCOUNTER       = 0x91,
            PAD             = 0x96,
            MAD             = 0x97,
            PDR             = 0x98,
            MDR             = 0x99,
            ACC             = 0x9A,
            EVA             = 0x9B,
            SPEED           = 0x9C,
            SEALSKILL       = 0x9D,
            BALROGCOUNTER   = 0x9E,
            MOBSILLL_SPREADSKILLFROMUSER = 0xA0,
            HEALBYDAMAGE    = 0xA1,
            BIND            = 0xA2,
            SUMMON          = 0xC8,
            SUMMON_CUBE     = 0xC9;

    public static final boolean isStatChange(int skillID) {
        switch (skillID) {
            case MobSkills.POWERUP:
            case MobSkills.MAGICUP:
            case MobSkills.PGUARDUP:
            case MobSkills.MGUARDUP:
            case MobSkills.HASTE:
            case MobSkills.SPEED:
            case MobSkills.PHYSICAL_IMMUNE:
            case MobSkills.MAGIC_IMMUNE:
            case MobSkills.HARDSKIN:
            case MobSkills.PAD:
            case MobSkills.MAD:
            case MobSkills.PDR:
            case MobSkills.MDR:
            case MobSkills.ACC:
            case MobSkills.EVA:
            case MobSkills.SEALSKILL:
                return true;
        }
        return false;
    }

    public static final boolean isUserStatChange(int skillID) {
        switch (skillID) {
            case MobSkills.SEAL:
            case MobSkills.DARKNESS:
            case MobSkills.WEAKNESS:
            case MobSkills.STUN:
            case MobSkills.CURSE:
            case MobSkills.POISON:
            case MobSkills.SLOW:
            case MobSkills.ATTRACT:
            case MobSkills.BANMAP:
            case MobSkills.DISPEL:
                return true;

        }
        return false;
    }

    public static final boolean isPartizanStatChange(int skillID) {
        switch (skillID) {
            case MobSkills.POWERUP_M:
            case MobSkills.MAGICUP_M:
            case MobSkills.PGUARDUP_M:
            case MobSkills.MGUARDUP_M:
            case MobSkills.HASTE_M:
                return true;
        }
        return false;
    }

    public static final boolean isPartizanOneTimeStatChange(int skillID) {
        switch (skillID) {
            case HEAL_M:
                return true;
        }
        return false;
    }

    public static final boolean isSummon(int skillID) {
        switch (skillID) {
            case SUMMON:
                return true;
        }
        return false;
    }

    public static final boolean isAffectArea(int skillID) {
        switch (skillID) {
            case AREA_FIRE:
            case AREA_POISON:
                return true;
        }
        return false;
    }
}
