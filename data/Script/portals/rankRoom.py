fieldID = self.userGetFieldID()

if fieldID == 130000000:
    self.registerTransferField(130000100, 5)
elif fieldID == 130000200:
    self.registerTransferField(130000100, 4)
elif fieldID == 140010100:
    self.registerTransferField(140010110, "")
elif fieldID == 120000101:
    self.registerTransferField(120000105, "")
elif fieldID == 103000003:
    self.registerTransferField(103000008, "")
elif fieldID == 100000201:
    self.registerTransferField(100000204, "")