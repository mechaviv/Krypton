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
package game.party;

/**
 *
 * @author Eric
 */
public class PartyResCode {
    public static final byte
            // PartyReq
            LoadParty                       = 0,
            CreateNewParty                  = 1,
            WithdrawParty                   = 2,
            JoinParty                       = 3,
            InviteParty                     = 4,
            KickParty                       = 5,
            ChangePartyBoss                 = 6,
            // PartyRes
            LoadParty_Done                  = 7,
            CreateNewParty_Done             = 8,
            CreateNewParty_AlreadyJoined    = 9,
            CreateNewParty_Beginner         = 10,
            CreateNewParty_Unknown          = 11,
            WithdrawParty_Done              = 12,
            WithdrawParty_NotJoined         = 13,
            WithdrawParty_Unknown           = 14,
            JoinParty_Done                  = 15,
            JoinParty_Done2                 = 16,
            JoinParty_AlreadyJoined         = 17,
            JoinParty_AlreadyFull           = 18,
            JoinParty_OverDesiredSize       = 19,
            JoinParty_UnknownUser           = 20,
            JoinParty_Unknown               = 21,
            InviteParty_Sent                = 22,
            InviteParty_BlockedUser         = 23,
            InviteParty_AlreadyInvited      = 24,
            InviteParty_AlreadyInvitedByInviter = 25,
            InviteParty_Rejected            = 26,
            InviteParty_Accepted            = 27,
            KickParty_Done                  = 28,
            KickParty_FieldLimit            = 29,
            KickParty_Unknown               = 30,
            ChangePartyBoss_Done            = 31,
            ChangePartyBoss_NotSameField    = 32,
            ChangePartyBoss_NoMemberInSameField = 33,
            ChangePartyBoss_NotSameChannel  = 34,
            ChangePartyBoss_Unknown         = 35,
            AdminCannotCreate               = 36,
            AdminCannotInvite               = 37,
            UserMigration                   = 38,
            ChangeLevelOrJob                = 39,
            SuccessToSelectPQReward         = 40,
            FailToSelectPQReward            = 41,
            ReceivePQReward                 = 42,
            FailToRequestPQReward           = 43,
            CanNotInThisField               = 44,
            ServerMsg                       = 45,
            PartyInfo_TownPortalChanged     = 46,
            PartyInfo_OpenGate              = 47
                    ;
}
