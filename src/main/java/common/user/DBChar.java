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
package common.user;

/**
 *
 * @author Eric
 */
public class DBChar {
    public static final int
            Character           = 0x1,
            Money               = 0x2,
            ItemSlotEquip       = 0x4,
            ItemSlotConsume     = 0x8,
            ItemSlotInstall     = 0x10,
            ItemSlotEtc         = 0x20,
            ItemSlotCash        = 0x40,
            InventorySize       = 0x80,
            SkillRecord         = 0x100,
            QuestRecord         = 0x200,
            MiniGameRecord      = 0x400,
            CoupleRecord        = 0x800,
            MapTransfer         = 0x1000,
            Avatar              = 0x2000,
            QuestComplete       = 0x4000,
            SkillCooltime       = 0x8000,
            MonsterBookCard     = 0x10000,
            MonsterBookCover    = 0x20000,
            NewYearCard         = 0x40000,
            QuestRecordEx       = 0x80000,
            AdminShopCount      = 0x100000,
            EquipTxt            = 0x100000,
            WildHunterInfo      = 0x200000,
            QuestComplete_Old   = 0x400000,
            VisitorLog          = 0x800000,
            VisitorLog_1        = 0x1000000,
            VisitorLog_2        = 0x2000000,
            VisitorLog_3        = 0x4000000,
            VisitorLog_4        = 0x8000000,
            All                 = 0xFFFFFFFF,
            ItemSlot            = 0x7C// Flag for all item slots
                    ;
}
