# Getting out of the Other Dimension
nRet = self.askYesNo("You can use the Sparkling Crystal to return back. Are you sure you want to come back?")
if nRet != 0:
    fieldID = self.userGetFieldID()
    if fieldID == 910540100: self.registerTransferField(102000000, "")
    elif fieldID == 910540200: self.registerTransferField(101000000, "")
    elif fieldID == 910540300:  self.registerTransferField(100000000, "")
    elif fieldID == 910540400: self.registerTransferField(103000000, "")
    elif fieldID == 910540500: self.registerTransferField(120000000, "")