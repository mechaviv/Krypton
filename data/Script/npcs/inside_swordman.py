# Warrior Job Instructor
if self.userGetJob() == 100 and self.userGetLevel() >= 30:
    if self.inventoryGetItemCount(4031013) >= 30:
        self.sayNext("Ohhhhh...you collected all 30 Dark Marbles!! It should have been difficult...just incredible! Alright! You've passed the test and for that..I'll reward you #b#t4031012##k. Take that and go back to #m102000000#.")
        nBlack = self.inventoryGetItemCount(4031013)
        ret = self.inventoryExchange(0, 4031013, -nBlack, 4031008, -1, 4031012, 1)
        if not ret: self.say("Something went wrong... make sure you have 30 Dark Marbles, the letter from #b#p1022000##k and an empty slot in your ETC inventory.")
        else:
            self.registerTransferField(102020300, "")
    else:
        nRet = self.askYesNo("What sup? I think you don't have 30 #bDark Marbles#k yet... if you are having problems with this you can leave now and try again later. So... do you want to give up and get out of here?")
        if nRet == 0: self.say("That's it! Stop complaining and start gathering the marbles. Come talk to me when you have gathered 30 #b#t4031013#s#k.")
        self.sayNext("Okay, I'll let you out. But don't give up. You can always try again, so don't give up. See ya, goodbye...")
        self.registerTransferField(102020300, "")
else:
    self.sayNext( "What is it? How did you get here? ... how strange ... well, I'll let you out. This is a very dangerous place. Go away or take more risks." );
    self.registerTransferField(102020300, "")