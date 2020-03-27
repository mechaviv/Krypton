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
package network.database;

import common.item.BodyPart;
import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemType;
import common.user.CharacterData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import util.FileTime;

/**
 * For identical queries across all JVM's.
 * 
 * @author Eric
 */
public class CommonDB {
    
    public static void rawGetInventorySize(int characterID, CharacterData cd) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `inventorysize` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String[] types = { "Equip", "Consume", "Install", "Etc", "Cash"};
                        for (int i = 1; i <= types.length; i++) {
                            int count = rs.getInt(String.format("%sCount", types[i - 1]));
                            for (int j = 0; j <= count; j++) {
                                cd.getItemSlot(i).add(j, null);
                                cd.getItemTrading().get(i).add(j, 0);
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawGetItemEquip(int characterID, CharacterData cd) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `itemslotequip` WHERE `CharacterID` = ? ORDER BY `SN`")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int itemID = rs.getInt("ItemID");
                        int pos = rs.getInt("POS");
                        
                        ItemSlotEquip item = new ItemSlotEquip(itemID);
                        item.setDateExpire(FileTime.longToFileTime(rs.getLong("ExpireDate")));
                        item.setCashItemSN(rs.getLong("CashItemSN"));
                        item.setItemSN(rs.getLong("ItemSN"));
                        item.item.ruc = rs.getByte("RUC");
                        item.item.cuc = rs.getByte("CUC");
                        item.item.iSTR = rs.getShort("I_STR");
                        item.item.iDEX = rs.getShort("I_DEX");
                        item.item.iINT = rs.getShort("I_INT");
                        item.item.iLUK = rs.getShort("I_LUK");
                        item.item.iMaxHP = rs.getShort("I_MaxHP");
                        item.item.iMaxMP = rs.getShort("I_MaxMP");
                        item.item.iPAD = rs.getShort("I_PAD");
                        item.item.iMAD = rs.getShort("I_MAD");
                        item.item.iPDD = rs.getShort("I_PDD");
                        item.item.iMDD = rs.getShort("I_MDD");
                        item.item.iACC = rs.getShort("I_ACC");
                        item.item.iEVA = rs.getShort("I_EVA");
                        item.item.iCraft = rs.getShort("I_Craft");
                        item.item.iSpeed = rs.getShort("I_Speed");
                        item.item.iJump = rs.getShort("I_Jump");

                        item.item.attribute = rs.getShort("Attribute");
                        item.item.levelUpType = rs.getByte("LevelUpType");
                        item.item.level = rs.getByte("Level");
                        item.item.exp = rs.getInt("Exp");
                        item.item.durability = rs.getInt("Durability");
                        item.item.iuc = rs.getInt("IUC");

                        item.option.grade = rs.getByte("O_Grade");
                        item.option.chuc = rs.getByte("O_CHUC");
                        item.option.option1 = rs.getShort("O_Option1");
                        item.option.option2 = rs.getShort("O_Option2");
                        item.option.option3 = rs.getShort("O_Option3");

                        String title = rs.getString("Title");
                        if (title == null) {
                            title = "";
                        }
                        item.title = title;

                        cd.setItem(ItemType.Equip, pos, item);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawGetItemBundle(int characterID, CharacterData cd) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `itemslotbundle` WHERE `CharacterID` = ? ORDER BY `SN`")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int itemID = rs.getInt("ItemID");
                        int pos = rs.getInt("POS");
                        byte ti = rs.getByte("TI");
                        
                        ItemSlotBundle item = new ItemSlotBundle(itemID);
                        item.setDateExpire(FileTime.longToFileTime(rs.getLong("ExpireDate")));
                        item.setCashItemSN(rs.getLong("CashItemSN"));
                        item.setItemSN(rs.getLong("ItemSN"));
                        item.setItemNumber(rs.getShort("Number"));
                        item.setItemAttribute(rs.getShort("Attribute"));
                        String title = rs.getString("Title");
                        if (title == null) {
                            title = "";
                        }
                        item.setItemTitle(title);
                        cd.setItem(ti, pos, item);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawLoadItemInitSN(int worldID, AtomicLong itemSN, AtomicLong cashItemSN) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `iteminitsn` WHERE `WorldID` = ?")) {
                ps.setInt(1, worldID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        itemSN.set(rs.getLong("ItemSN"));
                        cashItemSN.set(rs.getLong("CashItemSN"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawUpdateItemBundle(int characterID, List<List<ItemSlotBase>> itemSlot) {
        try (Connection con = Database.getDB().poolConnection()) {
            String removeSN = "";
            String removeCashSN = "";
            try (PreparedStatement ps = con.prepareStatement("UPDATE `itemslotbundle` SET `CharacterID` = ?, `POS` = ?, `ItemID` = ?, `Number` = ?, `TI` = ?, `ExpireDate` = ? WHERE `ItemSN` = ? AND `CashItemSN` = ?")) {
                for (int ti = ItemType.Consume; ti <= ItemType.Cash; ti++) {
                    for (int pos = 1; pos < itemSlot.get(ti).size(); pos++) {
                        ItemSlotBundle item = (ItemSlotBundle) itemSlot.get(ti).get(pos);
                        if (item != null) {
                            if (item.getSN() != 0) {
                                removeSN += item.getSN() + ", ";
                            }
                            if (item.getCashItemSN() != 0) {
                                removeCashSN += item.getCashItemSN() + ", ";
                            }
                            Database.execute(con, ps, characterID, pos, item.getItemID(), item.getItemNumber(), ti, item.getDateExpire().fileTimeToLong(), item.getSN(), item.getCashItemSN());
                        }
                    }
                }
            }
            if (removeSN.isEmpty() && removeCashSN.isEmpty()) {
                return;//wouldn't want to kill their inventory ;)
            }
            String query = "DELETE FROM `itemslotbundle` WHERE `CharacterID` = ?";
            if (!removeSN.isEmpty()) {
                query += String.format(" AND `ItemSN` NOT IN (%s)", removeSN.substring(0, removeSN.length() - 2));
            }
            if (!removeCashSN.isEmpty()) {
                query += String.format(" AND `CashItemSN` NOT IN (%s)", removeCashSN.substring(0, removeCashSN.length() - 2));
            }
            try (PreparedStatement ps = con.prepareStatement(query)) {
                Database.execute(con, ps, characterID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawUpdateItemEquip(int characterID, List<ItemSlotBase> equipped, List<ItemSlotBase> cashEquipped, List<ItemSlotBase> itemSlot) {
        try (Connection con = Database.getDB().poolConnection()) {
            String removeSN = "";
            String removeCashSN = "";
            // Equip Inventory
            if (itemSlot != null) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE `itemslotequip` SET `CharacterID` = ?, `POS` = ?, `ItemID` = ?, `RUC` = ?, `CUC` = ?, `I_STR` = ?, `I_DEX` = ?, `I_INT` = ?, `I_LUK` = ?, `I_MaxHP` = ?, `I_MaxMP` = ?, `I_PAD` = ?, `I_MAD` = ?, `I_PDD` = ?, `I_MDD` = ?, `I_ACC` = ?, `I_EVA` = ?, `I_Craft` = ?, `I_Speed` = ?, `I_Jump` = ?, `O_Grade` = ?, `O_CHUC` = ?, `O_Option1` = ?, `O_Option2` = ?, `O_Option3` = ?, `ExpireDate` = ?, `Title` = ?, `Attribute` = ?, `LevelUpType` = ?, `Level` = ?, `Exp` = ?, `Durability` = ?, `IUC` = ? WHERE `ItemSN` = ? AND `CashItemSN` = ?")) {
                    for (int pos = 1; pos < itemSlot.size(); pos++) {
                        ItemSlotEquip item = (ItemSlotEquip) itemSlot.get(pos);
                        if (item != null) {
                            if (item.getSN() != 0) {
                                removeSN += item.getSN() + ", ";
                            }
                            if (item.getCashItemSN() != 0) {
                                removeCashSN += item.getCashItemSN() + ", ";
                            }
                            Database.execute(con, ps, characterID, pos, item.getItemID(), item.item.ruc, item.item.cuc, item.item.iSTR, item.item.iDEX, item.item.iINT, item.item.iLUK, item.item.iMaxHP, item.item.iMaxMP, item.item.iPAD, item.item.iMAD, item.item.iPDD, item.item.iMDD, item.item.iACC, item.item.iEVA, item.item.iCraft, item.item.iSpeed, item.item.iJump, item.option.grade, item.option.chuc, item.option.option1, item.option.option2, item.option.option3, item.getDateExpire().fileTimeToLong(), item.title, item.item.attribute, item.item.levelUpType, item.item.level, item.item.exp, item.item.durability, item.item.iuc, item.getSN(), item.getCashItemSN());
                        }
                    }
                }
            }
            // Equipped Inventory
            if (equipped != null) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE `itemslotequip` SET `CharacterID` = ?, `POS` = ?, `ItemID` = ?, `RUC` = ?, `CUC` = ?, `I_STR` = ?, `I_DEX` = ?, `I_INT` = ?, `I_LUK` = ?, `I_MaxHP` = ?, `I_MaxMP` = ?, `I_PAD` = ?, `I_MAD` = ?, `I_PDD` = ?, `I_MDD` = ?, `I_ACC` = ?, `I_EVA` = ?, `I_Craft` = ?, `I_Speed` = ?, `I_Jump` = ?, `O_Grade` = ?, `O_CHUC` = ?, `O_Option1` = ?, `O_Option2` = ?, `O_Option3` = ?, `ExpireDate` = ?, `Title` = ?, `Attribute` = ?, `LevelUpType` = ?, `Level` = ?, `Exp` = ?, `Durability` = ?, `IUC` = ? WHERE `ItemSN` = ? AND `CashItemSN` = ?")) {
                    for (int pos = 1; pos < equipped.size(); pos++) {
                        ItemSlotEquip item = (ItemSlotEquip) equipped.get(pos);
                        if (item != null) {
                            if (item.getSN() != 0) {
                                removeSN += item.getSN() + ", ";
                            }
                            if (item.getCashItemSN() != 0) {
                                removeCashSN += item.getCashItemSN() + ", ";
                            }
                            Database.execute(con, ps, characterID, -pos, item.getItemID(), item.item.ruc, item.item.cuc, item.item.iSTR, item.item.iDEX, item.item.iINT, item.item.iLUK, item.item.iMaxHP, item.item.iMaxMP, item.item.iPAD, item.item.iMAD, item.item.iPDD, item.item.iMDD, item.item.iACC, item.item.iEVA, item.item.iCraft, item.item.iSpeed, item.item.iJump, item.option.grade, item.option.chuc, item.option.option1, item.option.option2, item.option.option3, item.getDateExpire().fileTimeToLong(), item.title, item.item.attribute, item.item.levelUpType, item.item.level, item.item.exp, item.item.durability, item.item.iuc, item.getSN(), item.getCashItemSN());
                        }
                    }
                }
            }
            // Cash Equipped Inventory
            if (cashEquipped != null) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE `itemslotequip` SET `CharacterID` = ?, `POS` = ?, `ItemID` = ?, `RUC` = ?, `CUC` = ?, `I_STR` = ?, `I_DEX` = ?, `I_INT` = ?, `I_LUK` = ?, `I_MaxHP` = ?, `I_MaxMP` = ?, `I_PAD` = ?, `I_MAD` = ?, `I_PDD` = ?, `I_MDD` = ?, `I_ACC` = ?, `I_EVA` = ?, `I_Craft` = ?, `I_Speed` = ?, `I_Jump` = ?, `O_Grade` = ?, `O_CHUC` = ?, `O_Option1` = ?, `O_Option2` = ?, `O_Option3` = ?, `ExpireDate` = ?, `Title` = ?, `Attribute` = ?, `LevelUpType` = ?, `Level` = ?, `Exp` = ?, `Durability` = ?, `IUC` = ? WHERE `ItemSN` = ? AND `CashItemSN` = ?")) {
                    for (int pos = 1; pos < cashEquipped.size(); pos++) {
                        ItemSlotEquip item = (ItemSlotEquip) cashEquipped.get(pos);
                        if (item != null) {
                            if (item.getSN() != 0) {
                                removeSN += item.getSN() + ", ";
                            }
                            if (item.getCashItemSN() != 0) {
                                removeCashSN += item.getCashItemSN() + ", ";
                            }
                            Database.execute(con, ps, characterID, -pos - BodyPart.BP_Count, item.getItemID(), item.item.ruc, item.item.cuc, item.item.iSTR, item.item.iDEX, item.item.iINT, item.item.iLUK, item.item.iMaxHP, item.item.iMaxMP, item.item.iPAD, item.item.iMAD, item.item.iPDD, item.item.iMDD, item.item.iACC, item.item.iEVA, item.item.iCraft, item.item.iSpeed, item.item.iJump, item.option.grade, item.option.chuc, item.option.option1, item.option.option2, item.option.option3, item.getDateExpire().fileTimeToLong(), item.title, item.item.attribute, item.item.levelUpType, item.item.level, item.item.exp, item.item.durability, item.item.iuc, item.getSN(), item.getCashItemSN());
                        }
                    }
                }
            }
            if (removeSN.isEmpty() && removeCashSN.isEmpty()) {
                return;//wouldn't want to kill their inventory ;)
            }
            String query = "DELETE FROM `itemslotequip` WHERE `CharacterID` = ?";
            if (!removeSN.isEmpty()) {
                query += String.format(" AND `ItemSN` NOT IN (%s)", removeSN.substring(0, removeSN.length() - 2));
            }
            if (!removeCashSN.isEmpty()) {
                query += String.format(" AND `CashItemSN` NOT IN (%s)", removeCashSN.substring(0, removeCashSN.length() - 2));
            }
            try (PreparedStatement ps = con.prepareStatement(query)) {
                Database.execute(con, ps, characterID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawUpdateItemInitSN(int worldID, AtomicLong itemSN, AtomicLong cashItemSN) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `iteminitsn` SET `ItemSN` = ?, `CashItemSN` = ? WHERE `WorldID` = ?")) {
                Database.execute(con, ps, itemSN.get(), cashItemSN.get(), worldID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
