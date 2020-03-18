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
package game.field;

import java.awt.Point;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import game.field.event.EventManager;
import util.Logger;
import util.Size;
import util.TimerThread;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class FieldMan {
    private static FieldMan[] managers;
	private static ReentrantLock lock;
    
    private final Map<Integer, Field> fields;
    private final Map<String, FieldSet> fieldSets;

    public FieldMan() {
        this.fields = new ConcurrentHashMap<>();
        this.fieldSets = new ConcurrentHashMap<>();
        TimerThread.Field.Register(() -> {
            FieldMan.this.update(System.currentTimeMillis());
            EventManager.update(System.currentTimeMillis());
        }, 100, 1000);
    }
    
    public static FieldMan getInstance(int channel) {
        if (channel >= 0 && channel < managers.length) {
            return managers[channel];
        }
        return null;
    }

    public static void init(int channels) {
        managers = new FieldMan[channels];
        lock = new ReentrantLock();

        boolean loaded = false;
        for (int i = 0; i < channels; i++) {
            managers[i] = new FieldMan();
            if (loaded) {
                // instead reload for each ch just copy from the prev ch
                managers[i].fields.putAll(managers[i - 1].fields);
                managers[i].fieldSets.putAll(managers[i - 1].fieldSets);
            } else {
                managers[i].registerField();
                managers[i].loadFieldSet(i);
                loaded = true;
            }
        }
    }

    public Field getField(int fieldID) {
        if (fieldID == Field.Invalid) {
            return null;
        }
        return fields.getOrDefault(fieldID, null);
    }

    public FieldSet getFieldSet(String name) {
        return fieldSets.getOrDefault(name, null);
    }

    public boolean isBlockedMap(int fieldID) {
        // Probably nothing even worth blocking in this version.
        return false;
    }

    private void registerField() {
        Logger.logReport("Loading Field Info");
        WzPackage mapDir = new WzFileSystem().init("Map/Map").getPackage();
        if (mapDir != null) {
            registerField(mapDir);
            mapDir.release();
        }
        mapDir = null;
    }

    public void registerField(WzPackage mainDir) {
        for (WzPackage fieldDir : mainDir.getChildren().values()) {
            for (WzProperty field : fieldDir.getEntries().values()) {
                int fieldID = Integer.parseInt(field.getNodeName().replaceAll(".img", ""));
                WzProperty info = field.getNode("info");

                String link = WzUtil.getString(info.getNode("link"), null);
                if (link != null) {
                    int fieldLink = Integer.parseInt(link);
                    field = mainDir.getChildren().get(String.format("Map%d", fieldLink / 100000000)).getItem(String.format("%d.img", fieldLink));
                }
                fields.put(fieldID, registerField(fieldID, field, info));
            }
            fieldDir.release();
        }
    }

    private Field registerField(int fieldID, WzProperty mapData, WzProperty info) {
        final Field field = new Field(fieldID);

        if (info != null) {
            field.setFieldReturn(WzUtil.getInt32(info.getNode("returnMap"), Field.Invalid));
            field.setForcedReturn(WzUtil.getInt32(info.getNode("forcedReturn"), Field.Invalid));
            field.setMobRate(WzUtil.getFloat(info.getNode("mobRate"), 1.0f));
            field.setRecoveryRate(WzUtil.getFloat(info.getNode("recovery"), 1.0f));
            field.setStreetName(WzUtil.getString(info.getNode("streetName"), "NULL"));
            field.setMapName(WzUtil.getString(info.getNode("mapName"), "NULL"));
            field.setOption(WzUtil.getInt32(info.getNode("fieldLimit"), 0));
            field.setAutoDecHP(WzUtil.getInt32(info.getNode("decHP"), 0));
            field.setAutoDecMP(WzUtil.getInt32(info.getNode("decMP"), 0));
            field.setClock(WzUtil.getBoolean(info.getNode("clock"), false));
            field.setTown(WzUtil.getBoolean(info.getNode("town"), false));
            field.setSwim(WzUtil.getBoolean(info.getNode("swim"), false));

            field.getSpace2D().setFieldAttr(WzUtil.getFloat(info.getNode("fs"), 1.0f), field.isSwim());
        }

        restoreFoothold(field, mapData.getNode("foothold"), mapData.getNode("ladderRope"), info);
        field.makeSplit();

        field.getPortal().restorePortal(mapData.getNode("portal"), field);

        field.getLifePool().init(field, mapData);
        return field;
    }
    
    private void restoreFoothold(Field field, WzProperty propFoothold, WzProperty ladderOrRope, WzProperty info) {
        field.getSpace2D().load(field, propFoothold, ladderOrRope, info);
        field.setLeftTop(new Point(field.getSpace2D().getMBR().left, field.getSpace2D().getMBR().top));
        field.setMapSize(new Size(field.getSpace2D().getMBR().right - field.getSpace2D().getMBR().left, field.getSpace2D().getMBR().bottom - field.getSpace2D().getMBR().top));
    }

    public void loadFieldSet(int channelID) {
        WzProperty fieldSetData = new WzFileSystem().init("Map").getPackage().getItem("FieldSet.img");
        if (fieldSetData != null) {
            for (WzProperty fieldSet : fieldSetData.getChildNodes()) {
                String fieldSetName = fieldSet.getNodeName();
                FieldSet set = new FieldSet(fieldSetName, channelID);
                if (set.init(fieldSet)) {
                    Logger.logReport("FieldSet [%s] loaded successfully", fieldSetName);
                    fieldSets.put(fieldSetName, set);
                }
            }
        }
    }

    private void update(long time) {
        updateField(time);
        updateFieldSet(time);
    }
    
    private void updateField(long time) {
        for (Field field : fields.values()) {
            field.update(time);
        }
    }

    private void updateFieldSet(long time) {
        for (FieldSet set : fieldSets.values()) {
            if (set.isFieldSetStart()) {
                set.updateFieldSet(time);
            }
        }
    }

    public boolean isConnected(int from, int to) {
        from /= 100000;
        to /= 100000;
        if (from == to) {
            return true;
        }
        // Not really sure how else to check this..
        return true;
    }
}
