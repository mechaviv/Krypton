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
package game.user.command;

import common.BroadcastMsg;
import common.Request;
import common.game.field.FieldEffectFlags;
import common.user.CharacterStat;
import game.field.Field;
import game.field.FieldPacket;
import game.field.FieldSplit;
import game.field.drop.Drop;
import game.field.drop.DropPool;
import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import game.field.life.LifePool;
import game.field.life.mob.MobTemplate;
import game.field.life.npc.Npc;
import game.field.life.npc.NpcTemplate;
import game.field.reactor.ReactorTemplate;
import game.user.User;
import game.user.WvsContext;
import game.user.item.ItemInfo;
import util.Rand32;

import java.util.Iterator;

/**
 *
 * @author Eric
 */
public class DeveloperCommands {
    
    public static String whereami(User user, CharacterStat stat, String[] args) {
        user.sendSystemMessage("You are currently on field " + stat.getPosMap());
        return null;
    }
    
    public static String pos(User user, Field field, String[] args) {
        int x = user.getCurrentPosition().x;
        int y = user.getCurrentPosition().y;
        int fh = field.getSpace2D().getFootholdUnderneath(x, y).getSN();
        
        user.sendSystemMessage("Your character's current position is (" + x + "," + y + ") on foothold (" + fh + ")");
        return null;
    }
    
    public static String spawn(User user, LifePool lifePool, String[] args) {
        if (args.length > 0) {
            int templateID = Integer.parseInt(args[0]);
            int count = 1;
            if (args.length > 1) {
                count = Math.min(100, Math.max(1, Integer.parseInt(args[1])));
            }
            for (int i = 0; i < count; i++) {
                lifePool.createMob(lifePool.createMob(templateID), user.getCurrentPosition());
            }
            return null;
        }
        return "!spawn <mobid> [count] - Spawns a mob, or optionally a specific amount of mobs";
    }

    public static String testMobDrops(User user, LifePool lifePool, String[] args) {
        int templateID = Integer.parseInt(args[0]);
        MobTemplate template = MobTemplate.getMobTemplate(templateID);
        if (template == null) {
            return null;
        }
        if (template.getHpTagColor() == 0 || template.getHpTagBgColor() == 0 || template.isHpGaugeHide()) {
            return null;
        }
        user.getField().splitSendPacket(user.getSplit(), FieldPacket.onFieldEffect(FieldEffectFlags.MobHPTag, null, templateID, 21679688, 21679688, template.getHpTagColor(), template.getHpTagBgColor()), null);

        return null;
        /*if (args.length > 0) {
            int templateID = Integer.parseInt(args[0]);
            int count = 1;
            if (args.length > 1) {
                count = Math.min(100, Math.max(1, Integer.parseInt(args[1])));
            }
            for (int i = 0; i < count; i++) {
                lifePool.createMob(lifePool.createMob(templateID), user.getCurrentPosition(), true);
            }
            return null;
        }
        return "!testMobDrops <mobid> [count] - Spawns a mob, or optionally a specific amount of mobs (with all mob drops)";*/
    }

    public static String npc(User user, LifePool lifePool, String[] args) {
        if (args.length > 0) {
            int templateID = Integer.parseInt(args[0]);
            //Npc npc = lifePool.getNpc(Integer.parseInt(args[0]));
            if (NpcTemplate.getNpcTemplate(templateID) != null) {
            //if (npc != null) {
                int x = user.getCurrentPosition().x;
                int y = user.getCurrentPosition().y;
                lifePool.createNpc(null, templateID, x, y);
            } else {
                user.sendSystemMessage("The Npc you have entered does not have an existing template.");
            }
            return null;
        }
        return "!npc <id> - Spawns the specified NPC at your location";
    }
    
    public static String itemvac(User user, Field field, DropPool dropPool, String[] args) {
        for (Iterator<Drop> it = dropPool.getDrops().values().iterator(); it.hasNext();) {
            Drop drop = it.next();
            if (user.sendDropPickUpResultPacket(drop, Request.None)) {
                it.remove();
                for (FieldSplit split : drop.getSplits()) {
                    field.splitUnregisterFieldObj(split, FieldSplit.Drop, drop, drop.makeLeaveFieldPacket(Drop.PickedUpByUser, user.getCharacterID()));
                }
            }
        }
        return null;
    }
    public static String checkreactordrops(User user, Field field, DropPool dropPool, String[] args) {
        if (args.length <= 0) {
            return "!checkreactordrops <id> - Reload & Prints reactor drops";
        }
        ReactorTemplate template = ReactorTemplate.getReactorTemplate(Integer.parseInt(args[0]));
        if (template == null) {
            return "Can't find reactor template";
        }

        double INC_DROP_RATE = 3.0;
        user.sendSystemMessage("Reactor Rewards list for " + template.getTemplateID());
        for (RewardInfo rewardInfo : template.getRewardInfos()) {
            double perc = (double) rewardInfo.getProb() / (10000000.0 / INC_DROP_RATE); //((int) (rewardInfo.getProb() / 1000000000.0 * 100d * 1000)) / 1000d;
            String rate = perc + "%";
            if (rewardInfo.getItemId() != 0) {
                user.sendSystemMessage(String.format("%s (%d) - %s", ItemInfo.getItemName(rewardInfo.getItemId()), rewardInfo.getItemId(), rate));
            } else {
                int min = 2 * rewardInfo.getMoney() / 5 + 1;
                int max = 4 * rewardInfo.getMoney() / 5;
                user.sendSystemMessage(String.format("Money - (%d - %d) %s", min, max, rate));
            }
        }
        return null;
    }

    public static String checkmobdrops(User user, Field field, DropPool dropPool, String[] args) {
        if (args.length <= 0) {
            return "!checkmobdrops <id> - Reloads & Prints mob drops";
        }
        MobTemplate template = MobTemplate.getMobTemplate(Integer.parseInt(args[0]));
        if (template == null) {
            return "Can't find mob template";
        }

        double INC_DROP_RATE = 3.0;
        StringBuilder sb = new StringBuilder();
        sb.append("#rMob Rewards list for ").append(template.getTemplateID()).append("#k").append("\r\n\r\n#b");
        user.sendSystemMessage("Mob Rewards list for " + template.getTemplateID());
        for (RewardInfo rewardInfo : template.getRewardInfo()) {
            double perc = (double) rewardInfo.getProb() / (10000000.0 / INC_DROP_RATE); //((int) (rewardInfo.getProb() / 1000000000.0 * 100d * 1000)) / 1000d;
            String rate = perc + "%";
            if (rewardInfo.getItemId() != 0) {
                sb.append("#i").append(rewardInfo.getItemId()).append("#  (#z").append(rewardInfo.getItemId()).append("#)").append(" - ").append(rate).append("\r\n");
                user.sendSystemMessage(String.format("%s (%d) - %s", ItemInfo.getItemName(rewardInfo.getItemId()), rewardInfo.getItemId(), rate));
                //System.out.println(String.format("%s(%d) - %s/%d", ItemInfo.getItemName(rewardInfo.getItemId()), rewardInfo.getItemId(), rate, rewardInfo.getProb()));
            } else {
                int min = 2 * rewardInfo.getMoney() / 5 + 1;
                int max = 4 * rewardInfo.getMoney() / 5;
                // 4031138
                sb.append("#i").append(4031138).append("#  (#z").append(4031138).append("#)").append(" (").append(min).append("~").append(max).append(") - ").append(rate).append("\r\n");
                user.sendSystemMessage(String.format("Money - (%d - %d) %s", min, max, rate));
                //sb.append("Money").append(" (").append(min).append(" - ").append(max).append(")").append(" - ").append(rate).append("\r\n");
            }
        }
        user.sendPacket(WvsContext.onBroadcastMsg(BroadcastMsg.UTIL_DLG_EX, sb.toString(), 2007));

        return null;
    }
    public static String reloadrewards(User user, Field field, DropPool dropPool, String[] args) {
        MobTemplate.unload();
        ReactorTemplate.unload();
        MobTemplate.load(false);
        ReactorTemplate.load();
        return "Finished reloading reward data (mob & reactor)";
    }
}
