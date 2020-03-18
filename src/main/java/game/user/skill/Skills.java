/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game.user.skill;

/**
 * All MapleStory Skills
 *
 * @author Eric
 */
public class Skills {
    /**
     * Beginner (Novice)
     */
    public static class Beginner extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Beginner">
        public static final int
                MULTI_PET = 8,
                BLESS_OF_NYMPH = 12,
                THROW_SNAIL = 1000,
                REGENERATION = 1001,
                MOVING_WITH_ACTIVITY = 1002,
                SOUL_OF_CRAFTMAN = 1003,
                MONSTER_RIDING = 1004,
                MAXLEVEL_ECHOBUFF = 1005,
                DAMAGEMETER = 1006,
                MAKER = 1007,
                BAMBOO = 1009,
                INVINCIBLE = 1010,
                BERSERK = 1011,
                DISABLE_EVENT_RIDING = 1013,
                DISABLE_EVENT_RIDING_DASH = 1014,
                DISABLE_EVENT_RIDING_REACTOR = 1015,
                DISABLE_YETI_EVENT_RIDING = 1017,
                DISABLE_YETI_EVENT_RIDING2 = 1018,
                DISABLE_BROOM_EVENT_RIDING = 1019,
                MASSACRE = 1020,
                WOODENHORSE_EVENT_RIDING = 1025,
                FLYING_SKILL = 1026,
                KROKO_EVENT_RIDING = 1027,
                NAKED_EVENT_RIDING = 1028,
                PINK_SCOOTER_EVENT_RIDING = 1029,
                FLYING_CLOUD_EVENT_RIDING = 1030,
                BALROG_EVENT_RIDING = 1031,
                KART_EVENT_RIDING = 1033,
                ZD_TIGER_EVENT_RIDING = 1034,
                MISTBALROG_EVENT_RIDING = 1035,
                LIONS_EVENT_RIDING = 1036,
                UNICORN_EVENT_RIDING = 1037,
                LOWRIDER_EVENT_RIDING = 1038,
                REDTRUCK_EVENT_RIDING = 1039,
                GARGOYLES_EVENT_RIDING = 1040,
                HOLLY_BIRD_EVENT_RIDING = 1042,
                ORANGE_MUSHROOM_EVENT_RIDING = 1044,
                SPACE_EVENT_RIDING = 1046,
                SPACE_EVENT_RIDING_DASH = 1047,
                SPACE_EVENT_RIDING_REACTOR = 1048,
                NIGHTMARE_EVENT_RIDING = 1049,
                YETI_EVENT_RIDING = 1050,
                OSTRICH_EVENT_RIDING = 1051,
                BEAR_BALOON_EVENT_RIDING = 1052,
                TRANS_ROBOT_EVENT_RIDING = 1053,
                CHICKEN_EVENT_RIDING = 1054,
                MOTORBIKE_EVENT_RIDING = 1063,
                POWERED_SUIT_EVENT_RIDING = 1064,
                VISITOR_EVENT_RIDING = 1065,
                VISITOR_MORPH_SKILL_NORMAL = 1066,
                VISITOR_MORPH_SKILL_SKILL = 1067,
                VISITOR_OWL_RIDING = 1069,
                VISITOR_MOTHERSHIP_RIDING = 1070,
                VISITOR_OS3A_RIDING = 1071,
                HASTE = 8000,
                MYSTIC_DOOR = 8001,
                SHARP_EYES = 8002,
                HYPER_BODY = 8003,
                MONSTER_HANDICAP_BEGIN = 9000,
                MONSTER_HANDICAP_END = 9002
                        ;
        // </editor-fold>
    }

    /**
     * Warrior
     */
    public static class Warrior extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Warrior">
        public static final int
                // not sure
                ImproveBasic = 1000000,
                Endure = 1000002,

        // exists
        MHPInc = 1000006,
                IronBody = 1001003,
                PowerStrike = 1001004,
                SlashBlast = 1001005
                        ;
        // </editor-fold>
    }

    /**
     * Fighter
     */
    public static class Fighter extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Fighter">
        public static final int
                // not sure
                WeaponMasteryEx = 1100001,
                FinalAttackEx = 1100003,
                WeaponBoosterEx = 1101005,

        // exists
        WeaponMastery = 1100000,
                FinalAttack = 1100002,
                ImproveBasic = 1100009,
                WeaponBooster = 1101004,
                Fury = 1101006,
                PowerGuard = 1101007,
                GroundSmash = 1101008
                        ;
        // </editor-fold>
    }

    /**
     * Crusader
     */
    public static class Crusader extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Crusader">
        public static final int
                UpgradeMpRecovery = 1110000,
                ChanceAttack      = 1110009,
                ComboAttack       = 1111002,
                Panic             = 1111003,
                Coma              = 1111005,
                MagicCrash        = 1111007,
                Shout             = 1111008,
                Brandish          = 1111010
                        ;
        // </editor-fold>
    }

    /**
     * Hero
     */
    public static class Hero extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Hero">
        public static final int
                AdvancedCombo   = 1120003,
                HardSkin        = 1120004,
                CombatMastery   = 1120012,
                MapleHero       = 1121000,
                MonsterMagnet   = 1121001,
                Stance          = 1121002,
                Rush            = 1121006,
                BraveSlash      = 1121008,
                Enrage          = 1121010,
                HEROS_WILL      = 1121011
                        ;
        // </editor-fold>
    }

    /**
     * Page
     */
    public static class Page extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Page">
        public static final int
                // not sure
                WeaponMasteryEx = 1200001,
                FinalAttackEx = 1200003,
                WeaponBoosterEx = 1201005,
        // exists
        WeaponMastery = 1200000,
                FinalAttack = 1200002,
                ImproveBasic = 1200009,
                WeaponBooster = 1201004,
                Threaten = 1201006,
                PowerGuard = 1201007,
                GroundSmash = 1201008
                        ;
        // </editor-fold>
    }

    /**
     * Knight
     */
    public static class Knight extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Knight">
        public static final int
                SHIELD_MASTERY      = 1210001,
                CHARGE_BLOW         = 1211002,
                FIRE_CHARGE         = 1211004,
                ICE_CHARGE          = 1211006,
                LIGHTNING_CHARGE    = 1211008,
                MAGIC_CRASH         = 1211009,
                RESTORATION         = 1211010,
                COMBAT_ORDERS       = 1211011
                        ;
        // </editor-fold>
    }

    /**
     * Paladin
     */
    public static class Paladin extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Paladin">
        public static final int
                HARD_SKIN           = 1220005,
                BLOCKING            = 1220006,
                ADVANCED_CHARGE     = 1220010,
                BLESSING_ARMOR      = 1220013,
                MAPLE_HERO          = 1221000,
                STANCE              = 1221002,
                DIVINE_CHARGE       = 1221004,
                RUSH                = 1221007,
                BLAST               = 1221009,
                SANCTUARY           = 1221011,
                HEROS_WILL          = 1221012
                        ;
        // </editor-fold>
    }

    /**
     * Spearman
     */
    public static class Spearman extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Spearman">
        public static final int
                // not sure
                WeaponMasteryEx     = 1300001,
                FinalAttackEx       = 1300003,
                WeaponBoosterEx     = 1301005,

        // exists
        WeaponMastery       = 1300000,
                FinalAttack         = 1300002,
                ImproveBasic        = 1300009,
                WeaponBooster       = 1301004,
                IronWall            = 1301006,
                HyperBody           = 1301007,
                GroundSmash         = 1301008
                        ;
        // </editor-fold>
    }

    /**
     * Dragon Knight
     */
    public static class DragonKnight extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dragon Knight">
        public static final int
                ELEMENT_RESISTANCE  = 1310000,
                DRAGON_JUDGEMENT    = 1310009,
                DRAGON_BURSTER      = 1311001,
                DRAGON_THRESHER     = 1311003,
                SACRIFICE           = 1311005,
                DRAGON_ROAR         = 1311006,
                MAGIC_CRASH         = 1311007,
                DRAGON_BLOOD        = 1311008
                        ;
        // </editor-fold>
    }

    /**
     * Dark Knight
     */
    public static class DarkKnight extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dark Knight">
        public static final int
                HARD_SKIN           = 1320005,
                DARK_FORCE          = 1320006,
                BEHOLDERS_HEALING   = 1320008,
                BEHOLDERS_BUFF      = 1320009,
                BEHOLDERS_REVENGE   = 1320011,
                MAPLE_HERO          = 1321000,
                MONSTER_MAGNET      = 1321001,
                STANCE              = 1321002,
                RUSH                = 1321003,
                BEHOLDER            = 1321007,
                HEROS_WILL          = 1321010
                        ;
        // </editor-fold>
    }

    /**
     * Magician
     */
    public static class Magician extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Magician">
        public static final int
                // not sure
                ImproveBasic = 2000000,

        // exists
        MMPInc = 2000006,
                MagicGuard = 2001002,
                MagicArmor = 2001003,
                EnergyBolt = 2001004,
                MagicClaw = 2001005
                        ;
        // </editor-fold>
    }

    /**
     * Fire Poison Wizard
     */
    public static class Wizard1 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Wizard1">
        public static final int
                MPEater = 2100000,
                SpellMastery = 2100006,
                Meditation = 2101001,
                Teleport = 2101002,
                Slow = 2101003,
                FireArrow = 2101004,
                PoisonBreath = 2101005
                        ;
        // </editor-fold>
    }

    /**
     * Fire Poison Mage
     */
    public static class Mage1 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Mage 1">
        public static final int
                PARTIAL_RESISTANCE = 2110000,
                ELEMENT_AMPLIFICATION = 2110001,
                EXPLOSION = 2111002,
                POISON_MIST = 2111003,
                SEAL = 2111004,
                MAGIC_BOOSTER = 2111005,
                MAGIC_COMPOSITION = 2111006,
                TELEPORT_MASTERY = 2111007,
                ELEMENTAL_RESET = 2111008
                        ;
        // </editor-fold>
    }

    /**
     * Fire Poison Arch Mage
     */
    public static class ArchMage1 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Arch Mage 1">
        public static final int
                MASTER_MAGIC = 2120009,
                MAPLE_HERO = 2121000,
                BIGBANG = 2121001,
                MANA_REFLECTION = 2121002,
                FIRE_DEMON = 2121003,
                INFINITY = 2121004,
                IFRIT = 2121005,
                PARALYZE = 2121006,
                METEOR = 2121007,
                HEROS_WILL = 2121008
                        ;
        // </editor-fold>
    }

    /**
     * Ice Lightning Wizard
     */
    public static class Wizard2 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Wizard2">
        public static final int
                MPEater = 2200000,
                SpellMastery = 2200006,
                Meditation = 2201001,
                Teleport = 2201002,
                Slow = 2201003,
                ColdBeam = 2201004,
                ThunderBolt = 2201005
                        ;
        // </editor-fold>
    }

    /**
     * Ice Lightning Mage
     */
    public static class Mage2 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Mage 2">
        public static final int
                PARTIAL_RESISTANCE = 2210000,
                ELEMENT_AMPLIFICATION = 2210001,
                ICE_STRIKE = 2211002,
                THUNDER_SPEAR = 2211003,
                SEAL = 2211004,
                MAGIC_BOOSTER = 2211005,
                MAGIC_COMPOSITION = 2211006,
                TELEPORT_MASTERY = 2211007,
                ELEMENTAL_RESET = 2211008
                        ;
        // </editor-fold>
    }

    /**
     * Ice Lightning Arch Mage
     */
    public static class ArchMage2 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Arch Mage 2">
        public static final int
                MASTER_MAGIC = 2220009,
                MAPLE_HERO = 2221000,
                BIGBANG = 2221001,
                MANA_REFLECTION = 2221002,
                ICE_DEMON = 2221003,
                INFINITY = 2221004,
                ELQUINES = 2221005,
                CHAIN_LIGHTNING = 2221006,
                BLIZZARD = 2221007,
                HEROS_WILL = 2221008
                        ;
        // </editor-fold>
    }

    /**
     * Cleric
     */
    public static class Cleric extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Cleric">
        public static final int
                MPEater = 2300000,
                SpellMastery = 2300006,
                Teleport = 2301001,
                Heal = 2301002,
                Invincible = 2301003,
                Bless = 2301004,
                HolyArrow = 2301005
                        ;
        // </editor-fold>
    }

    /**
     * Priest
     */
    public static class Priest extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Priest">
        public static final int
                ELEMENT_RESISTANCE = 2310000,
                HOLY_FOCUS = 2310008,
                DISPEL = 2311001,
                MYSTIC_DOOR = 2311002,
                HOLY_SYMBOL = 2311003,
                SHINING_RAY = 2311004,
                DOOM = 2311005,
                SUMMON_DRAGON = 2311006,
                TELEPORT_MASTERY = 2311007
                        ;
        // </editor-fold>
    }

    /**
     * Bishop
     */
    public static class Bishop extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Bishop">
        public static final int
                MASTER_MAGIC = 2320010,
                MAPLE_HERO = 2321000,
                BIGBANG = 2321001,
                MANA_REFLECTION = 2321002,
                BAHAMUT = 2321003,
                INFINITY = 2321004,
                HOLY_SHIELD = 2321005,
                RESURRECTION = 2321006,
                ANGELS_RAY = 2321007,
                GENESIS = 2321008,
                HEROS_WILL = 2321009
                        ;
        // </editor-fold>
    }

    /**
     * Archer/Bowman
     */
    public static class Archer extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Archer">
        public static final int
                // not sure
                AmazonBlessing = 3000000,
        // exists
        CriticalShot = 3000001,
                AmazonEye = 3000002,
                Focus = 3001003,
                ArrowBlow = 3001004,
                DoubleShot = 3001005
                        ;
        // </editor-fold>
    }

    /**
     * Hunter
     */
    public static class Hunter extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Hunter">
        public static final int
                BowMastery = 3100000,
                FinalAttack_Bow = 3100001,
                BowBooster = 3101002,
                PowerKnockback = 3101003,
                SoulArrow_Bow = 3101004,
                ArrowBomb = 3101005,
                ImproveBasic = 3101006
                        ;
        // </editor-fold>
    }

    /**
     * Ranger
     */
    public static class Ranger extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Ranger">
        public static final int
                THRUST = 3110000,
                MORTAL_BLOW = 3110001,
                DODGE = 3110007,
                PUPPET = 3111002,
                FIRE_SHOT = 3111003,
                ARROW_RAIN = 3111004,
                SILVER_HAWK = 3111005,
                STRAFE = 3111006
                        ;
        // </editor-fold>
    }

    /**
     * Bowmaster
     */
    public static class Bowmaster extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Bowmaster">
        public static final int
                BOW_EXPERT = 3120005,
                VENGEANCE = 3120010,
                MARKMAN_SHIP = 3120011,
                MAPLE_HERO = 3121000,
                SHARP_EYES = 3121002,
                DRAGON_PULSE = 3121003,
                STORM_ARROW = 3121004,
                PHOENIX = 3121006,
                HAMSTRING = 3121007,
                CONCENTRATION = 3121008,
                HEROS_WILL = 3121009
                        ;
        // </editor-fold>
    }

    /**
     * Crossbowman
     */
    public static class Crossbowman extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Crossbowman">
        public static final int
                CrossbowMastery = 3200000,
                FinalAttack_Crossbow = 3200001,
                ImproveBasic = 3200006,
                CrossbowBooster = 3201002,
                PowerKnockback = 3201003,
                SoulArrow_Crossbow = 3201004,
                IronArrow = 3201005
                        ;
        // </editor-fold>
    }

    /**
     * Sniper
     */
    public static class Sniper extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Sniper">
        public static final int
                THRUST = 3210000,
                MORTAL_BLOW = 3210001,
                DODGE = 3210007,
                PUPPET = 3211002,
                ICE_SHOT = 3211003,
                ARROW_ERUPTION = 3211004,
                GOLDEN_EAGLE = 3211005,
                STRAFE = 3211006
                        ;
        // </editor-fold>
    }

    /**
     * Crossbow Master
     */
    public static class CrossbowMaster extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Crossbow Master">
        public static final int
                CROSSBOW_EXPERT = 3220004,
                MARKMAN_SHIP = 3220009,
                ULTIMATE_STRAFE = 3220010,
                MAPLE_HERO = 3221000,
                PIERCING = 3221001,
                SHARP_EYES = 3221002,
                DRAGON_PULSE = 3221003,
                FREEZER = 3221005,
                BLIND = 3221006,
                SNIPING = 3221007,
                HEROS_WILL = 3221008
                        ;
        // </editor-fold>
    }
    /**
     * Rogue
     */
    public static class Rogue extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Rogue">
        public static final int
                NimbleBody = 4000000,
                KeenEyes = 4000001,
                Disorder = 4001002,
                DarkSight = 4001003,
                DoubleStab_Dagger = 4001334,
                LuckySeven = 4001344
                        ;
        // </editor-fold>
    }

    /**
     * Assassin
     */
    public static class Assassin extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Assassin">
        public static final int
                // not sure
                Endure = 4100002,
        // exists
        JavelinMastery = 4100000,
                CriticalThrow = 4100001,
                ShadowResistance = 4100006,
                JavelinBooster = 4101003,
                Haste = 4101004,
                Drain = 4101005
                        ;
        // </editor-fold>
    }

    /**
     * Hermit
     */
    public static class Hermit extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Hermit">
        public static final int
                ALCHEMIST = 4110000,
                MESO_UP = 4111001,
                SHADOW_PARTNER = 4111002,
                SHADOW_WEB = 4111003,
                SHADOW_MESO = 4111004,
                AVENGER = 4111005,
                FLASH_JUMP = 4111006,
                SHADOW_MIRROR = 4111007
                        ;
        // </editor-fold>
    }

    /**
     * Night Lord
     */
    public static class NightLord extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Night Lord">
        public static final int
                FAKE = 4120002,
                VENOM = 4120005,
                EXPERT_JAVELIN = 4120010,
                MAPLE_HERO = 4121000,
                SHOWDOWN = 4121003,
                NINJA_AMBUSH = 4121004,
                SPIRIT_JAVELIN = 4121006,
                TRIPLE_THROW = 4121007,
                NINJA_STORM = 4121008,
                HEROS_WILL = 4121009
                        ;
        // </editor-fold>
    }

    /**
     * Bandit
     */
    public static class Thief extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Thief">
        public static final int
                // not sure
                Endure = 4200001,
        // exists
        DaggerMastery = 4200000,
                ShadowResistance = 4200006,
                DaggerBooster = 4201002,
                Haste = 4201003,
                Steal = 4201004,
                SavageBlow_Dagger = 4201005
                        ;
        // </editor-fold>
    }

    /**
     * Chief Bandit
     */
    public static class ThiefMaster extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Thief Master">
        public static final int
                SHIELD_MASTERY = 4210000,
                CHAKRA = 4211001,
                ASSAULTER = 4211002,
                PICKPOCKET = 4211003,
                THIEVES = 4211004,
                MESO_GUARD = 4211005,
                MESO_EXPLOSION = 4211006,
                SHADOW_MIRROR = 4211007,
                SHADOW_PARTNER = 4211008,
                FLASH_JUMP = 4211009
                        ;
        // </editor-fold>
    }

    /**
     * Shadower
     */
    public static class Shadower extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Shadower">
        public static final int
                FAKE = 4220002,
                VENOM = 4220005,
                GRID = 4220009,
                MAPLE_HERO = 4221000,
                ASSASSINATION = 4221001,
                SHOWDOWN = 4221003,
                NINJA_AMBUSH = 4221004,
                SMOKE_SHELL = 4221006,
                BOOMERANG_STEP = 4221007,
                HEROS_WILL = 4221008
                        ;
        // </editor-fold>
    }

    /**
     * Blade Recruit
     */
    public static class Dual1 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dual 1">
        public static final int
                DUAL_MASTERY = 4300000,
                TRIPLE_STAB = 4301001,
                DUAL_BOOSTER = 4301002
                        ;
        // </editor-fold>
    }

    /**
     * Blade Acolyte
     */
    public static class Dual2 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dual 2">
        public static final int
                SHADOW_RESISTANCE = 4310004,
                SELF_HASTE = 4311001,
                FATAL_BLOW = 4311002,
                SLASH_STORM = 4311003
                        ;
        // </editor-fold>
    }

    /**
     * Blade Specialist
     */
    public static class Dual3 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dual 3">
        public static final int
                HUSTLE_DASH = 4321000,
                HUSTLE_RUSH = 4321001,
                FLASH_BANG = 4321002,
                FLASH_JUMP = 4321003
                        ;
        // </editor-fold>
    }

    /**
     * Blade Lord
     */
    public static class Dual4 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dual 4">
        public static final int
                ADVANCED_DARK_SIGHT = 4330001,
                BLOODY_STORM = 4331000,
                MIRROR_IMAGING = 4331002,
                OWL_DEATH = 4331003,
                UPPER_STAB = 4331004,
                FLYING_ASSAULTER = 4331005
                        ;
        // </editor-fold>
    }

    /**
     * Blade Master
     */
    public static class Dual5 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Dual 5">
        public static final int
                VENOM = 4340001,
                MAPLE_HERO = 4341000,
                FINAL_CUT = 4341002,
                MONSTER_BOMB = 4341003,
                SUDDEN_RAID = 4341004,
                ASSASSINATION = 4341005,
                DUMMY_EFFECT = 4341006,
                THORNS_EFFECT = 4341007,
                HEROS_WILL = 4341008
                        ;
        // </editor-fold>
    }

    /**
     * Pirate
     */
    public static class Pirate extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Pirate">
        public static final int
                QUICKMOTION = 5000000,
                STRAIGHT = 5001001,
                SOMERSAULT = 5001002,
                DOUBLE_FIRE = 5001003,
                DASH = 5001005
                        ;
        // </editor-fold>
    }

    /**
     * Brawler
     */
    public static class InFighter extends Skills {
        // <editor-fold defaultstate="collapsed" desc="InFighter">
        public static final int
                KNUCKLE_MASTERY = 5100001,
                CRITICAL_PUNCH = 5100008,
                MHP_INC = 5100009,
                BACKSPIN_BLOW = 5101002,
                DOUBLE_UPPER = 5101003,
                SCREW_PUNCH = 5101004,
                MP_RECOVERY = 5101005,
                KNUCKLE_BOOSTER = 5101006,
                OAK_CASK = 5101007
                        ;
        // </editor-fold>
    }

    /**
     * Marauder
     */
    public static class Buccaneer extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Buccaneer">
        public static final int
                STUN_MASTERY = 5110000,
                ENERGY_CHARGE = 5110001,
                INFIGHTING_MASTERY = 5110008,
                ENERGY_BURSTER = 5111002,
                ENERGY_DRAIN = 5111004,
                TRANSFORM = 5111005,
                SHOCKWAVE = 5111006,
                DICE = 5111007
                        ;
        // </editor-fold>
    }

    /**
     * Buccaneer
     */
    public static class Viper extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Viper">
        public static final int
                COUNTER_ATTACK = 5120011,
                MAPLE_HERO = 5121000,
                DRAGON_STRIKE = 5121001,
                ENERGY_ORB = 5121002,
                SUPER_TRANSFORM = 5121003,
                DEMOLITION = 5121004,
                SNATCH = 5121005,
                FIST = 5121007,
                HEROS_WILL = 5121008,
                WIND_BOOSTER = 5121009,
                TIME_LEAP = 5121010
                        ;
        // </editor-fold>
    }

    /**
     * Gunslinger
     */
    public static class Gunslinger extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Gunslinger">
        public static final int
                GUN_MASTERY = 5200000,
                CRITICAL_SHOT = 5200007,
                INVISIBLE_SHOT = 5201001,
                THROWING_BOMB = 5201002,
                GUN_BOOSTER = 5201003,
                FAKE_SHOT = 5201004,
                WINGS = 5201005,
                BACKSTEP_SHOT = 5201006
                        ;
        // </editor-fold>
    }

    /**
     * Outlaw
     */
    public static class Valkyrie extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Valkyrie">
        public static final int
                TRIPLE_FIRE = 5210000,
                OCTOPUS = 5211001,
                GABIOTA = 5211002,
                FIRE_BURNER = 5211004,
                COOLING_EFFECT = 5211005,
                HOMING = 5211006,
                DICE = 5211007
                        ;
        // </editor-fold>
    }

    /**
     * Corsair
     */
    public static class Captain extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Captain">
        public static final int
                PROPERTY_UPGRADE = 5220001,
                SUPPORT_OCTOPUS = 5220002,
                ADVANCED_HOMING = 5220011,
                COUNTER_ATTACK = 5220012,
                MAPLE_HERO = 5221000,
                AIR_STRIKE = 5221003,
                RAPID_FIRE = 5221004,
                BATTLESHIP = 5221006,
                BATTLESHIP_CANNON = 5221007,
                BATTLESHIP_TORPEDO = 5221008,
                MIND_CONTROL = 5221009,
                HEROS_WILL = 5221010,
                BATTLESHIP_D = 5221999
                        ;
        // </editor-fold>
    }

    /**
     * Manager
     */
    public static class Manager extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Manager">
        public static final int
                ANTI_MACRO = 8001000,
                TELEPORT = 8001001
                        ;
        // </editor-fold>
    }

    /**
     * Admin
     */
    public static class Admin extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Admin">
        public static final int
                HASTE = 9001000,
                DRAGON_ROAR = 9001001,
                TELEPORT = 9001002,
                ANTI_MACRO = 9001009,
                DISPEL = 9101000,
                SUPER_HASTE = 9101001,
                HOLY_SYMBOL = 9101002,
                BLESS = 9101003,
                HIDE = 9101004,
                RESURRECTION = 9101005,
                HYPER_BODY = 9101008
                        ;
        // </editor-fold>
    }

    /**
     * Noblesse
     */
    public static class Noblesse extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Noblesse">
        public static final int
                BLESS_OF_NYMPH = 10000012,
                MULTI_PET = 10000018,
                THROW_SNAIL = 10001000,
                REGENERATION = 10001001,
                MOVING_WITH_ACTIVITY = 10001002,
                SOUL_OF_CRAFTMAN = 10001003,
                MONSTER_RIDING = 10001004,
                MAXLEVEL_ECHOBUFF = 10001005,
                DAMAGEMETER = 10001006,
                MAKER = 10001007,
                BAMBOO = 10001009,
                INVINCIBLE = 10001010,
                BERSERK = 10001011,
                TUTOR = 10001013,
                DISABLE_EVENT_RIDING = 10001014,
                DISABLE_EVENT_RIDING_DASH = 10001015,
                DISABLE_EVENT_RIDING_REACTOR = 10001016,
                DISABLE_YETI_EVENT_RIDING = 10001019,
                MASSACRE = 10001020,
                DISABLE_YETI_EVENT_RIDING2 = 10001022,
                DISABLE_BROOM_EVENT_RIDING = 10001023,
                WOODENHORSE_EVENT_RIDING = 10001025,
                FLYING_SKILL = 10001026,
                KROKO_EVENT_RIDING = 10001027,
                NAKED_EVENT_RIDING = 10001028,
                PINK_SCOOTER_EVENT_RIDING = 10001029,
                FLYING_CLOUD_EVENT_RIDING = 10001030,
                BALROG_EVENT_RIDING = 10001031,
                KART_EVENT_RIDING = 10001033,
                ZD_TIGER_EVENT_RIDING = 10001034,
                MISTBALROG_EVENT_RIDING = 10001035,
                LIONS_EVENT_RIDING = 10001036,
                UNICORN_EVENT_RIDING = 10001037,
                LOWRIDER_EVENT_RIDING = 10001038,
                REDTRUCK_EVENT_RIDING = 10001039,
                GARGOYLES_EVENT_RIDING = 10001040,
                HOLLY_BIRD_EVENT_RIDING = 10001042,
                ORANGE_MUSHROOM_EVENT_RIDING = 10001044,
                SPACE_EVENT_RIDING = 10001046,
                SPACE_EVENT_RIDING_DASH = 10001047,
                SPACE_EVENT_RIDING_REACTOR = 10001048,
                NIGHTMARE_EVENT_RIDING = 10001049,
                YETI_EVENT_RIDING = 10001050,
                OSTRICH_EVENT_RIDING = 10001051,
                BEAR_BALOON_EVENT_RIDING = 10001052,
                TRANS_ROBOT_EVENT_RIDING = 10001053,
                CHICKEN_EVENT_RIDING = 10001054,
                MOTORBIKE_EVENT_RIDING = 10001063,
                POWERED_SUIT_EVENT_RIDING = 10001064,
                VISITOR_EVENT_RIDING = 10001065,
                VISITOR_MORPH_SKILL_NORMAL = 10001066,
                VISITOR_MORPH_SKILL_SKILL = 10001067,
                VISITOR_OWL_RIDING = 10001069,
                VISITOR_MOTHERSHIP_RIDING = 10001070,
                VISITOR_OS3A_RIDING = 10001071,
                HASTE = 10008000,
                MYSTIC_DOOR = 10008001,
                SHARP_EYES = 10008002,
                HYPER_BODY = 10008003
                        ;
        // </editor-fold>
    }

    /**
     * Dawn Warrior
     */
    public static class SoulMaster extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Soul Master">
        public static final int
                MHP_INC = 11000005,
                IRON_BODY = 11001001,
                POWER_STRIKE = 11001002,
                SLASH_BLAST = 11001003,
                SOUL = 11001004,
                SWORD_MASTERY = 11100000,
                SWORD_BOOSTER = 11101001,
                FINAL_ATTACK_SWORD = 11101002,
                FURY = 11101003,
                SOUL_BLADE = 11101004,
                SOUL_RUSH = 11101005,
                UPGRADE_MP_RECOVERY = 11110000,
                ADVANCED_COMBO = 11110005,
                COMBO_ATTACK = 11111001,
                PANIC_SWORD = 11111002,
                COMA_SWORD = 11111003,
                BRANDISH = 11111004,
                SOUL_DRIVER = 11111006,
                SOUL_CHARGE = 11111007
                        ;
        // </editor-fold>
    }

    /**
     * Blaze Wizard
     */
    public static class FlameWizard extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Flame Wizard">
        public static final int
                MMP_INC = 12000005,
                MAGIC_GUARD = 12001001,
                MAGIC_ARMOR = 12001002,
                MAGIC_CLAW = 12001003,
                FLAME = 12001004,
                SPELL_MASTERY = 12100007,
                MEDITATION = 12101000,
                SLOW = 12101001,
                FIRE_ARROW = 12101002,
                TELEPORT = 12101003,
                MAGIC_BOOSTER = 12101004,
                ELEMENTAL_RESET = 12101005,
                FIRE_PILLAR = 12101006,
                ELEMENT_RESISTANCE = 12110000,
                ELEMENT_AMPLIFICATION = 12110001,
                SEAL = 12111002,
                METEOR = 12111003,
                IFRIT = 12111004,
                FLAME_GEAR = 12111005,
                FIRE_STRIKE = 12111006
                        ;
        // </editor-fold>
    }

    /**
     * Wind Archer
     */
    public static class WindBreaker extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Wind Breaker">
        public static final int
                CRITICAL_SHOT = 13000000,
                AMAZON_EYE = 13000001,
                FOCUS = 13001002,
                DOUBLE_SHOT = 13001003,
                STORM = 13001004,
                BOW_MASTERY = 13100000,
                THRUST = 13100004,
                BOW_BOOSTER = 13101001,
                FINAL_ATTACK_BOW = 13101002,
                SOUL_ARROW_BOW = 13101003,
                STORM_BREAK = 13101005,
                WIND_WALK = 13101006,
                BOW_EXPERT = 13110003,
                ARROW_RAIN = 13111000,
                STRAFE = 13111001,
                STORM_ARROW = 13111002,
                PUPPET = 13111004,
                ALBATROSS = 13111005,
                WIND_SPEAR = 13111006,
                WIND_SHOT = 13111007
                        ;
        // </editor-fold>
    }

    /**
     * Night Walker
     */
    public static class NightWalker extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Night Walker">
        public static final int
                NIMBLE_BODY = 14000000,
                KEEN_EYES = 14000001,
                DISORDER = 14001002,
                DARK_SIGHT = 14001003,
                LUCKY_SEVEN = 14001004,
                DARKNESS = 14001005,
                JAVELIN_MASTERY = 14100000,
                CRITICAL_THROW = 14100001,
                VANISH = 14100005,
                JAVELIN_BOOSTER = 14101002,
                HASTE = 14101003,
                FLASH_JUMP = 14101004,
                VAMPIRE = 14101006,
                ALCHEMIST = 14110003,
                VENOM = 14110004,
                SHADOW_PARTNER = 14111000,
                SHADOW_WEB = 14111001,
                AVENGER = 14111002,
                TRIPLE_THROW = 14111005,
                POISON_BOMB = 14111006
                        ;
        // </editor-fold>
    }

    /**
     * Thunder Breaker
     */
    public static class Striker extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Striker">
        public static final int
                QUICKMOTION = 15000000,
                STRAIGHT = 15001001,
                SOMERSAULT = 15001002,
                DASH = 15001003,
                LIGHTNING = 15001004,
                KNUCKLE_MASTERY = 15100001,
                ENERGY_CHARGE = 15100004,
                MHP_INC = 15100007,
                KNUCKLE_BOOSTER = 15101002,
                SCREW_PUNCH = 15101003,
                ENERGY_BURSTER = 15101005,
                LIGHTNING_CHARGE = 15101006,
                CRITICAL_PUNCH = 15110000,
                ENERGY_DRAIN = 15111001,
                TRANSFORM = 15111002,
                SHOCKWAVE = 15111003,
                FIST = 15111004,
                WIND_BOOSTER = 15111005,
                SPARK = 15111006,
                SHARK_WAVE = 15111007
                        ;
        // </editor-fold>
    }

    /**
     * Legend (Aran Beginner)
     */
    public static class Legend extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Legend">
        public static final int
                BLESS_OF_NYMPH = 20000012,
                DOUBLE_SWING = 20000014,
                TRIPLE_SWING = 20000015,
                FINAL_BLOW = 20000016,
                COMBO_ABILITY = 20000017,
                COMBO_CRITICAL = 20000018,
                MULTI_PET = 20000024,
                THROW_SNAIL = 20001000,
                REGENERATION = 20001001,
                MOVING_WITH_ACTIVITY = 20001002,
                SOUL_OF_CRAFTMAN = 20001003,
                MONSTER_RIDING = 20001004,
                MAXLEVEL_ECHOBUFF = 20001005,
                DAMAGEMETER = 20001006,
                MAKER = 20001007,
                BAMBOO = 20001009,
                INVINCIBLE = 20001010,
                BERSERK = 20001011,
                TUTOR = 20001013,
                DISABLE_YETI_EVENT_RIDING = 20001019,
                MASSACRE = 20001020,
                DISABLE_YETI_EVENT_RIDING2 = 20001022,
                DISABLE_BROOM_EVENT_RIDING = 20001023,
                WOODENHORSE_EVENT_RIDING = 20001025,
                FLYING_SKILL = 20001026,
                KROKO_EVENT_RIDING = 20001027,
                NAKED_EVENT_RIDING = 20001028,
                PINK_SCOOTER_EVENT_RIDING = 20001029,
                FLYING_CLOUD_EVENT_RIDING = 20001030,
                BALROG_EVENT_RIDING = 20001031,
                KART_EVENT_RIDING = 20001033,
                ZD_TIGER_EVENT_RIDING = 20001034,
                MISTBALROG_EVENT_RIDING = 20001035,
                LIONS_EVENT_RIDING = 20001036,
                UNICORN_EVENT_RIDING = 20001037,
                LOWRIDER_EVENT_RIDING = 20001038,
                REDTRUCK_EVENT_RIDING = 20001039,
                GARGOYLES_EVENT_RIDING = 20001040,
                HOLLY_BIRD_EVENT_RIDING = 20001042,
                ORANGE_MUSHROOM_EVENT_RIDING = 20001044,
                SPACE_EVENT_RIDING = 20001046,
                SPACE_EVENT_RIDING_DASH = 20001047,
                SPACE_EVENT_RIDING_REACTOR = 20001048,
                NIGHTMARE_EVENT_RIDING = 20001049,
                YETI_EVENT_RIDING = 20001050,
                OSTRICH_EVENT_RIDING = 20001051,
                BEAR_BALOON_EVENT_RIDING = 20001052,
                TRANS_ROBOT_EVENT_RIDING = 20001053,
                CHICKEN_EVENT_RIDING = 20001054,
                MOTORBIKE_EVENT_RIDING = 20001063,
                POWERED_SUIT_EVENT_RIDING = 20001064,
                VISITOR_EVENT_RIDING = 20001065,
                VISITOR_MORPH_SKILL_NORMAL = 20001066,
                VISITOR_MORPH_SKILL_SKILL = 20001067,
                VISITOR_OWL_RIDING = 20001069,
                VISITOR_MOTHERSHIP_RIDING = 20001070,
                VISITOR_OS3A_RIDING = 20001071,
                HASTE = 20008000,
                MYSTIC_DOOR = 20008001,
                SHARP_EYES = 20008002,
                HYPER_BODY = 20008003
                        ;
        // </editor-fold>
    }

    /**
     * Evan Beginner
     */
    public static class EvanJr extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Evan Jr">
        public static final int
                BLESS_OF_NYMPH = 20010012,
                THROW_SNAIL = 20011000,
                REGENERATION = 20011001,
                MOVING_WITH_ACTIVITY = 20011002,
                SOUL_OF_CRAFTMAN = 20011003,
                MONSTER_RIDING = 20011004,
                MAXLEVEL_ECHOBUFF = 20011005,
                DAMAGEMETER = 20011006,
                MAKER = 20011007,
                BAMBOO = 20011009,
                INVINCIBLE = 20011010,
                BERSERK = 20011011,
                DISABLE_YETI_EVENT_RIDING2 = 20011018,
                DISABLE_BROOM_EVENT_RIDING = 20011019,
                MASSACRE = 20011020,
                MULTI_PET = 20011024,
                WOODENHORSE_EVENT_RIDING = 20011025,
                FLYING_SKILL = 20011026,
                KROKO_EVENT_RIDING = 20011027,
                NAKED_EVENT_RIDING = 20011028,
                PINK_SCOOTER_EVENT_RIDING = 20011029,
                FLYING_CLOUD_EVENT_RIDING = 20011030,
                BALROG_EVENT_RIDING = 20011031,
                KART_EVENT_RIDING = 20011033,
                ZD_TIGER_EVENT_RIDING = 20011034,
                MISTBALROG_EVENT_RIDING = 20011035,
                LIONS_EVENT_RIDING = 20011036,
                UNICORN_EVENT_RIDING = 20011037,
                LOWRIDER_EVENT_RIDING = 20011038,
                REDTRUCK_EVENT_RIDING = 20011039,
                GARGOYLES_EVENT_RIDING = 20011040,
                HOLLY_BIRD_EVENT_RIDING = 20011042,
                ORANGE_MUSHROOM_EVENT_RIDING = 20011044,
                SPACE_EVENT_RIDING = 20011046,
                SPACE_EVENT_RIDING_DASH = 20011047,
                SPACE_EVENT_RIDING_REACTOR = 20011048,
                NIGHTMARE_EVENT_RIDING = 20011049,
                YETI_EVENT_RIDING = 20011050,
                OSTRICH_EVENT_RIDING = 20011051,
                BEAR_BALOON_EVENT_RIDING = 20011052,
                TRANS_ROBOT_EVENT_RIDING = 20011053,
                CHICKEN_EVENT_RIDING = 20011054,
                MOTORBIKE_EVENT_RIDING = 20011063,
                POWERED_SUIT_EVENT_RIDING = 20011064,
                VISITOR_EVENT_RIDING = 20011065,
                VISITOR_MORPH_SKILL_NORMAL = 20011066,
                VISITOR_MORPH_SKILL_SKILL = 20011067,
                VISITOR_OWL_RIDING = 20011069,
                VISITOR_MOTHERSHIP_RIDING = 20011070,
                VISITOR_OS3A_RIDING = 20011071,
                HASTE = 20018000,
                MYSTIC_DOOR = 20018001,
                SHARP_EYES = 20018002,
                HYPER_BODY = 20018003
                        ;
        // </editor-fold>
    }

    /**
     * Aran
     */
    public static class Aran extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Aran">
        public static final int
                COMBO_ABILITY = 21000000,
                DOUBLE_SWING = 21000002,
                COMBAT_STEP = 21001001,
                POLEARM_BOOSTER = 21001003,
                POLEARM_MASTERY = 21100000,
                TRIPLE_SWING = 21100001,
                FINAL_CHARGE = 21100002,
                COMBO_SMASH = 21100004,
                COMBO_DRAIN = 21100005,
                BODY_PRESSURE = 21101003,
                COMBO_CRITICAL = 21110000,
                FULL_SWING = 21110002,
                FINAL_TOSS = 21110003,
                COMBO_FENRIR = 21110004,
                ROLLING_SPIN = 21110006,
                FULL_SWING_DS = 21110007,
                FULL_SWING_TS = 21110008,
                SMART_KNOCKBACK = 21111001,
                SNOW_CHARGE = 21111005,
                HIGH_MASTERY = 21120001,
                OVER_SWING = 21120002,
                HIGH_DEFENSE = 21120004,
                FINAL_BLOW = 21120005,
                COMBO_TEMPEST = 21120006,
                COMBO_BARRIER = 21120007,
                OVER_SWING_DS = 21120009,
                OVER_SWING_TS = 21120010,
                MAPLE_HERO = 21121000,
                FREEZE_STANDING = 21121003,
                HEROS_WILL = 21121008
                        ;
        // </editor-fold>
    }

    /**
     * Evan
     */
    public static class Evan extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Evan">
        public static final int
                DRAGON_SOUL = 22000000,
                MAGIC_MISSILE = 22001001,
                FIRECIRCLE = 22101000,
                TELEPORT = 22101001,
                LIGHTING_BOLT = 22111000,
                MAGIC_GUARD = 22111001,
                SPELL_MASTERY = 22120002,
                ICE_BREATH = 22121000,
                ELEMENTAL_RESET = 22121001,
                MAGIC_FLAIR = 22131000,
                MAGIC_SHIELD = 22131001,
                MAGIC_CRITICAL = 22140000,
                DRAGON_THRUST = 22141001,
                MAGIC_BOOSTER = 22141002,
                SLOW = 22141003,
                ELEMENT_AMPLIFICATION = 22150000,
                BREATH = 22151001,
                KILLING_WING = 22151002,
                MAGIC_REGISTANCE = 22151003,
                DRAGON_FURY = 22160000,
                EARTHQUAKE = 22161001,
                GHOST_LETTERING = 22161002,
                RECOVERY_AURA = 22161003,
                MAGIC_MASTERY = 22170001,
                MAPLE_HERO = 22171000,
                ILLUSION = 22171002,
                FLAME_WHEEL = 22171003,
                HEROS_WILL = 22171004,
                ONIX_BLESSING = 22181000,
                BLAZE = 22181001,
                DARK_FOG = 22181002,
                SOUL_STONE = 22181003
                        ;
        // </editor-fold>
    }

    /**
     * Citizen
     */
    public static class Citizen extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Citizen">
        public static final int
                EFFICIENCY = 30000002,
                BLESS_OF_NYMPH = 30000012,
                CRITICAL = 30000022,
                CRISTAL_THROW = 30001000,
                SNEAK = 30001001,
                SOUL_OF_CRAFTMAN = 30001003,
                MONSTER_RIDING = 30001004,
                MAXLEVEL_ECHOBUFF = 30001005,
                DAMAGEMETER = 30001006,
                MAKER = 30001007,
                BAMBOO = 30001009,
                INVINCIBLE = 30001010,
                BERSERK = 30001011,
                TUTOR = 30001013,
                MASSACRE = 30001020,
                MULTI_PET = 30001024,
                WOODENHORSE_EVENT_RIDING = 30001025,
                FLYING_SKILL = 30001026,
                KROKO_EVENT_RIDING = 30001027,
                NAKED_EVENT_RIDING = 30001028,
                PINK_SCOOTER_EVENT_RIDING = 30001029,
                FLYING_CLOUD_EVENT_RIDING = 30001030,
                BALROG_EVENT_RIDING = 30001031,
                KART_EVENT_RIDING = 30001033,
                ZD_TIGER_EVENT_RIDING = 30001034,
                MISTBALROG_EVENT_RIDING = 30001035,
                HOLLY_BIRD_EVENT_RIDING = 30001042,
                ORANGE_MUSHROOM_EVENT_RIDING = 30001044,
                NIGHTMARE_EVENT_RIDING = 30001049,
                YETI_EVENT_RIDING = 30001050,
                OSTRICH_EVENT_RIDING = 30001051,
                BEAR_BALOON_EVENT_RIDING = 30001052,
                TRANS_ROBOT_EVENT_RIDING = 30001053,
                CAPTURE = 30001061,
                SUMMON_MONSTER = 30001062,
                MOTORBIKE_EVENT_RIDING = 30001063,
                POWERED_SUIT_EVENT_RIDING = 30001064,
                VISITOR_EVENT_RIDING = 30001065,
                VISITOR_MORPH_SKILL_NORMAL = 30001066,
                VISITOR_MORPH_SKILL_SKILL = 30001067,
                RUSH = 30001068,
                VISITOR_OWL_RIDING = 30001069,
                VISITOR_MOTHERSHIP_RIDING = 30001070,
                VISITOR_OS3A_RIDING = 30001071,
                HASTE = 30008000,
                MYSTIC_DOOR = 30008001,
                SHARP_EYES = 30008002,
                HYPER_BODY = 30008003
                        ;
        // </editor-fold>
    }

    /**
     * Battle Mage
     */
    public static class BMage extends Skills {
        // <editor-fold defaultstate="collapsed" desc="BMage">
        public static final int
                TRIPLE_BLOW = 32001000,
                FINISH_ATTACK = 32001001,
                TELEPORT = 32001002,
                AURA_DARK = 32001003,
                FINISH_ATTACK1 = 32001007,
                FINISH_ATTACK2 = 32001008,
                FINISH_ATTACK3 = 32001009,
                FINISH_ATTACK4 = 32001010,
                FINISH_ATTACK5 = 32001011,
                SPELL_MASTERY = 32100006,
                QUAD_BLOW = 32101000,
                DARK_BOW = 32101001,
                AURA_BLUE = 32101002,
                AURA_YELLOW = 32101003,
                BLOOD_DRAIN = 32101004,
                STAFF_BOOSTER = 32101005,
                AURA_BLUE_ADVANCED = 32110000,
                STAFF_MASTERY = 32110001,
                SUPER_BODY_DARK = 32110007,
                SUPER_BODY_YELLOW = 32110008,
                SUPER_BODY_BLUE = 32110009,
                DEATH_BLOW = 32111002,
                DARK_SPEAR = 32111003,
                CONVERSION = 32111004,
                SUPER_BODY = 32111005,
                REVIVE = 32111006,
                TELEPORT_MASTERY = 32111010,
                ADVENCED_DARK_CHAIN = 32111011,
                AURA_DARK_ADVANCED = 32120000,
                AURA_YELLOW_ADVANCED = 32120001,
                ENERGIZE = 32120009,
                FINISH_BLOW = 32121002,
                CYCLONE = 32121003,
                NEMESIS = 32121004,
                STANCE = 32121005,
                SHELTER = 32121006,
                MAPLE_HERO = 32121007,
                HEROS_WILL = 32121008,

        AURA_ALL = 0,
                AURA_DARK_BLUE = 1,
                AURA_DARK_YELLOW = 2,
                AURA_BLUE_YELOW = 3
                        ;
        // </editor-fold>
    }

    /**
     * Wild Hunter
     */
    public static class WildHunter extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Wild Hunter">
        public static final int
                RAPID_SHOOT = 33001000,
                JAGUAR_RIDING = 33001001,
                DOUBLE_JUMP = 33001002,
                CROSSBOW_BOOSTER = 33001003,
                CROSSBOW_MASTERY = 33100000,
                FINAL_ATTACK = 33100009,
                BOMB_SHOOT = 33101001,
                JAGUAR_NUCKBACK = 33101002,
                SOUL_ARROW_CROSSBOW = 33101003,
                MINE = 33101004,
                SWALLOW = 33101005,
                SWALLOW_DUMMY_BUFF = 33101006,
                SWALLOW_DUMMY_ATTACK = 33101007,
                MINE_DUMMY_SUMMONED = 33101008,
                RIDING_MASTERY = 33110000,
                FIVE_SHOOT = 33111001,
                CROSS_ROAD = 33111002,
                TRAP = 33111003,
                BLIND = 33111004,
                SILVER_HAWK = 33111005,
                CLAW_CUT = 33111006,
                CROSSBOW_EXPERT = 33120000,
                WILD_INSTINCT = 33120010,
                FLASH_RAIN = 33121001,
                ELRECTRONICSHOCK = 33121002,
                SNIPING = 33121003,
                SHARP_EYES = 33121004,
                NERVEGAS = 33121005,
                MOREWILD = 33121006,
                MAPLE_HERO = 33121007,
                HEROS_WILL = 33121008,
                WILD_SHOOT = 33121009
                        ;
        // </editor-fold>
    }

    /**
     * Mechanic
     */
    public static class Mechanic extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Mechanic">
        public static final int
                FLAMETHROWER_DUMMY = 35000001,
                FLAMETHROWER = 35001001,
                HN07 = 35001002,
                DRILL_RUSH = 35001003,
                GATLING = 35001004,
                GUN_MASTERY = 35100000,
                ROCKET_BOOSTER_DUMMY = 35100004,
                WEAPONMASTERY = 35100008,
                FLAMETHROWER_UP_DUMMY = 35100009,
                GATLING_ROBOT_G007 = 35101001,
                EARTH_SLUG = 35101003,
                ROCKET_BOOSTER = 35101004,
                OPEN_GATE = 35101005,
                BOOSTER = 35101006,
                PERFECT_ARMOR = 35101007,
                FLAMETHROWER_UP = 35101009,
                GATLING_UP = 35101010,
                SIEGE1_DUMMY = 35110004,
                METAL_FIST_MASTERY = 35110014,
                SATELITE = 35111001,
                TESLA_COIL = 35111002,
                SIEGE1 = 35111004,
                VELOCITY_CONTROLER = 35111005,
                SATELITE2 = 35111009,
                SATELITE3 = 35111010,
                HEALING_ROBOT_H_LX = 35111011,
                DICE = 35111013,
                ROCKET_PUNCH = 35111015,
                HN07_UPGRADE = 35120000,
                MASTERY = 35120001,
                ROBOROBO_UPGRADE = 35120002,
                SIEGE2_DUMMY = 35120005,
                SIEGE2_SPECIAL_DUMMY = 35120013,
                SG88 = 35121003,
                CANON = 35121004,
                SIEGE2 = 35121005,
                SAFETY = 35121006,
                MAPLE_HERO = 35121007,
                HEROS_WILL = 35121008,
                ROBOROBO = 35121009,
                AR_01 = 35121010,
                ROBOROBO_DUMMY = 35121011,
                LASER = 35121012,
                SIEGE2_SPECIAL = 35121013
                        ;
        // </editor-fold>
    }

    /**
     * Unrecorded
     */
    public static class Unrecorded extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Unrecorded">
        public static final int
                DEADLY_ATTACK = 90000000,
                STUN = 90001001,
                SLOW = 90001002,
                POISON = 90001003,
                BLIND = 90001004,
                SEAL = 90001005,
                FREEZE = 90001006
                        ;
        // </editor-fold>
    }

    /**
     * Guild
     */
    public static class Guild extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Guild">
        public static final int
                GUILD_MESO_UP = 91000000,
                GUILD_EXPERIENCE_UP = 91000001,
                GUILD_DEFENCE_UP = 91000002,
                GUILD_ATTNMAG_UP = 91000003,
                GUILD_AGILITY_UP = 91000004,
                GUILD_BUSINESS_EFFICENY_UP = 91000005,
                GUILD_REGULAR_SUPPORT = 91000006
                        ;
        // </editor-fold>
    }
}
