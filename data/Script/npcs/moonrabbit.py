def moonrabbit_takeawayitem():
    nItem = self.inventoryGetItemCount(4001095)
    if nItem > 0:self.inventoryExchange(0, 4001095, -nItem)

    nItem = self.inventoryGetItemCount(4001096)
    if nItem > 0:self.inventoryExchange(0, 4001096, -nItem)

    nItem = self.inventoryGetItemCount(4001097)
    if nItem > 0:self.inventoryExchange(0, 4001097, -nItem)

    nItem = self.inventoryGetItemCount(4001098)
    if nItem > 0:self.inventoryExchange(0, 4001098, -nItem)

    nItem = self.inventoryGetItemCount(4001099)
    if nItem > 0:self.inventoryExchange(0, 4001099, -nItem)

    nItem = self.inventoryGetItemCount(4001100)
    if nItem > 0:self.inventoryExchange(0, 4001100, -nItem)

    nItem = self.inventoryGetItemCount(4001101)
    if nItem > 0:self.inventoryExchange(0, 4001101, -nItem)

cTime = self.currentTime()
esTime = self.compareTime(cTime, "07/08/23/00/00")
if esTime < 0:
    self.say("Hi! My name is Tory. This place is full of the mysterious aura of the full moon and no one can get past this point.")
else:
    fieldID = self.userGetFieldID()
    if fieldID == 100000200:
        if self.userIsPartyBoss() == False:
            self.sayNext("Hi! I'm Tory. This place is filled with the mysterious aura of the full moon and no one can enter alone!")
            self.say("If you want to get in here, your party leader needs to talk to me. Talk to the leader about it.")
        else:
            self.sayNext("Hi! I'm Tory. Inside there is a beautiful mountain where the primroses bloom. There is a tiger that lives on the mountain, the Growlie, and it seems to be looking for something to eat.")
            v0 = self.askMenu("Would you like to head to the mountain of primroses and join forces with your party to help Growlie?\r\n#b#L0# Yes, I will.#l")
            if v0 == 0:
                setParty = self.fieldSet("MoonRabbit")
                res = setParty.enter(self.userGetCharacterID(), 0)
                if res == -1: self.say("I have reasons for not being able to let you in. Please try again later.")
                elif res == 1: self.say("You cannot enter here alone! To enter here, the #bleader of the party#k have to talk with me.")
                elif res == 2: self.say("Sorry, but the your party doesn't have a minimum of 3 members. Your party must have at least 3 members of level 10 or higher.")
                elif res == 3: self.say("Sorry, but someone in your party is below level 10. Your party must have at least 3 members of level 10 or higher.")
                elif res == 4: self.say("Sorry, but there is another group in there. Please talk to me shortly.")
                else: moonrabbit_takeawayitem()
    elif fieldID == 910010100:
        v1 = self.askMenu("I would appreciate it if you could get a rice ball for the hungry Growlie. It looks like you don't have anything else to do here. Would you like to leave this place?\r\n#b#L0# Yes. Please get me out of here.#l")
        if v1 == 0:
            moonrabbit_takeawayitem()
            self.registerTransferField( 100000200, "" )
    elif fieldID == 910010400:
        v1 = self.askMenu("Guys, have you finished scaring these pigs out yet? It looks like you don't have anything else to do here. Would you like to leave this place?\r\n#b#L0# Yes, I would like to get out of here.#l")
        if v1 == 0:
            moonrabbit_takeawayitem()
            self.registerTransferField( 100000200, "" )