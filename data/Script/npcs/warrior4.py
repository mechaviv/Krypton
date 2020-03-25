jobCode = self.userGetJob()

if jobCode == 111 or jobCode == 121 or jobCode == 131:
    if self.userGetLevel() >= 120:
        if self.userRecordGetState(6904) == 2:
            if jobCode == 111: nRet = self.askMenu( "You're qualified to be a true warrior. \r\nDo you want job advancement?\r\n#b#L0# I want to advance to Hero.#l\r\n#b#L1#  Let me think for a while.#l" )
            if jobCode == 121: nRet = self.askMenu( "You're qualified to be a true warrior. \r\nDo you want job advancement?\r\n#b#L0# I want to advance to Paladin.#l\r\n#b#L1#  Let me think for a while.#l" )
            if jobCode == 131: nRet = self.askMenu( "You're qualified to be a true warrior. \r\nDo you want job advancement?\r\n#b#L0# I want to advance to Dark Knight.#l\r\n#b#L1#  Let me think for a while.#l" )
            if nRet == 1:  self.say( "You don't have to hesitate to be the best Warrior..Whenever you make your decision, talk to me. If you're ready, I'll let you make the 4th job advancement." )
            else:
                nPSP = (self.userGetLevel() - 120) * 3
                if self.userGetSP() > nPSP: self.say( "Hmm...You have too many #bSP#k. You can't make the 4th job advancement with too many SP left." )
                else:
                    ret = self.inventoryExchange(0, 2280003, 1)
                    if not ret: self.say( "You can't proceed as you don't have an empty slot in your inventory. Please clear your inventory and try again." )
                    else:
                        self.userIncSP(3, False)
                        self.userIncAP(5, False)

                        if jobCode == 111: self.userJob(112)
                        elif jobCode == 121: self.userJob(122)
                        elif jobCode == 131: self.userJob(132)

                        cJob = self.userGetJob()
                        if cJob == 112:
                            self.sayNext( "You have become the best of warriors, my #bHero#k.You will gain the #bRush#k Skill which makes you attack mutiple enemies and give you indomitable will along with #bStance#k and #bAchilles#k" )
                            self.sayNext( "This is not all about Hero. Hero is a well-balanced warrior who has excellent attack and defense power. It can learn various attack skills as well as combo attack if he trains himself." )
                            self.say( "Don't forget that it all depends on how much you train." )
                        elif cJob == 122:
                            self.sayNext( "You have become the best of warriors, my #bPaladin#k.You will gain the #bRush#k Skill which makes you attack mutiple enemies and give you indomitable will along with #bStance#k and #bAchilles#k" )
                            self.sayNext( "This is not all about Paladin. Paladin is good at element-based attack and defense. It can use a new element-based and may break the limit of charge blow if you train yourself." )
                            self.say( "Don't forget that it all depends on how much you train." )
                        elif cJob == 132:
                            self.sayNext( "You have become the best of warriors, my #bDark Knight#k.You will gain the #bRush#k Skill which makes you attack mutiple enemies and give you indomitable with along with #bStance#k and #bAchilles#k." )
                            self.sayNext( "This is not all about Dark Knight. Dark Knight can use the power of darkness. It can attack with power of darkness which is unbelievably strong and may summon the figure of darkness." )
                            self.say( "Don't forget that it all depends on how much you train." )
        else: self.say( "You're not ready to make 4th job advancement. When you're ready, talk to me." )
    else: self.say( "You're still weak to go to warrior extreme road. If you get stronger, come back to me." )
elif jobCode == 112 or jobCode == 122 or jobCode == 132:
    if jobCode == 112:self.say( "You became the best warrior, the position of #bHero#k. Stronger power means more responsibility. Hope you get over all the tests you will have in future." )
    elif jobCode == 122:self.say( "You became the best warrior, the position of #bPaladin#k. Stronger power means more responsibility. Hope you get over all the tests you will have in future." )
    elif jobCode == 132: self.say( "You became the best warrior, the position of #bDark Knight#k. Stronger power means more responsibility. Hope you get over all the tests you will have in future." )
else: self.say( "Why do you want to see me? There is nothing you want to ask me." )