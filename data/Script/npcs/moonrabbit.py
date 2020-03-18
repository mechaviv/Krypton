cTime = self.currentTime()
esTime = self.compareTime(cTime, "07/08/23/00/00")
if esTime < 0:
    self.say("Hi! My name is Tory. This place is full of the mysterious aura of the full moon and no one can get past this point.")
else:
    self.fieldSetEnter("MoonRabbit", self.userGetCharacterID(), 0)