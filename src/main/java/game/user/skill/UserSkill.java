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

import common.JobAccessor;
import common.JobCategory;
import common.Request;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import common.user.UserEffect;
import game.messenger.Character;
import game.user.User;
import game.user.WvsContext;
import game.user.skill.Skills.*;
import game.user.skill.data.SkillLevelData;
import game.user.skill.entries.SkillEntry;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.Flag;
import game.user.stat.SecondaryStatOption;

import java.util.ArrayList;
import java.util.List;

import game.user.stat.ts.PartyBoosterStat;
import game.user.stat.ts.TSIndex;
import game.user.stat.ts.TwoStateTemporaryStat;
import network.packet.InPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;

/**
 * @author Eric
 */
public class UserSkill {
    private final User user;

    public UserSkill(User user) {
        this.user = user;
    }

    public void onSkillUseRequest(InPacket packet) {
        packet.decodeInt();// time
        int skillID = packet.decodeInt();
        byte slv = packet.decodeByte();
        if (user.getField() == null) {
            sendFailPacket();
            return;
        }
        if (!checkMovementSkill(skillID, slv)) {
            return;
        }
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (slv <= 0 || SkillInfo.getInstance().getSkillLevel(user.getCharacter(), skillID, skillEntry) < slv
                || !SkillInfo.getInstance().adjustConsumeForActiveSkill(user, skillID, slv)) {
            sendFailPacket();
            return;
        }
        SkillEntry skill = skillEntry.get();
        if (SkillAccessor.isMobStatChange(skillID)) {
            doActiveSkill_MobStatChange(skill, slv, packet, true);
            return;
        } else if (SkillAccessor.isPartyStatChange(skillID)) {
            doActiveSkill_PartyStatChange(skill, slv, packet);
            return;
        } else if (SkillAccessor.isSelfStatChange(skillID)) {
            doActiveSkill_SelfStatChange(skill, slv, packet);
            return;
        } else if (SkillAccessor.isWeaponBooster(skillID)) {
            int wt1 = 0, wt2 = 0, wt3 = 0, wt4 = 0;
            if (skillID == Page.WeaponBooster) {
                wt1 = ItemAccessor.WeaponTypeFlag.OH_SWORD; wt2 = ItemAccessor.WeaponTypeFlag.TH_SWORD;
                wt3 = ItemAccessor.WeaponTypeFlag.OH_MACE;  wt4 = ItemAccessor.WeaponTypeFlag.TH_MACE;
            } else if (skillID == SoulMaster.SWORD_BOOSTER || skillID == Fighter.WeaponBooster) {
                wt1 = ItemAccessor.WeaponTypeFlag.OH_SWORD; wt2 = ItemAccessor.WeaponTypeFlag.TH_SWORD;
                wt3 = ItemAccessor.WeaponTypeFlag.OH_AXE;   wt4 = ItemAccessor.WeaponTypeFlag.TH_AXE;
            } else if (skillID == Spearman.WeaponBooster) {
                wt1 = ItemAccessor.WeaponTypeFlag.SPEAR;   wt2 = ItemAccessor.WeaponTypeFlag.SPEAR;
                wt3 = ItemAccessor.WeaponTypeFlag.POLEARM; wt4 = ItemAccessor.WeaponTypeFlag.POLEARM;
            } else if (skillID == Mage1.MAGIC_BOOSTER || skillID == Mage2.MAGIC_BOOSTER || skillID == FlameWizard.MAGIC_BOOSTER || skillID == Evan.MAGIC_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.STAFF; wt2 = ItemAccessor.WeaponTypeFlag.WAND;
            } else if (skillID == Hunter.BowBooster || skillID == WindBreaker.BOW_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.BOW;
            } else if (skillID == Crossbowman.CrossbowBooster || skillID == WildHunter.CROSSBOW_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.CROSSBOW;
            } else if (skillID == Assassin.JavelinBooster || skillID == NightWalker.JAVELIN_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.THROWINGGLOVE;
            } else if (skillID == Thief.DaggerBooster) {
                wt1 = ItemAccessor.WeaponTypeFlag.DAGGER;
            } else if (skillID == Dual1.DUAL_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.DAGGER;
                wt2 = ItemAccessor.WeaponTypeFlag.SUB_DAGGER;
            } else if (skillID == Gunslinger.GUN_BOOSTER || skillID == Mechanic.BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.GUN;
            } else if (skillID == InFighter.KNUCKLE_BOOSTER || skillID == Striker.KNUCKLE_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.KNUCKLE;
            } else if (skillID == Aran.POLEARM_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.POLEARM;
            } else if (skillID == BMage.STAFF_BOOSTER) {
                wt1 = ItemAccessor.WeaponTypeFlag.STAFF;
            }
            if (wt1 == 0 && wt2 == 0 && wt3 == 0 && wt4 == 0) Logger.logError("New weapon booster [%d]", skillID);
            doActiveSkill_WeaponBooster(skill, slv, wt1, wt2, wt3, wt4);
            return;
        }
        switch (skillID) {
            case Wizard2.Teleport:
            case Wizard1.Teleport:
            case Cleric.Teleport:
                doActiveSkill_Teleport(skill, slv);
                break;
            default: {
                Logger.logReport("Found new skill: %d", skillID);
                user.sendCharacterStat(Request.Excl, 0);
            }
        }
    }

    public void onSkillCancelRequest(InPacket packet) {
        int skillID = packet.decodeInt();

        if (SkillAccessor.isWeaponBooster(skillID) || SkillAccessor.isSelfStatChange(skillID) || SkillAccessor.isPartyStatChange(skillID)) {
            Flag reset = user.getSecondaryStat().resetByReasonID(skillID);
            if (skillID == Rogue.DarkSight) {
                //nCur = timeGetTime();
                //if (!nCur) nCur = 1;
                //pUser->m_secondaryStat.tDarkSight_ = nCur;
            }
            if (reset.isSet()) {
                user.validateStat(false);
                user.sendTemporaryStatReset(reset);
            }
        }
        // handle skill prepare here
    }

    public void onSkillUpRequest(InPacket packet) {
        if (user.lock()) {
            try {
                packet.decodeInt();// update time
                int skillID = packet.decodeInt();
                List<SkillRecord> change = new ArrayList<>();
                if (UserSkillRecord.skillUp(user, skillID, true, change)) {
                    user.validateStat(false);
                    user.sendCharacterStat(Request.None, CharacterStatType.SP);
                }
                UserSkillRecord.sendCharacterSkillRecord(user, Request.Excl, change);
                change.clear();
            } finally {
                user.unlock();
            }
        }
    }

    public void doActiveSkill_PartyStatChange(SkillEntry skill, byte slv, InPacket packet) {
        int affectedMemberBitmap = packet.decodeByte(true);
        long duration = System.currentTimeMillis() + 1000 * skill.getLevelData(slv).Time;

        int hpRate = 0;
        int partyCount = 1;
        if (skill.getLevelData(slv).HP != 0) {
            int baseInt = user.getBasicStat().getINT();
            int rand = 0;
            if ((baseInt - (baseInt * 0.8d)) > 0) {
                rand = (int) (Rand32.getInstance().random() % (baseInt - (baseInt * 0.8d)));
                rand += (int) (baseInt - (baseInt * 0.8d));
            }
            int rate = user.getSecondaryStat().mad + user.getSecondaryStat().getStatOption(CharacterTemporaryStat.MAD);
            rate = (int) (((double) rand * 1.5d + (double) user.getBasicStat().getLUK()) * (double) rate * 0.01d);
            hpRate = (int) ((double) rate * ((double) partyCount * 0.3d + 1.0d) * (double) skill.getLevelData(slv).HP * 0.01d);
        }

        Flag resetFlag = new Flag(Flag.INT_128);
        Flag skillFlag = processSkill(skill, slv, duration, resetFlag);
        int statFlag = 0;
        if (skill.getLevelData(slv).HP != 0) {
            double inc = Math.ceil(hpRate / partyCount);
            if (user.incHP((int) (long) inc, false)) {
                statFlag |= CharacterStatType.HP;
                if (skill.getSkillID() == Cleric.Heal) {
                    if (affectedMemberBitmap != 0) {
                        // Ignore because this only ever adjusts from other party members.
                        // statFlag |= user.incEXP(affectedMemberBitmap, false);
                    }
                }
            }
        }
        user.validateStat(false);
        user.sendCharacterStat(Request.Excl, statFlag);
        user.sendTemporaryStatReset(resetFlag);
        user.sendTemporaryStatSet(skillFlag);
        if (user.getField() != null) {
            user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
        }
    }

    public void doActiveSkill_SelfStatChange(SkillEntry skill, byte slv, InPacket packet) {
        int time = 1000 * skill.getLevelData(slv).Time;
        long cur = System.currentTimeMillis();
        long duration = Math.max(cur + time, 1);

        Flag resetFlag = new Flag(Flag.INT_128);
        Flag skillFlag = processSkill(skill, slv, duration, resetFlag);
        if (skill.getSkillID() == Rogue.DarkSight) {
            SecondaryStatOption opt = user.getSecondaryStat().getStat(CharacterTemporaryStat.DarkSight);
            if (opt != null) {
                // tCur = HIDWORD(tCur);
                // Convert the time into timeGetTime() seconds
                cur /= 1000;
                cur = Math.max(1, cur - 3000);
                opt.setModOption((int) cur);
                user.getSecondaryStat().setStat(CharacterTemporaryStat.DarkSight, opt);
            }
        }

        int statFlag = 0;
        int hp = skill.getLevelData(slv).HP;
        if (hp > 0) {
            user.incHP(hp, false);
            statFlag |= CharacterStatType.HP;
        }
        int mp = skill.getLevelData(slv).MP;
        if (mp > 0) {
            user.incMP(mp, false);
            statFlag |= CharacterStatType.MP;
        }

        user.validateStat(true);
        user.sendCharacterStat(Request.Excl, statFlag);
        user.sendTemporaryStatReset(resetFlag);
        user.sendTemporaryStatSet(skillFlag);
        if (user.getField() != null) {
            user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
        }
    }

    public void doActiveSkill_Teleport(SkillEntry skill, byte slv) {
        user.sendCharacterStat(Request.Excl, 0);
    }

    public void doActiveSkill_MobStatChange(SkillEntry skill, byte slv, InPacket packet, boolean sendResult) {
        packet.decodeInt();// user pos
        int count = packet.decodeByte();
        for (int i = 0; i < count; i++) {
            int mobID = packet.decodeInt();

            user.getField().getLifePool().onMobStatChangeSkill(user, mobID, skill, slv);
        }
        if (sendResult) {
            user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
        }
        user.sendCharacterStat(Request.Excl, 0);
    }

    public void doActiveSkill_WeaponBooster(SkillEntry skill, byte slv, int wt1, int wt2, int wt3, int wt4) {
        SkillLevelData levelData = skill.getLevelData(slv);
        int wt = ItemAccessor.getWeaponType(user.getCharacter().getItem(ItemType.Equip, -BodyPart.Weapon).getItemID());
        if (skill.getSkillID() == Dual1.DUAL_BOOSTER) {
            int subWT = ItemAccessor.getWeaponType(user.getCharacter().getItem(ItemType.Equip, -BodyPart.Shield).getItemID());
            if (!user.getCharacter().isEquippedDualDagger() || wt != wt1 || subWT != wt2) {
                user.sendCharacterStat(Request.Excl, 0);
                return;
            }
        } else if (wt <= 0 || wt != wt1 && wt != wt2 && wt != wt3 && wt != wt4 || levelData.Time <= 0) {
            user.sendCharacterStat(Request.Excl, 0);
            return;
        }

        long duration = System.currentTimeMillis() + 1000 * levelData.Time;
        user.getSecondaryStat().setStat(CharacterTemporaryStat.Booster, new SecondaryStatOption(1, skill.getSkillID(), duration));
        user.sendCharacterStat(Request.Excl, 0);
        user.sendTemporaryStatSet(CharacterTemporaryStat.getMask(CharacterTemporaryStat.Booster));
        if (user.getField() != null) {
            user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
        }
    }

    public boolean checkMovementSkill(int skillID, byte slv) {
        if (user.isGM()) {
            return true;
        }
        String character = user.getCharacterName();
        int fieldID = user.getField().getFieldID();
        short job = user.getCharacter().getCharacterStat().getJob();
        String format = "";
        if (SkillAccessor.isTeleportSkill(skillID)) {
            if (JobAccessor.getJobCategory(job) == JobCategory.WIZARD) {
                int skillLevel = SkillAccessor.getTeleportSkillLevel(user.getCharacter());
                if (slv > skillLevel + 3) {
                    format = String.format("[SkillHack] Illegal Teleport LEVEL Tried [ %s ] Field: %d / SkillID: %d / (CurLev: %d, ReqLev: %d) (DISCONNECTED)", character, fieldID, skillID, skillLevel, slv);
                }
            } else {
                format = String.format("[SkillHack] Illegal Teleport Tried [ %s ] Field: %d / SkillID: %d (DISCONNECTED)", character, fieldID, skillID);
            }
        }
        if (!format.isEmpty()) {
            Logger.logError(format);

            user.closeSocket();
            return false;
        }
        return true;
    }

    public void sendFailPacket() {
        user.sendPacket(WvsContext.onSkillUseResult(Request.None));
    }

    private Flag processSkill(SkillEntry skill, byte slv, long duration, Flag resetFlag) {
        Flag flag = new Flag(Flag.INT_128);

        SkillLevelData level = skill.getLevelData(slv);
        if (level.PAD != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.PAD, new SecondaryStatOption(level.PAD, skill.getSkillID(), duration)));
        }
        if (level.PDD != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.PDD, new SecondaryStatOption(level.PDD, skill.getSkillID(), duration)));
        }
        if (level.MAD != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.MAD, new SecondaryStatOption(level.MAD, skill.getSkillID(), duration)));
        }
        if (level.MDD != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.MDD, new SecondaryStatOption(level.MDD, skill.getSkillID(), duration)));
        }
        if (level.ACC != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.ACC, new SecondaryStatOption(level.ACC, skill.getSkillID(), duration)));
        }
        if (level.EVA != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.EVA, new SecondaryStatOption(level.EVA, skill.getSkillID(), duration)));
        }
        if (level.Speed != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.Speed, new SecondaryStatOption(level.Speed, skill.getSkillID(), duration)));
        }
        if (level.Jump != 0) {
            flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.Jump, new SecondaryStatOption(level.Jump, skill.getSkillID(), duration)));
        }

        switch (skill.getSkillID()) {
            // BEGINNER
            case Beginner.KROKO_EVENT_RIDING: {
                flag.performOR(CharacterTemporaryStat.getMask(CharacterTemporaryStat.RideVehicle));
                TwoStateTemporaryStat ts = (TwoStateTemporaryStat) user.getSecondaryStat().temporaryStats[TSIndex.RIDE_VEHICLE];
                ts.setReason(skill.getSkillID());
                ts.setValue(1932007);
                ts.setLastUpdated(System.currentTimeMillis() + 21000);
                break;
            }
            // WARRIOR
            case Fighter.PowerGuard:
            case Page.PowerGuard: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.PowerGuard, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            case Crusader.ComboAttack: {
                SecondaryStatOption option = new SecondaryStatOption(1, skill.getSkillID(), duration);
                option.setModOption(level.X);

                Pointer<SkillEntry> adv = new Pointer<>();
                int advSLV;
                if (slv >= 20
                        && (advSLV = SkillInfo.getInstance().getSkillLevel(user.getCharacter(), Hero.AdvancedCombo, adv)) != 0) {
                    SkillLevelData sd = adv.get().getLevelData(advSLV);
                    option.setModOption(sd.X | (sd.Prop << 16));
                }
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.ComboCounter, option));
                break;
            }
            case Hero.Enrage: {
                user.getSecondaryStat().setStatOption(CharacterTemporaryStat.ComboCounter, 1);
                flag.performOR(CharacterTemporaryStat.getMask(CharacterTemporaryStat.ComboCounter));
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.DamR, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            case Knight.FIRE_CHARGE:
            case Knight.ICE_CHARGE:
            case Knight.LIGHTNING_CHARGE:
            case Paladin.DIVINE_CHARGE:
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.WeaponCharge, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            case Spearman.HyperBody: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.MaxHP, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.MaxMP, new SecondaryStatOption(level.Y, skill.getSkillID(), duration)));
                break;
            }
            case DragonKnight.DRAGON_BLOOD: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.DragonBlood, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            // MAGICIAN
            case Magician.MagicGuard: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.MagicGuard, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            case Cleric.Invincible: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.Invincible, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            // BOWMAN
            case Hunter.SoulArrow_Bow:
            case Crossbowman.SoulArrow_Crossbow: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.SoulArrow, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            // THIEF
            case Rogue.DarkSight: {
                flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.DarkSight, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                break;
            }
            // PIRATE
            case Viper.WIND_BOOSTER: {
                PartyBoosterStat ts = (PartyBoosterStat) user.getSecondaryStat().temporaryStats[TSIndex.PARTY_BOOSTER];
                ts.setValue(level.X);
                ts.setReason(skill.getSkillID());
                ts.setLastUpdated(duration);
                ts.setExpireTerm(level.Time);
                ts.setCurrentTime(System.currentTimeMillis());
                flag.performOR(CharacterTemporaryStat.getMask(CharacterTemporaryStat.PartyBooster));
                break;
            }
            default: {
                if (SkillAccessor.isMapleHero(skill.getSkillID())) {
                    flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.BasicStatUp, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                }
                if (SkillAccessor.isStance(skill.getSkillID())) {
                    flag.performOR(user.getSecondaryStat().setStat(CharacterTemporaryStat.Stance, new SecondaryStatOption(level.X, skill.getSkillID(), duration)));
                }
            }
        }

        return flag;
    }
}
