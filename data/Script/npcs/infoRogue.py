self.sayNext("Thieves are a perfect blend of luck, dexterity, and power that are adept at surprise attacks against helpless enemies. A high level of avoidability and speed allows Thieves to attack enemies from various angles.")

nRet = self.askYesNo("Would you like to experience what it's like to be a Thief?")
if nRet == 0: self.sayNext("If you wish to experience what it's like to be a Thief, come see me again.")
else:
    self.userSetDirectionMode(True, 0)
    self.userSetStandAloneMode(True)
    self.registerTransferField(1020400, "sp")