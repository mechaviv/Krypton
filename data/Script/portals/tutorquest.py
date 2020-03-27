fieldID = self.userGetFieldID()
if fieldID == 130030001:
    if self.userRecordGetState(20010) != 1: self.userScriptMessage("Please start the quest first.")
    else: self.registerTransferField(130030002, "")
elif fieldID == 130030002:
    if self.userRecordGetState(20011) != 2: self.userScriptMessage("Please finish all the quests first.")
    else: self.registerTransferField(130030003, "")
elif fieldID == 130030003:
    if self.userRecordGetState(20012) != 2: self.userScriptMessage("Please finish all the quests first.")
    else: self.registerTransferField(130030004, "")
elif fieldID == 130030004:
    if self.userRecordGetState(20013) != 2: self.userScriptMessage("Please finish all the quests first.")
    else: self.registerTransferField(130030005, "")
else:
    self.userScriptMessage(str(fieldID))