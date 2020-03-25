job = self.userGetJob()
level = self.userGetLevel()
str = self.userGetSTR()

val = self.questRecordGet(7500)
if val == "s" and (job == 110 or job == 120 or job == 130):
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
        self.say("Do you wish to become a Warrior? You need to meet some criteria for this. #bYou must be at least level 10, with at least 35 STR #k.", True)
        if level <= 9 or str <= 34:
            self.say("I don't believe you already have the qualities to be a Warrior yet. You need to train hard to become one or unable to handle the situation. Become much stronger and then come look for me.", True)
        else:
            ret = self.askYesNo("You definitely look like a warrior. It may not have been there yet, but I can already see a Warrior in you. What do you think? Do you wish to become a Warrior?")
            if ret == 0:
                self.say("So you need to think a little more... There is no reason to hurry... there is no something to do anyway... let me know when to make your decision, okay?")
            else:
                if self.inventoryGetSlotCount(1) > self.inventoryGetHoldCount(1):
                    self.say("You are much stronger now. In addition, all your inventories have more slots. A whole row, to be exact. You can check. I just gave you a little #bSP#k. When you open the #b skills menu in the lower left corner of the screen, you will see the skills you can learn using SP. #rWarning#k: You cannot increase them all at once. There are also those that will be available only after you learn some skills first.", True)
                    ret2 = self.inventoryExchange(0, 1302077, 1)
                    if ret2 == 0:
                        self.say("Hmm ... Check if there is an empty slot in your Equip inventory. I'm trying to give you a weapon as a reward for your performance.")
                    else:
                        self.userJob(100)
                        incval = self.random(200, 250)
                        self.userIncMHP(incval, False)
                        self.userIncSP(1, False)
                        self.inventoryIncSlotCount(1, 4)
                        self.inventoryIncSlotCount(2, 4)
                        self.inventoryIncSlotCount(3, 4)
                        self.inventoryIncSlotCount(4, 4)

                        self.sayNext("You are much stronger now. In addition, all your inventories have more slots. A whole row, to be exact. You can check. I just gave you a little #bSP#k. When you open the #b skills menu in the lower left corner of the screen, you will see the skills you can learn using SP. #rWarning#k: You cannot increase them all at once. There are also those that will be available only after you learn some skills first.")
                        self.sayNext("One more warning. Once you've chosen your job, try to stay alive for as long as you can. If you die, you will lose your experience. You don't want to lose your experience that you gained from so much sacrifice, do you? That's all I can teach you ... from now on, you'll have to work harder and harder to become better and better. Come see me when you realize that you are feeling more powerful than now.")
                        self.say("Oh, and... if you have any doubt about being a Warrior, you just come ask me. I don't know EVERYTHING, but I will help you with everything I know. See ya...")
                else:
                    self.say("Hmm ... Check if there is an empty slot in your Equip inventory. I'm trying to give you a weapon as a reward for your performance.")
    elif job == 100:
        if level >= 30:
            if self.inventoryGetItemCount(4031008) >= 1:
                self.say("Haven't found the person yet? Find #b#p1072000##k near #b#m102020300##k in #m102000000#. Give him the letter and maybe he'll tell you what you need to do.")
            elif self.inventoryGetItemCount(4031012) >= 1:
                self.sayNext("OHH...you came back safe! I knew you'd breeze through... I'll admit you are a strong, formidable Warrior... alright I'll make you an even stronger Warrior than you already are right now...Before THAT! you need to choose one of the three paths that you'll be given...it isn't going to be easy, so if you have any questions, feel free to ask.")
                jobInfoSel = self.askMenu("Alright, when you have made your decision, click on [I'll choose my occupation!] at the very bottom.\r\n#b#L0#Please explain the role of the Fighter.#k#l\r\n#b#L1#Please explain the role of the Page.#k#l\r\n#b#L2#Please explain the role of the Spearman.#k#l\r\n#b#L3#I'll choose my occupation!#k#l")
                if jobInfoSel == 0:
                    self.sayNext("Let me explain the role of the Fighter. He is the most common type of Warrior. The weapons used are the #bSword#k and the #bAxe#k, as there are advanced skills that can be acquired later. Do not use both weapons at the same time. Just keep the one that pleases you the most...")
                    self.sayNext("In addition, there are also skills such as #b#q1101006##k and #b#q1101007##k available to fighters. #b#q1101006##k is the type of skill that allows you and your party to upgrade their weapons. With it, you can take down your enemies with a sudden charge of power, which makes it very useful. The disadvantage is that your protection (defense) ability is somewhat reduced.")
                    self.say("#b#q1101007##k is the ability that allows you to return a portion of the damage dealt by the enemy's weapon. The larger attack, the worse the damage it will take back. This will help those who prefer close combat. What do you think? Isn't it cool to be a Fighter?")
                elif jobInfoSel == 1:
                    self.sayNext("Let me explain the role of Page. The Page is a knight's apprentice taking his first steps. He usually uses #bSwords#k and/or #b BWs #k. It's not a good idea to use both weapons, so you better pick one and stay with it.")
                    self.sayNext("Also, there are skills like #b#q1201006##k and #b#q1101007##k for you to learn. #b#q1201006##k causes any opponent around you to lose some attack and defense skills for some time. It is very useful against powerful monsters with good attack skills. It also works well in cooperative games.")
                    self.say("#b#q1101007##k an ability that allows you to return for a certain amount of damage dealt by monsters. The more damage you receive, the more damage you also deal to the enemy. It is the perfect skill for Warriors who are specializing in melee combat. What do you think? Isn't it cool to be a Page?")
                elif jobInfoSel == 2:
                    self.sayNext("Let me explain the role of the Spearman. It is a class that specializes in the use of long weapons, such as #bSpear#k and #bPolearm#k. There are many useful skills to acquire with both weapons, but I recommend that you choose one and stay with it.")
                    self.sayNext("Also, there are skills like #b#q1301006##k and #b#q1301007##k for you to learn. #b#q1301006##k allows you and your group members to improve attack and magic defense for a while. It is a very useful skill for spearman with weapons that require both hands and cannot defend themselves at the same time.")
                    self.say("#b#q1301007##k is a skill that allows you and your group to temporarily improve maximum HP and MP. You will be able to increase up to 160%, so the ability will help you and your party especially when tackling really powerful opponents. What do you think? Don't you think it's cool to be a Spearman?")
                elif jobInfoSel == 3:
                    jobSel = self.askMenu("Hmmm, have you made up your mind? Choose the 2nd job advancement of your liking.\r\n#b#L0#Fighter#k#l\r\n#b#L1#Page#k#l\r\n#b#L2#Spearman#k#l")
                    if jobSel == 0:
                        mJob = self.askYesNo("So you want to make the 2nd job advancement as a #bFighter#k? Once you make that decision you can't go back and choose another job... do you still wanna do it?")
                        if mJob == 0: self.say("Same? Getting stronger quickly will help you a lot during your journey... if you change your mind in the future, you can come back here anytime you want. Remember that I will make you so much more powerful than you already are.")
                        elif mJob == 1:
                            nPSP = (level - 30) * 3
                            if self.userGetSP() > nPSP: self.say("Hmmm... you have too many #bSP#k... you can't go up to the 2nd job with so much SP saved. Use more SP on 1st job skills and come back later.")
                            else:
                                ret = self.inventoryExchange(0, 4031012, -1)
                                if not ret: self.say("Hmm ... Are you sure you have #b#t4031012##k from #p1072000#? I can't allow you to raise a job level without it.")
                                #job adv. - Fighter
                                else:
                                    self.userJob(110)
                                    self.userIncSP(1, False)
                                    incval = self.random(300, 350)
                                    self.userIncMHP(incval, False)
                                    self.inventoryIncSlotCount(2, 4)
                                    self.inventoryIncSlotCount(4, 4)
                                    self.sayNext("Alright! You have now become the #bFighter#k! A fighter strives to become the strongest of the strong, and never stops fighting. Don't ever lose that will to fight and push forward 24/7. I'll help you become even stronger than you already are.")
                                    self.sayNext("I have just given you a book that gives you the list of skills you can acquire as a Fighter. In that book you'll find a bunch of skills the Fighter can learn. Your use and etc inventories have also been extended with an additional row of slots also available. Your max MP has also been increased... go check and see for it yourself.")
                                    self.sayNext("I have also given you a little bit of SP. Open the Skill Menu located at the bottom right corner. You'll be able to boost up your newly-acquired 2nd job skills. A word of warring though. You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.")
                                    self.say("Fighter have to be strong. But remember that you can't abouse that power and use it on a weaking. Please use your anormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger.Find me after you have advanced much further.")
                    elif jobSel == 1:
                        mJob = self.askYesNo("So you want to make the 2nd job advancement as a #bPage#k? Once you make that decision you can't go back and choose another job... do you still wanna do it?")
                        if mJob == 0: self.say("Same? Getting stronger quickly will help you a lot during your journey... if you change your mind in the future, you can come back here anytime you want. Remember that I will make you so much more powerful than you already are.")
                        elif mJob == 1:
                            nPSP = (level - 30) * 3
                            if self.userGetSP() > nPSP: self.say("Hmmm... you have too many #bSP#k... you can't go up to the 2nd job with so much SP saved. Use more SP on 1st job skills and come back later.")
                            else:
                                ret = self.inventoryExchange(0, 4031012, -1)
                                if not ret: self.say("Hmm ... Are you sure you have #b#t4031012##k from #p1072000#? I can't allow you to raise a job level without it.")
                                #job adv. - Page
                                else:
                                    self.userJob(120)
                                    self.userIncSP(1, False)
                                    incval = self.random(100, 150)
                                    self.userIncMMP(incval, False)
                                    self.inventoryIncSlotCount(2, 4)
                                    self.inventoryIncSlotCount(4, 4)
                                    self.sayNext("Alright! You have now become the #bPage#k! A fighter strives to become the strongest of the strong, and never stops fighting. Don't ever lose that will to fight and push forward 24/7. I'll help you become even stronger than you already are.")
                                    self.sayNext("I have just given you a book that gives you the list of skills you can acquire as a Page. In that book you'll find a bunch of skills the Page can learn. Your use and etc inventories have also been extended with an additional row of slots also available. Your max MP has also been increased... go check and see for it yourself.")
                                    self.sayNext("I have also given you a little bit of SP. Open the Skill Menu located at the bottom right corner. You'll be able to boost up your newly-acquired 2nd job skills. A word of warring though. You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.")
                                    self.say("Page have to be strong. But remember that you can't abouse that power and use it on a weaking. Please use your anormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger.Find me after you have advanced much further.")
                    elif jobSel == 2:
                        mJob = self.askYesNo("So you want to make the 2nd job advancement as a #bSpearman#k? Once you make that decision you can't go back and choose another job... do you still wanna do it?")
                        if mJob == 0: self.say("Same? Getting stronger quickly will help you a lot during your journey... if you change your mind in the future, you can come back here anytime you want. Remember that I will make you so much more powerful than you already are.")
                        elif mJob == 1:
                            nPSP = (level - 30) * 3
                            if self.userGetSP() > nPSP: self.say("Hmmm... you have too many #bSP#k... you can't go up to the 2nd job with so much SP saved. Use more SP on 1st job skills and come back later.")
                            #job adv. - Spearman
                            else:
                                self.userJob(130)
                                self.userIncSP(1, False)
                                incval = self.random(100, 150)
                                self.userIncMMP(incval, False)
                                self.inventoryIncSlotCount(2, 4)
                                self.inventoryIncSlotCount(4, 4)
                                self.sayNext("Alright! You have now become the #bSpearman#k! A fighter strives to become the strongest of the strong, and never stops fighting. Don't ever lose that will to fight and push forward 24/7. I'll help you become even stronger than you already are.")
                                self.sayNext("I have just given you a book that gives you the list of skills you can acquire as a Spearman. In that book you'll find a bunch of skills the Spearman can learn. Your use and etc inventories have also been extended with an additional row of slots also available. Your max MP has also been increased... go check and see for it yourself.")
                                self.sayNext("I have also given you a little bit of SP. Open the Skill Menu located at the bottom right corner. You'll be able to boost up your newly-acquired 2nd job skills. A word of warring though. You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.")
                                self.say("Spearman have to be strong. But remember that you can't abouse that power and use it on a weaking. Please use your anormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger.Find me after you have advanced much further.")
            else:
                sec = self.askYesNo("Whoa, you have definitely grown up! you don't look small and weak anymore...rather, now I can feel your presence as the Warrior! Impressive...so, what do you think? Do you want to get even stronger than you are right now? Pass a simple test and I'll do just that! Wanna do it?")
                if sec == 0: self.say("Same? Getting stronger quickly will help you a lot during your journey... if you change your mind in the future, you can come back here anytime you want. Remember that I will make you so much more powerful than you already are.")
                elif sec == 1:
                    self.sayNext("Good thinking. You look strong, don't let me wrong, but there's still a need to test your strength and see if you are for real. The test isn't too difficult, so you'll do just fine... Here, take this letter first. Make sure you don't lose it.")
                    ret = self.inventoryExchange(0, 4031008, 1)
                    if not ret: self.say("Hmmm...I can't give you my letter because you don't have any room on your etc inventory. Please come back after you have made a space on your inventory, because the letter is the only way you can take the test.")
                    self.say("Give this letter to #b#p1072000##k which may be close to #b#m102020300##k in #m102000000#. He is replacing me as an instructor because I am busy here. Hand him the letter and he will apply the test instead. Other information will be passed directly by him to you. Good luck to you.")
        else:
            ret = self.askMenu("Oh, do you have a question? \r\n#b#L0#What are the general characteristics of a Warrior?#l\r\n#L1#What are the weapons of a Warrior?#l\r\n#L2#What are a Warrior's armor?#l\r\n#L3#What are the skills available to a Warrior?#l")
            if ret == 0:
                self.sayNext("Let me explain the role of the Warrior. Warriors have incredible physical strength and power. They also know how to defend themselves from attacks by monsters, so they are the best to fight in close combat with monsters. With a high level of vigor, you will not die easily.")
                self.say("However, in order to accurately attack the monster, you will need a good deal of DEX, so don't just focus on improving your STR. If you want to improve quickly, I recommend that you face stronger monsters.")
            elif ret == 1:
                self.sayNext("Let me explain the weapons that a Warrior uses. He uses weapons that allow him to cut, stab and attack. You will not be able to use weapons such as bows and projectile weapons.")
                self.say("The most common weapons are swords, maces, pole-arms, spears, axes, etc... Every weapon has its advantages and disadvantages, so examine them well before choosing one. For now, try to use those with a high attack level.")
            elif ret == 2:
                self.sayNext("Let me explain about warrior's armor. Warriors are strong and very vigorous, so they can wear heavy and resistant armor. They are not very pretty... but they serve their purpose well: armor = safety.")
                self.say("Shields, especially, are perfect for Warriors. Remember, however, that you will not be able to use a shield if you are wielding a two-handed weapon. I know it will be a difficult decision ...")
            elif ret == 3:
                self.sayNext("The skills available to the Warriors are aimed at their incredible physical strength and power. Those that enhance hand-to-hand combat are the ones that will help you the most. There is also an ability that allows you to recover your HP. You better become an expert on it.")
                self.say("The two available attack skills are #b#q1001004##k and #b#q1001005##k. #q1001004# is the one that deals great damage to a single enemy. You can improve this skill from the beginning.")
                self.say("In turn, #q1001005# does not do much damage, but attacks multiple enemies in one area at once. You will only be able to use it once you have upgraded #q1001004# once. You decide.")
    elif job == 110: self.say("Ahhh... it's you! What do you think? How is the path of a Fighter? You... look a lot stronger than before! I hope you continue to improve.")
    elif job == 120: self.say("Ahhh... it's you! What do you think? How is the path of a Page? I know you are still an apprentice, but soon the training will end and you will be called a Knight!")
    elif job == 130: self.say("Ahhh... it's you! What do you think? How is the path of a Spearman? Keep training with dedication, because one day you will become an Dragon Knight...")
    elif job == 111: self.say("Ahhh... You finally became a Crusader... I knew you wouldn't let me down. So, what do you think of life as a Crusader? Please dedicate yourself and train even more.")
    elif job == 121: self.say("Ahhh... You finally became a White Knight... I knew you wouldn't let me down. So, what do you think of life as a White Knight? Please dedicate yourself and train even more.")
    elif job == 131: self.say("Ahhh... You finally became a Dragon Knight... I knew you wouldn't let me down. So, what do you think of life as a Dragon Knight? Please dedicate yourself and train even more.")
    elif job == 112: self.say("Ahhh... You finally became a Hero... I knew you wouldn't let me down. So, what do you think of life as a Hero? Please dedicate yourself and train even more.")
    elif job == 122: self.say("Ahhh... You finally became a Paladin... I knew you wouldn't let me down. So, what do you think of life as a Paladin? Please dedicate yourself and train even more.")
    elif job == 132: self.say("Ahhh... You finally became a Dark Knight... I knew you wouldn't let me down. So, what do you think of life as a Dark Knight? Please dedicate yourself and train even more.")
    else: self.say("What a magnificent physicist! What incredible power! Warriors are the best !!!! What do you think? Do you want to rise as a warrior?")