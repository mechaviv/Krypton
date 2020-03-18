ret = self.askYesNo("Are you ready?")
if ret:
    setParty = self.fieldSetEnter("Adin", self.userGetCharacterID(), 0)
    if setParty is not 0:
        self.say("Result = " + str(setParty))
else:
    self.say("okay")