self.sayNext("Magicians are armed with flashy element-based spells and secondary magic that aids party as a whole. After the 2nd job adv., the elemental-based magic will provide ample amount of damage to enemies of opposite element.")

nRet = self.askYesNo("Would you like to experience what it's like to be a Magician?")
if nRet == 0: self.sayNext("If you wish to experience what it's like to be a Magician, come see me again.")
else:
    self.userSetDirectionMode(True, 0)
    self.userSetStandAloneMode(True)
    self.registerTransferField(1020200, "sp")