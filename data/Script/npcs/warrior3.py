level = self.userGetLevel()
job = self.userGetJob()

def changeJob(jobCode):
    nRet = self.askYesNo( "Right! Now I will turn you into a much more powerful warrior. But before that, make sure your SP has been well used. You need to use at least all SP received up to level 70 to make the 3rd job advance. Oh, and since you already chose your profession at the second job level, you don't have to choose again at the third. Want to do it now?" )
    if nRet == 0: self.say( "You already passed the test, so there is no reason to worry... well, come talk to me when you make your decision. Once you are done, I will give you your 3rd job advance. As long as you are ready ..." )
    else:
        nPSP = (level - 70) * 3
        if self.userGetSP() > nPSP: self.say( "Hummm... you seem to have too much #bSP#k. You will not be able to make the 3rd job advancement with so much unused SP. Use more SP on 1s and 2nd job skills." )
        else:
            if jobCode == 110: self.userJob(111)
            elif jobCode == 120: self.userJob(121)
            elif jobCode == 130: self.userJob(131)

            cJob = self.userGetJob()
            self.userIncSP(1, False)
            self.userIncAP(5, False)

            if cJob == 111: self.sayNext( "You just became a #bCrusader#k. Some of his new attacking skills, such as #bShout#k and #bCombo Attack#k, are devastating, and #bArmor Crash # k cause the monsters defense skills to fail. You better focus on gaining skills for the weapon you specialize in in your days as Hero." )
            elif cJob == 121: self.sayNext( "You just became a #bWhite Knight#k. You will be presented with a new skill book with various unspoken attack skills, as well as element-based attacks. It is recommended that the type of weapon complementary to the Squire, be it a sword or apple, continue to exist for the White Knight. There is a skill called #bCharge#k that adds the fire, ice or lightning element to the weapon, making the White Knight the only warrior who can perform element-based attacks. Charge your weapon with an element that weakens the monster and then deals massive damage with #bCharge Attack#k. This will definitely make you a devastating force." )
            elif cJob == 131: self.sayNext( "From now on, you are a #bDragon Knight #k You will meet a number of new attack skills for Spears and Polearms, and the weapon that was chosen for the Lancer must continue to exist for the Draconian Knight. Skills such as #bSacrifice#k (maximum damage on a single monster) and #bDragon's Fury#k (damage to multiple monsters) are recommended as preferred attack skills, and the skill called #bDragon Roar#k deals damage to any creature on the screen with a devastating force. The downside is that it uses more than half of the available HP." )
            self.say( "I also gave you some SP and AP, which will help you get started. Now you have become a truly powerful warrior. But remember that the real world is waiting for you with even more difficult obstacles to overcome. When you find that you are no longer able to train to reach a higher level, and only then will you come see me. I'll be here waiting." )

nRet = -1
if level < 50:
    self.say("Hmm... Looks like there's nothing I can do to help you. Come back when you get stronger.")
    nRet = -1
elif level >= 50 and level < 70: nRet = self.askMenu("Can I help you?\r\n#b#L1#Please allow me to do the Zakum Dungeon Quest#l#k")
elif level >= 70:
    #if self.userRecordGetState(6192) == 1: nRet = self.askMenu("Can I help you?\r\n")
    if job == 110 or job == 120 or job == 130: nRet = self.askMenu("Can I help you?\r\n#b#L0#I want to make the 3rd job advancement#l#k\r\n#b#L1#Please allow me to do the Zakum Dungeon Quest#l#k")
    else: nRet = self.askMenu("Can I help you?\r\n#b#L1#Please allow me to do the Zakum Dungeon Quest#l#k")
if nRet == 0:
    val = self.userRecordGetState(7500)
    info = self.questRecordGet(7500)
    if level >= 70:
        if job == 110 or job == 120 or job == 130:
            if val == 0:
                nRet = self.askYesNo("Welcome. I'm #bTylus#k, the chief of all warriors, in charge of bringing out the best in each and every warrior that needs my guidance. You seem like the kind of warrior that wants to make the leap forward, the one ready to take on the challenges of the 3rd job advancement. But I've seen countless warriors eager to make the jump just like you, only to see them fail. What about you? Are you ready to be tested and make the 3rd job advancement?")
                if nRet == 0: self.say("I do not think you are ready to face the challenges ahead. Come see me only when you convince yourself that you are ready to face the challenges that come along with your advancement.")
                else:
                    self.questRecordSet(7500, "s")
                    self.sayNext("Good. You will be tested on two important aspects of the warrior: strength and wisdom. I'll now explain to you the physical half of the test. Remember #b#p1022000##k from Perion? Go see him, and he'll give you the details on the first half of the test. Please complete the mission, and get #b#t4031057##k from #p1022000#.")
                    self.say("The mental half of the test can only start after you pass the physical part of the test. #b#t4031057##k will be the proof that you have indeed passed the test. I'll let #b#p1022000##k in advance that you've making your way there, so get ready. It won't be easy, but I have the utmost faith in you. Good luck.")
            elif val == 1 and (info == "s" or info == "p1"): self.say("You don't have #b#t4031057##k with you. Go see #b#p1022000##k in perion, pass the test and bring #b#t4031057##k with you. Only then can you take the second test. Good luck to you.")
            elif val == 1 and info == "p2":
                if self.inventoryGetItemCount(4031057) >= 1:
                    self.sayNext("Great job completing the physical part of the test. I knew you could do it. Now that you have passed the first half of the test, here's the second half. Please give me the necklace first.")
                    ret = self.inventoryExchange(0, 4031057, -1)
                    if not ret: self.say("Are you sure you own #b#t4031057##k by #b#p1022000##k? Be sure to leave a space in your ETC inventory.")
                    else:
                        self.questRecordSet(7500, "end1")
                        self.sayNext("Here's the 2nd half of the test. This test will determine whether you are smart enough to take the next step towards greatness. There is a dark, snow-covered area called the Holy Ground at the snowfield in Ossyria, where even the monsters can't reach. On the center of the area lies a huge stone called the Holy Stone. You'll need to offer a special item as the sacrifice, then the Holy Stone will test your wisdom right there on the spot.")
                        self.say("You'll need to answer each and every question given to you with honesty and conviction. If you correctly answer all the questions, then the Holy Stone will formally accept you and hand you #b#t4031058##k. Bring back the necklace, and I will help you make the next leap forward. Good luck.")
                else:
                    self.say("Are you sure you own #b#t4031057##k by #b#p1022000##k? Be sure to leave a space in your ETC inventory.")
            elif val == 1 and info == "end1":
                if self.inventoryGetItemCount(4031058) >= 1:
                    self.sayNext("Great job completing the mental part of the test. You have answered all questions correctly and wisely. I must say that I am quite impressed with the level of wisdom you have shown. Give me the necklace first before we take the next step.")
                    ret = self.inventoryExchange(0, 4031058, -1)
                    if not ret: self.say("Are you sure you have #b#t4031058##k granted by the Holy Stone? If you’re sure, don’t forget to leave a space in your Etc inventory.")
                    else:
                        #self.questRecordSetComplete(7500)
                        changeJob(job)
                else:
                    self.say("You don't have the necklace with you. Find the dark, snow-covered area called the Holy Ground at the snowfield in Ossyria, offer the special item as a sacrifice, and answer all questions honestly and with conviction to receive the necklace. Bring it back to me to complete the 3rd job test. Good luck...")
            elif val == 2:
                changeJob(job)
elif nRet == 1:
    val2 = self.questRecordGet(7000)
    if val2 == "":
        if level >= 50:
            if job >= 0 and job < 200:
                self.questRecordSet(7000, "s")
                self.say("You want permission to perform the Zakum Dungeon Mission, right? It must be #b#p2030008##k... ok, right! I'm sure you'll be fine in the dungeon. I hope you take care of that...")
            else: self.say("You want permission to perform the Zakum Dungeon Mission. I'm sorry, but you don't look like a warrior. Go and look for the head of your profession.")
        else:
            self.say("You want permission to perform the Zakum Dungeon Mission. I'm sorry, but the dungeon is very difficult for you. You must be at least level 50... train more and then come back here.")
    else: self.say("How are you doing on the Zakum Dungeon Mission? I heard that there is an incredible monster in the depths of this place... anyway, good luck. I'm sure you'll make it.")
elif nRet == 2:
    #s4common1;
    self.say("Under construction ... Please wait ...")
else:
    self.say("Under construction ... Please wait ...")