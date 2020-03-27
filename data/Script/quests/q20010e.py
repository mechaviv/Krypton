self.sayNext("Are you the Noblesse my brother Kimu sent? Nice to meet you! I'm Kizan. I'll give you the reward Kimu asked me to give you. Remember, you can check your Inventory by pressing the #bI key#k. Red potions help you recover HP, and blue ones help recover MP. It's a good idea to learn how to use them beforehand so you'll be ready with them when you're in danger. \r\n\r\n#fUI/UIWindow.img/Quest/reward# \r\n\r\n#v2000020# #z2000020# \r\n#v2000021# #z2000021# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#15 exp")
nRet = self.inventoryExchange(0, 2000020, 5, 2000021, 5)
if not nRet: self.say("Your inventory is full")
else:
    self.userIncEXP(15, False)
    self.userAvatarOriented("UI/tutorial.img/3")
    self.questRecordSetState(20010, 2)
    self.questEndEffect()