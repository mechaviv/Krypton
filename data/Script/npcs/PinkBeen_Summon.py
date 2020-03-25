nRet = self.askAccept("With only the Mirror of the Goddess, I can summon the Black Mage again! But... Why isn't it working? What is this strange energy? It's completely different from the Black Mage... AHHHH!\r\n\r\n#b(You place your hands on Kirston's shoulders.)#k")

if nRet == 1:
    fieldID = 270050100
    '''self.fieldSummonMob(fieldID, 8820008, 5, -42, 1)
    self.fieldSummonMob(fieldID, 8820002, 5, -42, 1)
    self.fieldSummonMob(fieldID, 8820003, 5, -42, 1)
    self.fieldSummonMob(fieldID, 8820004, 5, -42, 1)
    self.fieldSummonMob(fieldID, 8820005, 5, -42, 1)
    self.fieldSummonMob(fieldID, 8820006, 5, -42, 1)
    self.fieldSummonMob(fieldID, 8820014, 5, -42, 1)'''

    self.fieldSummonMob(5, -75, 2100070)
    #remove npc 2141000
    self.fieldRemoveMob(fieldID, 8820019)
    self.fieldRemoveMob(fieldID, 8820020)
    self.fieldRemoveMob(fieldID, 8820021)
    self.fieldRemoveMob(fieldID, 8820022)
    self.fieldRemoveMob(fieldID, 8820023)
    #self.fieldRemoveAllMob(270050100)
    #self.fieldSummonMob(5, -75, 2100070)
    self.fieldSetMobGen(fieldID, False, 0)
