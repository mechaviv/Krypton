# The door of dimension
# Fighting map = 910540000 + (100 * jobType) (Warrior = 910540100)

val = self.questRecordGet(7500)
cJob = self.userGetJob()

if val == "p1":
    if cJob == 110 or cJob == 120 or cJob == 130 or \
       cJob == 210 or cJob == 220 or cJob == 230 or \
       cJob == 310 or cJob == 320 or \
       cJob == 410 or cJob == 420 or \
       cJob == 510 or cJob == 520:
        setParty = self.fieldSet("ThirdJob")
        res = setParty.enter(self.userGetCharacterID(), 0)
        if res != 0: self.say("There is already someone fighting #b#p1022000#'s#k clone. Come back later.")
    else:
        self.say("It looks like there is a door that will take me to another dimension, but I can't get in for some reason.")
else:
    self.say("It looks like there is a door that will take me to another dimension, but I can't get in for some reason.")