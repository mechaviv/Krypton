# The job instructor for warriors
if self.userGetJob() == 100 and self.userGetLevel() >= 30:
    nBlack = self.inventoryGetItemCount(4031013)
    if self.inventoryGetItemCount(4031008) >= 1:
        if nBlack == 0:
            self.sayNext("Hmmm...it is definitely the letter from #b#p1022000##k...so you came all the way here to take the test and make the 2nd job advancement as the warrior. Alright, I'll explain the test to you. Don't need to worry, it's not that complicated.")
            self.sayNext("I'll send you to a hidden map. You'll see monsters you don't normally see. They look the same like the regular ones, but with a totally different attitude. They neither boost your experience level nor provide you with item.")
            nRet = self.askYesNo("Once you go inside, you can't leave until you take care of your mission. If you die, your experience level will decrease...so you better fasten up your belt and get ready...well, do you want to go for it now?")
            if not nRet: self.say("You don't look very prepared for this. Call me only when you are READY. There are no portals or stores around there, so you better be 100% prepared.")
            else:
                self.sayNext("Alright I'll let you in! Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you #b#t4031012##k, the proof that you've passed the test. Best of luck to you.")
                self.registerTransferField(910230000, "")
        elif nBlack > 0:
            nRet = self.askYesNo("So you already gave up once. Don't worry, you can always retake the test. Now...do you want to go back there and try one more time?")
            if not nRet: self.say("You don't look very prepared for this. Call me only when you are READY. There are no portals or stores around there, so you better be 100% prepared.")
            self.inventoryExchange(0, 4031013, -nBlack)
            self.registerTransferField(910230000, "")
    else:
        self.say("Do you want to become a Warrior much stronger than you already are? Let me take care of that. You seem to be more than qualified. Go find #b#p1022000##k from #m102000000# first ...")
elif self.userGetJob() == 100 and self.userGetLevel() < 30:
    self.say("Want to become a much stronger Warrior than you're now? Let me take care of it. You seem to be more than qualified. Go look for #b#p1022000##k in #m102000000# first...")
elif self.userGetJob() == 110 or self.userGetJob() == 120 or self.userGetJob() == 130:
    self.say("You became really strong after you passed my test!")