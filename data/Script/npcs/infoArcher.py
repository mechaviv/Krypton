self.sayNext("Bowmen are blessed with dexterity and power, taking charge of long-distance attacks, providing support for those at the front line of the battle. Very adept at using landscape as part of the arsenal.")

nRet = self.askYesNo("Would you like to experience what it's like to be a Bowman?")
if nRet == 0: self.sayNext("If you wish to experience what it's like to be a Bowman, come see me again.")
else:
    self.userSetDirectionMode(True, 0)
    self.userSetStandAloneMode(True)
    self.registerTransferField(1020300, "sp")