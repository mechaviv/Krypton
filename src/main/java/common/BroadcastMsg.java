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
package common;

/**
 *
 * @author Eric
 */
public class BroadcastMsg {
    public static final byte
            ALL = 0x0,
            CLONE = 0x1,
            MAP = 0x2,
            NOTICE = 0x0,
            ALERT = 0x1,
            SPEAKER_CHANNEL = 0x2,
            SPEAKER_WORLD = 0x3,
            SLIDE = 0x4,
            EVENT = 0x5,
            NOTICE_WITHOUT_PREFIX = 0x6,
            UTIL_DLG_EX = 0x7,
            ITEM_SPEAKER = 0x8,
            SPEAKER_BRIDGE = 0x9,
            ART_SPEAKER_WORLD = 0xA,
            BLOW_WEATHER = 0xB,
            GACHAPON_ANNOUNCE = 0xC,
            GACHAPON_ANNOUNCE_OPEN = 0xD,
            GACHAPON_ANNOUNCE_COPY = 0xE,
            U_LIST_CLIP = 0xF,
            FREE_MARKET_CLIP = 0x10,
            DESTROY_SHOP = 0x11,
            CASH_SHOP_AD = 0x12,
            HEART_SPEAKER = 0x13,
            SKULL_SPEAKER = 0x14
    ;
}
