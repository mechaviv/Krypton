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
package game.user;

import common.user.UserEffect;
import game.field.MovePath;
import game.field.life.AttackIndex;
import game.user.skill.Skills;
import game.user.stat.Flag;
import game.user.stat.SecondaryStat;
import java.awt.Point;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserRemote {
    
    /**
     * The (remote) user effect packet. 
     * This sends both skill and level-up effects of a user to the field.
     * 
     * @param characterID The ID of the character displaying the effect
     * @param userEffect The type of user effect (@see game.user.User.UserEffect)
     * @param args The optional arguments (nSkillID and nSLV for skill effects)
     * 
     * @return The remote user effect packet
     */
    public static OutPacket onEffect(int characterID, int userEffect, int... args) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEffectRemote);
        packet.encodeInt(characterID);
        packet.encodeByte(userEffect);
        switch (userEffect) {
            case UserEffect.LevelUp:
                break;
            case UserEffect.SkillUse:
                int skillID = args[0];
                packet.encodeInt(args[0]);
                packet.encodeByte(args[2]);
                packet.encodeByte(args[1]);
                if (skillID == Skills.Citizen.SUMMON_MONSTER) {
                    packet.encodeByte(args[3]);// bLeft
                    packet.encodeShort(args[4]);// x
                    packet.encodeShort(args[5]);// y
                }
                if (skillID == Skills.Citizen.CAPTURE) {
                    packet.encodeByte(args[3]);
                }
                break;
            case UserEffect.SkillAffected:
                packet.encodeInt(args[0]);
                packet.encodeByte(args[1]);
                break;
            case UserEffect.SkillSpecial:
                packet.encodeInt(args[0]);
                break;
        }
        return packet;
    }

    /**
     * The character buff packet.
     * This will give a user a set of buffs based on the flag.
     *
     * @param characterID The ID of the user to buff
     * @param ss The SecondaryStat object of the user
     * @param flag The flag containing which buffs to give
     *
     * @return The character buff packet
     */
    public static OutPacket onTemporaryStatSet(int characterID, SecondaryStat ss, Flag flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserTemporaryStatSet);
        packet.encodeInt(characterID);
        ss.encodeForRemote(packet, flag);
        packet.encodeShort(0);// delay
        return packet;
    }

    /**
     * The buff reset packet.
     * This will cancel a set of buffs for the character based on the flag.
     *
     * @param characterID The ID of the character to remove buffs from
     * @param flag The flag containing which buffs to cancel
     *
     * @return The buff reset packet
     */
    public static OutPacket onResetTemporaryStat(int characterID, Flag flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserTemporaryStatReset);
        packet.encodeInt(characterID);
        packet.encodeBuffer(flag.toByteArray());
        return packet;
    }

    /**
     * The character emotion packet.
     * Displays a character using an emotion to the field.
     *
     * @param characterID The ID of the user using an emotion
     * @param emotion The type of emotion (F1~F7)
     *
     * @return The emotion packet
     */
    public static OutPacket onEmotion(int characterID, int emotion) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEmotion);
        packet.encodeInt(characterID);
        packet.encodeInt(emotion);
        packet.encodeInt(1000);
        packet.encodeBool(false);//bEmotionByItemOption
        return packet;
    }
    
    // This is handled directly in LifePool. 
    // @see game.field.life.LifePool.onUserAttack.
    public static OutPacket onAttack(int characterID) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserMeleeAttack);
        packet.encodeInt(characterID);
        return packet;
    }
    
    /**
     * The character movement packet.
     * Displays a user moving around to the field.
     * 
     * @param characterID The ID of the user moving
     * @param mp A MovePath containing the movement data
     * 
     * @return The movement packet
     */
    public static OutPacket onMove(int characterID, MovePath mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserMove);
        packet.encodeInt(characterID);
        mp.encode(packet);
        return packet;
    }

    public static OutPacket onSkillPrepare(int characterID, int skillID, int slv, int action, int speed) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserSkillPrepare);
        packet.encodeInt(characterID);
        packet.encodeInt(skillID);
        packet.encodeByte(slv);
        packet.encodeShort(action);
        packet.encodeByte(speed);
        return packet;
    }

    public static OutPacket onSkillCancel(int characterID, int skillID) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserSkillCancel);
        packet.encodeInt(characterID);
        packet.encodeInt(skillID);
        return packet;
    }

    /**
     * The user damaged packet.
     * When a user is hit or takes damage, this displays the damage dealt.
     * 
     * @param characterID The ID of the user who received damage
     * @param mobAttackIdx The mob attack index (@see game.field.life.AttackIndex)
     * @param clientDamage The damage dealt (as received from the client)
     * @param mobTemplateID The TemplateID of the mob who attacked the user
     * @param left The direction the user was facing
     * @param reflect If power guard is enabled and damage was reflected
     * @param mobID The ObjectID of the mob to deal reflective damage to
     * @param hitAction The hit action towards the mob
     * @param hit The coordinates of the hit reflection
     * 
     * @return The user hit packet
     */
    public static OutPacket onHit(int characterID, byte mobAttackIdx, int clientDamage, int mobTemplateID, byte left, byte reflect, int mobID, byte hitAction, Point hit) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserHit);
        packet.encodeInt(characterID);
        packet.encodeByte(mobAttackIdx);
        packet.encodeInt(clientDamage);
        if (mobAttackIdx > AttackIndex.Counter) {
            packet.encodeInt(mobTemplateID);
            packet.encodeByte(left);
            packet.encodeByte(reflect);
            if (reflect > 0) {
                packet.encodeInt(mobID);
                packet.encodeByte(hitAction);
                packet.encodeShort(hit.x);
                packet.encodeShort(hit.y);
            }
        }
        return packet;
    }
    
    /**
     * The avatar modification packet.
     * This displays changes in a user's avatar to the field.
     * 
     * @param user The user who's avatar has changed
     * @param avatarModFlag The flag determining the changes to display
     * 
     * @return The avatar modification packet
     */
    public static OutPacket onAvatarModified(User user, int avatarModFlag) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserAvatarModified);
        packet.encodeInt(user.getCharacterID());
        packet.encodeInt(avatarModFlag);
        if ((avatarModFlag & AvatarLook.Face) != 0) {
            packet.encodeInt(user.getCharacter().getCharacterStat().getFace());
        }
        if ((avatarModFlag & AvatarLook.Unknown2) != 0) {
            // This int never gets used in the client.
            // Could be for Speed, or it isn't even implemented yet.
            packet.encodeInt(0);
        }
        if ((avatarModFlag & AvatarLook.Unknown3) != 0) {
            // This int never gets used in the client either.
            // My best guess is this isn't even implemented yet.
            packet.encodeInt(0);
        }
        if ((avatarModFlag & AvatarLook.Look) != 0) {
            packet.encodeBool(true);
            user.getAvatarLook().encode(packet);
            // This int does get used and updates something in CUserRemote.
            // Currently unknown still, however. MIGHT be hair?
            packet.encodeInt(0);
        } else {
            packet.encodeBool(false);
        }
        return packet;
    }
}
