self.sayNext("Warriors possess an enormous power with stamina to back it up, and they shine the brightest in melee combat situation. Regular attacks are powerful to begin with, and armed with complex skills, the job is perfect for explosive attacks.")

nRet = self.askYesNo("Would you like to experience what it's like to be a Warrior?")
if nRet == 0: self.sayNext("If you wish to experience what it's like to be a Warrior, come see me again.")
else:
    self.userSetDirectionMode(True, 0)
    self.userSetStandAloneMode(True)
    self.registerTransferField(1020100, "sp")