if self.userRecordGetState(1041) == 1:
    self.registerTransferField(1010100, 4)
elif self.userRecordGetState(1042) == 1:
    self.registerTransferField(1010200, 4)
elif self.userRecordGetState(1043) == 1:
    self.registerTransferField(1010300, 4)
elif self.userRecordGetState(1044) == 1:
    self.registerTransferField(1010400, 4)
else:
    self.userScriptMessage("Only the adventurers that have been trained by Mai may enter.")