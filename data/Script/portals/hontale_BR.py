quest = self.fieldSet("Hontale3")
fieldID = self.userGetFieldID()

if fieldID == 240060000:
    if quest.getVariable("boss1") == "1":
        mNum = self.fieldGetMobCount(fieldID, 8810000)
        if mNum < 1:
            # target.playPortalSE;
            self.registerTransferField( 240060100, "st00" )
        else:
            self.userBroadcastMessage(6, "The portal doesn't work now.")
    else:
        self.userBroadcastMessage(6, "The portal doesn't work now.")
elif fieldID == 240060100:
    if quest.getVariable("boss2") == "1":
        mNum = self.fieldGetMobCount(fieldID, 8810001)
        if mNum < 1:
            # target.playPortalSE;
            self.registerTransferField( 240060200, "st00" )
        else:
            self.userBroadcastMessage(6, "The portal doesn't work now.")
    else:
        self.userBroadcastMessage(6, "The portal doesn't work now.")