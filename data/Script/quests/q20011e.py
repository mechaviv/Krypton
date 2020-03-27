self.sayNext("Ah, it seems like you've successfully hunted a #o100120#. Pretty simple, right? Regular Attacks may be easy to use, but they are pretty weak. Don't worry, though. #p1102006# will teach you how to use more powerful skills. Wait, let me give you a well-deserved quest reward before you go.")
self.sayNext("This equipment is for Noblesses. It's much cooler than what you're wearing right now, isn''t it? Follow the arrows to your left to meet my younger brother #b#p1102006##k. How about you change into your new Noblesse outfit before you go? \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i1002869# #t1002869# - 1 \r\n#i1052177# #t1052177# - 1 \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 30 exp")

nRet = self.inventoryExchange(0, 1002869, 1, 1052177, 1)
if not nRet: self.say("Your inventory is full")
else:
    self.userIncEXP(30, False)
    self.userAvatarOriented("UI/tutorial.img/6")
    self.questRecordSetState(20011, 2)
    self.questEndEffect()