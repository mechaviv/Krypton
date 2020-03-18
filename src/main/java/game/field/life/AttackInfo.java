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
package game.field.life;

import game.field.life.mob.Mob;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class AttackInfo {
    private static final int DEFAULT_MAX_ATTACKS = 15;

    public int mobID;
    public int templateID;
    public Mob deadMob;
    public int hitAction;
    public int foreAction;
    public int frameIDx;
    public int doomed;
    public int left;
    public Point hit;
    public Point posPrev;
    public int delay;
    public int attackCount;
    public int calcDamageStatIndex;
    public List<Integer> damageCli;
    public List<Integer> damageSvr;
    public List<Boolean> critical;
    public AttackInfo() {
        this.hit = new Point(0, 0);
        this.posPrev = new Point(0, 0);
        this.damageCli = new ArrayList<>(DEFAULT_MAX_ATTACKS);
        this.damageSvr = new ArrayList<>(DEFAULT_MAX_ATTACKS);
        this.critical = new ArrayList<>(DEFAULT_MAX_ATTACKS);
        for (int i = 0; i < DEFAULT_MAX_ATTACKS; i++) {
            this.damageCli.add(i, 0);
            this.damageSvr.add(i, 0);
            this.critical.add(i, false);
        }
    }
}
