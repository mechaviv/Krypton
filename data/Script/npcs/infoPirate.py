self.sayNext("Pirates are blessed with outstanding dexterity and power, utilizing their guns for long-range attacks while using their power in melee combat situations. Gunslingers use elemental-based bullets for added damage, while Brawlers transform into a different being for maximum effect.")

nRet = self.askYesNo("Would you like to experience what it's like to be a Pirate?")
if nRet == 0: self.sayNext("If you wish to experience what it's like to be a Pirate, come see me again.")
else:
    self.userSetDirectionMode(True, 0)
    self.userSetStandAloneMode(True)
    self.registerTransferField(1020500, "sp")