# inside door of dimension
val = self.questRecordGet(7500)
cJob = self.userGetJob()

if self.userGetFieldID() == 108010400:
    if cJob == 110 or cJob == 120 or cJob == 130: self.registerTransferField(910540100, "")
    elif cJob == 210 or cJob == 220 or cJob == 230: self.registerTransferField(910540200, "")
    elif cJob == 310 or cJob == 320: self.registerTransferField(910540300, "")
    elif cJob == 410 or cJob == 420: self.registerTransferField(910540400, "")
    elif cJob == 510 or cJob == 520: self.registerTransferField(910540500, "")