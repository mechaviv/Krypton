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
package game.script;

/**
 *
 * @author Eric
 */
public class ScriptMessage {
    public static final byte
            Say                 = 0,
            SayImage            = 1,
            AskYesNo            = 2,
            AskText             = 3,
            AskNumber           = 4,
            AskMenu             = 5,
            AskQuiz             = 6,
            AskSpeedQuiz        = 7,
            AskAvatar           = 8,
            AskMemberShopAvatar = 9,
            AskPet              = 10,
            AskPetAll           = 11,
            Script              = 12,
            AskAccept           = 13,
            AskBoxText          = 14,
            AskSlideMenu        = 15,
            AskCenter           = 16
                    ;
}
