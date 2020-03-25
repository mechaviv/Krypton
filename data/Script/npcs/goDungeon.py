# The NPC that allows you into the dungeon
if self.userRecordGetState(6263) == 2:
    if self.inventoryGetItemCount(4031450) > 0:
        v0 = self.askMenu("You ... You really did the #b#t4031450##k. No one guarantees more quality than Vogen. Okay, I'll let you in #m921100100#.\r\n#b#L0# Yes. Please take me to #m921100100#.#l\r\n#L1# I would like to stay here.#l")
        if v0 == 0:
            self.say("Not coded yet")
            #quest = self.fieldSet("S4freeze")
            #ret = quest.enter(self.userGetCharacterID(), 0)
            #if ret != 0: self.say("There is already someone on the mission. Talk to me later. ")
else:
    self.sayNext("Hey, it looks like you want to go much further away from here. There, however, you will find yourself with monsters everywhere, aggressive, dangerous and, even if you think you are ready, be careful. A long time ago, some brave men from our city went to eliminate whoever threatened the city, but they never came back...")
    if self.userGetLevel() >= 50:
        nRet = self.askYesNo("If you are thinking of entering, I suggest you change your mind. But if you really want to get in... Only those who are strong enough to stay alive inside will be allowed. I don't want to see anyone else die. Let me see... Hmmm... you look strong enough. Okay, do you want to come in?")
        if nRet == 0: self.say("Even though your level is high, it is difficult to enter there. But if you change your mind, talk to me. After all, my duty is to protect this place.")
        elif nRet == 1: self.registerTransferField(211040300, "under00")
    else: self.say("If you are thinking of entering, I suggest you change your mind. But if you really want to get in... Only those who are strong enough to stay alive inside will be allowed. I don't want to see anyone else die. Let me see... Hmmm... you haven't reached level 50 yet. I can't let you in, forget about it.")