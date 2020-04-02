job = self.userGetJob()
level = self.userGetLevel()
dex = self.userGetDEX()

val = self.questRecordGet(7500)

val = self.questRecordGet(7500)
if val == "s" and (job == 410 or job == 420):
    self.questRecordSet(7500, "p1")
    self.sayNext("I was waiting for you. Few days ago, I heard about you from #bTylus#k in Ossyria. Well... I'd like to test your strength. There is a secret passage near the ant tunnel. Nobody but you can go into that passage. If you go into the passage, you will meet my other self. Beat him and bring #b#t4031059##k to me.")
    self.say("My other self is quite strong. He uses many special skills and you should fight with him 1 on 1. However people cannot stay long in the secret passage, so it is important to beat him ASAP. Well... Good Luck! I will look forward to you bringing #b#t4031059##k to me.")
elif val == "p1":
    if self.inventoryGetItemCount(4031059) >= 1:
        self.sayNext("Wow ... You defeated my other self and brought #b#t4031059##k to me. Very good! It certainly proves your strength. In terms of strength, you can now go to the 3rd job. As promised, I'll give you #b#t4031057##k. Give this necklace to #bTylus#k from Ossyria and you can make a second test for 3rd job. Good luck~.")
        ret = self.inventoryExchange(0, 4031059, -1, 4031057, 1)
        if ret == 0: self.say("Hmm... how strange. Are you sure you have #b#t4031059##k? If so, make sure you have an empty slot in etc inventory.")
        else: self.questRecordSet(7500, "p2")
    else:
        self.say("There is a secret passage near the ant tunnel. Nobody but you can go into that passage. If you go into the passage, you will meet my other self. Beat him and bring #b#t4031059##k to me.")
elif val == "p2":
    if self.inventoryGetItemCount(4031057) <= 0:
        self.sayNext("Ahh! You missed #b#t4031057##k, huh? I said you should be careful... For God's sake, I'll give you another one... AGAIN. Please be careful this time. Without this, you will not be able to take the test for the 3rd job.")
        ret = self.inventoryExchange(0, 4031057, 1)
        if ret == 0: self.say("Hmm... how strange... make sure you have an empty slot in etc inventory.")
    else:
        self.say("Give this necklace to #bTylus#k from Ossyria and you can make a second test for 3rd grade. Good luck ~!")
else:
    if job == 0:
        self.say("Do you wish to become a Rogue? You need to meet some criteria for this. #bYou must be at least level 10, with at least 25 DEX #k.", True)
        if level <= 9 or dex <= 24: self.say("I don't believe you already have the qualities to be a Rogue yet. You need to train hard to become one or unable to handle the situation. Become much stronger and then come look for me.", True)
        else:
            ret = self.askYesNo("You definitely look like a rogue. It may not have been there yet, but I can already see a Rogue in you. What do you think? Do you wish to become a Rogue?")
            if ret == 0:
                self.say("So you need to think a little more... There is no reason to hurry... there is no something to do anyway... let me know when to make your decision, okay?")
            else:
                if self.inventoryGetSlotCount(1) > self.inventoryGetHoldCount(1):
                    self.say("You are much stronger now. In addition, all your inventories have more slots. A whole row, to be exact. You can check. I just gave you a little #bSP#k. When you open the #b skills menu in the lower left corner of the screen, you will see the skills you can learn using SP. #rWarning#k: You cannot increase them all at once. There are also those that will be available only after you learn some skills first.", True)
                    ret2 = self.inventoryExchange(0, 1472061, 1, 1332063, 1, 2070015, 1000, 2070015, 1000, 2070015, 1000)
                    if ret2 == 0:
                        self.say("Hmm ... Check if there is an empty slot in your Equip inventory. I'm trying to give you a weapon as a reward for your performance.")
                    else:
                        self.userJob(400)
                        self.userIncMHP(self.random(100, 150), False)
                        self.userIncMMP(self.random(30, 50), False)
                        self.userIncSP(1, False)
                        self.inventoryIncSlotCount(1, 4)
                        self.inventoryIncSlotCount(4, 4)

                        self.sayNext("You are much stronger now. In addition, all your inventories have more slots. A whole row, to be exact. You can check. I just gave you a little #bSP#k. When you open the #b skills menu in the lower left corner of the screen, you will see the skills you can learn using SP. #rWarning#k: You cannot increase them all at once. There are also those that will be available only after you learn some skills first.")
                        self.sayNext("One more warning. Once you've chosen your job, try to stay alive for as long as you can. If you die, you will lose your experience. You don't want to lose your experience that you gained from so much sacrifice, do you? That's all I can teach you ... from now on, you'll have to work harder and harder to become better and better. Come see me when you realize that you are feeling more powerful than now.")
                        self.say("Oh, and... if you have any doubt about being a Warrior, you just come ask me. I don't know EVERYTHING, but I will help you with everything I know. See ya...")
                else:
                    self.say("Hmm ... Check if there is an empty slot in your Equip inventory. I'm trying to give you a weapon as a reward for your performance.")