# The Holy Land
def wizQuestion(index):
    if index == 1:
        v1 = self.askMenu("Here's the 1st question. Which NPC won't you see in Henesys on Victoria Island ...?#b\r\n#L0# #p1012101##l\r\n#L1# #p1002001##l\r\n#L2# #p1010100##l\r\n#L3# #p1012100##l\r\n#L4# #p1012102##l")
        if v1 == 0 or v1 == 2 or v1 == 3 or v1 == 4:self.say("Wrong...\r\nStart again.")
        elif v1 == 1:
            v2 = self.askMenu("Here's the second question. Which of the following monsters will you not see on Maple Island ...?#b\r\n#L0# #o100101##l\r\n#L1# #o1210102##l\r\n#L2# #o130101##l\r\n#L3# #o1210100##l\r\n#L4# #o120100##l")
            if v2 == 0 or v2 == 1 or v2 == 2 or v2 == 4:self.say("Wrong...\r\nStart again.")
            elif v2 == 3:
                v3 = self.askMenu("Here's the third question. Which of the following items did Maya ask to cure her illness ...?#b\r\n#L0# #t4000036##l\r\n#L1# #t4031205##l\r\n#L2# #t4031738##l\r\n#L3# #t4032490##l\r\n#L4# #t4031006##l")
                if v3 == 0 or v3 == 1 or v3 == 2 or v3 == 3:self.say("Wrong...\r\nStart again.")
                elif v3 == 4:
                    v4 = self.askMenu("Here's the fourth question. Which of the following cities is not part of Victoria Island ...?#b\r\n#L0# Sleepywood#l\r\n#L1# Amherst#l\r\n#L2# Perion#l\r\n#L3# Kerning City#l\r\n#L4# Ellinia#l")
                    if v4 == 0 or v4 == 2 or v4 == 3 or v4 == 4:self.say("Wrong...\r\nStart again.")
                    elif v4 == 1:
                        v5 = self.askMenu("Here's the 5th question. What monster will you not find in the ant tunnel in Victoria Island's central dungeon ...?#b\r\n#L0# #o2110200##l\r\n#L1# #o2230100##l\r\n#L2# #o5130100##l\r\n#L3# #o2230101##l\r\n#L4# #o3000000##l")
                        if v5 == 0 or v5 == 1 or v5 == 2 or v5 == 3:self.say("Wrong...\r\nStart again.")
                        elif v5 == 4:
                            self.sayNext("Very well. All of your answers were correct...\r\nYour wisdom has been proven.\r\nTake this necklace and go back there...")
                            ret = self.inventoryExchange(0, 4031058, 1)
                            if not ret:self.say("Is your ETC inventory full ...?")
    elif index == 2:
        v1 = self.askMenu("Here's the 1st question. Which pair of monster drops is correct ...?#b\r\n#L0# #o3210100# - Fire Boar Nose#l\r\n#L1# #o4230100# - Eye of the Cold Eye#l\r\n#L2# #o1210100# - Pig's ear#l\r\n#L3# #o2300100# - #t4000042##l\r\n#L4# #o2230101# - Zombie Mushroom Sunshade#l")
        if v1 == 0 or v1 == 1 or v1 == 2 or v1 == 4:self.say("Wrong...\r\nStart again.")
        elif v1 == 3:
            v2 = self.askMenu("Here's the second question. Which NPC will you not see in Perion on Victoria Island ...?#b\r\n#L0# #p1021100##l\r\n#L1# #p1032002##l\r\n#L2# #p1022002##l\r\n#L3# #p1022003##l\r\n#L4# #p1022100##l")
            if v2 == 0 or v2 == 2 or v2 == 3 or v2 == 4:self.say("Wrong...\r\nStart again.")
            elif v2 == 1:
                v3 = self.askMenu("Here's the third question. Which of the following NPCs is Alex's father, the runaway boy, that you will see in Kerning City ...?#b\r\n#L0# #p1012005##l\r\n#L1# #p1012002##l\r\n#L2# #p12000##l\r\n#L3# #p20000##l\r\n#L4# #p1012003##l")
                if v3 == 0 or v3 == 1 or v3 == 2 or v3 == 3:self.say("Wrong...\r\nStart again.")
                elif v3 == 4:
                    v4 = self.askMenu("Here's the fourth question. Which of the following items will you receive from the NPC after joining 30 Black Marbles during the test for the 2nd job advancement ...?#b\r\n#L0# #t4031012##l\r\n#L1# The Necklace of a Hero#l\r\n#L2# The Pendant of a Hero#l\r\n#L3# The Medal of a Hero#l\r\n#L4# The Brand of a Hero#l")
                    if v4 == 1 or v4 == 2 or v4 == 3 or v4 == 4:self.say("Wrong...\r\nStart again.")
                    elif v4 == 0:
                        v5 = self.askMenu("Here's the 5th question. Which job / stat pair is required for the 1st job advancement ...?#b\r\n#L0# Warrior - STR 30+#l\r\n#L1# Magician - INT 25+#l\r\n#L2# Archer - DEX 25+#l\r\n#L3# Thief - DES 20+#l\r\n#L4# Thief - LUK 20+#l")
                        if v5 == 0 or v5 == 1 or v5 == 3 or v5 == 4:self.say("Wrong...\r\nStart again.")
                        elif v5 == 2:
                            self.sayNext("Very well. All of your answers were correct...\r\nYour wisdom has been proven.\r\nTake this necklace and go back there...")
                            ret = self.inventoryExchange(0, 4031058, 1)
                            if not ret:self.say("Is your ETC inventory full ...?")
    elif index == 3:
        v1 = self.askMenu("Here's the 1st question. Which NPC will you see FIRST on MapleStory ...?#b\r\n#L0# #p2000##l\r\n#L1# #p1010100##l\r\n#L2# #p2102##l\r\n#L3# #p2001##l\r\n#L4# #p2101##l")
        if v1 == 0 or v1 == 1 or v1 == 2 or v1 == 3:self.say("Wrong...\r\nStart again.")
        elif v1 == 4:
            v2 = self.askMenu("Here's the second question. How many EXP needed to go from level 1 to level 2 ...?#b\r\n#L0# 10#l\r\n#L1# 15#l\r\n#L2# 20#l\r\n#L3# 25#l\r\n#L4# 30#l")
            if v2 == 0 or v2 == 2 or v2 == 3 or v2 == 4:self.say("Wrong...\r\nStart again.")
            elif v2 == 1:
                v3 = self.askMenu("Here's the third question. What NPC will you not see in El Nath in Ossyria ...?#b\r\n#L0# #p2020000##l\r\n#L1# #p2020003##l\r\n#L2# #p2012010##l\r\n#L3# #p2020006##l\r\n#L4# #p2020007##l")
                if v3 == 0 or v3 == 1 or v3 == 3 or v3 == 4:self.say("Wrong...\r\nStart again.")
                elif v3 == 2:
                    v4 = self.askMenu("Here's the fourth question. Which of the following jobs will you not be able to acquire after the 2nd job ...?#b\r\n#L0# Page#l\r\n#L1# Bandit#l\r\n#L2# Assassin#l\r\n#L3# Cleric#l\r\n#L4# Magician#l")
                    if v4 == 0 or v4 == 1 or v4 == 2 or v4 == 3:self.say("Wrong...\r\nStart again.")
                    elif v4 == 4:
                        v5 = self.askMenu("Here's the 5th question. What mission can be done again ...?#b\r\n#L0# Maya and the Weird Medicine#l\r\n#L1# Alex the Runaway Kid#l\r\n#L2# Pia and the Blue Mushroom#l\r\n#L3# Arwen and the Glass Shoe#l\r\n#L4# Icarus and the Flying Pill#l")
                        if v5 == 0 or v5 == 1 or v5 == 2 or v5 == 4:self.say("Wrong...\r\nStart again.")
                        elif v5 == 3:
                            self.sayNext("Very well. All of your answers were correct...\r\nYour wisdom has been proven.\r\nTake this necklace and go back there...")
                            ret = self.inventoryExchange(0, 4031058, 1)
                            if not ret:self.say("Is your ETC inventory full ...?")

val = self.questRecordGet(7500)
if val == "end1":
    nRet = self.askYesNo("If you want to test your wisdom, will you have to offer #b#t4005004##k as a sacrifice...\r\nAre you ready to offer #t4005004# and answer my questions?")
    if nRet == 0:self.say("Come back when you're ready.")
    else:
        if self.inventoryGetSlotCount(4) > self.inventoryGetHoldCount(4):
            if self.inventoryGetItemCount(4031058) >= 1: self.say("You already have #b#t4031058##k...\r\nTake the necklace and go back there...")
            else:
                ret = self.inventoryExchange(0, 4005004, -1)
                if not ret: self.say("If you want to test your wisdom, you will have to offer #b#t4005004##k as a sacrifice.")
                else:
                    self.sayNext("Okay ... I'm going to test your wisdom now. Answer all questions correctly to pass the test. BUT, if you lie to me just once, you'll have to start all over again... ok, come on.")
                    rnum = self.random(1, 3)
                    if rnum == 1: wizQuestion(1)
                    elif rnum == 2: wizQuestion(2)
                    elif rnum == 3: wizQuestion(3)

        else:
            self.say("Your ETC inventory is full... make some space on it or you won't be able to take the test. After making the adjustments, try again ...")