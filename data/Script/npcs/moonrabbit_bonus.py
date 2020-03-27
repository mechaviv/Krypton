fieldID = self.userGetFieldID()

if fieldID == 910010100:
    self.sayNext("Hello, there! I'm Tommy. There's a Pig Town nearby where we're standing. The pigs there are rowdy and uncontrollable to the point where they have stolen numerous weapons from travelers. They were kicked out from their towns, and are currently hiding out at the Pig Town.")
    if self.isPartyBoss():
        v0 = self.askMenu("What do you think about going up there with your party and teaching those rowdy pigs a lesson?\r\n#b#L0# Yeah, that sounds good! Take me there!")
        if v0 == 0:
            setParty = self.fieldSet("MoonPig")
            res = setParty.enter(self.userGetCharacterID(), 0)
            if res == -1: self.say("I have reasons for not being able to let you in. Please try again later.")
            elif res == 1: self.say("You cannot enter here alone! To enter here, the #bleader of the party#k have to talk with me.")
            elif res == 2: self.say("Sorry, but the your party doesn't have a minimum of 3 members. Your party must have at least 3 members of level 10 or higher.")
            elif res == 3: self.say("Sorry, but someone in your party is below level 10. Your party must have at least 3 members of level 10 or higher.")
            elif res == 4: self.say("Sorry, but there is another group in there. Please talk to me shortly.")
    else:
        self.say("If you really want to teach these pigs a lesson, enter the site through your party leader.")
elif fieldID == 910010200:
    v1 = self.askMenu("Would you like to stop hunting and leave this place?\r\n#b#L0# Yes. I would like to leave this place.#l")
    if v1 == 0: self.registerTransferField( 910010400, "st00" )
elif fieldID == 910010300:
    v1 = self.askMenu("I think you're done here. Would you like to leave this place?\r\n#b#L0# Yes. I would like to leave this place.#l")
    if v1 == 0: self.registerTransferField( 100000200, "" )